package jcgp.backend.modules.es;

import jcgp.backend.modules.Module;
import jcgp.backend.modules.mutator.Mutator;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * This class specifies the characteristics of an evolutionary strategy. The evolutionary
 * strategy's job is to generate the next population of solutions. In JCGP this is done by modifying
 * the provided population object rather than creating a new one. 
 * <br><br>
 * A typical implementation of EvolutionaryStratey iterates through the chromosomes
 * in the population and selects the individual(s) to be promoted. It then uses
 * {@code mutator.mutate()} to generically mutate the promoted individual(s). Parameter-dependent
 * strategies can be implemented by accessing the parameters via the resources
 * argument.
 * <br><br>
 * Parameters may be specified to control the implemented strategy. Any parameters
 * registered with {@code registerParameters()} should be displayed by the user interface,
 * if it is being used. See {@link Module} for more information. 
 * <br><br>
 * It is advisable to use {@code Resources.reportln()} and {@code Resources.report()}
 * to print any relevant information. Note that reportln() and report() are affected
 * by the report interval base parameter. Use {@code Resources.println()} and
 * {@code Resources.print()} to print information regardless of the current generation.
 * See {@link Resources} for more information.
 * 
 * @see Module
 * @author Eduardo Pedroni
 *
 */
public abstract class EvolutionaryStrategy extends Module {
	
	/**
	 * For internal use only, initialises the resources field.
	 * 
	 * @param resources the experiment's resources.
	 */
	protected EvolutionaryStrategy(Resources resources) {
		super(resources);
	}
	
	/**
	 * Performs the selection algorithm and uses the mutator to create
	 * the next generation of solutions.
	 * 
	 * @param population the population to evolve.
	 * @param mutator the mutator with which to mutate the promoted individuals.
	 */
	public abstract void evolve(Population population, Mutator mutator);

}
