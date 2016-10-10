package jcgp.gui.population;

import javafx.event.EventHandler;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.gui.constants.Constants;

public class GUIInput extends GUIGene {

	private Input input;

	public GUIInput(ChromosomePane parentRef, final Input input) {
		super();
		
		this.parent = parentRef;
		this.input = input;
		
		relocate(Constants.NODE_RADIUS,
				(input.getIndex() * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);
		
		updateText();

		Circle outputSocket = new Circle(Constants.NODE_RADIUS, 0, Constants.SOCKET_RADIUS, Paint.valueOf("white"));
		outputSocket.setId(String.valueOf(0));
		outputSocket.setStroke(Paint.valueOf("black"));

		getChildren().addAll(mainCircle, text, outputSocket);
		
		/*
		 * Mouse event handlers on whole gene
		 */
		addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// the drag has entered this node, react appropriately
				// this happens even if we are the source of the drag
				((GUIGene) event.getGestureSource()).setConnectionLine((GUIGene) event.getSource());
				Connection source = ((GUIGene) event.getGestureSource()).getChangingConnection();
				if (input == source) {
					setState(GUIGeneState.NO_CHANGE_TARGET);
				} else {
					setState(GUIGeneState.VALID_TARGET);
				}
			}
		});

		addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// the drag has exited this node, react appropriately
				// this happens even if we are the source of the drag
				parent.setTarget(false);
				if (event.isPrimaryButtonDown()) {
					if (getState() == GUIGeneState.NO_CHANGE_TARGET) {
						setState(GUIGeneState.INDIRECT_HOVER);
					} else {
						setState(GUIGeneState.NEUTRAL);
						((GUIGene) event.getGestureSource()).setConnectionStates(GUIGeneState.INDIRECT_HOVER);
					}
				}
			}

		});

		addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				GUIGene source = ((GUIGene) event.getGestureSource());
				// set states to reflect the new situation
				if (source.isLocked()) {
					source.setState(GUIGeneState.HOVER);
					source.setConnectionStates(GUIGeneState.HOVER);
				} else {
					source.setState(GUIGeneState.NEUTRAL);
					source.setConnectionStates(GUIGeneState.NEUTRAL);
				}
				
				// the user released the drag gesture on this node, react appropriately
				if (source.isLocked()) {
					// remove locks from the old connection, add the to the new
					// note that the old connection may still have locks after this
					parent.getGuiGene(source.getChangingConnection()).removeLocks(source.getLocks());
					source.setChangingConnection(input);
					addLocks(source.getLocks());
				} else {
					source.setChangingConnection(input);
				}

				source.updateLines();
				setState(GUIGeneState.HOVER);			
			}
		});
		
		addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// cursor has entered this node without dragging, or it is dragging and this is the source
				if (getState() == GUIGeneState.NEUTRAL) {
					setState(GUIGeneState.HOVER);
				}
			}
		});
		
		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// cursor has left this node without dragging, or it is dragging and this is the source
				if (getState() == GUIGeneState.HOVER) {
					setState(GUIGeneState.NEUTRAL);
					setConnectionStates(GUIGeneState.NEUTRAL);
				}
			}
		});
	}
	
	@Override
	public void setState(GUIGeneState newState) {
		super.setState(newState);
		
		switch (newState) {
		case ACTIVE_HOVER:
			if (locked > 0) {
				setState(GUIGeneState.LOCKED_HOVER);
			} else {
				mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			}
			break;
		case INVALID_TARGET:
			mainCircle.setFill(Paint.valueOf(Constants.BAD_SELECTION_COLOUR));
			break;
		case LOCKED_HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			break;
		case HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.MEDIUM_HIGHLIGHT_COLOUR));
			break;
		case INDIRECT_HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			break;
		case NEUTRAL:
			if (locked > 0) {
				setState(GUIGeneState.HOVER);
			} else {
				mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_COLOUR));
			}
			break;
		case NO_CHANGE_TARGET:
			parent.setTarget(true);
			mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_SELECTION_COLOUR));
			break;
		case SOURCE:
			mainCircle.setFill(Paint.valueOf(Constants.MEDIUM_HIGHLIGHT_COLOUR));
			break;
		case VALID_TARGET:
			parent.setTarget(true);
			mainCircle.setFill(Paint.valueOf(Constants.GOOD_SELECTION_COLOUR));
			break;
		default:
			break;
		}
		
	}

	/**
	 * Set all connections to a given state.
	 * 
	 * @param newState the state to set connections to.
	 */
	@Override
	public void setConnectionStates(GUIGeneState newState) {
		// nothing
	}

	@Override
	public void resetState() {
		setState(GUIGeneState.NEUTRAL);
	}

	@Override
	protected void setLocked(boolean value) {
		locked += value ? 1 : -1;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.ACTIVE_HOVER);
	}

	@Override
	public void setChangingConnection(Connection newConnection) {
		// do nothing
	}

	@Override
	public Connection getChangingConnection() {
		return null;
	}

	@Override
	public void addLocks(int value) {
		locked += value;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.ACTIVE_HOVER);
	}

	@Override
	public void updateLines() {
		// nothing
	}

	@Override
	public void removeLocks(int value) {
		locked -= value;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.NEUTRAL);
	}

	@Override
	public void setConnectionLine(GUIGene gene) {
		// nothing
	}

	public void setValue(Object newValue) {
		input.setValue(newValue);
	}

	@Override
	public void updateText() {
		if (parent.isEvaluating()) {
			text.setText("I: " + input.getIndex() + "\n" + input.getValue().toString());
		} else {
			text.setText("I: " + input.getIndex());
		}
	}	
}
