package jcgp.backend.tests;

import static org.junit.Assert.assertTrue;
import jcgp.backend.function.SymbolicRegressionFunctions;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Population;
import jcgp.backend.resources.ModifiableResources;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Tests which cover the behaviour specified for a population.
 * 
 *  - It should be possible to iterate through all the chromosomes in a population.
 *  - When constructed with no arguments, it should generate populationSize
 *    random chromosomes, distributed according to the EA parameters.
 *  - If one or more chromosomes are passed into the constructor, it should use them
 *    as parents to create the rest of the population.
 *  
 * 
 * @author Eduardo Pedroni
 *
 */
public class PopulationTests {

	private Population population;
	private static ModifiableResources resources;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		resources = new ModifiableResources();
		resources.setFunctionSet(new SymbolicRegressionFunctions());
	}

	@Before
	public void setUp() throws Exception {
		population = new Population(resources);
	}

	@Test
	public void defaultPopulationTest() {
		// check that the constructor really generates populationSize chromosomes when none is given
		int chromosomes = 0;
		while (true) {
			try {
				population.get(chromosomes);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			chromosomes++;
		}

		assertTrue("Incorrect number of chromosomes generated.", chromosomes == resources.populationSize());
	}
	
	@Test
	public void preinitialisedChromosomeTest() {
		// the original chromosome that will be cloned
		Chromosome oc = new Chromosome(resources);

		// initialise a population with a copy of it
		population = new Population(oc, resources);
		// check that the first parent chromosome is identical to, but not the same instance as, the one given
		assertTrue("Incorrect chromosome in population.", population.get(0).compareGenesTo(oc) && population.get(0) != oc);
	}
}
