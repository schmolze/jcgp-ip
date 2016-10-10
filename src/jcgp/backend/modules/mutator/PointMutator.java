package jcgp.backend.modules.mutator;

import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Mutable;
import jcgp.backend.resources.Resources;

/**
 * Point mutator
 * <br><br>
 * In point mutation, a number of random genes
 * is picked and mutated until all required
 * mutations have been performed. The actual number
 * of genes to be mutated can be defined in any
 * arbitrary way.
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class PointMutator extends Mutator {

	protected IntegerParameter genesMutated;
	protected BooleanParameter report;
	
	/**
	 * For internal use only, initialises the resources field.
	 * 
	 * @param resources the experiment's resources.
	 */
	protected PointMutator(Resources resources) {
		super(resources);
	}

	@Override
	public void mutate(Chromosome chromosome) {
		if (report.get()) getResources().reportln("[Mutator] Number of mutations to be performed: " + genesMutated.get());
		
		// for however many genes must be mutated
		for (int i = 0; i < genesMutated.get(); i++) {
			// choose a random mutable
			Mutable mutable = chromosome.getRandomMutable();
			
			if (report.get()) getResources().reportln("[Mutator] Mutation " + i + " selected " + mutable);
			
			// mutate a random gene
			mutable.mutate();
		}
	}

}
