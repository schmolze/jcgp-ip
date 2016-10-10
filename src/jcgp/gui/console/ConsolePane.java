package jcgp.gui.console;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import jcgp.backend.resources.Console;
import jcgp.gui.constants.Constants;

/**
 * Console pane used by the GUI to display CGP output messages.
 * This class realises {@code Console}. It consists of a JavaFX
 * {@code TextArea} and a {@code StringBuffer}. The buffer is filled
 * as print messages are queued. Calling {@code flush()} writes the
 * contents of the buffer to the {@code TextArea} and empties the buffer.
 * 
 * @see Console
 * @author Eduardo Pedroni
 *
 */
public class ConsolePane extends AnchorPane implements Console {
	
	private TextArea textArea = new TextArea("Welcome to JCGP!\n");
	private StringBuffer printBuffer = new StringBuffer();
		
	/**
	 * Creates a new instance of this class.
	 */
	public ConsolePane() {
		super();
		textArea.setEditable(false);
		/*
		 * This nasty hack is needed because the default TextArea ContextMenu is not
		 * in the public API, making it impossible to override it with a custom one.
		 * This has not been fixed as of 8/4/2014.
		 * 
		 * The following code modifies the EventDispatcher to consume the right mouse
		 * button click, preventing the default menu from appearing. It propagates the mouse
		 * click further so other elements will respond appropriately.
		 * 
		 * TODO this should be refactored once the API is updated.
		 */
		final EventDispatcher initial = textArea.getEventDispatcher();
		textArea.setEventDispatcher(new EventDispatcher() {
			@Override
			public Event dispatchEvent(Event event, EventDispatchChain tail) {
				if (event instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent)event;
					if (mouseEvent.getButton() == MouseButton.SECONDARY || 
							(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isControlDown())) {
						event.consume();
					}
				}
				return initial.dispatchEvent(event, tail);
			}
		});
		
		// make the new context menu including the clear option		
		MenuItem copySelected = new MenuItem("Copy");
		copySelected.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent event) {
				textArea.copy();
			}
		});
		MenuItem selectAll = new MenuItem("Select all");
		selectAll.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent event) {
				textArea.selectAll();
			}
		});
		MenuItem clearConsole = new MenuItem("Clear");
		clearConsole.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent event) {
				textArea.setText("");
			}
		});
				
		textArea.setContextMenu(new ContextMenu(copySelected,
												selectAll, 
												new SeparatorMenuItem(),
												clearConsole));
		
		// anchor the text area so it resizes automatically
		AnchorPane.setTopAnchor(textArea, Constants.RESIZE_MARGIN);
		AnchorPane.setBottomAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);
		AnchorPane.setLeftAnchor(textArea, 0.0);
		
		setMinHeight(Constants.CONSOLE_MIN_HEIGHT);
		setPrefHeight(Constants.CONSOLE_MIN_HEIGHT);
		
		getChildren().add(textArea);
	}

	@Override
	public void println(String s) {
		printBuffer.append(s + "\n");
	}

	@Override
	public void print(String s) {
		printBuffer.append(s);
	}

	@Override
	public void flush() {
		textArea.appendText(printBuffer.toString());
		printBuffer = new StringBuffer();
	}
	
}
