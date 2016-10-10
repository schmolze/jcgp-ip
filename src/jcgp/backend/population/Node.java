package jcgp.backend.population;

import java.util.ArrayList;

import jcgp.backend.function.Function;
import jcgp.backend.resources.Resources;

/**
 * Nodes make up the main part of the chromosome, 
 * where the actual functions are evolved. Each node
 * contains a function and a number of connections.
 * The node outputs the result of performing its function
 * on the values of its connections. Nodes therefore
 * implement both {@code Mutable} and {@code Connection}
 * since they can be mutated but also connected to. 
 * Nodes are constructed with a fixed number of connections
 * (determined by the maximum arity of the function set)
 * and must be reinstantiated if the experiment arity
 * changes.
 * <br><br>
 * When mutating a node, it is easiest to use {@code mutate()}.
 * Alternatively, you may also perform a specific mutation using
 * {@code setConnection(...)} and {@code setFunction(...)}.
 * 
 * @author Eduardo Pedroni
 *
 */
public class Node extends Gene implements Mutable, Connection {

	private Function function;
	private Connection[] connections;
	private int column, row;
	private Chromosome chromosome;

	/**
	 * Constructs a new instance of {@code Node} with the
	 * specified parameters. Nodes must contain their
	 * own row and column for ease of copying.
	 * 
	 * @param chromosome the chromosome this node belongs to.
	 * @param row the node's row.
	 * @param column the node's column.
	 */
	public Node(Chromosome chromosome, int row, int column) {
		this.chromosome = chromosome;
		this.column = column;
		this.row = row;
	}
	
	/**
	 * Initialises the node with the specified values.
	 * The number of connections passed as argument must
	 * be exactly the same as the experiment arity, or 
	 * an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param newFunction the node function to set.
	 * @param newConnections the node connections to set.
	 */
	public void initialise(Function newFunction, Connection... newConnections) {
		function = newFunction;
		if (newConnections.length == chromosome.getResources().arity()) {
			connections = newConnections;
		} else {
			throw new IllegalArgumentException("Received " + newConnections.length + " connections but needed exactly " + chromosome.getResources().arity());
		}
	}

	/**
	 * @return this node's column.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return this node's row.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return this node's function.
	 */
	public Function getFunction() {
		return function;
	}
	
	/**
	 * Sets the node function.
	 * 
	 * @param newFunction the new function to set.
	 */
	public void setFunction(Function newFunction) {
		function = newFunction;
	}
	
	/**
	 * @param index the connection to return.
	 * @return the indexed connection.
	 */
	public Connection getConnection(int index) {
		return connections[index];
	}
	
	/**
	 * This method sets the indexed connection to the specified new connection.
	 * If the given connection is null or disrespects levels back, it is discarded
	 * and no connections are changed.
	 * 
	 * @param index the connection index to set.
	 * @param newConnection the {@code Connection} to connect to.
	 */
	public void setConnection(int index, Connection newConnection) {
		// connection must not be null
		if (newConnection != null) {
			connections[index] = newConnection;
			chromosome.recomputeActiveNodes();
		}
	}

	/**
	 * For package use, this is a recursive method
	 * used to create a collection of active nodes
	 * in the chromosome. If this node is not already
	 * in the active node list, this method adds it.
	 * It then calls {@code getActive()} on each of its
	 * connections, therefore recursively adding every
	 * single active node to the given list.
	 * 
	 * @param activeNodes the list of active nodes being built.
	 */
	protected void getActive(ArrayList<Node> activeNodes) {
		// don't add the same node twice
		if (!activeNodes.contains(this)) {
			activeNodes.add(this);
		}
		// call getActive on all connections - they are all active
		for (int i = 0; i < function.getArity(); i++) {
			if (connections[i] instanceof Node) {
				((Node) connections[i]).getActive(activeNodes);
			}
		}
	}

	@Override
	public boolean copyOf(Mutable element) {
		// both cannot be the same instance
		if (this != element) {
			// element must be instance of node
			if (element instanceof Node) {
				Node n = (Node) element;
				// must have the same function
				if (function == n.getFunction()) {
					// row and column must be the same
					if (column == n.getColumn() && row == n.getRow()) {
						// connections must be the equivalent, but not the same instance
						for (int i = 0; i < connections.length; i++) {
							if (connections[i] != n.getConnection(i)) {
								if (connections[i] instanceof Input && n.getConnection(i) instanceof Input) {
									if (((Input) connections[i]).getIndex() != ((Input) n.getConnection(i)).getIndex()) {
										return false;
									}
								} else if (connections[i] instanceof Node && n.getConnection(i) instanceof Node) {
									if (((Node) connections[i]).getRow() != ((Node) n.getConnection(i)).getRow() &&
											((Node) connections[i]).getColumn() != ((Node) n.getConnection(i)).getColumn()) {
										return false;
									}
								} else {
									return false;
								}
							} else {
								return false;
							}
						}
						// all connections checked, this really is a copy
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public Object getValue() {
		// build list of arguments recursively
		Object[] args = new Object[function.getArity()];
		for (int i = 0; i < function.getArity(); i++) {
			args[i] = connections[i].getValue();
		}
		// return function result
		return function.run(args);
	}

	@Override
	public void mutate() {
		Resources resources = chromosome.getResources();
		
		// choose to mutate the function or a connection
		int geneType = resources.getRandomInt(1 + resources.arity());
		
		// if the int is less than 1, mutate function, else mutate connections 
		if (geneType < 1) {			
			setFunction(resources.getRandomFunction());
		} else {
			// if we decided to mutate connection, subtract 1 from geneType so it fits into the arity range
			geneType--;
			setConnection(geneType, chromosome.getRandomConnection(column));
		}
	}

	@Override
	public String toString() {
		return "Node [" + row + ", " + column + "]";
	}
}
