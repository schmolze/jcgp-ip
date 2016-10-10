package jcgp.backend.population;

import java.util.Arrays;
import java.util.Collections;

import jcgp.backend.modules.problem.BestFitness;
import jcgp.backend.resources.Resources;

/**
 * This class primarily holds a collection of chromosomes. In addition, 
 * it provides a few utility methods for manipulating and copying 
 * chromosomes, useful for evolutionary strategies.
 * <br><br>
 * {@code copyChromosome()} is used to create copies of chromosomes, 
 * though it is also possible to create a new instance of population
 * directly from a seed chromosome using the right constructor. 
 * <br><br>
 * For convenience, a random chromosome can be retrieved using
 * {@code getRandomChromosome()}, which is guaranteed to use the
 * experiment's specified seed. If an entirely random population
 * is needed, {@code reinitialise()} should be used to randomise
 * all chromosomes without creating a new instance of {@code Population}.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public class Population {
	
	private final Chromosome[] chromosomes;
	private final Resources resources;
	
	/**
	 * Initialise a random population according to the parameters specified
	 * in the resources.
	 * 
	 * @param resources the experiment's resources.
	 */
	public Population(Resources resources) {
		this.resources = resources;
		
		chromosomes = new Chromosome[resources.populationSize()];
		for (int c = 0; c < chromosomes.length; c++) {
			chromosomes[c] = new Chromosome(resources);
		}
	}
	
	/**
	 * Initialise a population of copies of the given chromosome.
	 * 
	 * @param parent the chromosome to use as a model.
	 * @param resources a reference to the experiment's resources.
	 */
	public Population(Chromosome parent, Resources resources) {
		this.resources = resources;
		
		chromosomes = new Chromosome[resources.populationSize()];
		for (int c = 0; c < chromosomes.length; c++) {
			chromosomes[c] = new Chromosome(parent);
		}
	}

	/**
	 * Returns the indexed chromosome.
	 * 
	 * @param index the chromosome to return.
	 * @return the indexed chromosome.
	 */
	public Chromosome get(int index) {
		return chromosomes[index];
	}
	
	/**
	 * @return a random chromosome from this population.
	 */
	public Chromosome getRandomChromosome() {
		return chromosomes[resources.getRandomInt(chromosomes.length)];
	}

	/**
	 * Copy a chromosome into a different position.
	 * After this returns, the target chromosome has
	 * identical connections and functions to the source
	 * one, though they are separate instances.
	 * 
	 * This method does nothing if source == target.
	 * 
	 * @param source the chromosome to copy from.
	 * @param target the chromosome to copy to.
	 */
	public void copyChromosome(int source, int target) {
		if (source != target) {
			chromosomes[target].copyGenes(chromosomes[source]);
		}
	}
	
	/**
	 * Loop through all chromosomes and randomise all connections
	 * and functions.
	 */
	public void reinitialise() {
		for (int c = 0; c < chromosomes.length; c++) {
			chromosomes[c].reinitialiseConnections();
		}
	}
		
	/**
	 * Sorts the population in ascending order of fitness quality.
	 * What this means is that the best fitness chromosome will be
	 * in the last position, even though it might have the lowest
	 * fitness value. Fitness orientation as specified in the resources
	 * is respected.
	 */
	public void sort() {
		if (resources.fitnessOrientation() == BestFitness.HIGH) {
			Arrays.sort(chromosomes);
		} else {
			Arrays.sort(chromosomes, Collections.reverseOrder());
		}
	}
}
