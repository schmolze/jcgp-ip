package jcgp.backend.tests;

import static org.junit.Assert.assertTrue;
import jcgp.backend.function.Function;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Node;
import jcgp.backend.resources.ModifiableResources;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Tests which cover the behaviour specified for a node.
 * 
 *  - A node should contain read-only row and column values which are set upon construction.
 *  - It should contain a fully accessible Function object.
 *  - It should contain a set of connections which can be initialised and randomly 
 *    modified. It should be able to return an addressed connection.
 *  - It should be able to compute a value using its function with its connections as arguments.
 *  
 *  WARNING: changing parameters may cause the tests to incorrectly fail!
 * 
 * @author Eduardo Pedroni
 *
 */
public class NodeTests {

	private Node node;
	private static Chromosome chromosome;
	private static ModifiableResources resources;
	// these numbers will be used by the node under test
	private final int arg1 = 2;
	private final int arg2 = 5;

	@BeforeClass
	public static void setUpBeforeClass() {
		
		resources = new ModifiableResources();
		resources.setFunctionSet(new TestFunctionSet());
		chromosome = new Chromosome(resources);
	}

	@Before
	public void setUp() throws Exception {
		node = new Node(chromosome, 0, 0);
		// make node with addition function and hard-coded value connections
		node.initialise(resources.getFunction(0),
				new Connection[]{new Connection() {

					@Override
					public Object getValue() {
						// hardcode a value
						return arg1;
					}
				},
				new Connection() {

					@Override
					public Object getValue() {
						// hardcode a value
						return arg2;
					}
				}});
	}

	@Test
	public void rowAndColumnTest() {
		assertTrue("Incorrect row.", node.getRow() == 0);
		assertTrue("Incorrect column.", node.getColumn() == 0);
	}

	@Test
	public void functionTest() {
		// make a new function and assign to node
		Function f = new Function() {

			@Override
			public Object run(Object... connections) {
				// blank
				return 0;
			}

			@Override
			public int getArity() {
				// blank
				return 0;
			}
		};

		node.setFunction(f);

		// check that the function returned by the node is f
		assertTrue("Incorrect function returned.", node.getFunction() == f);
		// check that it outputs 0 as it should
		assertTrue("Incorrect function output.", ((Integer) node.getValue()) == 0);
	}

	@Test
	public void evaluationTest() {
		// check that addition is working
		assertTrue("Node did not return expected value (sum of arguments). Output was: " + ((int) node.getValue()), 
				((int) node.getValue()) == arg1 + arg2);

		// put in a different function, check the output has changed appropriately
		node.setFunction(resources.getFunction(1));

		assertTrue("Node did not return expected value (difference of arguments).", ((Integer) node.getValue()) == arg1 - arg2);

	}

	@Test
	public void connectionsTest() {
		// make new blank connections, check that they are returned correctly when addressed
		Connection conn0, conn1, conn2;
		conn0 = new Connection() {

			@Override
			public Object getValue() {
				// blank
				return 0;
			}
		};
		conn1 = new Connection() {

			@Override
			public Object getValue() {
				// blank
				return 0;
			}
		};
		
		Function function = new Function() {
			@Override
			public Object run(Object... connections) {
				// blank
				return null;
			}

			@Override
			public int getArity() {
				return 2;
			}
		};
		
		node.initialise(function, conn0, conn1);

		assertTrue("Connection 0 is incorrect.", node.getConnection(0) == conn0);
		assertTrue("Connection 1 is incorrect.", node.getConnection(1) == conn1);

		// make yet another connection, set it randomly, check that it is one of the node's connections
		conn2 = new Connection() {

			@Override
			public Object getValue() {
				// blank
				return 0;
			}
		};
		node.setConnection(resources.getRandomInt(resources.arity()), conn2);

		assertTrue("Connection was not found in node.", node.getConnection(0) == conn2 || node.getConnection(1) == conn2);

	}


}
