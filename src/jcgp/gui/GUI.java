package jcgp.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jcgp.JCGP;
import jcgp.backend.modules.problem.TestCaseProblem.TestCase;
import jcgp.gui.console.ConsolePane;
import jcgp.gui.dragresize.HorizontalDragResize;
import jcgp.gui.dragresize.VerticalDragResize;
import jcgp.gui.population.FunctionSelector;
import jcgp.gui.population.GUINode;
import jcgp.gui.population.PopulationPane;
import jcgp.gui.settings.SettingsPane;

/**
 * Main class for the graphical user interface (GUI).
 * <br><br>
 * This class declares the main method used when running the GUI.
 * In addition, all main GUI panes are declared and instantiated here.
 * <br><br>
 * The user interface is divided into 3 main components: the node grid
 * ({@link PopulationPane}), the control pane ({@link SettingsPane}) and
 * the console ({@link ConsolePane}). Click on any of the links in
 * brackets to see more information about each interface component.
 * <br><br>
 * This class also contains the instance of JCGP responsible for
 * running the experiments in GUI mode. JCGP's execution must be delegated
 * to a separate thread so that the GUI remains unblocked. This is done using
 * a JavaFX {@code Service} which calls {@code nextGeneration()} in a loop
 * until it is interrupted by the main JavaFX thread.
 * <br>
 * This service also handles flushing the console in a thread safe way. This
 * is done by synchronizing the {@code nextGeneration()} and {@code flush()}
 * method calls on a lock object.
 * 
 * @author Eduardo Pedroni
 *
 */
public class GUI extends Application {
    
	/*
	 * Actual GUI elements
	 */
	private Stage stage;
	private PopulationPane populationPane;
	private ConsolePane console;
	private SettingsPane settingsPane;
	private final FunctionSelector functionSelector;
	
	/*
	 * Flow control objects
	 */
	private boolean running = false;
	private final Object printLock = new Object();
	private Service<Void> jcgpService;
	private Runnable consoleFlush;
	
	/*
	 * The experiment itself
	 */
	private final JCGP jcgp;
	
	
	/**
	 * Start JCGP with the user interface.
	 * 
	 * @param args no arguments are used.
	 */
	public static void main(String[] args) {
		// not much to do, simply launch the JavaFX application
		launch();
	}
	
	/**
	 * Makes a new instance of GUI. This initialises the JCGP experiment and 
	 * instantiates the function selector. It also creates the console flush task
	 * and the service responsible for running the JCGP experiment.
	 */
	public GUI() {
		jcgp = new JCGP();
		functionSelector = new FunctionSelector(jcgp.getResources().getFunctionSet());
		
		/*
		 * This task flushes the console in a thread-safe way.
		 * The problem is that this task is executed using Platform.runLater()
		 * to ensure that the flush itself happens on the JavaFX thread. However,
		 * runLater() is not guaranteed to run anytime soon. If the time taken for
		 * jcgp to perform a single generation is shorter than the time taken for
		 * this task to be executed by the platform, consoleFlush tasks will be 
		 * scheduled faster than they can be executed and the console will eventually 
		 * freeze. 
		 * 
		 * This is addressed by synchronizing the flushes with each nextGeneration() call.
		 */
		consoleFlush = new Runnable() {
			@Override
			public void run() {
				/* 
				 * Try to acquire printlock - wait here until jcgpService relinquishes it
				 * by calling wait(). This means that it is finished with the current generation
				 * and will wait for the console to be flushed to move on.
				 * It might be the case that the service has already released the lock by waiting
				 * on it; it makes no difference. In that case this will acquire the lock
				 * immediately and proceed to flush the console.
				 */
				synchronized(printLock) {
					/*
					 * The lock is acquired, at this point we are certain that jcgpService
					 * cannot execute; it is currently waiting to be notified about the lock.
					 * No additional consoleFlush tasks can be scheduled with runLater() because
					 * the service is waiting. We can now take our time to flush the console.
					 */
					console.flush();
					/*
					 * Once the console finishes flushing, we notify jcgpService to perform the
					 * next generation.
					 */
					printLock.notifyAll();
				}
			}
		};
		
		/* 
		 * This service runs on a separate thread and performs
		 * the experiment, including console prints, in a thread-safe
		 * way. It is synchronized with consoleFlush.
		 */
		jcgpService = new Service<Void> () {
			@Override
			protected Task<Void> createTask() {
				Task<Void> t = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						/*
						 * Only execute if the experiment isn't finished
						 * and the service hasn't been cancelled.
						 */
						while (!isCancelled() && !jcgp.isFinished()) {
							/*
							 * Attempt to acquire the printlock.
							 * Successfully doing so means no printing
							 * is currently taking place and we are free
							 * to schedule a print task.
							 * This lock acquisition should never block. It should
							 * not be possible to execute this statement without
							 * having been notified by consoleFlush.
							 */
							synchronized (printLock) {
								/*
								 * Lock has been acquired, schedule a print
								 * task ahead of time. The actual print messages
								 * haven't been send to the console yet, that happens
								 * during nextGeneration(), but since we have the lock
								 * consoleFlush() will sit and wait for us to release it
								 * whenever we are finished queueing prints.
								 */
								Platform.runLater(consoleFlush);
								/*
								 * Perform the actual generation. Here zero or more
								 * strings might be sent to the console buffer.
								 */
								jcgp.nextGeneration();
								/*
								 * The generation is complete, relinquish the lock.
								 * By this point chances are the platform is already trying
								 * to execute the consoleFlush task that we scheduled. If it
								 * hasn't already started though, it doesn't matter; we will
								 * wait for a notification on the lock, which will only come
								 * when printing is complete.
								 */
								printLock.wait();
								/*
								 * We have been notified. This means all buffered messages have
								 * been successfully flushed to the actual console control and
								 * we are now ready to perform another generation (or break out
								 * of the loop if the loop conditions are no longer met).
								 */
							}
							/*
							 * We no longer own the lock, but neither does consoleFlush. 
							 * The synchrony cycle has returned to its initial state, and we
							 * are free to acquire the lock again.
							 */
						}
						/*
						 * Something happened to break the while loop -
						 * either the experiment finished or the user pressed
						 * pause.
						 */
						if (jcgp.isFinished()) {
							// the experiment has finished, switch to pause mode
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									runningMode(false);
								}
							});
						}
						return null;
					}
				};
				return t;
			}
		};
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * This method gets called when the application launches. Once it
		 * returns, the application falls into the main loop which handles 
		 * events, so all elements must be constructed here.
		 */
		
		// make the console and set it so it is used for JCGP prints
		console = new ConsolePane();
		jcgp.setConsole(console);
		
		// store reference to the stage
		stage = primaryStage;

		/*
		 * The experiment layer contains all of the experiment-related panes. 
		 * The only element that sits higher than this is the function selector.
		 */
		BorderPane experimentLayer = new BorderPane();
		/*
		 * The left frame encapsulates the population pane and the console.
		 * It goes into the center position of the experiment layer, next to the settings pane.
		 */
		BorderPane leftFrame = new BorderPane();
		
		/*
		 * The population pane is a TabPane containing a tab for each chromosome. 
		 */
		populationPane = new PopulationPane(this);
		
		/*
		 * The settings pane is a big class containing the entire control pane
		 */
		settingsPane = new SettingsPane(this);
		
		// make control pane and console resizable
		HorizontalDragResize.makeDragResizable(settingsPane);
		VerticalDragResize.makeDragResizable(console);
		// prevent resizables from growing larger than the experiment layer
		settingsPane.maxWidthProperty().bind(experimentLayer.widthProperty());
		console.maxHeightProperty().bind(experimentLayer.heightProperty());

		// put console and population pane in the main frame
		leftFrame.setCenter(populationPane);
		leftFrame.setBottom(console);
		
		// set the main frame and the control pane in the experiment layer
		experimentLayer.setCenter(leftFrame);
		experimentLayer.setRight(settingsPane);
		
		/*
		 * Now we deal with the stage.
		 */
		primaryStage.setTitle("JCGP");
		
		// this pane holds the entire scene, that is its sole job.
		Pane sceneParent = new Pane();
		// the experiment layer should fill the entire scene parent
		experimentLayer.prefHeightProperty().bind(sceneParent.heightProperty());
		experimentLayer.prefWidthProperty().bind(sceneParent.widthProperty());
		// the function selector goes over the experiment layer so it doesn't get covered by other panes
		sceneParent.getChildren().addAll(experimentLayer, functionSelector);
		
		// set the scene, minimum sizes, show
		primaryStage.setScene(new Scene(sceneParent));
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		primaryStage.show();
		
		// when the main stage closes, close the test case table as well
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (settingsPane.getTestCaseTable() != null) {
					settingsPane.getTestCaseTable().close();
				}
			}
		});
	}

	/**
	 * Run/pause method.
	 * Run the experiment if it is paused, or pause it if it is running.
	 * <br>
	 * This method is the callback used by the run/pause button. It
	 * controls the jcgp service.
	 */
	public void runPause() {
		// do nothing if experiment is finished or parameters aren't valid
		if (!jcgp.isFinished() && settingsPane.areParametersValid()) {
			if (!running) {
				runningMode(true);
				jcgpService.restart();
			} else {
				jcgpService.cancel();
				runningMode(false);
			}
		}
	}
	
	/**
	 * Perform a single generation using {@code nextGeneration()}. 
	 * <br>
	 * On top of that, this method performs all of the housekeeping 
	 * that is normally done before and after running, such as 
	 * refreshing the chromosome panes.
	 */
	public void step() {
		// do nothing if experiment is finished, running or parameters aren't valid
		if (!running && !jcgp.isFinished() && settingsPane.areParametersValid()) {
			if (settingsPane.isResetRequired()) {
				reset();
			}
			populationPane.unlockOutputs();
			jcgp.nextGeneration();
			console.flush();
			
			populationPane.updateGenes();
			populationPane.relockOutputs();
			settingsPane.revalidateParameters();
			settingsPane.updateControls(false, jcgp.isFinished());
		}
	}
	
	/**
	 * Reset button callback. If the parameters are valid, 
	 * this resets the entire experiment by calling {@code reset()}
	 * on jcgp.
	 */
	public void reset() {
		if (!running && settingsPane.areParametersValid()) {
			setEvaluating(false);
			jcgp.reset();
			settingsPane.applyParameters();
			reDraw();
		}
	}
	
	/**
	 * Does a complete GUI refresh. 
	 * This is potentially lengthy, so use with care.
	 */
	public void reDraw() {
		populationPane.remakeTabs();
		settingsPane.revalidateParameters();
		settingsPane.updateControls(false, jcgp.isFinished());
		console.flush();
	}
	
	/**
	 * Toggles the entire GUI between run and pause
	 * mode. 
	 * <br><br>
	 * A lot of the GUI must be enabled or disabled
	 * depending on what the experiment is doing. This
	 * method provides a one-line way to make
	 * all required adjustments.
	 * 
	 * @param value true if experiment is running, false otherwise.
	 */
	private void runningMode(boolean value) {		
		if (value) {
			populationPane.unlockOutputs();
			if (settingsPane.isResetRequired()) {
				reset();
			}
		} else {
			populationPane.updateGenes();
			populationPane.relockOutputs();
			settingsPane.revalidateParameters();
		}
		populationPane.setDisable(value);
		settingsPane.updateControls(value, jcgp.isFinished());
		
		running = value;
	}
	
	/**
	 * Refresh the function selector, used when functions are enabled or disabled.
	 */
	public void updateFunctionSelector() {
		functionSelector.remakeFunctions(jcgp.getResources().getFunctionSet());
	}
	
	/**
	 * @return true if jcgp is evolving.
	 */
	public boolean isWorking() {
		return running;
	}

	/**
	 * Relocate the function selector to the right position
	 * relative to the specified node and set it visible.
	 * 
	 * @param event the mouse event containing cursor coordinates.
	 * @param node the node whose function should be changed.
	 */
	public void bringFunctionSelector(MouseEvent event, GUINode node) {
		functionSelector.relocateAndShow(event, node);
	}

	/**
	 * @return a reference to the {@code JCGP} experiment.
	 */
	public JCGP getExperiment() {
		return jcgp;
	}
	
	/**
	 * Starts the evaluation process with the given test case.
	 * It does so by calling {@code evaluateTestCase()} on
	 * the population pane.
	 * 
	 * @param testCase the test case to evaluate.
	 */
	public void evaluateTestCase(TestCase<Object> testCase) {
		populationPane.evaluateTestCase(testCase);
	}

	/**
	 * Hide all evaluated values. This should be called when
	 * evaluations are no longer being performed.
	 */
	public void hideGeneValues() {
		populationPane.hideValues();
	}

	/**
	 * Set the system into evaluation mode. 
	 * When in evaluation mode, the population pane
	 * refreshes the node values whenever connection
	 * changes happen.
	 * 
	 * @param value true if evaluations are happening, false otherwise.
	 */
	public void setEvaluating(boolean value) {
		populationPane.setEvaluating(value);
	}

	/**
	 * @return a reference to the GUI stage.
	 */
	public Stage getStage() {
		return stage;
	}
	
	/**
	 * Writes all buffered content out to the GUI console.
	 */
	public void flushConsole() {
		console.flush();
	}

	/**
	 * @return the index of the chromosome currently being looked at.
	 */
	public int getChromosomeIndex() {
		return populationPane.getSelectionModel().getSelectedIndex();
	}
}
