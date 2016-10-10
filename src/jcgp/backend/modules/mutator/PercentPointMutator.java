package jcgp.backend.modules.mutator;

import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.DoubleParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.parameters.monitors.IntegerMonitor;
import jcgp.backend.resources.Resources;

/**
 * Percent point mutator
 * <br><br>
 * This operator calculates how many genes to mutate based on the mutation rate
 * parameter. The total number of genes is computed from the number of nodes, 
 * the arity and the number of outputs. It then uses the point mutation 
 * algorithm to perform the required number of mutations.
 * 
 * 
 * @see PointMutator
 * @author Eduardo Pedroni
 *
 */
public class PercentPointMutator extends PointMutator {
	
	private DoubleParameter mutationRate;

	/**
	 * Creates a new instance of PercentPointMutator. 
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public PercentPointMutator(final Resources resources) {
		super(resources);
		mutationRate = new DoubleParameter(10, "Percent mutation", false, false) {
			@Override
			public void validate(Number newValue) {

				int totalGenes = (resources.nodes() * (resources.arity() + 1)) + resources.outputs();
				int mutations = (int) (newValue.doubleValue() * (double) (totalGenes / 100.0));
				genesMutated.set(mutations);
				
				if (newValue.doubleValue() <= 0 || newValue.doubleValue() > 100) {
					status = ParameterStatus.INVALID;
					status.setDetails("Mutation rate must be > 0 and <= 100");
				} else if (genesMutated.get() <=  0) {
					status = ParameterStatus.WARNING;
					status.setDetails("With mutation rate " + mutationRate.get() + ", 0 genes will be mutated.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		genesMutated = new IntegerMonitor(0, "Genes mutated");
		report = new BooleanParameter(false, "Report");
		
		setName("Percent point mutation");
		registerParameters(mutationRate, genesMutated, report);
	}
}
