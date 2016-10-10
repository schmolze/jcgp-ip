package jcgp.backend.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.backend.population.Mutable;
import jcgp.backend.population.Node;
import jcgp.backend.population.Output;
import jcgp.backend.resources.ModifiableResources;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Tests which cover the behaviour specified for a chromosome.
 * 
 *  - The chromosome should be able to return a specified node, input or output.
 *  - It should be able to return a random Mutable.
 *  - It should be able to return a random allowed connection given a column.
 *  - It should be able to return a random connection.
 *  - It should contain a freely modifiable fitness value.
 *  - For truth table evaluations, it should be able to have its inputs set.
 *  - For truth table evaluations, the output should return a value according to the inputs.
 *  - It should feature a copy method, which creates a deep copy of a specified Chromosome object.
 *  - It should be able to return a list of active nodes.
 *  - It should contain a method to evaluate whether a given chromosome is identical
 *    to it.
 *  - Same as above, but only looking at the active portion of a chromosome.
 *  
 *  
 *  WARNING: changing parameters may cause the tests to incorrectly fail!
 * 
 * @author Eduardo Pedroni
 *
 */
public class ChromosomeTests {

	private Chromosome chromosome;
	private static ModifiableResources resources;

	@BeforeClass
	public static void setUpBeforeClass() {
		resources = new ModifiableResources();
		resources.setFunctionSet(new TestFunctionSet());
	}

	@Before
	public void setUp() throws Exception {
		chromosome = new Chromosome(resources);
	}

	/**
	 * 
	 */
	@Test
	public void cloneTest() {
		// create a clone, check to see if it really is a clone
		Chromosome clone = new Chromosome(chromosome);

		// compare all elements, one by one
		// check outputs
		for (int o = 0; o < resources.outputs(); o++) {
			// check that no cross-references exist between chromosomes
			assertTrue("Cloned chromosome contains a reference to a member of the original chromosome.",
					clone.getOutput(o) != chromosome.getOutput(o) && 
					clone.getOutput(o).getSource() != chromosome.getOutput(o).getSource());
			// check that the connections are equivalent
			if (clone.getOutput(o).getSource() instanceof Input && chromosome.getOutput(o).getSource() instanceof Input) {
				assertTrue("Outputs did not connect to equivalent inputs.", 
						((Input) clone.getOutput(o).getSource()).getIndex() == ((Input) chromosome.getOutput(o).getSource()).getIndex());
			} else if (clone.getOutput(o).getSource() instanceof Node && chromosome.getOutput(o).getSource() instanceof Node) {
				assertTrue("Outputs did not connect to equivalent nodes.", 
						((Node) clone.getOutput(o).getSource()).getRow() == ((Node) chromosome.getOutput(o).getSource()).getRow() && 
						((Node) clone.getOutput(o).getSource()).getColumn() == ((Node) chromosome.getOutput(o).getSource()).getColumn());
			} else {
				fail("Output source types did not match.");
			}
		}
		// check nodes, rows first
		for (int row = 0; row < resources.rows(); row++) {
			for (int column = 0; column < resources.columns(); column++) {
				// check that nodes are not pointers to the same instance
				assertTrue("Both chromosomes contain a reference to the same node.", clone.getNode(row, column) != chromosome.getNode(row, column));
				// check that both nodes reference their own position in the grid correctly
				assertTrue("Equivalent nodes self-reference differently.", clone.getNode(row, column).getRow() == chromosome.getNode(row, column).getRow() &&
						clone.getNode(row, column).getColumn() == chromosome.getNode(row, column).getColumn());
				// check that the two nodes have the same function
				assertTrue("Equivalent nodes have different functions.", clone.getNode(row, column).getFunction() == chromosome.getNode(row, column).getFunction());

				// compare each connection
				for (int connection = 0; connection < resources.arity(); connection++) {
					// first look at whether they are actually the same instance
					assertTrue("Nodes are connected to the same connection instance.", 
							clone.getNode(row, column).getConnection(connection) !=	chromosome.getNode(row, column).getConnection(connection));

					// if the connections aren't the same instance, check that their addresses are the same
					if (clone.getNode(row, column).getConnection(connection) instanceof Input && 
							chromosome.getNode(row, column).getConnection(connection) instanceof Input) {

						assertTrue("Nodes did not connect to equivalent inputs.", 
								((Input) clone.getNode(row, column).getConnection(connection)).getIndex() ==
								((Input) chromosome.getNode(row, column).getConnection(connection)).getIndex());

					} else if (clone.getNode(row, column).getConnection(connection) instanceof Node && 
							chromosome.getNode(row, column).getConnection(connection) instanceof Node) {

						assertTrue("Nodes did not connect to equivalent nodes.", 
								((Node) clone.getNode(row, column).getConnection(connection)).getRow() ==
								((Node) chromosome.getNode(row, column).getConnection(connection)).getRow() &&

								((Node) clone.getNode(row, column).getConnection(connection)).getColumn() ==
								((Node) chromosome.getNode(row, column).getConnection(connection)).getColumn());

					} else {
						fail("Connection types did not match.");
					}
				}
			}
		}

		// check cloning given a known topology
		chromosome = createKnownConfiguration();
		clone = new Chromosome(chromosome);
		
		Integer[] testInputs = new Integer[] {5, 8, 4};
		chromosome.setInputs((Object[]) testInputs);
		clone.setInputs((Object[]) testInputs);

		// check that both chromosomes have the same outputs
		for (int i = 0; i < resources.outputs(); i++) {
			assertTrue("Incorrect output returned", ((Integer) chromosome.getOutput(i).calculate()) == ((Integer) clone.getOutput(i).calculate()));
		}

		// mutate an output in clone, check that the same node in chromosome produces a different output
		clone.getOutput(1).setSource(clone.getInput(2));

		assertTrue("Mutation affected nodes in both chromosomes.",
				clone.getOutput(1).calculate() != chromosome.getOutput(1).calculate());

	}
	/**
	 * 
	 */
	@Test
	public void fitnessTest() {
		// set a fitness value, check if returned value is the same
		chromosome.setFitness(10);
		assertTrue("Incorrect fitness returned.", chromosome.getFitness() == 10);
	}

	/**
	 * 
	 */
	@Test
	public void randomConnectionTest() {
		// get random connections with column 0, check that they are all inputs
		for (int i = 0; i < 10000; i++) {
			boolean connectionReturn = chromosome.getRandomConnection(0) instanceof Input;
			assertTrue("Connection is not an input.", connectionReturn);
		}

		// get random connections with the last column as reference, check that they're all within range
		int connectionNodes = 0, connectionOutOfRange = 0, connectionInputs = 0, connectionPicks = 100000;
		int chosenColumn = resources.columns() - 1;
		for (int i = 0; i < connectionPicks; i++) {
			Connection c = chromosome.getRandomConnection(chosenColumn);
			if (c instanceof Node) {
				connectionNodes++;
				if (((Node) c).getColumn() >= chosenColumn) {
					connectionOutOfRange++;
				}
				assertTrue("Connection is not allowed : " + ((Node) c).getColumn(), ((Node) c).getColumn() < chosenColumn && ((Node) c).getColumn() < chosenColumn);
			} else if (c instanceof Input) {
				connectionInputs++;
			} else {
				fail("Return is neither Node nor Input.");

			}	
		}
		System.out.println("Out of " + connectionPicks + " connections picked from " + ((chosenColumn >= resources.levelsBack()) ? resources.levelsBack() : chosenColumn) * resources.rows() +
				" allowed nodes and " + resources.inputs() + " inputs, " + connectionNodes + " were nodes and " + connectionInputs + " were inputs.");

		System.out.println("Node/input ratio: " + (chosenColumn >= resources.levelsBack() ? resources.levelsBack() : chosenColumn) * (double) resources.rows() / (double) resources.inputs() +
				", picked ratio: " + (double) connectionNodes / (double) connectionInputs);

		System.out.println(connectionOutOfRange + " nodes that disrespected levels back were picked.");
	}
	/**
	 * 
	 */
	@Test
	public void randomMutableTest() {
		// get mutable elements, check Node to Output ratio
		int mutablePicks = 100000;
		int mutableNodes = 0, mutableOutputs = 0;
		for (int i = 0; i < mutablePicks; i++) {
			Mutable m = chromosome.getRandomMutable();

			if (m instanceof Node) {
				mutableNodes++;
			} else if (m instanceof Output) {
				mutableOutputs++;
			} else {
				fail("Return is neither Node nor Output.");
			}
		}
		System.out.println("Out of " + mutablePicks + " mutable elements picked from " + resources.nodes() +
				" nodes and " + resources.outputs() + " outputs, " + mutableNodes + " were nodes and " +
				mutableOutputs + " were outputs.");
		System.out.println("Node/output ratio: " + (double) resources.nodes() / (double) resources.outputs() +
				", picked ratio: " + (double) mutableNodes / (double) mutableOutputs + "\n");
	}

	/**
	 * 
	 */
	@Test
	public void getOutputsTest() {
		chromosome = createKnownConfiguration();

		chromosome.setInputs(5, 8, 4);

		Integer output0 = (Integer) chromosome.getOutput(0).calculate();
		Integer output1 = (Integer) chromosome.getOutput(1).calculate();
		
		// with this configuration, the outputs should be 13 and 25.
		assertTrue("Incorrect output returned: " + output0, output0 == 13.0);
		assertTrue("Incorrect output returned: " + output1, output1 == 25.0);
	}

	/**
	 * 
	 */
	@Test
	public void setInputTest() {
		// set input values, check that acquired values are correct
		Integer[] testInputs = new Integer[resources.inputs()];
		for (int i = 0; i < resources.inputs(); i++) {
			testInputs[i] = i * 2 - 3;
		}
		chromosome.setInputs((Object[]) testInputs);
		for (int i = 0; i < resources.inputs(); i++) {
			assertTrue("Incorrect input returned.", ((Integer) chromosome.getInput(i).getValue()) == i * 2 - 3);
		}
	}

	/**
	 * 
	 */
	@Test
	public void getNodeTest() {
		// get all nodes one by one, check that they are all correct
		for (int r = 0; r < resources.rows(); r++) {
			for (int c = 0; c < resources.columns(); c++) {
				assertTrue("Incorrect node returned.", chromosome.getNode(r, c).getColumn() == c &&
						chromosome.getNode(r, c).getRow() == r);
			}
		}
	}

	/**
	 * 
	 */
	@Test
	public void activeNodeTest() {
		// active node detection happens recursively, the user only calls a single method
		// set connections to a known configuration
		chromosome = createKnownConfiguration();
		
		assertTrue("Active node missing from list.", chromosome.getActiveNodes().contains(chromosome.getNode(0, 0)));
		assertTrue("Active node missing from list.", chromosome.getActiveNodes().contains(chromosome.getNode(1, 1)));
		assertTrue("Active node missing from list.", chromosome.getActiveNodes().contains(chromosome.getNode(1, 2)));
		
		chromosome.printNodes();
		
		assertTrue("List has the wrong number of nodes: " + chromosome.getActiveNodes(), chromosome.getActiveNodes().size() == 3);
	}

	/**
	 * 
	 */
	@Test
	public void compareActiveTest() {
		// create a clone of the chromosome, compare active nodes - should return true
		Chromosome c = new Chromosome(chromosome);
		assertTrue("Active nodes did not match.", chromosome.compareActiveGenesTo(c));
		assertTrue("Symmetry not obeyed.", c.compareActiveGenesTo(chromosome));

		// create a new random chromosome, this time they should not match
		c = new Chromosome(resources);
		assertTrue("Active nodes did match.", !chromosome.compareActiveGenesTo(c));
	}

	/**
	 * 
	 */
	@Test
	public void compareTest() {
		// create a clone of the chromosome, compare - should return true
		Chromosome c = new Chromosome(chromosome);
		assertTrue("Chromosomes did not match.", chromosome.compareGenesTo(c));
		assertTrue("Symmetry not obeyed.", c.compareGenesTo(chromosome));

		// create a new random chromosome, this time they should not match
		c = new Chromosome(resources);
		assertTrue("Chromosomes did match.", !chromosome.compareGenesTo(c));
	}
	/**
	 * Utility for creating a chromosome of known configuration.
	 * Topology is 3x3, with 3 inputs and 2 outputs.
	 * Given inputs 5, 8 and 4 outputs should be 13 and 25.
	 * 
	 * Active nodes (r, c): [0, 0], [1, 1], [1, 2]
	 * 
	 * @return the configured chromosome
	 */
	private Chromosome createKnownConfiguration() {
		// with a small topology, build a chromosome of known connections and check outputs
		resources.setColumns(3);
		resources.setRows(3);
		resources.setInputs(3);
		resources.setOutputs(2);
		resources.setLevelsBack(3);

		Chromosome c = new Chromosome(resources);

		c.getNode(0, 0).initialise(resources.getFunction(0), c.getInput(0), c.getInput(1));
		c.getNode(1, 1).initialise(resources.getFunction(0), c.getNode(0, 0), c.getInput(1));
		c.getNode(1, 2).initialise(resources.getFunction(0), c.getNode(1, 1), c.getInput(2));

		c.getOutput(0).setSource(c.getNode(0, 0));
		c.getOutput(1).setSource(c.getNode(1, 2));
		
		return c;
	}
}
