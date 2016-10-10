package jcgp.gui.population;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.backend.population.Node;
import jcgp.backend.population.Output;
import jcgp.gui.GUI;
import jcgp.gui.constants.Constants;

public class GUIOutput extends GUIGene {

	private Line sourceLine;
	private Output output;

	public GUIOutput(ChromosomePane parentRef, final Output output, Line line, GUI gui) {
		super();
		
		this.parent = parentRef;
		this.output = output;
		this.sourceLine = line;

		relocate(((gui.getExperiment().getResources().columns() + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS,
				(output.getIndex() * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);

		// set the line ends correctly
		updateLines();
		updateText();

		Circle socket = new Circle(-Constants.NODE_RADIUS, 0, Constants.SOCKET_RADIUS, Paint.valueOf("white"));
		socket.setId(String.valueOf(0));
		socket.setStroke(Paint.valueOf("black"));

		final Label connectionLabel = new Label("S");
		connectionLabel.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-border-color:rgba(0, 0, 0, 0.5); ");
		connectionLabel.relocate(socket.getCenterX() + 5, socket.getCenterY() - 10);
		connectionLabel.setVisible(false);

		/*
		 * Mouse event handlers on sockets
		 * 
		 */
		socket.addEventFilter(MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// the mouse has been dragged out of the socket, this means a full drag is in progress
				startFullDrag();
			}
		});

		socket.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// user is hovering over connection socket
				connectionLabel.setVisible(true);
			}
		});

		socket.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// user exits the connection socket
				connectionLabel.setVisible(false);
			}
		});

		socket.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// mouse was pressed on the socket
				setState(GUIGeneState.SOURCE);
			}
		});

		socket.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!parent.isTarget()) {
					sourceLine.setEndX(event.getX() + ((Circle) event.getSource()).getParent().getLayoutX());
					sourceLine.setEndY(event.getY() + ((Circle) event.getSource()).getParent().getLayoutY());
				}
				
			}
		});	

		socket.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isStillSincePress()) {
					// mouse was released before dragging out of the socket
					updateLines();
					setState(GUIGeneState.HOVER);
				} else if (getState() == GUIGeneState.SOURCE) {
					// no connection has been made, fallback
					resetState();
					updateLines();
				}
				
			}
		});


		/*
		 * Mouse event handlers on whole gene
		 */
		addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// the drag has entered this node, react appropriately
				setState(GUIGeneState.INVALID_TARGET);
			}
		});

		addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// the drag has exited this node, react appropriately
				// this happens even if we are the source of the drag
				if (event.isPrimaryButtonDown()) {
					if (event.getGestureSource() == event.getSource()) {
						setState(GUIGeneState.SOURCE);
					} else {
						setState(isLocked() ? GUIGeneState.HOVER : GUIGeneState.NEUTRAL);
					}
				}
			}
		});

		addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// making a connection to an output is illegal
				// set states to reflect the new situation
				GUIGene source = ((GUIGene) event.getGestureSource());
				
				if (source.isLocked()) {
					source.setState(GUIGeneState.HOVER);
					source.setConnectionStates(GUIGeneState.HOVER);
				} else {
					source.setState(GUIGeneState.NEUTRAL);
					source.setConnectionStates(GUIGeneState.NEUTRAL);
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

		addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setLocked(!isLocked());
			}
		});
		
		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// cursor has left this node without dragging, or it is dragging and this is the source
				if (getState() == GUIGeneState.HOVER && !isLocked()) {
					setState(GUIGeneState.NEUTRAL);
					setConnectionStates(GUIGeneState.NEUTRAL);
				}
			}
		});


		getChildren().addAll(mainCircle, text, socket, connectionLabel);
		
	}

	@Override
	public void setState(GUIGeneState newState) {
		super.setState(newState);
		
		switch (newState) {
		case ACTIVE_HOVER:
			break;
		case INVALID_TARGET:
			mainCircle.setFill(Paint.valueOf(Constants.BAD_SELECTION_COLOUR));
			break;
		case HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.MEDIUM_HIGHLIGHT_COLOUR));
			sourceLine.setVisible(true);
			if (!isLocked()) {
				setConnectionStates(GUIGeneState.ACTIVE_HOVER);
			}
			break;
		case INDIRECT_HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			break;
		case NEUTRAL:
			mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_COLOUR));
			sourceLine.setVisible(false);
			break;
		case NO_CHANGE_TARGET:
			mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_SELECTION_COLOUR));
			break;
		case SOURCE:
			mainCircle.setFill(Paint.valueOf(Constants.HARD_HIGHLIGHT_COLOUR));
			setConnectionStates(GUIGeneState.NEUTRAL);
			setConnectionStates(GUIGeneState.INDIRECT_HOVER);
			break;
		case VALID_TARGET:
			mainCircle.setFill(Paint.valueOf(Constants.GOOD_SELECTION_COLOUR));
			break;
		default:
			break;
		}
	}
	
	@Override
	public void updateLines() {
		if (output.getSource() instanceof Node) {
			int row = ((Node) output.getSource()).getRow(), 
					column = ((Node) output.getSource()).getColumn();
			sourceLine.setEndX(((column + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + 2 * Constants.NODE_RADIUS);
			sourceLine.setEndY((row * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);
		} else if (output.getSource() instanceof Input) {
			int inputIndex = ((Input) output.getSource()).getIndex();
			sourceLine.setEndX(2 * Constants.NODE_RADIUS);
			sourceLine.setEndY(inputIndex * (2 * Constants.NODE_RADIUS + Constants.SPACING) + Constants.NODE_RADIUS);
		}
	}

	@Override
	public void setConnectionStates(GUIGeneState newState) {
		parent.getGuiGene(output.getSource()).setState(newState);
	}

	@Override
	public void resetState() {
		if (locked > 0) {
			setState(GUIGeneState.HOVER);
			setConnectionStates(GUIGeneState.HOVER);
		} else {
			setState(GUIGeneState.NEUTRAL);
			setConnectionStates(GUIGeneState.NEUTRAL);
		}
	}

	@Override
	protected void setLocked(boolean value) {
		locked += value ? 1 : -1;
		setConnectionStates(value ? GUIGeneState.HOVER : GUIGeneState.ACTIVE_HOVER);

		parent.getGuiGene(output.getSource()).setLocked(value);

	}

	@Override
	public void setChangingConnection(Connection newConnection) {
		output.setSource(newConnection);	
		updateText();
	}

	@Override
	public Connection getChangingConnection() {
		return output.getSource();
	}

	@Override
	public void addLocks(int value) {
		locked += value;
	}

	@Override
	public void removeLocks(int value) {
		locked -= value;
	}

	@Override
	public void setConnectionLine(GUIGene gene) {
		sourceLine.setEndX(gene.getLayoutX() + Constants.NODE_RADIUS);
		sourceLine.setEndY(gene.getLayoutY());	
	}
	
	public void unlock() {
		if (isLocked()) {
			setLocked(false);
			setState(GUIGeneState.NEUTRAL);
			setConnectionStates(GUIGeneState.NEUTRAL);
		}
	}

	public void lock() {
		if (!isLocked()) {
			setState(GUIGeneState.HOVER);
			setLocked(true);
		}
	}

	@Override
	public void updateText() {
		if (parent.isEvaluating()) {
			text.setText("O: " + output.getIndex() + "\n" + output.getSource().getValue().toString());
		} else {
			text.setText("O: " + output.getIndex());
		}
		
	}

	public void setOutput(Output newOutput) {
		output = newOutput;
	}

}
