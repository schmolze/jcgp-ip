package jcgp.backend.modules.es;

import java.util.Arrays;

import jcgp.backend.modules.mutator.Mutator;
import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * Tournament selection
 * <br><br>
 * This strategy generates a new population by selecting a specified number
 * of chromosomes from the original population and selecting the fittest out
 * of the isolated subset (the tournament). The selected individual is mutated
 * using the specified mutator. This process is repeated until the new population
 * is complete.
 * <br><br>
 * One integer parameter is used to control this strategy: tournament
 * size. This must always be greater than 0 and smaller than or equal to the
 * population size. Setting it to equal population size results in the same
 * chromosome being selected for every tournament, and setting it to 1 leads
 * to an effectively random search.
 * <br>
 * One additional parameter, report, controls whether a detailed log of the
 * algorithm's operation is to be printed or not. Reports respect the report
 * interval base parameter.
 * 
 * @see EvolutionaryStrategy
 * @author Eduardo Pedroni
 *
 */
public class TournamentSelection extends EvolutionaryStrategy {
	
	private IntegerParameter tournamentSize;
	private BooleanParameter report;
	
	/**
	 * Creates a new instance of TournamentSelection. 
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public TournamentSelection(final Resources resources) {	
		super(resources);
		tournamentSize = new IntegerParameter(1, "Tournament size") {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Tournament size must be greater than 0.");
				} else if (newValue.intValue() > resources.populationSize()) {
					status = ParameterStatus.INVALID;
					status.setDetails("Tournament size must not be greater than the population size.");
				} else if (newValue.intValue() == 1) {
					status = ParameterStatus.WARNING;
					status.setDetails("A tournament size of 1 results in a random search.");
				} else if (newValue.intValue() == resources.populationSize()) {
					status = ParameterStatus.WARNING;
					status.setDetails("A tournament size equal to population size results in the same individual being selected every time.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		report = new BooleanParameter(false, "Report");
		
		setName("Tournament selection");
		registerParameters(tournamentSize, report);
	}

	@Override
	public void evolve(Population population, Mutator mutator) {
		/* Create an entirely new population by isolating random subsets of
		 * the original population and choosing the fittest individual within
		 * that subset. Each chosen individual is mutated and copied back into the
		 * population.
		 */
		
		// sort the population by fitness to make things easier
		population.sort();
		
		// this array holds the new population temporarily, until it is copied over
		Chromosome[] newPopulation = new Chromosome[getResources().populationSize()];
		
		// start by selecting all of the chromosomes that will be promoted
		for (int i = 0; i < getResources().populationSize(); i++) {
			if (report.get()) getResources().reportln("[ES] Starting tournament " + i);
			
			/* the population is sorted in ascending order of fitness,
			 * meaning the higher the index of the contender, the fitter
			 * it is
			 */
			int[] contenders = new int[tournamentSize.get()];
			for (int t = 0; t < tournamentSize.get() - 1; t++) {
				contenders[t] = getResources().getRandomInt(getResources().populationSize());
			}
			if (report.get()) getResources().reportln("[ES] Selected contenders: " + Arrays.toString(contenders));
			Arrays.sort(contenders);
			if (report.get()) getResources().reportln("[ES] Chr " + contenders[contenders.length - 1] + " wins the tournament, copying and mutating...");
			// create a copy of the selected chromosome and mutate it
			newPopulation[i] = new Chromosome(population.get(contenders[contenders.length - 1]));
			mutator.mutate(newPopulation[i]);
		}
		if (report.get()) getResources().reportln("[ES] Tournaments are finished, copying new chromosomes into population");
		// newPopulation has been generated, copy into the population
		for (int c = 0; c < getResources().populationSize(); c++) {
			population.get(c).copyGenes(newPopulation[c]);
		}
		
		if (report.get()) getResources().reportln("[ES] Generation is complete");
	}
}
