package jcgp.backend.modules.es;

import jcgp.backend.modules.mutator.Mutator;
import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * (μ + λ)-ES
 * <br><br>
 * This strategy selects the μ fittest chromosomes from the population.
 * The promoted individuals are copied into the new population and mutated
 * λ times, but also carried forward unchanged. The total population size
 * is μ + λ. 
 * <br><br>
 * Two integer parameters are used to control this strategy: parents
 * and offspring. They are constrained in that they must always add up to
 * the population size, and must never be smaller than 1.
 * <br>
 * One additional parameter, report, controls whether a detailed log of the
 * algorithm's operation is to be printed or not. Reports respect the report
 * interval base parameter.
 * 
 * @see EvolutionaryStrategy
 * @author Eduardo Pedroni
 *
 */
public class MuPlusLambda extends EvolutionaryStrategy {

	private IntegerParameter mu, lambda;
	private BooleanParameter report;

	/**
	 * Creates a new instance of MuPlusLambda. 
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public MuPlusLambda(final Resources resources) {
		super(resources);
		mu = new IntegerParameter(1, "Parents (\u03BC)") {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() + lambda.get() != getResources().populationSize()) {
					status = ParameterStatus.INVALID;
					status.setDetails("Parents + offspring must equal population size.");
				} else if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("ES needs at least 1 parent.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};

		lambda = new IntegerParameter(4, "Offspring (\u03BB)") {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() + mu.get() != getResources().populationSize()) {
					status = ParameterStatus.INVALID;
					status.setDetails("Parents + offspring must equal population size.");
				} else if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("ES needs at least 1 offspring.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};

		report = new BooleanParameter(false, "Report");

		setName("(\u03BC + \u03BB)");
		registerParameters(mu, lambda, report);
	}

	@Override
	public void evolve(Population population, Mutator mutator) {	
		// sort the population neutrally
		sort(population);
		
		// population is now sorted such that the new parents are in the last mu positions
		for (int i = 0; i < getResources().populationSize() - mu.get(); i++) {
			// select a random parent out of the mu population parents
			int randomParent = getResources().populationSize() - 1 - getResources().getRandomInt(mu.get());
			if (report.get()) getResources().reportln("[ES] Copying Chr " + randomParent + " to population position " + i);
			
			// copy it into the offspring position
			population.copyChromosome(randomParent, i);

			// mutate the new offspring chromosome
			if (report.get()) getResources().reportln("[ES] Mutating copied chromosome");
			mutator.mutate(population.get(i));
		}

		if (report.get()) getResources().reportln("[ES] Generation is complete");
	}

	/**
	 * Neutrally sorts the specified population. 
	 * <br><br>
	 * Optimised sorting methods tend to be stable, meaning 
	 * the order of elements which are already ordered is not
	 * changed. While performing faster, such sorting algorithms
	 * do not promote neutral drift, an important aspect of CGP.
	 * <br><br>
	 * This sort iterates through the population offspring (first lambda
	 * elements) and compares each with each of the parents (last mu
	 * elements), overwriting the parent if the offspring's fitness
	 * is greater than or equal to the parent's. 
	 * It is biased towards offspring: parents are replaced with
	 * equally fit offspring as often as possible. 
	 * 
	 * @param population the population to sort.
	 */
	private void sort(Population population) {
		/* Create an array with the index of each of the current parents.
		 * This is done to speed up the sort. No deep chromosome copies are
		 * made until the sort is finished; instead, only indices are copied.
		 */
		int[] parents = new int[mu.get()];
		for (int i = 0; i < parents.length; i++) {
			parents[i] = lambda.get() + i;
		}
		
		// cycle through the offspring, i.e. the first lambda elements of the population
		for (int o = 0; o < getResources().populationSize() - mu.get(); o++) {
			// compare each offspring with each parent, as stored in parents
			for (int p = 0; p < parents.length; p++) {
				/* replace parent if the offspring fitness and greater than or equal to its own
				 * if it is equal to, only replace if it is an old parent, if it is greater than,
				 * replace regardless
				 */
				if ((population.get(o).getFitness() == population.get(parents[p]).getFitness() && parents[p] >= lambda.get())
						|| population.get(o).getFitness() >= population.get(parents[p]).getFitness()) {
					parents[p] = o;
					// offspring has been selected, check the next one
					break;
				}
			}
		}
		
		/* selection is complete, parents now contains the indices of each selected offspring
		 * time to perform the deep copies
		 */
		for (int c = 0; c < parents.length; c++) {
			// copy each selected index in parent to each parent position in the population
			population.copyChromosome(parents[c], lambda.get() + c);
		}
		
	}
}
