package jcgp.gui.population;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jcgp.backend.function.Function;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.backend.population.Node;
import jcgp.backend.resources.Resources;
import jcgp.gui.GUI;
import jcgp.gui.constants.Constants;

public class GUINode extends GUIGene {

	private Line[] lines;
	private Node node;
	private Resources resources;
	private int connectionIndex = 0;
	

	public GUINode(ChromosomePane parentRef, final Node node, Line[] connectionLines, final GUI gui) {
		super();
		
		// store references
		this.parent = parentRef;
		this.node = node;
		this.lines = connectionLines;
		this.resources = gui.getExperiment().getResources();
		
		// move the GUIGene to the right position
		relocate(((node.getColumn() + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS,
				(node.getRow() * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);

		// set the line ends correctly
		updateLines();

		final Label connectionNumber = new Label();
		connectionNumber.setStyle("-fx-background-color:rgb(255, 255, 255); -fx-border-color:rgba(0, 0, 0, 0.5); ");
		connectionNumber.setVisible(false);

		Circle output = new Circle(Constants.NODE_RADIUS, 0, Constants.SOCKET_RADIUS, Paint.valueOf("white"));
		output.setStroke(Paint.valueOf("black"));

		updateText();

		Circle[] sockets = new Circle[resources.arity()];
		double angle, xPos, yPos;
		for (int l = 0; l < sockets.length; l++) {
			angle = (((l + 1) / ((double) (resources.arity() + 1))) * Constants.THETA) - (Constants.THETA / 2);
			xPos = -Math.cos(angle) * Constants.NODE_RADIUS;
			yPos = Math.sin(angle) * Constants.NODE_RADIUS;

			sockets[l] = new Circle(xPos, yPos, Constants.SOCKET_RADIUS, Paint.valueOf("white"));
			sockets[l].setId(String.valueOf(l));
			sockets[l].setStroke(Paint.valueOf("black"));

			final Circle s = sockets[l];
			final int index = l;

			/*
			 * Mouse event handlers on sockets
			 * 
			 */
			s.addEventFilter(MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// the mouse has been dragged out of the socket, this means a full drag is in progress
					startFullDrag();
				}
			});

			s.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// user is hovering over connection socket
					connectionNumber.setText("C: " + s.getId());
					connectionNumber.relocate(s.getCenterX() + 5, s.getCenterY() - 10);
					connectionNumber.setVisible(true);
				}
			});

			s.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// user exits the connection socket
					connectionNumber.setVisible(false);
				}
			});

			s.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// mouse was pressed on the socket
					setState(GUIGeneState.SOURCE);
					connectionIndex = index;
				}
			});

			s.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (!parent.isTarget()) {
						lines[connectionIndex].setEndX(event.getX() + ((Circle) event.getSource()).getParent().getLayoutX());
						lines[connectionIndex].setEndY(event.getY() + ((Circle) event.getSource()).getParent().getLayoutY());
					}
				}
			});	

			s.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {					
					if (event.isStillSincePress()) {
						// mouse was released before dragging out of the socket
						updateLine(index);
						setState(GUIGeneState.HOVER);
					} else if (getState() == GUIGeneState.SOURCE) {
						// no connection has been made, fallback
						resetState();
						updateLines();
					}
				}
			});
		}

		/*
		 * Mouse event handlers on whole gene
		 */
		addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				gui.bringFunctionSelector(event, (GUINode) event.getSource());
			}
		});
		
		addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				// the drag has entered this node, react appropriately
				// this happens even if we are the source of the drag
				if (isAllowed((GUIGene) event.getGestureSource(), (GUIGene) event.getSource())) {
					((GUIGene) event.getGestureSource()).setConnectionLine((GUIGene) event.getSource());
					
					Connection source = ((GUIGene) event.getGestureSource()).getChangingConnection();
					if (node == source) {
						setState(GUIGeneState.NO_CHANGE_TARGET);
					} else {
						setState(GUIGeneState.VALID_TARGET);
					}
				} else {
					setState(GUIGeneState.INVALID_TARGET);
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
					if (event.getGestureSource() == event.getSource()) {
						setState(GUIGeneState.SOURCE);
					} else {
						if (getState() == GUIGeneState.NO_CHANGE_TARGET) {
							setState(GUIGeneState.INDIRECT_HOVER);
						} else {
							setState(GUIGeneState.NEUTRAL);
							((GUIGene) event.getGestureSource()).setConnectionStates(GUIGeneState.INDIRECT_HOVER);
						}
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
				if (isAllowed((GUIGene) event.getGestureSource(), (GUIGene) event.getSource())) {
					if (source.isLocked()) {
						// remove locks from the old connection, add the to setConnethe new
						// note that the old connection may still have locks after this
						parent.getGuiGene(source.getChangingConnection()).removeLocks(source.getLocks());
						addLocks(source.getLocks());
					} else {
						if (source instanceof GUIOutput) {
							source.resetState();
						}
					}
					source.setChangingConnection(node);

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
				} else if (locked > 0) {
					setConnectionStates(GUIGeneState.LOCKED_HOVER);
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// cursor has left this node without dragging, or it is dragging and this is the source
				if (getState() == GUIGeneState.HOVER && locked <= 0) {
					setState(GUIGeneState.NEUTRAL);
					setConnectionStates(GUIGeneState.NEUTRAL);
				} else if (locked > 0) {
					if (getState() == GUIGeneState.SOURCE || getState() == GUIGeneState.INVALID_TARGET) {
						setConnectionStates(GUIGeneState.INDIRECT_HOVER);
					} else {
						setConnectionStates(GUIGeneState.HOVER);
					}
					
				}
			}
		});

		getChildren().addAll(mainCircle, text);
		getChildren().addAll(sockets);
		getChildren().addAll(output, connectionNumber);

	}

	@Override
	public void setState(GUIGeneState newState) {
		switch (newState) {
		case ACTIVE_HOVER:
			if (locked > 0) {
				setState(GUIGeneState.LOCKED_HOVER);
			} else {
				mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
				showLines(true);
			}
			setConnectionStates(GUIGeneState.ACTIVE_HOVER);
			break;
		case LOCKED_HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			break;
		case INVALID_TARGET:
			mainCircle.setFill(Paint.valueOf(Constants.BAD_SELECTION_COLOUR));
			break;
		case HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.MEDIUM_HIGHLIGHT_COLOUR));
			showLines(true);
			if (locked <= 0) {
				setConnectionStates(GUIGeneState.INDIRECT_HOVER);
			} else {
				setConnectionStates(GUIGeneState.HOVER);
			}
			break;
		case INDIRECT_HOVER:
			mainCircle.setFill(Paint.valueOf(Constants.SOFT_HIGHLIGHT_COLOUR));
			break;
		case NEUTRAL:
			if (locked > 0) {
				setState(GUIGeneState.HOVER);
			} else {
				mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_COLOUR));
				showLines(false);
				if (getState() == GUIGeneState.ACTIVE_HOVER) {
					setConnectionStates(GUIGeneState.NEUTRAL);
				}
			}
			break;
		case NO_CHANGE_TARGET:
			parent.setTarget(true);
			mainCircle.setFill(Paint.valueOf(Constants.NEUTRAL_SELECTION_COLOUR));
			break;
		case SOURCE:
			mainCircle.setFill(Paint.valueOf(Constants.HARD_HIGHLIGHT_COLOUR));
			break;
		case VALID_TARGET:
			parent.setTarget(true);
			mainCircle.setFill(Paint.valueOf(Constants.GOOD_SELECTION_COLOUR));
			break;
		default:
			break;
		}

		super.setState(newState);
	}
	
	@Override
	public Connection getChangingConnection() {
		return node.getConnection(connectionIndex);
	}

	private boolean isAllowed(GUIGene source, GUIGene target) {
		if (source instanceof GUINode) {
			// if the source is a node, all inputs and some nodes are valid
			if (target instanceof GUIInput) {
				return true;
			} else if (target instanceof GUINode) {
				// target and source are nodes, let's look at levels back
				Node t = ((GUINode) target).getNode(), s = ((GUINode) source).getNode();
				if (s.getColumn() - t.getColumn() > 0 && s.getColumn() - t.getColumn() <= resources.levelsBack()) {
					return true;
				}
				return false;
			} else if (target instanceof GUIOutput) {
				return false;
			} else {
				throw new ClassCastException("Target was neither GUINode nor GUIInput nor GUIOutput.");
			}
		} else if (source instanceof GUIOutput) {
			// if the source is an output, any node or input is valid
			if (target instanceof GUINode || target instanceof GUIInput) {
				return true;
			} else if (target instanceof GUIOutput) {
				return false;
			} else {
				throw new ClassCastException("Target was neither GUINode nor GUIInput nor GUIOutput.");
			}
		}
		// if the source was neither node nor output, something bad is happening
		throw new ClassCastException("Source was neither GUINode nor GUIOutput.");
	}


	public Node getNode() {
		return node;
	}

	/**
	 * Place the end of the specified line on the output of the associated connection. 
	 * 
	 * @param index the line to be updated.
	 */
	public void updateLine(int index) {
		if (node.getConnection(index) instanceof Node) {
			int row = ((Node) node.getConnection(index)).getRow(), 
					column = ((Node) node.getConnection(index)).getColumn();
			lines[index].setEndX(((column + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + 2 * Constants.NODE_RADIUS);
			lines[index].setEndY((row * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);
		} else if (node.getConnection(index) instanceof Input) {
			int inputIndex = ((Input) node.getConnection(index)).getIndex();
			lines[index].setEndX(2 * Constants.NODE_RADIUS);
			lines[index].setEndY(inputIndex * (2 * Constants.NODE_RADIUS + Constants.SPACING) + Constants.NODE_RADIUS);
		}
	}

	/**
	 * Updates the end of all lines to match the associated connections. 
	 */
	@Override
	public void updateLines() {
		for (int c = 0; c < lines.length; c++) {
			updateLine(c);
		}
	}

	/**
	 * Toggle visibility of all connection lines.
	 * 
	 * @param value whether to show the lines or not.
	 */
	private void showLines(boolean value) {
		for (int i = 0; i < lines.length; i++) {
			lines[i].setVisible(value);
		}
	}

	@Override
	public void setConnectionStates(GUIGeneState newState) {
		for (int i = 0; i < lines.length; i++) {
			parent.getGuiGene(node.getConnection(i)).setState(newState);
		}
	}

	@Override
	public void setChangingConnection(Connection newConnection) {
		node.setConnection(connectionIndex, newConnection);
		if (parent.isEvaluating()) {
			parent.updateValues();
		}
	}


	@Override
	public void resetState() {
		if (locked > 0) {
			setState(GUIGeneState.HOVER);
		} else {
			setState(GUIGeneState.NEUTRAL);
			setConnectionStates(GUIGeneState.NEUTRAL);
		}

	}

	@Override
	protected void setLocked(boolean value) {
		locked += value ? 1 : -1;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.ACTIVE_HOVER);

		for (int i = 0; i < lines.length; i++) {
			parent.getGuiGene(node.getConnection(i)).setLocked(value);
		}
	}

	@Override
	public void addLocks(int value) {
		locked += value;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.ACTIVE_HOVER);

		for (int i = 0; i < lines.length; i++) {
			parent.getGuiGene(node.getConnection(i)).addLocks(value);
		}
	}

	@Override
	public void removeLocks(int value) {
		locked -= value;
		setState(locked > 0 ? GUIGeneState.HOVER : GUIGeneState.NEUTRAL);

		for (int i = 0; i < lines.length; i++) {
			parent.getGuiGene(node.getConnection(i)).removeLocks(value);
		}
	}

	@Override
	public void setConnectionLine(GUIGene gene) {
		lines[connectionIndex].setEndX(gene.getLayoutX() + Constants.NODE_RADIUS);
		lines[connectionIndex].setEndY(gene.getLayoutY());
	}

	public void updateText() {
		if (parent.isEvaluating()) {
			text.setText(node.getFunction() + "\n" + node.getValue().toString());
		} else {
			text.setText(node.getFunction().toString());
		}
	}
	
	public void setFunction(Function function) {
		node.setFunction(function);
		if (parent.isEvaluating()) {
			parent.updateValues();
		} else {
			updateText();
		}
	}

	public void setNode(Node newNode) {
		node = newNode;
	}
}
