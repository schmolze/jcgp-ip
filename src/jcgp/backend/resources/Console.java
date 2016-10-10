package jcgp.backend.resources;

/**
 * Defines the basic model for a console.
 * <br><br>
 * This interface will typically be implemented by a GUI class
 * and GUI packages such as JavaFX are usually single-threaded.
 * If the CGP experiment is running on a side thread (which would
 * be the case so as not to block the entire GUI), updating a GUI
 * element such as the console from a different thread would lead
 * to concurrency problems. For this reason, this console is 
 * intended to buffer printed messages and only output them to the
 * actual GUI control when {@code flush()} is called (which is
 * guaranteed to be done in a thread-safe way by the library).
 * 
 * @author Eduardo Pedroni
 *
 */
public interface Console {
	
	/**
	 * Prints a string and automatically adds a line break at the end.
	 * 
	 * @param s the string to print.
	 */
	public void println(String s);
	
	/**
	 * Prints a string without line break at the end (unless the string 
	 * itself specifies one).
	 * 
	 * @param s the string to print.
	 */
	public void print(String s);
	
	/**
	 * Outputs all buffered messages to the console. Only necessary
	 * if concurrent accesses must be avoided.
	 */
	public void flush();

}
