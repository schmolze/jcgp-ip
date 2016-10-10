package jcgp.backend.modules.problem;

import jcgp.backend.function.PolynomialFunctions;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * Polynomial solver problem
 * <br><br>
 * Using this problem type, regression problems can be solved.
 * {@code parseData()} must be used to load the desired function
 * data in the standard CGP .dat format.
 * <br><br>
 * 
 * @see PolynomialFunctions
 * @author Eduardo Pedroni
 *
 */
public class PolynomialProblem extends TestCaseProblem<Integer> {
	
		
	/**
	 * Creates a new instance of PolynomialProblem.
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public PolynomialProblem(Resources resources) {
		
		super(resources);
		setFunctionSet(new PolynomialFunctions());
		setName("Polynomial solver");
		setFileExtension(".txt");
		
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
					Integer cgpValue = (Integer) population.get(i).getOutput(o).calculate();
					Integer dataValue = testCases.get(t).getOutputs()[o];
				
					fitness += 1 - Math.abs(cgpValue - dataValue);
				
				}
			}
			// assign the resulting fitness to the respective individual
			population.get(i).setFitness(fitness);
		}
	}
	
	@Override
	public TestCase<Integer> parseTestCase(String[] inputs, String[] outputs) {
		// cast the test case values to UnsignedInteger
		Integer[] inputCases = new Integer[inputs.length];
		Integer[] outputCases = new Integer[outputs.length];
		for (int i = 0; i < inputCases.length; i++) {
			inputCases[i] = Integer.parseInt(inputs[i]);
		}
		for (int o = 0; o < outputCases.length; o++) {
			outputCases[o] = Integer.parseInt(outputs[o]);
		}
		
		return new TestCase<Integer>(inputCases, outputCases);
	}

	@Override
	public int hasPerfectSolution(Population population) {
		// higher fitness is better
		for (int i = 0; i < getResources().populationSize(); i++) {
			if (population.get(i).getFitness() >= maxFitness.get()) {
				return i;
			}
		}
		return -1;
	}
}
