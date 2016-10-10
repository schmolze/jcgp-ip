package jcgp;

import java.io.File;

import jcgp.backend.modules.es.EvolutionaryStrategy;
import jcgp.backend.modules.es.MuPlusLambda;
import jcgp.backend.modules.es.TournamentSelection;
import jcgp.backend.modules.mutator.FixedPointMutator;
import jcgp.backend.modules.mutator.Mutator;
import jcgp.backend.modules.mutator.PercentPointMutator;
import jcgp.backend.modules.mutator.ProbabilisticMutator;
import jcgp.backend.modules.problem.DigitalCircuitProblem;
import jcgp.backend.modules.problem.Problem;
import jcgp.backend.modules.problem.SymbolicRegressionProblem;
import jcgp.backend.modules.problem.TestCaseProblem;
import jcgp.backend.modules.problem.PolynomialProblem;
import jcgp.backend.parsers.ChromosomeParser;
import jcgp.backend.parsers.FunctionParser;
import jcgp.backend.parsers.ParameterParser;
import jcgp.backend.parsers.TestCaseParser;
import jcgp.backend.population.Population;
import jcgp.backend.resources.Console;
import jcgp.backend.resources.ModifiableResources;
import jcgp.backend.statistics.StatisticsLogger;

/**
 * 
 * Top-level JCGP class. This class is the entry point for a CGP experiment. 
 * <br><br>
 * An instance of JCGP encapsulates the entire experiment. It contains a {@code Resources}
 * object which can be retrieved via a getter. Modules can be selected using their
 * respective setters.
 * <br><br>
 * The flow of the experiment is controlled using {@code start()}, {@code nextGeneration()}
 * and {@code reset()}. Files can be loaded with their respective load methods and
 * chromosome configurations can be saved with {@code saveChromosome()}. 
 * <br><br>
 * JCGP supports an extra console in addition to {@code System.console()}, so that messages
 * can also be printed to a GUI, for example. This extra console can be set with {@code setConsole()}, 
 * and must implement jcgp.resources.Console.
 * 
 * @author Eduardo Pedroni
 */
public class JCGP {
	
	private final ModifiableResources resources = new ModifiableResources();
	
	/*
	 * The following arrays contain all available modules. These collections are read by the GUI
	 * when generating menus.
	 * 
	 * Each array is accompanied by a field which contains a reference to the currently selected
	 * module, index 0 by default.
	 */
	// mutators
	private Mutator[] mutators = new Mutator[] {
			new PercentPointMutator(resources),
			new FixedPointMutator(resources),
			new ProbabilisticMutator(resources)
	};
	private Mutator mutator;
	
	// evolutionary algorithms
	private EvolutionaryStrategy[] evolutionaryStrategies = new EvolutionaryStrategy[] {
			new MuPlusLambda(resources),
			new TournamentSelection(resources)
	};
	private EvolutionaryStrategy evolutionaryStrategy;
	
	// problem types
	private Problem[] problems = new Problem[] {
			new DigitalCircuitProblem(resources),
			new SymbolicRegressionProblem(resources),
			new PolynomialProblem(resources)
	};
	private Problem problem;

	private Population population;

	private StatisticsLogger statistics = new StatisticsLogger();
	
	// these record the best results found in the run, in case the runs ends before a perfect solution is found
	private int lastImprovementGeneration = 0, activeNodes = 0;
	private double bestFitnessFound = 0;
	
	private boolean finished = false;
	
	/**
	 * JCGP main method, this is used to execute JCGP from the command line. 
	 * <br><br>
	 * In this case the program works in the same way as the classic CGP implementation,
	 * requiring a .par file and an optional problem data file. As in the traditional CGP
	 * implementation, the program must be compiled with the right problem type selected.
	 * 
	 * @param args one or more files needed to perform the experiment.
	 */
	public static void main(String... args) {
		// check that files have been provided
		if (args.length < 1) {
			System.err.println("JCGP requires at least a .par file.");
			System.exit(1);
		}
		// prepare experiment
		JCGP jcgp = new JCGP();
		jcgp.loadParameters(new File(args[0]));
		
		if (jcgp.getProblem() instanceof TestCaseProblem) {
			TestCaseParser.parse(new File(args[2]), (TestCaseProblem<?>) jcgp.getProblem(), jcgp.getResources());
		}
		// kick it off
		jcgp.start();
	}
	
	
	/**
	 * Creates a new instance of JCGP.
	 */
	public JCGP() {
		// initialise modules
		setEvolutionaryStrategy(0);
		setMutator(0);
		setProblem(0);

		// create a new population
		population = new Population(resources);
	}
	

	/**
	 * Returns a reference to the {@code ModifiableResources} used by the
	 * experiment. <br>
	 * Use this with care, since changing experiment parameters may
	 * have unintended effects if not done properly. 
	 * 
	 * @return a reference to the experiment's resources.
	 */
	public ModifiableResources getResources() {
		return resources;
	}
	
	/**
	 * @return a reference to the experiment's population.
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * @return a complete list of the experiment's mutators.
	 */
	public Mutator[] getMutators() {
		return mutators;
	}


	/**
	 * @return the currently selected mutator.
	 */
	public Mutator getMutator() {
		return mutator;
	}


	/**
	 * @return a complete list of the experiment's evolutionary strategies.
	 */
	public EvolutionaryStrategy[] getEvolutionaryStrategies() {
		return evolutionaryStrategies;
	}


	/**
	 * @return the currently selected evolutionary strategy.
	 */
	public EvolutionaryStrategy getEvolutionaryStrategy() {
		return evolutionaryStrategy;
	}


	/**
	 * @return a complete list of the experiment's problem types.
	 */
	public Problem[] getProblems() {
		return problems;
	}


	/**
	 * @return the currently selected problem type.
	 */
	public Problem getProblem() {
		return problem;
	}
	
	
	/**
	 * @param index the index of the desired mutator.
	 */
	public void setMutator(int index) {
		this.mutator = mutators[index];
		resources.println("[CGP] Mutator selected: " + mutator.toString());
	}


	/**
	 * @param index the index of the desired evolutionary strategy.
	 */
	public void setEvolutionaryStrategy(int index) {	
		this.evolutionaryStrategy = evolutionaryStrategies[index];
		resources.println("[CGP] Evolutionary strategy selected: " + evolutionaryStrategy.toString());
	}


	/**
	 * @param index the index of the desired problem type.
	 */
	public void setProblem(int index) {
		this.problem = problems[index];
		resources.setFunctionSet(problem.getFunctionSet());
		resources.setFitnessOrientation(problem.getFitnessOrientation());
	}
	
	/**
	 * Performs one full generational cycle. More specifically, 
	 * this method evaluates the current population using the 
	 * selected problem, and checks whether a solution has been found.
	 * <br>
	 * If the experiment is to continue, a new generation is created 
	 * using the selected evolutionary strategy and mutator.
	 * <br><br>
	 * This method also deals with ending runs, in other words, 
	 * a new population is created at the end of each run automatically.
	 * When all runs have been performed, this method sets the experiment
	 * finished flag and does nothing until {@code reset()} is called.
	 */
	public void nextGeneration() {
		if (!finished) {
			problem.evaluate(population);

			if (resources.currentGeneration() < resources.generations()) {
				
				// we still have generations left to go
				int perfect = problem.hasPerfectSolution(population);
				if (perfect >= 0) {
					// log results
					statistics.logRun(resources.currentGeneration(), population.get(perfect).getFitness(), population.get(perfect).getActiveNodes().size(), true);
					resetStatisticsValues();
					
					// solution has been found, start next run
					resources.println("[CGP] Solution found: generation " + resources.currentGeneration() + ", chromosome " + perfect + "\n");
					resources.println("[CGP] Printing chromosome...");
					ChromosomeParser.print(population.get(perfect), resources);
					resources.println("[CGP] Printing done. ");
					if (resources.currentRun() < resources.runs()) {
						
						// there are still runs left
						resources.incrementRun();
						resources.setCurrentGeneration(1);
						
						// start a new population
						population.reinitialise();
					} else {
						// no more generations and no more runs, we're done
						printStatistics();
						finished = true;
					}
				} else {
					// solution not found, look for improvement
					int improvement = problem.hasImprovement(population);
					
					if (improvement >= 0) {
						// there has been improvement, print it
						printImprovement(improvement);
						lastImprovementGeneration = resources.currentGeneration();
						bestFitnessFound = population.get(improvement).getFitness();
						activeNodes = population.get(improvement).getActiveNodes().size();
					} else {
						// there has been no improvement, report generation
						reportGeneration();
					}
					resources.incrementGeneration();
					
					// we still have generations left, evolve more!
					evolutionaryStrategy.evolve(population, mutator);
				}
			} else {
				// the run has ended, tell the user and log it 
				resources.println("[CGP] Solution not found, best fitness achieved was "
						+ bestFitnessFound + "\n");
				
				statistics.logRun(lastImprovementGeneration, bestFitnessFound, activeNodes, false);
				resetStatisticsValues();
				
				// check if any more runs must be done
				if (resources.currentRun() < resources.runs()) {
					// the run has ended but there are still runs left
					resources.incrementRun();
					resources.setCurrentGeneration(1);
					
					// start a new population
					population.reinitialise();
				} else {
					// no more generations and no more runs, we're done
					printStatistics();
					finished = true;
				}
			}
		}
	}
	
	/**
	 * Used internally for printing statistics at the end of the experiment.
	 * This method currently prints the exact same statistics as the ones
	 * provided by the classic CGP implementation.
	 */
	private void printStatistics() {
		resources.println("[CGP] Experiment finished");
		resources.println("[CGP] Average fitness: " + statistics.getAverageFitness());
		resources.println("[CGP] Std dev fitness: " + statistics.getAverageFitnessStdDev());
		
		resources.println("[CGP] Average number of active nodes: " + statistics.getAverageActiveNodes());
		resources.println("[CGP] Std dev number of active nodes: " + statistics.getAverageActiveNodesStdDev());
		
		resources.println("[CGP] Average best generation: " + statistics.getAverageGenerations());
		resources.println("[CGP] Std dev best generation: " + statistics.getAverageGenerationsStdDev());
		
		resources.println("[CGP] Highest fitness of all runs: " + statistics.getHighestFitness());
		resources.println("[CGP] Lowest fitness of all runs: " + statistics.getLowestFitness());
		
		resources.println("[CGP] Perfect solutions: " + statistics.getSuccessfulRuns());
		resources.println("[CGP] Success rate: " + (statistics.getSuccessRate() * 100) + "%");
		
		resources.println("[CGP] Average generations for perfect solutions only: " + statistics.getAverageSuccessfulGenerations());
		resources.println("[CGP] Std dev generations for perfect solutions only: " + statistics.getAverageSuccessfulGenerationsStdDev());
	}
	
	/**
	 * Used internally for reporting improvement, which happens independently of
	 * the report interval parameter.
	 */
	private void printImprovement(int chromosome) {
		resources.println("[CGP] Generation: " + resources.currentGeneration() + ", fittest chromosome (" 
				+ chromosome + ") has fitness: " + population.get(chromosome).getFitness());
	}

	/**
	 * Used internally for reporting generation information, which is affected
	 * by the report interval parameter.
	 */
	private void reportGeneration() {
		resources.reportln("[CGP] Generation: " + resources.currentGeneration() + ", best fitness: " 
				+ problem.getBestFitness());
	}

	/**
	 * This method calls {@code nextGeneration()} in a loop
	 * until the experiment is flagged as finished. This is
	 * performed on the same thread of execution, so this
	 * method will most likely block for a significant amount
	 * of time (problem-dependent, but anywhere from seconds to days).
	 * <br>
	 * Once the experiment is finished, calling this method does
	 * nothing until {@code reset()} is called.
	 */
	public void start() {
		if (!finished) {
			while (!finished) {
				nextGeneration();
			}
		}
	}
	
	/**
	 * Resets the experiment.
	 * <br>
	 * More specifically: this creates a new population, resets
	 * the current generation and run parameters to 1 and prints
	 * a complete list of the experiment's parameters.
	 * 
	 */
	public void reset() {
		statistics = new StatisticsLogger();
		resources.setArity(problem.getFunctionSet().getMaxArity());
		if (resources.arity() < 1) {
			resources.println("[CGP] Error: arity is smaller than 1. Check that at least one function is enabled");
			return;
		}
		finished = false;
		population = new Population(resources);
		resetStatisticsValues();
		resources.setCurrentGeneration(1);
		resources.setCurrentRun(1);
		resources.println("*********************************************************");
		resources.println("[CGP] New experiment: " + problem.toString());
		resources.println("[CGP] Rows: " + resources.rows());
		resources.println("[CGP] Columns: " + resources.columns());
		resources.println("[CGP] Levels back: " + resources.levelsBack());
		resources.println("[CGP] Population size: " + resources.populationSize());
		resources.println("[CGP] Total generations: " + resources.generations());
		resources.println("[CGP] Total runs: " + resources.runs());
		resources.println("[CGP] Report interval: " + resources.reportInterval());
		resources.println("[CGP] Seed: " + resources.seed());
		resources.println("");
		resources.println("[CGP] Evolutionary strategy: " + evolutionaryStrategy.toString());
		resources.println("[CGP] Mutator: " + mutator.toString());
	}

	/**
	 * Internally used to reset the fields used
	 * for logging results statistics. 
	 */
	private void resetStatisticsValues() {
		problem.reset();
		lastImprovementGeneration = 0;
		bestFitnessFound = 0;
		activeNodes = 0;
	}
	
	/**
	 * When given a .par file, this method loads the parameters into the
	 * experiment's resources. This causes an experiment-wide reset.
	 * 
	 * @param file the file to parse.
	 */
	public void loadParameters(File file) {
		ParameterParser.parse(file, resources);
		FunctionParser.parse(file, problem.getFunctionSet(), resources);
		reset();
	}
	
	/**
	 * Parses a problem data file. This is problem-dependent, not
	 * all problems require a data file. 
	 * 
	 * @param file the file to parse.
	 */
	public void loadProblemData(File file) {
		problem.parseProblemData(file, resources);
		reset();
	}
	
	/**
	 * Loads a chromosome from the given file into
	 * the specified population index.
	 * 
	 * @param file the chromosome to parse.
	 * @param chromosomeIndex the population index into which to parse.
	 */
	public void loadChromosome(File file, int chromosomeIndex) {
		ChromosomeParser.parse(file, population.get(chromosomeIndex), resources);
	}
	
	/**
	 * Saves a copy of the specified chromosome 
	 * into the given file.
	 * 
	 * @param file the target file.
	 * @param chromosomeIndex the index of the chromosome to save.
	 */
	public void saveChromosome(File file, int chromosomeIndex) {
		ChromosomeParser.save(file, population.get(chromosomeIndex), resources);
	}

	/**
	 * Returns the experiment's status. When finished, the only
	 * way to continue is by calling {@code reset()}.
	 * 
	 * @return true if the experiment is finished.
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Sets an extra console. The entire JCGP library prints
	 * messages to {@code System.console()} but also to an 
	 * additional console, if one is defined. This is used so 
	 * that messages are printed on a user interface as well, 
	 * or written directly to a file, for example.
	 * 
	 * @param console the extra console to be used.
	 */
	public void setConsole(Console console) {
		resources.setConsole(console);
	}
}
