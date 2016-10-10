package jcgp.backend.modules.problem;

import jcgp.backend.function.SymbolicRegressionFunctions;
import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.DoubleParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * Symbolic regression functions
 * <br><br>
 * Using this problem type, regression problems can be solved.
 * {@code parseData()} must be used to load the desired function
 * data in the standard CGP .dat format.
 * <br><br>
 * This problem uses quite a few parameters:
 * <ul>
 * <li>Error threshold: the maximum difference allowed between an
 * evolved output and the equivalent output from the problem data.
 * Outputs within the error threshold will be considered correct.
 * This is only used if hits is enabled.</li>
 * <li>Perfection threshold: if the fitness is calculated without
 * using the hits method, it is a decimal value. A solution is
 * considered perfect when the difference between its fitness and
 * the maximum possible fitness is within the perfection threshold.</li>
 * <li>Hits-based fitness: increment the fitness by 1 whenever the
 * chromosome output is within the error threshold.</li></ul>
 * 
 * 
 * @see SymbolicRegressionFunctions
 * @author Eduardo Pedroni
 *
 */
public class SymbolicRegressionProblem extends TestCaseProblem<Double> {
	
	private DoubleParameter errorThreshold, perfectionThreshold;
	private BooleanParameter hitsBasedFitness;
		
	/**
	 * Creates a new instance of SymbolicRegressionProblem.
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public SymbolicRegressionProblem(Resources resources) {
		super(resources);
		setFunctionSet(new SymbolicRegressionFunctions());
		setName("Symbolic regression");
		setFileExtension(".dat");
		
		errorThreshold = new DoubleParameter(0.01, "Error threshold") {
			@Override
			public void validate(Number newValue) {
				if (newValue.doubleValue() < 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Error threshold must be a positive value.");
				} else if (newValue.doubleValue() == 0) {
					status = ParameterStatus.WARNING;
					status.setDetails("An error threshold of 0 is very rigorous and difficult to achieve.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		perfectionThreshold = new DoubleParameter(0.000001, "Perfection threshold") {
			@Override
			public void validate(Number newValue) {
				if (newValue.doubleValue() < 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Perfection threshold must be a positive value.");
				} else if (newValue.doubleValue() == 0) {
					status = ParameterStatus.WARNING;
					status.setDetails("A perfection threshold of 0 is very rigorous and difficult to achieve.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		hitsBasedFitness = new BooleanParameter(false, "Hits-based fitness");
		
		registerParameters(errorThreshold, perfectionThreshold, hitsBasedFitness);
	}
	
	@Override
	public void evaluate(Population population) {
		// for every chromosome in the population
		for (int i = 0; i < getResources().populationSize(); i++) {
			// assume an initial fitness of 0
			double fitness = 0;
			// for each test case
			for (int t = 0; t < testCases.size(); t++) {
				population.get(i).setInputs((Object[]) testCases.get(t).getInputs());
				// check each output
				for (int o = 0; o < getResources().outputs(); o++) {
					Double cgpValue = (Double) population.get(i).getOutput(o).calculate();
					Double dataValue = testCases.get(t).getOutputs()[o];
					if (hitsBasedFitness.get()) {
						if (Math.abs(cgpValue - dataValue) <= errorThreshold.get()) {
							fitness++;
						}
					} else {
						fitness += 1 - Math.abs(cgpValue - dataValue);
					}
				}
			}
			// assign the resulting fitness to the respective individual
			population.get(i).setFitness(fitness);
		}
	}
	
	@Override
	public TestCase<Double> parseTestCase(String[] inputs, String[] outputs) {
		// cast the test case values to UnsignedInteger
		Double[] inputCases = new Double[inputs.length];
		Double[] outputCases = new Double[outputs.length];
		for (int i = 0; i < inputCases.length; i++) {
			inputCases[i] = Double.parseDouble(inputs[i]);
		}
		for (int o = 0; o < outputCases.length; o++) {
			outputCases[o] = Double.parseDouble(outputs[o]);
		}
		
		return new TestCase<Double>(inputCases, outputCases);
	}

	@Override
	public int hasPerfectSolution(Population population) {
		// higher fitness is better
		for (int i = 0; i < getResources().populationSize(); i++) {
			if (population.get(i).getFitness() >= maxFitness.get() - perfectionThreshold.get()) {
				return i;
			}
		}
		return -1;
	}
}
