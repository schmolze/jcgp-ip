package jcgp.gui.population;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jcgp.backend.population.Connection;
import jcgp.gui.constants.Constants;

public abstract class GUIGene extends Group {

	public enum GUIGeneState {
		NEUTRAL,
		HOVER,
		INDIRECT_HOVER,
		ACTIVE_HOVER,
		LOCKED_HOVER,
		SOURCE,
		VALID_TARGET,
		NO_CHANGE_TARGET,
		INVALID_TARGET
	}
	
	protected Text text = new Text();
	protected Circle mainCircle = new Circle(Constants.NODE_RADIUS, Paint.valueOf("white"));
	
	private GUIGeneState state = GUIGeneState.NEUTRAL;

	protected ChromosomePane parent;
	
	protected int locked = 0;

	public GUIGene() {
		text.setFont(Font.font("Arial", 12));
		text.setTextOrigin(VPos.CENTER);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setWrappingWidth(Constants.NODE_RADIUS * 2);
		text.setX(-Constants.NODE_RADIUS);
		text.setVisible(true);
		
		mainCircle.setStroke(Paint.valueOf("black"));
	}
	
	public void setState(GUIGeneState newState) {
		state = newState;
	}
	
	public GUIGeneState getState() {
		return state;
	}

	public boolean isLocked() {
		return locked > 0;
	}
	
	public int getLocks() {
		return locked;
	}
	
	protected abstract void setLocked(boolean value);
	
	public abstract void addLocks(int value);
	
	public abstract void removeLocks(int value);
	
	public abstract void updateLines();
	
	public abstract void setChangingConnection(Connection newConnection);
	
	public abstract Connection getChangingConnection();

	public abstract void setConnectionStates(GUIGeneState newState);

	public abstract void resetState();
	
	public abstract void setConnectionLine(GUIGene gene);
	
	public abstract void updateText();
}
