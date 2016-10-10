package jcgp.backend.population;

import java.util.ArrayList;

import jcgp.backend.resources.Resources;

/**
 * This class encapsulates a CGP chromosome.
 * <br><br>
 * A chromosome contains a matrix of nodes and arrays of inputs and outputs.
 * These elements are all interconnected, and actually form the chromosome
 * network itself. Individual nodes can be retrieved using {@code getNode()}
 * which requires the row and column to be specified. The same works for
 * inputs and outputs using the associated getters, in which case only the
 * index is necessary.
 * <br><br>
 * In evolutionary computation it is often necessary to make copies of 
 * chromosomes; this can be accomplished in JCGP in two ways. The recommended
 * way to do this is using {@code copyChromosome()} in {@code Population}, but alternatively
 * it can be done by using the {@code Chromosome} copy constructor and specifying the
 * object to copy from, or by using the {@code copyGenes()} method. 
 * <br><br>
 * To illustrate this, given two chromosomes, chr1 and chr2, the following code:
 * <br><br>
 * <code>
 * chr1.copyGenes(chr2);
 * </code><br><br>
 * will modify all of chr1's connections and functions to match those of chr2, without
 * creating a new instance. In contrast, 
 * <br><br>
 * <code>
 * chr1 = new Chromosome(chr2);
 * </code><br><br>
 * creates a new instance of chromosome which is identical to chr2 and assigns it to chr1,
 * meaning any old references to chr1 that are not updated will still refer to a chromosome
 * that is not identical to chr2. In practice, the most reliable way is to use the copy method
 * in {@code Population}. Assuming chr1 and chr2 are indexed 1 and 2 in {@code population} respectively,
 * <br><br>
 * population.copyChromosome(2, 1);
 * <br><br>
 * will copy chr2 into chr1 without creating new instances or requiring access to the underlying
 * chromosome array. {@code Chromosome} offers a variety of methods to compare chromosomes as well, 
 * such as {@code compareGenesTo()} and {@code compareActiveGenesTo()}. {@code Comparable} is implemented
 * to compare fitness value, meaning {@code compareTo()} returns a value depending the relative fitness
 * of the compared chromosomes.
 * <br><br>
 * In order to set the chromosome's input values for decoding, {@code setInputs()} should be used. A few 
 * utility methods are provided in order to retrieve random elements from the chromosome, which are used
 * internally to initialise with random connections but also externally by mutators when performing 
 * mutations.
 * 
 * @author Eduardo Pedroni
 *
 */
public class Chromosome implements Comparable<Chromosome> {
	
	private Resources resources;

	private Input[] inputs;
	private Node[][] nodes;
	private Output[] outputs;

	private ArrayList<Node> activeNodes;

	private double fitness = 0;
	private boolean recomputeActiveNodes = true;

	/**
	 * Initialise a chromosome with the specified parameters. Random valid connections
	 * are created upon initialisation.
	 *
	 * @param resources the experiment's resources.
	 */
	public Chromosome(Resources resources) {
		// store a reference to the parameters
		this.resources = resources;
		// allocate memory for all elements of the chromosome
		instantiateElements();
		// set random connections so that the chromosome can be evaluated
		reinitialiseConnections();
	}

	/**
	 * Copy constructor.
	 * 
	 * Initialise a new chromosome with the exact same connections as a given instance of Chromosome.
	 * 
	 * @param clone the chromosome to be copied.
	 */
	public Chromosome(Chromosome clone) {
		// store a reference to the parameters
		this.resources = clone.getResources();
		// allocate memory for all elements of the chromosome
		instantiateElements();
		// initialise all connections based on argument
		copyGenes(clone);
	}

	/**
	 * Allocates the necessary memory for all of the nodes, inputs
	 * and outputs in the chromosome according to the experiment
	 * resources. 
	 * <br>
	 * Note that this does not actually initialise the genes, as it
	 * is not possible to do so until they have all been initialised;
	 * to initialise the genes, {@code reinitialiseConnections()} should
	 * be used.
	 */
	private void instantiateElements() {
		inputs = new Input[(resources.inputs())];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = new Input(i);
		}

		// rows first
		nodes = new Node[(resources.rows())][(resources.columns())];
		for (int r = 0; r < nodes.length; r++) {
			for (int c = 0; c < nodes[r].length; c++) {
				nodes[r][c] = new Node(this, r, c);
			}
		}
		outputs = new Output[resources.outputs()];
		for (int o = 0; o < outputs.length; o++) {
			outputs[o] = new Output(this, o);
		}
	}

	/**
	 * Sets random connections and functions across the entire
	 * chromosome. This method can be used more than once for
	 * each instance, if entirely random chromosomes are desired.
	 */
	public void reinitialiseConnections() {

		int arity = resources.arity();
		
		// initialise nodes - [rows][columns]
		for (int r = 0; r < nodes.length; r++) {
			for (int c = 0; c < nodes[r].length; c++) {
				Connection[] connections = new Connection[arity];
				for (int i = 0; i < connections.length; i++) {
					connections[i] = getRandomConnection(c);
				}
				nodes[r][c].initialise(resources.getRandomFunction(), connections);
			}
		}

		// set random outputs
		for (Output output : outputs) {
			output.setSource(getRandomConnection());
		}

	}

	/**
	 * Creates a deep copy of the specified chromosome in the
	 * this instance. In practice, this iterates through the
	 * entire chromosome making equivalent connections and
	 * setting functions to the same values as those in the
	 * specified chromosome. It also sets the fitness of the
	 * copy to the same value as the original.
	 * <br>
	 * It is assumed that both chromosomes have the same 
	 * topology; while this method will still run if that is not
	 * the case, the effects might be undesirable and null pointer
	 * access might occur.
	 * 
	 * @param clone the chromosome to clone.
	 */
	public void copyGenes(Chromosome clone) {
		int arity = resources.arity();
		// copy nodes - [rows][columns]
		for (int r = 0; r < nodes.length; r++) {
			for (int c = 0; c < nodes[r].length; c++) {
				// make array of connections to initialise with
				Connection[] connections = new Connection[arity];
				// populate with connections equivalent to clone
				Connection copyConnection;
				for (int i = 0; i < connections.length; i++) {
					copyConnection = clone.getNode(r, c).getConnection(i);
					if (copyConnection instanceof Input) {
						connections[i] = inputs[((Input) copyConnection).getIndex()];
					} else if (copyConnection instanceof Node) {
						connections[i] = nodes[((Node) copyConnection).getRow()][((Node) copyConnection).getColumn()];
					} else {
						System.out.println("Error: Connection of subtype " + copyConnection.getClass().toString() + " is not explicitly handled by copy method.");
					}
				}
				// initialise with copied arguments
				nodes[r][c].initialise(clone.getNode(r, c).getFunction(), connections);
			}
		}

		// do the same to outputs
		Connection copyOutput;
		for (int o = 0; o < outputs.length; o++) {
			copyOutput = clone.getOutput(o).getSource();
			if (copyOutput instanceof Input) {
				outputs[o].setSource(inputs[((Input) copyOutput).getIndex()]);
			} else if (copyOutput instanceof Node) {
				outputs[o].setSource(nodes[((Node) copyOutput).getRow()][((Node) copyOutput).getColumn()]);
			} else {
				// something bad happened
				System.out.println("Warning: Connection of subtype " + copyOutput.getClass().toString() + " is not explicitly handled by copy constructor.");
			}
		}
		
		// copy fitness as well
		this.fitness = clone.getFitness();
	}

	/**
	 * Returns a reference to the indexed input.
	 * 
	 * @param index the input index.
	 * @return the input reference.
	 */
	public Input getInput(int index) {
		return inputs[index];
	}
	
	/**
	 * Returns a reference to any node, addressed by row and column.
	 * 
	 * @param row the row of the node.
	 * @param column the column of the node.
	 * @return the addressed node.
	 */
	public Node getNode(int row, int column) {
		return nodes[row][column];
	}

	/**
	 * Returns a reference to the indexed output.
	 * 
	 * @param index the output index.
	 * @return the output reference.
	 */
	public Output getOutput(int index) {
		return outputs[index];
	}

	/**
	 * @return the fitness of the chromosome.
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Sets the fitness of the chromosome. This method
	 * should be used by the experiment problem when the
	 * population is evaluated in order to assign a fitness
	 * to each individual.
	 * 
	 * @param newFitness the fitness to assign.
	 */
	public void setFitness(double newFitness) {
		fitness = newFitness;
	}

	/**
	 * Loops through the inputs and sets the specified values,
	 * so that evaluations can be performed. If the number of
	 * elements in the array of values does not match the
	 * number of inputs exactly, an exception is thrown.
	 * 
	 * @param values the values the input should take.
	 */
	public void setInputs(Object ... values) {
		// if the values provided don't match the specified number of inputs, the user should be warned
		if (values.length == inputs.length) {
			// set inputs for evaluation
			for (int i = 0; i < values.length; i++) {
				inputs[i].setValue(values[i]);
			}
		} else {
			throw new IllegalArgumentException("Received " + values.length + " inputs but needed exactly " + inputs.length);
		}
	}

	/**
	 * This method is useful for mutating chromosomes. It returns any
	 * random {@code Mutable} out of the chromosome with equal
	 * probability. 
	 * 
	 * @return a random element that can be mutated - node or output.
	 */
	public Mutable getRandomMutable() {
		// choose output or node
		int index = resources.getRandomInt(outputs.length + (resources.rows() * resources.columns()));

		if (index < outputs.length) {
			// outputs
			return outputs[index];
		} else {
			// node	
			index -= outputs.length;
			return nodes[index / resources.columns()][index % resources.columns()];
		}
	}

	/**
	 * Returns a random allowed connection respecting levels back.<br>
	 * This method may always pick inputs, as they can be picked
	 * regardless of the column.
	 * 
	 * @param column the column to use as reference.
	 * @return a random connection.
	 */
	public Connection getRandomConnection(int column) {
		// work out the allowed range obeying levels back
		int allowedColumns = column >= resources.levelsBack() ? resources.levelsBack() : column;
		int offset = ((column - allowedColumns) * nodes.length) - inputs.length;

		// choose input or allowed node
		int index = resources.getRandomInt(inputs.length + (nodes.length * allowedColumns));
		if (index < inputs.length) {
			// input
			return inputs[index];
		} else {
			// node	
			// offset it to address the right columns
			index += offset;
			return nodes[index % nodes.length][index / nodes.length];
		}
	}

	/**
	 * This method will pick a completely random connection, independently
	 * of levels back, including inputs. It is useful for setting outputs.
	 * 
	 * @return a random connection regardless of levels back.
	 */
	public Connection getRandomConnection() {
		// choose output or node
		int index = resources.getRandomInt(inputs.length + (resources.columns() * resources.rows()));
		if (index < inputs.length) {
			// outputs
			return inputs[index];
		} else {
			// node	
			index -= inputs.length;
			return nodes[index / resources.columns()][index % resources.columns()];
		}
	}

	/**
	 * This causes the list of active nodes to be recomputed lazily (once it is actually requested).
	 */
	public void recomputeActiveNodes() {
		recomputeActiveNodes = true;
	}

	/**
	 * This method computes a list of active nodes (if necessary) and returns it.
	 *
	 * @return the list of active nodes.
	 */
	public ArrayList<Node> getActiveNodes() {
		computeActiveNodes();
		return activeNodes;
	}

	/**
	 * For internal use only, this method actually computes the list of active nodes
	 * from the chromosome. This is done recursively by calling {@code getActive()}
	 * on the nodes until the first node returns.
	 */
	private void computeActiveNodes() {
		// lazy recomputation has been triggered, do it
		if (recomputeActiveNodes) {
			recomputeActiveNodes = false;
			activeNodes = new ArrayList<Node>();
			// recursive operation
			for (Output output : outputs) {
				output.getActiveNodes(activeNodes);
			}
		}
	}

	/**
	 * Performs a deep comparison between this chromosome and the provided one.
	 * This is done on a gene-by-gene basis.
	 * 
	 * This method returns true if and only if:
	 * <ul>
	 * <li>the chromosomes being compared are not the same instance;</li>
	 * <li>the connections of the compared chromosomes are not the same instance;</li>
	 * <li>the grid position of the chromosome's elements are the same;</li>
	 * </ul>
	 * <br><br>
	 * The relationship computed by this method is:
	 * <ul>
	 * <li>symmetric: a.copyOf(b) == b.copyOf(a);</li>
	 * <li>not reflexive: a.copyOf(a) returns false;</li>
	 * <li>not transitive: if a.copyOf(b) is true and b.copyOf(c) is true, a.copyOf(c) is
	 *    not necessarily true since it is possible that a == c.</li>
	 * </ul>
	 * @param chromosome the chromosome to compare to.
	 * @return true if it is a copy of this chromosome, but not the same chromosome.
	 *
	 */
	public boolean compareGenesTo(Chromosome chromosome) {
		for (int r = 0; r < resources.rows(); r++) {
			for (int c = 0; c < resources.columns(); c++) {
				if (!(nodes[r][c].copyOf(chromosome.getNode(r, c)))) {
					return false;
				}
			}
		}
		
		for (int o = 0; o < resources.outputs(); o++) {
			if (!(outputs[o].copyOf(chromosome.getOutput(o)))) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Does the same as {@code compareGenesto()} but only looks
	 * at the active portion of the chromosome.
	 * 
	 * @param chromosome the chromosome to compare to.
	 * @return true if the two active portions are identical.
	 */
	public boolean compareActiveGenesTo(Chromosome chromosome) {
		// update list if it is out of date
		computeActiveNodes();
		
		if (activeNodes.size() == chromosome.getActiveNodes().size()) {
			for (int i = 0; i < activeNodes.size(); i++) {
				if (!(activeNodes.get(i).copyOf(chromosome.getActiveNodes().get(i)))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Iterates through the nodes and prints all connections and functions.
	 * This is intended for debugging purposes only and does not print to the
	 * GUI console.
	 */
	public void printNodes() {
		int arity = resources.arity();
		
		for (int r = 0; r < resources.rows(); r++) {
			System.out.print("r: " + r + "\t");
			for (int c = 0; c < resources.columns(); c++) {
				System.out.print("N: (" + r + ", " + c + ") ");
				for (int i = 0; i < arity; i++) {
					System.out.print("C" + i + ": (" + nodes[r][c].getConnection(i).toString() + ") ");
				}
				System.out.print("F: " + nodes[r][c].getFunction() + "\t");
			}
			System.out.print("\n");
		}
		
		for (int o = 0; o < resources.outputs(); o++) {
			System.out.print("o: " + o + " (" + outputs[o].getSource().toString() + ")\t");
		}
		
		System.out.println();
	}
	
	/**
	 * @return a reference to the resources based on which the chromosome was built.
	 */
	public Resources getResources() {
		return resources;
	}

	@Override
	public int compareTo(Chromosome o) {
		if (fitness < o.getFitness()) {
			return -1;
		} else if (fitness > o.getFitness()) {
			return 1;
		} else {
			return 0;
		}
	}
}
