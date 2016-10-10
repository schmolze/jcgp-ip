package jcgp.gui.population;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.backend.population.Node;
import jcgp.backend.resources.Resources;
import jcgp.gui.GUI;
import jcgp.gui.constants.Constants;

/**
 * This extension of {@code ScrollPane} contains a series of
 * nodes, inputs and outputs spread across a grid. It also contains
 * all of the connection lines overlaid over the nodes, inputs and outputs.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public class ChromosomePane extends ScrollPane {

	private GUINode[][] guiNodes;
	private GUIInput[] guiInputs;
	private GUIOutput[] guiOutputs;
	
	private Pane content;
	
	private ArrayList<Line> connectionLines;
	private ArrayList<GUIOutput> relock = new ArrayList<GUIOutput>();
	
	private int rows, columns;
	
	private Object[] testInputs;
	
	private boolean target = false;
	private PopulationPane parent;
	
	public ChromosomePane(Chromosome chromosome, GUI gui, PopulationPane parent) {
		super();
		
		final Resources resources = gui.getExperiment().getResources();
		this.parent = parent;
		
		rows = resources.rows();
		columns = resources.columns();
		
		connectionLines = new ArrayList<Line>();
		
		content = new Pane();
		content.setId("content pane for genes");
		
		// generate the GUIGenes
		// inputs
		guiInputs = new GUIInput[resources.inputs()];
		for (int i = 0; i < guiInputs.length; i++) {
			// make the GUI elements
			guiInputs[i] = new GUIInput(this, chromosome.getInput(i));
			content.getChildren().addAll(guiInputs[i]);
		}
		// nodes
		guiNodes = new GUINode[rows][columns];
		double angle, xPos, yPos;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				// make the connection lines
				Line lines[] = new Line[resources.arity()];
				for (int l = 0; l < lines.length; l++) {
					angle = ((((double) (l + 1)) / ((double) (lines.length + 1))) * Constants.THETA) - (Constants.THETA / 2);
					xPos = (-Math.cos(angle) * Constants.NODE_RADIUS) + (((c + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);
					yPos = (Math.sin(angle) * Constants.NODE_RADIUS) + ((r * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS);
					
					lines[l] = new Line(xPos, yPos, xPos, yPos);
					lines[l].setMouseTransparent(true);
					lines[l].setVisible(false);
					connectionLines.add(lines[l]);
				}
				// make the GUI elements
				guiNodes[r][c] = new GUINode(this, chromosome.getNode(r, c), lines, gui);
			}
			content.getChildren().addAll(guiNodes[r]);
		}
		// outputs
		guiOutputs = new GUIOutput[resources.outputs()];
		for (int i = 0; i < guiOutputs.length; i++) {
			xPos = ((resources.columns() + 1) * (2 * Constants.NODE_RADIUS + Constants.SPACING));
			yPos = (chromosome.getOutput(i).getIndex() * (2 * Constants.NODE_RADIUS + Constants.SPACING)) + Constants.NODE_RADIUS;
			// make the line
			Line line = new Line(xPos, yPos, xPos, yPos);
			line.setMouseTransparent(true);
			line.setVisible(false);
			connectionLines.add(line);
			// make the GUI elements
			guiOutputs[i] = new GUIOutput(this, chromosome.getOutput(i), line, gui);
			content.getChildren().addAll(guiOutputs[i]);
		}

		content.getChildren().addAll(connectionLines);
		setPrefWidth(620);	
		setContent(content);
	}
	
	protected GUIGene getGuiGene(Connection gene) {
		if (gene instanceof Input) {
			return guiInputs[((Input) gene).getIndex()];
		} else if (gene instanceof Node) {
			return guiNodes[((Node) gene).getRow()][((Node) gene).getColumn()];
		} else {
			// something bad happened!
			throw new ClassCastException();
		}	
	}
	
	protected boolean isTarget() {
		return target;
	}
	
	protected void setTarget(boolean newValue) {
		target = newValue;
	}
	
	public void updateGenes(Chromosome chr) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				guiNodes[r][c].setNode(chr.getNode(r, c));
				guiNodes[r][c].updateLines();
				guiNodes[r][c].updateText();
			}
		}
		for (int i = 0; i < guiOutputs.length; i++) {
			guiOutputs[i].setOutput(chr.getOutput(i));
			guiOutputs[i].updateLines();
		}
		if (isEvaluating()) {
			setInputs(testInputs);
		}
	}
	
	public void unlockOutputs() {
		relock.clear();
		for (int i = 0; i < guiOutputs.length; i++) {
			if (guiOutputs[i].isLocked()) {
				guiOutputs[i].unlock();
				relock.add(guiOutputs[i]);
			}	
		}
	}
	
	public void relockOutputs() {
		for (int i = 0; i < relock.size(); i++) {
			relock.get(i).lock();
		}
	}
	
	public void setInputs(Object[] values) {
		testInputs = values;
		for (int i = 0; i < guiInputs.length; i++) {
			guiInputs[i].setValue(values[i]);
		}
		updateValues();
	}

	public void updateValues() {
		for (int i = 0; i < guiInputs.length; i++) {
			guiInputs[i].updateText();
		}
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				guiNodes[r][c].updateText();
			}
		}
		for (int o = 0; o < guiOutputs.length; o++) {
			guiOutputs[o].updateText();
		}
	}

	/**
	 * @return the evaluating attribute.
	 */
	public boolean isEvaluating() {
		return parent.isEvaluating();
	}
}
