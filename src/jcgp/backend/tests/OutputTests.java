package jcgp.backend.tests;

import static org.junit.Assert.assertTrue;
import jcgp.backend.function.SymbolicRegressionFunctions;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Output;
import jcgp.backend.resources.ModifiableResources;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Tests which cover the behaviour specified for an output.
 * 
 *  - An output contains a single source Connection, which can be set and got.
 *  - It should return the value of its source connection.
 *  - It must be addressable by an index set upon construction only.
 *  
 * 
 * @author Eduardo Pedroni
 *
 */
public class OutputTests {

	private Output output;
	private static Chromosome chromosome;
	private static ModifiableResources resources;
	// these are the test values
	private final int outputValue = 10;
	private final int outputIndex = 2;

	@BeforeClass
	public static void setUpBeforeClass() {
		resources = new ModifiableResources();
		resources.setFunctionSet(new SymbolicRegressionFunctions());
		chromosome = new Chromosome(resources);
	}

	@Before
	public void setUp() throws Exception {
		output = new Output(chromosome, outputIndex);
	}

	@Test
	public void evaluationsTest() {
		// set source connection, check that the appropriate value is returned
		output.setSource(new Connection() {

			@Override
			public Object getValue() {
				// test value
				return outputValue;
			}
		});

		assertTrue("Incorrect evaluation.", ((Integer) output.calculate()) == outputValue);
	}

	@Test
	public void connectionTest() {
		// set a new connection, check that it is correctly returned
		Connection newConn = new Connection() {

			@Override
			public Object getValue() {
				// blank
				return 0;
			}
		};
		output.setSource(newConn);

		assertTrue("Incorrect connection returned.", output.getSource() == newConn);
	}
	
	@Test
	public void indexTest() {
		// check that the index returned is the one passed to the constructor
		assertTrue("Incorrect index returned.", output.getIndex() == outputIndex);
	}
}
