package jcgp.backend.population;

import java.util.ArrayList;

/**
 * This is a chromosome output. Outputs are a special
 * type of mutable element with a single connection. It
 * returns the value of its single connection, but it 
 * may not be connected to - it terminates a chromosome
 * active connection path. 
 * <br><br>
 * When mutating an output, it is easiest to use {@code mutate()}.
 * Alternatively, you may also perform a specific mutation using
 * {@code setSource(...)}.
 * 
 * @author Eduardo Pedroni
 *
 */
public class Output extends Gene implements Mutable {
	
	private Connection source;
	private Chromosome chromosome;
	private int index;
	
	/**
	 * Makes a new instance of {@code Output} with the 
	 * specified arguments.
	 * 
	 * @param chromosome the chromosome this output belongs to.
	 * @param index the output index.
	 */
	public Output(Chromosome chromosome, int index) {
		this.chromosome = chromosome;
		this.index = index;
	}

	/**
	 * @return the value of the output's source.
	 */
	public Object calculate() {
		return source.getValue();
	}

	/**
	 * @return this output's index.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * This method sets the output source to the specified connection.
	 * 
	 * @param newConnection the {@code Connection} to connect to.
	 */
	public void setSource(Connection newConnection) {
		source = newConnection;
		// trigger active path recomputation
		chromosome.recomputeActiveNodes();
	}

	/**
	 * @return the source of this output's value.
	 */
	public Connection getSource() {
		return source;
	}

	/**
	 * Calls {@code getActive(...)} on this output's
	 * source. This kicks off a recursive process whereby
	 * all nodes connected to this output are added to the
	 * specified list of nodes. This is used to create a
	 * list of all active nodes.
	 * 
	 * @param activeNodes the list to add all active nodes to.
	 */
	public void getActiveNodes(ArrayList<Node> activeNodes) {
		// do not add if the source is an input
		if (source instanceof Node) {
			((Node) source).getActive(activeNodes);
		}
	}
	
	@Override
	public boolean copyOf(Mutable m) {
		// both cannot be the same instance
		if (this != m) {
			// element must be instance of output
			if (m instanceof Output) {
				Output o = (Output) m;
				// index must be the same
				if (index == o.getIndex()) {
					// source must be the same
					if (source != o.getSource()) {
						if (source instanceof Input && o.getSource() instanceof Input) {
							if (((Input) source).getIndex() == ((Input) o.getSource()).getIndex()) {
								return true;
							}
						} else if (source instanceof Node && o.getSource() instanceof Node) {
							if (((Node) source).getRow() == ((Node) o.getSource()).getRow() &&
									((Node) source).getColumn() == ((Node) o.getSource()).getColumn()) {
								return true;
							}
						} 
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void mutate() {
		// simply change output to a new, random connection
		setSource(chromosome.getRandomConnection());
	}
	
	@Override
	public String toString() {
		return "Output " + index;
	}
}
