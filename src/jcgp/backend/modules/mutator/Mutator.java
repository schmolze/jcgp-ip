package jcgp.backend.modules.mutator;

import jcgp.backend.modules.Module;
import jcgp.backend.population.Chromosome;
import jcgp.backend.resources.Resources;

/**
 * This class specifies the basic characteristics of a mutation operator. Its job is 
 * to modify the connections and functions of the chromosome according to the operator's
 * parameters.
 * <br><br>
 * Parameters may be specified to control the implemented mutation. Any parameters
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
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class Mutator extends Module {
	
	/**
	 * For internal use only, initialises the resources field.
	 * 
	 * @param resources the experiment's resources.
	 */
	protected Mutator(Resources resources) {
		super(resources);
	}

	/**
	 * Applies mutations to the specified chromosome according
	 * to the parameter values.
	 * 
	 * @param chromosome the chromosome to mutate.
	 */
	public abstract void mutate(Chromosome chromosome);
	
}
