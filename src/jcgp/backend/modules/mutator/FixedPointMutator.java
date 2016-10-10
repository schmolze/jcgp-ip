package jcgp.backend.modules.mutator;

import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.resources.Resources;

/**
 * Fixed point mutator
 * <br><br>
 * This operator uses the point mutator 
 * algorithm to mutate a user-defined fixed
 * number of genes.
 * 
 * 
 * @see PointMutator
 * @author Eduardo Pedroni
 *
 */
public class FixedPointMutator extends PointMutator {

	/**
	 * Creates a new instance of FixedPointMutator. 
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public FixedPointMutator(final Resources resources) {
		super(resources);
		genesMutated = new IntegerParameter(5, "Genes mutated", false, false) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("At least 1 mutation must take place.");
				} else if (newValue.intValue() > (resources.nodes() * (resources.arity() + 1)) + resources.outputs()) {
					status = ParameterStatus.WARNING;
					status.setDetails("More genes are mutated than there are genes in the genotype.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		report = new BooleanParameter(false, "Report");
		
		setName("Fixed point mutation");
		registerParameters(genesMutated, report);
	}
}
