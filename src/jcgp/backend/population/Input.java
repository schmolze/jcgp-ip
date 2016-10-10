package jcgp.backend.population;

/**
 * This is a chromosome input. Inputs are a special
 * type of connection which simply return a set value.
 * They do not have connections and instead provide a
 * starting point for the chromosome's active paths.
 * 
 * @author Eduardo Pedroni
 *
 */
public class Input extends Gene implements Connection {
	
	private Object value;
	private int index;
	
	/**
	 * Initialises a new input with the current index.
	 * 
	 * @param index the index of the new input.
	 */
	public Input(int index) {
		this.index = index;
	}
	
	/**
	 * Sets this input's value. The new value
	 * will now be returned by this input's
	 * {@code getValue()} method.
	 * 
	 * @param newValue the value to set.
	 */
	public void setValue(Object newValue) {
		value = newValue;
	}

	/**
	 * @return the input's index.
	 */
	public int getIndex() {
		return index;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Input " + index;
	}
}
