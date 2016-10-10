package jcgp.backend.resources;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import jcgp.backend.function.FunctionSet;
import jcgp.backend.modules.problem.BestFitness;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.backend.parameters.monitors.IntegerMonitor;

/**
 * 
 * This subclass of Resources allows modifications to be made.
 * A read-only cast of this class is passed to modules for safety, 
 * and only classes with access to a JCGP instance may modify
 * the resources.
 * 
 * @author Eduardo Pedroni
 *
 */
public class ModifiableResources extends Resources {

	/**
	 * Creates an instance of this class and initialises
	 * all base parameters to default values. See
	 * {@code createBaseParameters} for the exact parameter
	 * initialisation.
	 */
	public ModifiableResources() {
		createBaseParameters();
	}
	
	/**
	 * @param rows the number of rows to set.
	 */
	public void setRows(int rows) {
		this.rows.set(rows);
	}

	/**
	 * @param columns the number of columns to set.
	 */
	public void setColumns(int columns) {
		this.columns.set(columns);
	}

	/**
	 * @param inputs the number of inputs to set.
	 */
	public void setInputs(int inputs) {
		this.inputs.set(inputs);
	}

	/**
	 * @param outputs the number of outputs to set.
	 */
	public void setOutputs(int outputs) {
		this.outputs.set(outputs);
	}

	/**
	 * @param populationSize the population size to set.
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize.set(populationSize);
	}

	/**
	 * @param levelsBack the levels back to set.
	 */
	public void setLevelsBack(int levelsBack) {
		this.levelsBack.set(levelsBack);
	}

	/**
	 * @param currentGeneration the current generation to set.
	 */
	public void setCurrentGeneration(int currentGeneration) {
		this.currentGeneration.set(currentGeneration);
	}
	
	/**
	 * Adds 1 to the current generation.
	 */
	public void incrementGeneration() {
		this.currentGeneration.set(currentGeneration.get() + 1);
	}

	/**
	 * @param generations the total generations to set.
	 */
	public void setGenerations(int generations) {
		this.generations.set(generations);
	}
	
	/**
	 * @param currentRun the current run to set.
	 */
	public void setCurrentRun(int currentRun) {
		this.currentRun.set(currentRun);
	}

	/**
	 * Adds 1 to the current generation.
	 */
	public void incrementRun() {
		currentRun.set(currentRun.get() + 1);
	}
	
	/**
	 * @param runs the total runs to set.
	 */
	public void setRuns(int runs) {
		this.runs.set(runs);
	}

	/**
	 * This is called automatically by the experiment when the arity changes.
	 * 
	 * @param arity the arity to set.
	 */
	public void setArity(int arity) {
		this.arity.set(arity);
	}

	/**
	 * @param seed the seed to set.
	 */
	public void setSeed(long seed) {
		this.seed.set(seed);
	}

	/**
	 * @param report the report interval to set.
	 */
	public void setReportInterval(int report) {
		this.reportInterval.set(report);
	}
	
	/**
	 * @param newOrientation the new orientation to set.
	 */
	public void setFitnessOrientation(BestFitness newOrientation) {
		this.fitnessOrientation = newOrientation;
	}
	
	/**
	 * @return the rows parameter.
	 */
	public IntegerParameter getRowsParameter() {
		return rows;
	}

	/**
	 * @return the columns parameter.
	 */
	public IntegerParameter getColumnsParameter() {
		return columns;
	}

	/**
	 * @return the inputs parameter.
	 */
	public IntegerParameter getInputsParameter() {
		return inputs;
	}

	/**
	 * @return the outputs parameter.
	 */
	public IntegerParameter getOutputsParameter() {
		return outputs;
	}

	/**
	 * @return the population size parameter.
	 */
	public IntegerParameter getPopulationSizeParameter() {
		return populationSize;
	}

	/**
	 * @return the levels back parameter.
	 */
	public IntegerParameter getLevelsBackParameter() {
		return levelsBack;
	}

	/**
	 * @return the current generation parameter.
	 */
	public IntegerParameter getCurrentGenerationParameter() {
		return currentGeneration;
	}

	/**
	 * @return the total generations parameter.
	 */
	public IntegerParameter getGenerationsParameter() {
		return generations;
	}

	/**
	 * @return the current run parameter.
	 */
	public IntegerParameter getCurrentRunParameter() {
		return currentRun;
	}

	/**
	 * @return the total runs parameter.
	 */
	public IntegerParameter getRunsParameter() {
		return runs;
	}

	/**
	 * @return the arity parameter.
	 */
	public IntegerParameter getArityParameter() {
		return arity;
	}

	/**
	 * @return the seed parameter.
	 */
	public IntegerParameter getSeedParameter() {
		return seed;
	}

	/**
	 * @return the report interval parameter.
	 */
	public IntegerParameter getReportIntervalParameter() {
		return reportInterval;
	}

	/**
	 * Update the current function set.
	 * 
	 * @param functionSet the new function set.
	 */
	public void setFunctionSet(FunctionSet functionSet) {
		this.functionSet = functionSet;
		setArity(functionSet.getMaxArity());
	}
	
	/**
	 * This can be set to null if no extra console is desired.
	 * 
	 * @param console the extra console for the experiment to use.
	 */
	public void setConsole(Console console) {
		this.console = console;
	}
	
	/**
	 * For internal use only, this initialises all base parameters to default values.
	 */
	private void createBaseParameters() {
		rows = new IntegerParameter(5, "Rows", false, true) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Chromosome must have at least 1 row.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		columns = new IntegerParameter(5, "Columns", false, true) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Chromosome must have at least 1 column.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		inputs = new IntegerMonitor(3, "Inputs");
		
		outputs = new IntegerMonitor(3, "Outputs");
		
		populationSize = new IntegerParameter(5, "Population", false, true) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Population size must be at least 1.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		levelsBack = new IntegerParameter(2, "Levels back", false, true) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Levels back must be at least 1.");
				} else if (newValue.intValue() > columns()) {
					status = ParameterStatus.INVALID;
					status.setDetails("Levels back must be less than or equal to the number of columns.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		generations = new IntegerParameter(1000000, "Generations") {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Number of generations must be greater than 0.");
				} else if (newValue.intValue() < currentGeneration.get()) {
					status = ParameterStatus.WARNING_RESET;
					status.setDetails("Setting generations to less than the current generation will cause the experiment to restart.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		currentGeneration = new IntegerMonitor(1, "Generation");

		runs = new IntegerParameter(5, "Runs") {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() <= 0) {
					status = ParameterStatus.INVALID;
					status.setDetails("Number of runs must be greater than 0.");
				} else if (newValue.intValue() < currentRun.get()) {
					status = ParameterStatus.WARNING_RESET;
					status.setDetails("Setting runs to less than the current run will cause the experiment to restart.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
		
		currentRun = new IntegerMonitor(1, "Run");

		arity = new IntegerMonitor(0, "Max arity");
		
		seed = new IntegerParameter(1234, "Seed", false, true) {
			@Override
			public void validate(Number newValue) {
				status = ParameterStatus.VALID;
			}
		};
		seed.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				numberGenerator.setSeed(newValue.longValue());
			}
		});
		numberGenerator.setSeed(seed.get());
		
		reportInterval = new IntegerParameter(1, "Report interval", false, false) {
			@Override
			public void validate(Number newValue) {
				if (newValue.intValue() > generations.get()) {
					status = ParameterStatus.WARNING;
					status.setDetails("No reports will be printed.");
				} else {
					status = ParameterStatus.VALID;
				}
			}
		};
	}
}
