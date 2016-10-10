package jcgp.backend.resources;

import java.util.Random;

import jcgp.backend.function.Function;
import jcgp.backend.function.FunctionSet;
import jcgp.backend.modules.problem.BestFitness;
import jcgp.backend.parameters.IntegerParameter;

/**
 * 
 * Encapsulates all of the resources based on which the program operates.
 * Each instance of JCGP contains a single instance of {@code Resources}.
 * <br><br>
 * The experiment's {@code Resources} object is passed to modules as the program operates, and
 * the actual parameter values can be obtained using getter methods. Note that, for code brevity,
 * this class's getters do not start with the word "get". For instance, to get the number of rows, 
 * one would use {@code rows()} instead of {@code getRows()} which doesn't exist.
 * The fitness orientation of the problem being solved can also be retrieved using {@code fitnessOrientation()}.
 * Evolutionary strategies will typically use this to perform selection.
 * <br><br>
 * In addition to parameters, this class also offers utility methods. Any necessary random numbers
 * should be obtained using {@code getRandomInt()} and {@code getRandomDouble()} as these methods
 * use a particular {@code Random} object guaranteed to generate random numbers based on the seed 
 * parameter. Functions from the selected function set can be obtained through this class as well.
 * Finally, printing to the console should be done via the resources using the report and print
 * methods, so that these prints also get sent to the GUI console (if one is present).
 * 
 * @see jcgp.backend.parameters.Parameter
 * @author Eduardo Pedroni
 *
 */
public class Resources {
	protected IntegerParameter rows, columns, inputs, outputs, populationSize,
			levelsBack, currentGeneration, generations, currentRun, runs,
			arity, seed, reportInterval;

	protected Random numberGenerator = new Random();
	protected FunctionSet functionSet;
	
	protected Console console;
	
	protected BestFitness fitnessOrientation;
	
	/**
	 * @return the number of rows.
	 */
	public int rows() {
		return rows.get();
	}

	/**
	 * @return the number of columns.
	 */
	public int columns() {
		return columns.get();
	}

	/**
	 * @return the number of inputs.
	 */
	public int inputs() {
		return inputs.get();
	}

	/**
	 * @return the number of outputs.
	 */
	public int outputs() {
		return outputs.get();
	}

	/**
	 * @return the population size.
	 */
	public int populationSize() {
		return populationSize.get();
	}

	/**
	 * @return the levels back value.
	 */
	public int levelsBack() {
		return levelsBack.get();
	}

	/**
	 * @return the total number of nodes.
	 */
	public int nodes() {
		return columns.get() * rows.get();
	}

	/**
	 * @return the current generation.
	 */
	public int currentGeneration() {
		return currentGeneration.get();
	}

	/**
	 * @return the total number of generations.
	 */
	public int generations() {
		return generations.get();
	}

	/**
	 * @return the current run.
	 */
	public int currentRun() {
		return currentRun.get();
	}

	/**
	 * @return the total number of runs.
	 */
	public int runs() {
		return runs.get();
	}

	/**
	 * @return the maximum arity out of the function set.
	 */
	public int arity() {
		return arity.get();
	}

	/**
	 * @return the random seed being used.
	 */
	public int seed() {
		return seed.get();
	}

	/**
	 * @return the report interval.
	 */
	public int reportInterval() {
		return reportInterval.get();
	}
	
	/**
	 * @return the fitness orientation.
	 */
	public BestFitness fitnessOrientation() {
		return fitnessOrientation;
	}
	
	/*
	 * Utility functions
	 */
	/**
	 * Gets the next random integer using the experiment's random
	 * number generator. The integer returned will be between 0 (inclusive)
	 * and limit (exclusive).
	 * 
	 * @param limit the limit value.
	 * @return a random integer between 0 and limit.
	 */
	public int getRandomInt(int limit) {
		return numberGenerator.nextInt(limit);
	}
	
	/**
	 * Gets the next random double using the experiment's random
	 * number generator. The double returned will be between 0 (inclusive)
	 * and limit (exclusive).
	 * 
	 * @param limit the limit value.
	 * @return a random double between 0 and limit.
	 */
	public double getRandomDouble(int limit) {
		return numberGenerator.nextDouble() * limit;
	}
	
	/**
	 * Gets the next random integer using the experiment's random
	 * number generator. The integer returned will be between 0 (inclusive)
	 * and 1 (exclusive).
	 * 
	 * @return a random integer between 0 and 1.
	 */
	public double getRandomDouble() {
		return numberGenerator.nextDouble();
	}
	
	/*
	 * FunctionSet functions
	 */
	/**
	 * Gets a random allowed function from the problem function set.
	 * This function uses {@code getRandomInt()} to choose the random
	 * function.
	 * 
	 * @return a random allowed function.
	 */
	public Function getRandomFunction() {
		Function f = functionSet.getAllowedFunction(numberGenerator.nextInt(functionSet.getAllowedFunctionCount()));
		return f;
	}

	/**
	 * Gets the indexed function out of the
	 * complete set of functions.
	 * 
	 * @param index the function to return.
	 * @return the indexed function.
	 */
	public Function getFunction(int index) {
		return functionSet.getFunction(index);
	}
	
	/**
	 * @return the problem's function set.
	 */
	public FunctionSet getFunctionSet() {
		return functionSet;
	}
	
	/**
	 * Returns the index of a specified function. If the function is not found,
	 * -1 is returned.
	 * 
	 * @param function the function with unknown index.
	 * @return the index of the function, or -1 if it was not found.
	 */
	public int getFunctionIndex(Function function) {
		for (int i = 0; i < functionSet.getTotalFunctionCount(); i++) {
			if (function == functionSet.getFunction(i)) {
				return i;
			}
		}
		// not found, default to -1
		return -1;
	}
	
	/*
	 * Console functionality
	 * These are affected by parameter report interval
	 */
	/**
	 * Prints a message to the consoles taking into account the
	 * report interval parameter. If no reports are allowed in
	 * the current generation, this does nothing.
	 * <br>
	 * This method automatically appends a line break to the message
	 * being printed.
	 * 
	 * @param message the message to print.
	 */
	public void reportln(String message) {
		if (reportInterval.get() > 0) {
			if (currentGeneration.get() % reportInterval.get() == 0) {
				System.out.println(message);
				if (console != null) {
					console.println(message);
				}
			}
		}
	}
	
	/**
	 * Prints a message to the consoles taking into account the
	 * report interval parameter. If no reports are allowed in
	 * the current generation, this does nothing.
	 * <br>
	 * This method does not append a line break to the message
	 * being printed.
	 * 
	 * @param message the message to print.
	 */
	public void report(String message) {
		if (reportInterval.get() > 0) {
			if (currentGeneration.get() % reportInterval.get() == 0) {
				System.out.print(message);
				if (console != null) {
					console.print(message);
				}
			}
		}
	}
	
	/*
	 * Console functionality
	 * These are not affected by parameter report interval
	 */
	/**
	 * Prints a message to the consoles ignoring
	 * report interval. In other words, messages printed 
	 * using this method will always appear (though the
	 * GUI console will still need to be flushed).
	 * <br>
	 * This method automatically appends a line break to the message
	 * being printed.
	 * 
	 * @param message the message to print.
	 */
	public void println(String message) {
		System.out.println(message);
		if (console != null) {
			console.println(message);
		}
	}
	
	/**
	 * Prints a message to the consoles ignoring
	 * report interval. In other words, messages printed 
	 * using this method will always appear (though the
	 * GUI console will still need to be flushed).
	 * <br>
	 * This method does not append a line break to the message
	 * being printed.
	 * 
	 * @param message the message to print.
	 */
	public void print(String message) {
		System.out.print(message);
		if (console != null) {
			console.print(message);
		}
	}
}