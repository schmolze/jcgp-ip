package jcgp.backend.modules.problem;

import jcgp.backend.function.DigitalCircuitFunctions;
import jcgp.backend.function.UnsignedInteger;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Resources;

/**
 * Digital circuit problem
 * <br><br>
 * Using this problem type, digital logic circuits can be evolved.
 * {@code parseData()} must be used to load the desired circuit
 * truth table in the standard CGP .plu format. 
 * 
 * @see DigitalCircuitFunctions
 * @author Eduardo Pedroni
 *
 */
public class DigitalCircuitProblem extends TestCaseProblem<UnsignedInteger> {

	/**
	 * Construct a new instance of DigitalCircuitProblem.
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	public DigitalCircuitProblem(Resources resources) {
		super(resources);
		setFunctionSet(new DigitalCircuitFunctions());
		setName("Digital circuit");
		setFileExtension(".plu");
	}

	@Override
	public void evaluate(Population population) {
		// for every chromosome in the population
		for (int i = 0; i < getResources().populationSize(); i++) {
			// assume an initial fitness of 0
			int fitness = 0;

			// iterate over every test case
			for (int t = 0; t < testCases.size(); t++) {
				population.get(i).setInputs((Object[]) testCases.get(t).getInputs());
				// check each output
				for (int o = 0; o < getResources().outputs(); o++) {
					Integer output = ((UnsignedInteger) population.get(i).getOutput(o).calculate()).get();
					Integer matches = ~(output ^ testCases.get(t).getOutputs()[o].get());
					// check only the relevant bits
					int bits;
					if (getResources().inputs() < 5) {
						bits = (int) Math.pow(2.0, (double) getResources().inputs());
					} else {
						bits = 32;
					}
					for (int b = 0; b < bits; b++) {
						fitness += (matches >>> b) & 1;
					}
				}
			}
			// assign the resulting fitness to the respective individual
			population.get(i).setFitness(fitness);
		}
	}

	@Override
	protected double getMaxFitness() {
		// calculate the fitness by looking at inputs, not number of test cases
		double maxFitness = Math.pow(2.0, (double) getResources().inputs()) * getResources().outputs();
		return maxFitness;
	}

	@Override
	public TestCase<UnsignedInteger> parseTestCase(String[] inputs, String[] outputs) {
		// cast the test case values to UnsignedInteger
		UnsignedInteger[] inputCases = new UnsignedInteger[inputs.length];
		UnsignedInteger[] outputCases = new UnsignedInteger[outputs.length];
		for (int i = 0; i < inputCases.length; i++) {
			inputCases[i] = new UnsignedInteger(inputs[i]);
		}
		for (int o = 0; o < outputCases.length; o++) {
			outputCases[o] = new UnsignedInteger(outputs[o]);
		}

		return new TestCase<UnsignedInteger>(inputCases, outputCases);
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
