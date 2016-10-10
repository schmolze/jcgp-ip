package jcgp.backend.modules.mutator;

import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.DoubleParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Node;
import jcgp.backend.population.Output;
import jcgp.backend.resources.Resources;

/**
 * Probabilistic mutator
 * <br><br>
 * This operator iterates through every mutable gene in the chromosome and 
 * decides whether to mutate each of them individually. 
 * The decision is made based on the difference between the mutation probability
 * and a randomly generated double between 0 and 100.
 * 
 * 
 * @see Mutator
 * @author Eduardo Pedroni
 *
 */
public class ProbabilisticMutator extends Mutator {

	private DoubleParameter mutationProbability;
	private BooleanParameter report;
		
	/**
	 * Creates a new instance of ProbabilisticMutator. 
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public ProbabilisticMutator(Resources resources) {
		super(resources);
		
		mutationProbability = new DoubleParameter(10, "Mutation probability", false, false) {
			@Override
			public void validate(Number newValue) {
				if (newValue.doubleValue() <= 0 || newValue.doubleValue() > 100) {
					status = ParameterStatus.INVALID;
					status.setDetails("Mutation rate must be > 0 and <= 100");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		report = new BooleanParameter(false, "Report");
		
		setName("Probabilisic mutation");
		registerParameters(mutationProbability, report);
	}

	@Override
	public void mutate(Chromosome chromosome) {
		if (report.get()) getResources().reportln("[Mutator] Starting mutations");
		
		// go through nodes - [rows][columns]
		for (int r = 0; r < getResources().rows(); r++) {
			for (int c = 0; c < getResources().columns(); c++) {
				// go through all connections
				for (int a = 0; a < getResources().arity(); a++) {
					if (mutateGene()) {
						Node n = chromosome.getNode(r, c);
						
						if (report.get()) getResources().report("[Mutator] Mutating " + n +
								", changed connection " + a + " from " + n.getConnection(a) + " ");
						
						n.setConnection(a, chromosome.getRandomConnection(c));
						
						if (report.get()) getResources().reportln("to " + n.getConnection(a));
						
					}
				}
				// deal with node function next
				if (mutateGene()) {
					Node n = chromosome.getNode(r, c);
					if (report.get()) getResources().report("[Mutator] Mutating " + n +
							", changed function from " + n.getFunction());
					
					n.setFunction(getResources().getRandomFunction());
					
					if (report.get()) getResources().reportln(" to " + n.getFunction());
				}
			}
		}
		// finally, mutate outputs
		for (int o = 0; o < getResources().outputs(); o++) {
			if (mutateGene()) {
				Output out = chromosome.getOutput(o);
				
				if (report.get()) getResources().report("[Mutator] Mutating " + out +
						", changed source from " + out.getSource());
				
				out.setSource(chromosome.getRandomConnection());
				
				if (report.get()) getResources().reportln("to " + out.getSource());
			}
		}
		
		if (report.get()) getResources().reportln("[Mutator] Mutation finished");

	}
	
	/**
	 * This method provides a shorthand to decide whether a mutation should occur or not.
	 * A random double is generated in the range 0 <= x < 100 and compared with the
	 * mutation probability parameter. If the generated number is less than the mutation
	 * probability, this returns true meaning a mutation should occur.
	 * 
	 * @return true if a mutation should be performed, false if otherwise.
	 */
	private boolean mutateGene() {
		return getResources().getRandomDouble(100) < mutationProbability.get();
	}
}
