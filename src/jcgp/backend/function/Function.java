package jcgp.backend.function;

/**
 * Function is a callback wrapper. 
 * <br><br>
 * A concrete implementation of Function overrides {@code run()} to perform
 * any arbitrary operation on the arguments specified. It must also override
 * {@code getArity()} to return the function arity.
 * 
 * @author Eduardo Pedroni
 */
public abstract class Function {
	
	/**
	 * Executes the function.
	 * 
	 * @param arguments the function arguments.
	 * @return the function result.
	 */
	public abstract Object run(Object... arguments);
	
	/**
	 * @return the arity of the function.
	 */
	public abstract int getArity();
}
