package jcgp.gui.population;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jcgp.backend.function.FunctionSet;
import jcgp.gui.constants.Constants;

/**
 * A menu class, exposes all of the allowed functions
 * when called by a node, so that the node function can be changed.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public class FunctionSelector extends VBox {
	
	private GUINode target;
		
	public FunctionSelector(FunctionSet functionSet) {
		setFillWidth(true);
		setVisible(false);
		setStyle("-fx-border-color: #A0A0A0; -fx-border-width: 1 1 0 1");
		
		remakeFunctions(functionSet);
		
		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dismiss();
			}
		});
	}
	
	public void remakeFunctions(final FunctionSet fs) {
		getChildren().clear();
		
		for (int i = 0; i < fs.getAllowedFunctionCount(); i++) {
			final int index = i;
			Label l = new Label(fs.getAllowedFunction(i).toString());
			l.setMaxWidth(Double.MAX_VALUE);
			l.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #A0A0A0; -fx-border-width: 0 0 1 0; -fx-padding: 2");
			
			l.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					((Label) event.getSource()).setStyle("-fx-background-color: " + Constants.SOFT_HIGHLIGHT_COLOUR + "; -fx-border-color: #B0B0B0; -fx-border-width: 0 0 1 0; -fx-padding: 2");
				}
			});
			l.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					((Label) event.getSource()).setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #A0A0A0; -fx-border-width: 0 0 1 0; -fx-padding: 2");
				}
			});
			l.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					target.setFunction(fs.getAllowedFunction(index));
					dismiss();
				}
			});
			
			getChildren().add(l);
		}
	}
	
	public void relocateAndShow(MouseEvent event, GUINode node) {
		relocate(event.getSceneX() - 5, event.getSceneY() - 5);
		target = node;
		setVisible(true);
	}
	
	private void dismiss() {
		setVisible(false);
	}
	
}
