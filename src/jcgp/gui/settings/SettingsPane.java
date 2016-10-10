package jcgp.gui.settings;

import java.io.File;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jcgp.JCGP;
import jcgp.backend.function.FunctionSet;
import jcgp.backend.modules.es.EvolutionaryStrategy;
import jcgp.backend.modules.mutator.Mutator;
import jcgp.backend.modules.problem.Problem;
import jcgp.backend.modules.problem.TestCaseProblem;
import jcgp.backend.parameters.Parameter;
import jcgp.gui.GUI;
import jcgp.gui.constants.Constants;
import jcgp.gui.settings.parameters.GUIParameter;
import jcgp.gui.settings.testcase.TestCaseTable;

/**
 * This is a fairly hefty class which encapsulates the entire right-hand
 * control pane. It contains base parameters, module selectors and their
 * associated parameters, flow controls and file loading/saving buttons.
 * <br><br>
 * A single instance of this class is used in {@code GUI}.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public class SettingsPane extends AnchorPane {
	
	/*
	 * The primary containers, these make up each section of the settings pane.
	 */
	private VBox mainContainer;
	private VBox baseParameterPane, eaPane, mutatorPane, problemPane;
	private VBox nodeFunctions;
	
	// all buttons
	private Button runPause = new Button("Run"), step = new Button("Step"), reset = new Button("Reset");
	private Button loadParameters = new Button("Load parameters"), loadChromosome = new Button("Load chromosome"), saveChromosome = new Button("Save chromosome");
	
	// this is a list of parameters used for parameter validity checks
	private ArrayList<GUIParameter<?>> parameters = new ArrayList<GUIParameter<?>>();
	
	// the test case table stage
	private TestCaseTable testCaseTable;
	
	// a reference to the parent GUI
	private GUI gui;
	
	private int currentArity;
		
	/**
	 * Create a new instance of {@code SettingsPane} associated
	 * with the specified {@code GUI} object.
	 * 
	 * @param gui a reference to this object's parent.
	 */
	public SettingsPane(GUI gui) {
		super();
		this.gui = gui;
		
		// acquire a reference to jcgp, for convenience
		final JCGP jcgp = gui.getExperiment();
		
		// make the overarching container
		mainContainer = new VBox(8);
		mainContainer.setPadding(new Insets(5, Constants.RESIZE_MARGIN, 0, 2));
		
		setMinWidth(Constants.SETTINGS_MIN_WIDTH);
		setPrefWidth(Constants.SETTINGS_MIN_WIDTH);
		
		// initialise all sub-divisions
		initialiseBaseParameters(jcgp);
		
		initialiseEAParameters(jcgp);
		
		initialiseMutatorParameters(jcgp);
		
		initialiseProblemTypeParameters(jcgp, gui);
		
		createControls(gui);
		
		// prepare the scroll pane
		ScrollPane scroll = new ScrollPane();
		scroll.setFitToWidth(true);
		scroll.setContent(mainContainer);
		scroll.setStyle("-fx-background-color: #FFFFFF");
		
		// anchor the scroll pane to itself, bearing in mind the resize margin
		AnchorPane.setTopAnchor(scroll, 0.0);
		AnchorPane.setBottomAnchor(scroll, 0.0);
		AnchorPane.setRightAnchor(scroll, 0.0);
		AnchorPane.setLeftAnchor(scroll, Constants.RESIZE_MARGIN);
		
		// add the scroll pane, all done!
		getChildren().add(scroll);
	}

	/**
	 * Creates the base parameters pane
	 * 
	 * @param jcgp
	 */
	private void initialiseBaseParameters(JCGP jcgp) {
		baseParameterPane = new VBox(2);
		
		Text header = new Text("Base Parameters");
		header.setFont(Font.font("Arial", 14));
		header.setUnderline(true);
		
		baseParameterPane.getChildren().add(header);

		parameters.add(GUIParameter.create(jcgp.getResources().getRowsParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getColumnsParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getInputsParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getOutputsParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getLevelsBackParameter(), this));
		
		GUIParameter<?> gp = GUIParameter.create(jcgp.getResources().getPopulationSizeParameter(), this);
		gp.setPadding(new Insets(0, 0, 10, 0));
		parameters.add(gp);
		
		parameters.add(GUIParameter.create(jcgp.getResources().getCurrentGenerationParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getGenerationsParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getCurrentRunParameter(), this));
		
		gp = GUIParameter.create(jcgp.getResources().getRunsParameter(), this);
		gp.setPadding(new Insets(0, 0, 10, 0));
		parameters.add(gp);
		
		parameters.add(GUIParameter.create(jcgp.getResources().getSeedParameter(), this));
		parameters.add(GUIParameter.create(jcgp.getResources().getReportIntervalParameter(), this));
		
		baseParameterPane.getChildren().addAll(parameters);
		mainContainer.getChildren().add(baseParameterPane);
	}

	private void initialiseEAParameters(final JCGP jcgp) {
		eaPane = new VBox(2);
		
		Text header = new Text("Evolutionary Strategy");
		header.setFont(Font.font("Arial", 14));
		header.setUnderline(true);
		
		final ComboBox<EvolutionaryStrategy> esCBox = new ComboBox<EvolutionaryStrategy>();
		esCBox.getItems().addAll(jcgp.getEvolutionaryStrategies());
		esCBox.getSelectionModel().select(jcgp.getEvolutionaryStrategy());
		esCBox.prefWidthProperty().bind(mainContainer.widthProperty());
		
		final VBox eaParameters = new VBox(2);
		
		refreshParameters(jcgp.getEvolutionaryStrategy().getLocalParameters(), eaParameters);
		
		esCBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				jcgp.setEvolutionaryStrategy(esCBox.getSelectionModel().getSelectedIndex());
				refreshParameters(esCBox.getSelectionModel().getSelectedItem().getLocalParameters(), eaParameters);
				gui.flushConsole();
			}
		});
		
		eaPane.getChildren().addAll(header, esCBox, eaParameters);	
		mainContainer.getChildren().add(eaPane);
	}

	private void initialiseMutatorParameters(final JCGP jcgp) {
		mutatorPane = new VBox(2);
		
		Text header = new Text("Mutator");
		header.setFont(Font.font("Arial", 14));
		header.setUnderline(true);
		
		final ComboBox<Mutator> mutatorCBox = new ComboBox<Mutator>();
		mutatorCBox.getItems().addAll(jcgp.getMutators());
		mutatorCBox.getSelectionModel().select(jcgp.getMutator());
		mutatorCBox.prefWidthProperty().bind(mainContainer.widthProperty());
		
		final VBox mutatorParameters = new VBox(2);
		refreshParameters(jcgp.getMutator().getLocalParameters(), mutatorParameters);
		
		mutatorCBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				jcgp.setMutator(mutatorCBox.getSelectionModel().getSelectedIndex());
				refreshParameters(mutatorCBox.getSelectionModel().getSelectedItem().getLocalParameters(), mutatorParameters);
				gui.flushConsole();
			}
		});
		
		mutatorPane.getChildren().addAll(header, mutatorCBox, mutatorParameters);
		mainContainer.getChildren().add(mutatorPane); 
	}
	
	private void initialiseProblemTypeParameters(final JCGP jcgp, final GUI gui) {
		updateArity();
		
		problemPane= new VBox(2);
		
		Text header = new Text("Problem Type");
		header.setFont(Font.font("Arial", 14));
		header.setUnderline(true);
		
		final ComboBox<Problem> problemCBox = new ComboBox<Problem>();
		problemCBox.getItems().addAll(jcgp.getProblems());
		problemCBox.getSelectionModel().select(jcgp.getProblem());
		problemCBox.prefWidthProperty().bind(mainContainer.widthProperty());
		
		final VBox problemParameters = new VBox(2);
		problemParameters.setPadding(new Insets(0, 0, 4, 0));
		refreshParameters(jcgp.getProblem().getLocalParameters(), problemParameters);
		
		final HBox testCaseControlContainer = new HBox(2);
		
		final Button showTestCaseButton = makeTestCaseButton();
		final Button loadProblemDataButton = makeLoadTestCaseButton();
		HBox.setHgrow(showTestCaseButton, Priority.ALWAYS);
		showTestCaseButton.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(loadProblemDataButton, Priority.ALWAYS);
		loadProblemDataButton.setMaxWidth(Double.MAX_VALUE);
		
		if (jcgp.getProblem() instanceof TestCaseProblem<?>) {
			testCaseControlContainer.getChildren().addAll(showTestCaseButton, loadProblemDataButton);
			remakeTestCaseTable();
		} else {
			testCaseControlContainer.getChildren().add(loadProblemDataButton);
		}
		
		nodeFunctions = new VBox(2);
		nodeFunctions.setPadding(new Insets(0, 0, 4, 0));
		refreshFunctions();
		
		problemCBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				jcgp.setProblem(problemCBox.getSelectionModel().getSelectedIndex());
				updateArity();
				refreshParameters(jcgp.getProblem().getLocalParameters(), problemParameters);
				if (testCaseTable != null) {
					testCaseTable.close();
				}
				gui.setEvaluating(false);
				refreshFunctions();
				testCaseControlContainer.getChildren().clear();
				if (jcgp.getProblem() instanceof TestCaseProblem) {
					testCaseControlContainer.getChildren().addAll(showTestCaseButton, loadProblemDataButton);
					remakeTestCaseTable();
				} else {
					testCaseControlContainer.getChildren().add(loadProblemDataButton);
				}
				gui.reset();
			}
		});
		
		problemPane.getChildren().addAll(header, problemCBox, problemParameters, nodeFunctions, testCaseControlContainer); 
		mainContainer.getChildren().add(problemPane);
		
	}
	
	private Button makeLoadTestCaseButton() {
		Button b = new Button("Load data");
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Open problem file...");
				fc.getExtensionFilters().add(new ExtensionFilter("CGP " + gui.getExperiment().getProblem() + " files", "*" + ((TestCaseProblem<?>) gui.getExperiment().getProblem()).getFileExtension()));
				fc.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
				File chrFile = fc.showOpenDialog(gui.getStage());
				if (chrFile != null) {
					gui.getExperiment().loadProblemData(chrFile);
					remakeTestCaseTable();
					gui.reDraw();
				}
			}
		});
		return b;
	}

	private Button makeTestCaseButton() {
		Button b = new Button("Show data");
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				testCaseTable.show(); 
			}
		});
		return b;
	}

	private void createControls(final GUI gui) {
		Text header = new Text("Experiment controls");
		header.setFont(Font.font("Arial", 14));
		header.setUnderline(true);
		
		final VBox controls = new VBox(2);
		controls.setFillWidth(true);
		
		final HBox flowButtons = new HBox(2);
		runPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {				
				gui.runPause();
			}
		});
		
		step.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gui.step();
			}
		});
		
		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gui.reset();
			}
		});
		
		HBox.setHgrow(runPause, Priority.ALWAYS);
		runPause.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(step, Priority.ALWAYS);
		step.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(reset, Priority.ALWAYS);
		reset.setMaxWidth(Double.MAX_VALUE);
		
		flowButtons.getChildren().addAll(runPause, step, reset);
		flowButtons.setPadding(new Insets(0, 0, 10, 0));
		
		loadParameters.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {				
				FileChooser fc = new FileChooser();
				fc.setTitle("Open .par file...");
				fc.getExtensionFilters().add(new ExtensionFilter("CGP parameter files", "*.par"));
				fc.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
				File parFile = fc.showOpenDialog(gui.getStage());
				if (parFile != null) {
					gui.getExperiment().loadParameters(parFile);
					gui.reDraw();
					refreshFunctions();
				}
				gui.flushConsole();
			}
		});
		
		loadChromosome.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Load .chr file...");
				fc.getExtensionFilters().add(new ExtensionFilter("CGP chromosome files", "*.chr"));
				fc.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
				File chrFile = fc.showOpenDialog(gui.getStage());
				if (chrFile != null) {
					gui.getExperiment().loadChromosome(chrFile, gui.getChromosomeIndex());
					gui.reDraw();
				}
				gui.flushConsole();
			}
		});
		saveChromosome.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Save .chr file...");
				fc.getExtensionFilters().add(new ExtensionFilter("CGP chromosome files", "*.chr"));
				fc.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
				File chrFile = fc.showSaveDialog(gui.getStage());
				if (chrFile != null) {
					gui.getExperiment().saveChromosome(chrFile, gui.getChromosomeIndex());
				}
				gui.flushConsole();
			}
		});
		
		HBox.setHgrow(loadParameters, Priority.ALWAYS);
		loadParameters.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(loadChromosome, Priority.ALWAYS);
		loadChromosome.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(saveChromosome, Priority.ALWAYS);
		saveChromosome.setMaxWidth(Double.MAX_VALUE);
		
		controls.getChildren().addAll(header, flowButtons, loadParameters, loadChromosome, saveChromosome);
		
		mainContainer.getChildren().add(controls);	
	}
	
	/**
	 * Builds {@code GUIParameter}s and adds them to the provided {@code VBox}. 
	 * The parameters built are taken from the specified list.
	 *
	 * @param newParameters the list of parameters to add.
	 * @param container the container to add the parameters to.
	 */
	private void refreshParameters(ArrayList<Parameter<?>> newParameters, VBox container) {
		// remove what is currently in the container from the parameter list
		parameters.removeAll(container.getChildren());
		// remove everything in the container
		container.getChildren().clear();
		// if there are parameters to add, add them all
		if (newParameters != null) {
			for (int i = 0; i < newParameters.size(); i++) {
				// factory method returns the right subtype of GUIParameter
				GUIParameter<?> guiParameter = GUIParameter.create(newParameters.get(i), this);
				// make sure to add it to the parameter list as well
				parameters.add(guiParameter);
				container.getChildren().add(guiParameter);
			}
		}
		// do a quick refresh just in case something is invalid
		revalidateParameters();
	}
	
	/**
	 * This method handles a problem type change by updating the list of allowed
	 * node functions.
	 * <br><br>
	 * It does so by creating new checkboxes for each function in the function set.
	 */
	private void refreshFunctions() {
		// remove all current functions
		nodeFunctions.getChildren().clear();
		CheckBox checkBox;
		// get a reference to the function set
		final FunctionSet functionSet = gui.getExperiment().getResources().getFunctionSet();
		for (int i = 0; i < functionSet.getTotalFunctionCount(); i++) {
			// add a checkbox for each function
			checkBox = new CheckBox(functionSet.getFunction(i).toString());
			checkBox.setId(String.valueOf(i));
			// make sure the selection matches the function set
			checkBox.setSelected(functionSet.isEnabled(functionSet.getFunction(i)));
			final int index = i;
			// set listener so function set gets updated if the checkboxes change
			checkBox.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (((CheckBox) event.getSource()).isSelected()) {
						functionSet.enableFunction(index);
					} else {
						functionSet.disableFunction(index);
					}
					gui.updateFunctionSelector();
					revalidateParameters();
				}
			});
			// add the new checkbox
			nodeFunctions.getChildren().add(checkBox);
		}
		// make sure function selector has all functions
		gui.updateFunctionSelector();
	}
	
	/**
	 * @return true if the experiment is currently evolving something, false otherwise.
	 */
	public boolean isExperimentRunning() {
		return gui.isWorking();
	}
	
	/**
	 * 
	 * @return true if the experiment needs to be reset, false if otherwise.
	 */
	public boolean isResetRequired() {
		for (GUIParameter<?> parameter : parameters) {
			if (parameter.requiresReset()) {
				return true;
			}
		}
		if (arityChanged()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if no parameters have their status set to ParameterStatus.INVALID.
	 */
	public boolean areParametersValid() {
		for (GUIParameter<?> parameter : parameters) {
			if (!parameter.isValid()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calls validate() on every parameter. This is called whenever a parameter changes,
	 * so that other parameters update their status in case they were dependent on the
	 * changed parameter.
	 * <br><br>
	 * This also disables the controls if a reset is necessary, preventing the experiment
	 * from running until it has happened.
	 */
	public void revalidateParameters() {
		boolean disableControls = false;
		for (GUIParameter<?> parameter : parameters) {
			parameter.validate();
			if (parameter.requiresReset()) {
				disableControls = true;
			}
		}
		if (arityChanged()) {
			disableControls = true;
		}
		
		runPause.setDisable(disableControls);
		step.setDisable(disableControls);
	}
	
	/**
	 * Calls applyValue() on every parameter. This is called when a reset occurs, so that
	 * the new value will be used as a reference instead of the old reference value.
	 * <br><br>
	 * It also closes the test case table, just in case.
	 */
	public void applyParameters() {
		for (GUIParameter<?> parameter : parameters) {
			parameter.applyValue();
		}
		updateArity();
		if (testCaseTable != null) {
			testCaseTable.close();
		}
	}

	/**
	 * Updates all of the controls to their appropriate state based on the status of the
	 * experiment, in order to prevent inappropriate operations if the experiment is
	 * running or finished.
	 * 
	 * @param running true if the experiment is running.
	 * @param finished true if the experiment is finished.
	 */
	public void updateControls(boolean running, boolean finished) {
		baseParameterPane.setDisable(running);
		eaPane.setDisable(running);
		mutatorPane.setDisable(running);
		problemPane.setDisable(running);
		
		runPause.setText(running ? "Pause" : "Run");
		runPause.setDisable(finished);
		step.setDisable(running || finished);
		reset.setDisable(running);
		
		loadParameters.setDisable(running);
		loadChromosome.setDisable(running);
		saveChromosome.setDisable(running);
		
		testCaseTable.getTable().setDisable(running);
	}
	
	private void remakeTestCaseTable() {
		boolean wasShowing = false;
		if (testCaseTable != null) {
			wasShowing = testCaseTable.isShowing();
			testCaseTable.close();
		}
		testCaseTable = new TestCaseTable((TestCaseProblem<Object>) gui.getExperiment().getProblem(), gui);
		if (wasShowing) {
			testCaseTable.show();
		}
	}
	
	public TestCaseTable getTestCaseTable() {
		return testCaseTable;
	}
	
	private void updateArity() {
		currentArity = gui.getExperiment().getProblem().getFunctionSet().getMaxArity();
	}
	
	private boolean arityChanged() {
		return currentArity != gui.getExperiment().getProblem().getFunctionSet().getMaxArity();
	}
}
