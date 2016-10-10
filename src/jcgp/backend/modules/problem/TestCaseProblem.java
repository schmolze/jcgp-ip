package jcgp.backend.modules.problem;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jcgp.backend.parsers.TestCaseParser;
import jcgp.backend.population.Population;
import jcgp.backend.resources.ModifiableResources;
import jcgp.backend.resources.Resources;

/**
 * Abstract model for a problem that uses test cases. A test case
 * problem is any problem that compares the chromosome output to
 * an expected output taken from a table of input-output mappings.
 * <br><br>
 * This class defines a basic data type for storing test cases, 
 * TestCase, and provides core functionality to add and manipulate
 * test cases in the problem. A subclass of {@code TestCaseProblem}
 * must simply override {@code parseTestCase()} to convert parsed
 * problem data strings into the required data type (T).
 * 
 * @see Problem
 * @author Eduardo Pedroni
 * @param <T> the data type to be used by the TestCaseProblem.
 */
public abstract class TestCaseProblem<T> extends Problem {
	
	/**
	 * Basic data type for encapsulating test cases, it simply
	 * contains arrays of inputs and outputs and associated getters.
	 * 
	 * @author Eduardo Pedroni
	 * @param <U> the data type of the test case.
	 */
	public static class TestCase<U> {
		private U[] inputs;
		private U[] outputs;
		
		/**
		 * Creates a new test case, inputs and outputs
		 * must be specified upon instantiation.
		 * 
		 * @param inputs the array of inputs.
		 * @param outputs the array of outputs.
		 */
		public TestCase(U[] inputs, U[] outputs) {
			this.inputs = inputs;
			this.outputs = outputs;
		}
		
		/**
		 * @return the complete array of inputs.
		 */
		public U[] getInputs() {
			return inputs;
		}
		
		/**
		 * @return the complete array of outputs.
		 */
		public U[] getOutputs() {
			return outputs;
		}
	}
	
	protected ObservableList<TestCase<T>> testCases;
	
	/**
	 * Creates a new TestCaseProblem object.
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	protected TestCaseProblem(Resources resources) {
		super(resources);
		testCases = FXCollections.observableArrayList();
	}
	
	/**
	 * For internal use only, this method computes and returns the maximum fitness
	 * based on the number of test cases. Subclasses should override this method
	 * as necessary.
	 * 
	 * @return the maximum fitness based on number of test cases.
	 */
	protected double getMaxFitness() {
		int fitness = 0;
		for (TestCase<T> tc : testCases) {
			fitness += tc.getOutputs().length;
		}
		return fitness;
	}
	
	/**
	 * @return a list containing the test cases.
	 */
	public ObservableList<TestCase<T>> getTestCases() {
		return testCases;
	}
	
	/**
	 * This method is used internally by {@code addTestCase()} in order
	 * to appropriately parse strings into the right data type for the 
	 * test cases. Since the data type is problem-dependent, subclasses must 
	 * implement this method. This method must return a built {@code TestCase}
	 * object from the arguments given.
	 * 
	 * @param inputs the inputs represented as strings.
	 * @param outputs the outputs represented as strings.
	 * @return the parsed test case.
	 */
	protected abstract TestCase<T> parseTestCase(String[] inputs, String[] outputs);
	
	/**
	 * Adds test cases to the problem instance as they get parsed from a 
	 * problem data file. This template method uses {@code parseTestCase}, which
	 * must be implemented by subclasses.
	 * 
	 * @param inputs the inputs represented as strings.
	 * @param outputs the outputs represented as strings.
	 */
	public final void addTestCase(String[] inputs, String[] outputs) {
		TestCase<T> testCase = parseTestCase(inputs, outputs);
		
		if (testCase.getInputs().length != getResources().inputs()) {
			throw new IllegalArgumentException("Received test case with " + testCase.getInputs().length + 
					" inputs but need exactly " + getResources().inputs());
		} else if (testCase.getOutputs().length != getResources().outputs()) {
			throw new IllegalArgumentException("Received test case with " + testCase.getOutputs().length + 
					" outputs but need exactly " + getResources().outputs());
		} else {
			this.testCases.add(testCase);
			maxFitness.set(getMaxFitness());
		}
	}

	/**
	 * Remove all test cases.
	 */
	public void clearTestCases() {
		testCases.clear();
		maxFitness.set(getMaxFitness());
	}

	@Override
	public void parseProblemData(File file, ModifiableResources resources) {
		// use standard test case parser for this
		TestCaseParser.parse(file, this, resources);
	}
	
	@Override
	public int hasImprovement(Population population) {
		for (int i = 0; i < getResources().populationSize(); i++) {
			if (getFitnessOrientation() == BestFitness.HIGH) {
				if (population.get(i).getFitness() > bestFitness.get()) {
					bestFitness.set(population.get(i).getFitness());
					return i;
				}
			} else {
				if (population.get(i).getFitness() < bestFitness.get()) {
					bestFitness.set(population.get(i).getFitness());
					return i;
				}
			}
		}
		return -1;
	}
}


