package jcgp.backend.population;

/**
 * This abstract class defines a generic CGP gene.
 * Three types of gene exist, primarily: {@code Input}, {@code Node} and {@code Output}.
 * <br><br>
 * In practice, this class facilitates support for a graphical user interface. An arbitrary
 * object can be associate with each gene using {@code setGUIObject(...)} and retrieved using
 * {@code getGUIObject()}. 
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class Gene {

	private Object guiObject;
	
	/**
	 * Sets a new GUI object.
	 * 
	 * @param guiObject the object to set.
	 */
	public void setGUIObject(Object guiObject) {
		this.guiObject = guiObject;
	}
	
	/**
	 * @return the current GUI object associated with this instance.
	 */
	public Object getGUIObject() {
		return guiObject;
	}
}
