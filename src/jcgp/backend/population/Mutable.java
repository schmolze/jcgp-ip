package jcgp.backend.population;

/**
 * {@code Mutable} declares the expected behaviour of any
 * part of a chromosome that is mutable, more specifically
 * nodes or outputs. Inputs are not mutable since they don't have
 * connections or functions.
 * <br><br>
 * This interface provides a way to deal with mutable elements
 * generically without having to specify whether they are nodes
 * or outputs. When mutating a mutable, {@code mutate()} is guaranteed
 * to perform a fair mutation.
 * 
 * @author Eduardo Pedroni
 *
 */
public interface Mutable {
	
	/**
	 * This method performs an arbitrary mutation on the {@code Mutable}.
	 * <br><br>
	 * In the case of nodes, this chooses to mutate a function or connection
	 * fairly, and carries out the required mutation by using the node's own
	 * reference to chromosome.
	 * <br><br>
	 * In the case of outputs, this simply picks a random connection to serve
	 * as the source - any connection is allowed.
	 */
	public void mutate();
	
	/**
	 * Asserts if the specified element is a copy of the elements
	 * this is called on.<br>
	 * This method returns true if and only if:
	 * <ul>
	 * <li>the elements being compared are not the same instance;</li>
	 * <li>the connections of the compared elements are not the same instance;</li>
	 * <li>the elements have the same function (in the case of Node);</li>
	 * <li>the grid position of the elements themselves are the same;</li>
	 * <li>the grid position of all equivalent connections are the same;</li>
	 * </ul>
	 * <br><br>
	 * The relationship computed by this method is:
	 * <ul>
	 * <li>symmetric: a.copyOf(b) == b.copyOf(a);</li>
	 * <li>not reflexive: a.copyOf(a) returns false;</li>
	 * <li>not transitive: if a.copyOf(b) is true and b.copyOf(c) is true, a.copyOf(c) is
	 *    not necessarily true since it is possible that a == c.</li>
	 * </ul>
	 * @param element the mutable element to compare to.
	 * @return true if {@code element} is a copy of this element.
	 */
	public boolean copyOf(Mutable element);
	
}
