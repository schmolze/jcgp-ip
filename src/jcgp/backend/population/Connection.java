package jcgp.backend.population;

/**
 * {@code Connection} declares the expected behaviour of any
 * part of a chromosome that can be connected to, specifically
 * nodes or inputs. Outputs are not connections since they 
 * mark the end of chromosome paths.
 * <br><br>
 * This interface provides a way to deal with connections
 * generically without having to specify whether they are nodes
 * or inputs. In this way a random connection can be picked and
 * dealt with more easily, facilitating mutations.
 * 
 * @author Eduardo Pedroni
 *
 */
public interface Connection {

	/**
	 * Compute and return the value of this connection. In
	 * the case of inputs no computation is necessary, this
	 * simply returns the value the input is set to. In the
	 * case of nodes, the value is computed based on the
	 * node's function and the value of its own connections.
	 * 
	 * @return the connection's value.
	 */
	public Object getValue();

}
