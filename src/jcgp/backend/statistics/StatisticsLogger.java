package jcgp.backend.statistics;

import java.util.ArrayList;

/**
 * This is a utility class for logging experiment statistics when doing multiple runs.
 * <br><br>
 * Information about each run is added via the {@code logRun()} method. The many getters
 * can be used to obtain statistics about the logged runs, such as success rate and average
 * fitness.
 * <br><br>
 * {@code JCGP} uses this class to perform its logging and print out experiment data at the end.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public class StatisticsLogger {

	// this list holds the logged entries
	private ArrayList<RunEntry> runEntries;
	
	/**
	 * Create a new statistics logger, use this when resetting is necessary.
	 */
	public StatisticsLogger() {
		runEntries = new ArrayList<RunEntry>();
	}
	
	/**
	 * Log a new run. Calling any of the statistics getters will
	 * now take this logged run into account as well as all previously
	 * logged runs.
	 * 
	 * @param generation the last generation when improvement occurred.
	 * @param fitness the best fitness achieved in the run.
	 * @param active the number of active nodes in the best chromosome found.
	 * @param successful true if a perfect solution was found, false if otherwise.
	 */
	public void logRun(int generation, double fitness, int active, boolean successful) {
		runEntries.add(new RunEntry(generation, fitness, active, successful));
	}
	
	/**
	 * Averages the best fitness obtained in each run.
	 * 
	 * @return the average fitness.
	 */
	public double getAverageFitness() {
		double average = 0;
		for (RunEntry runEntry : runEntries) {
			average += runEntry.getFitness() / runEntries.size();
		}
		return average;
	}
	
	/**
	 * Calculates the standard deviation of
	 * the best fitness obtained in each run.
	 * 
	 * @return the standard deviation of average fitnesses.
	 */
	public double getAverageFitnessStdDev() {
		double average = getAverageFitness();
		double temp, stdDev = 0;
		for (RunEntry runEntry : runEntries) {
			temp = runEntry.getFitness() - average;
			temp = temp * temp;
			stdDev += temp;
		}
		return stdDev;
	}
	
	/**
	 * Averages the number of active nodes in the 
	 * best chromosomes obtained across all runs.
	 * 
	 * @return the average number of active nodes.
	 */
	public double getAverageActiveNodes() {
		double average = 0;
		for (RunEntry runEntry : runEntries) {
			average += runEntry.getActiveNodes() / runEntries.size();
		}
		return average;
	}
	
	/**
	 * Calculates the standard deviation of
	 * the number of active nodes in the best solution
	 * in each run.
	 * 
	 * @return the standard deviation of active node counts.
	 */
	public double getAverageActiveNodesStdDev() {
		double average = getAverageActiveNodes();
		double temp, stdDev = 0;
		for (RunEntry runEntry : runEntries) {
			temp = runEntry.getActiveNodes() - average;
			temp = temp * temp;
			stdDev += temp;
		}
		return stdDev;
	}
	
	/**
	 * Calculates the average generation out of all runs. 
	 * The generation value in each run corresponds to the
	 * last generation in which improvement happened. 
	 * <br><br>
	 * Note that this method includes runs where no perfect
	 * solution was found. For the average number of generations
	 * for perfect solutions only, use {@code getAverageSuccessfulGenerations}.
	 * 
	 * @return the average number of generations.
	 */
	public double getAverageGenerations() {
		double average = 0;
		for (RunEntry runEntry : runEntries) {
			average += runEntry.getGeneration() / runEntries.size();
		}
		return average;
	}
	
	/**
	 * Calculates the standard deviation of
	 * the average number of generations in
	 * each run.
	 * 
	 * @return the standard deviation of the number of generations.
	 */
	public double getAverageGenerationsStdDev() {
		double average = getAverageGenerations();
		double temp, stdDev = 0;
		for (RunEntry runEntry : runEntries) {
			temp = runEntry.getGeneration() - average;
			temp = temp * temp;
			stdDev += temp;
		}
		return stdDev;
	}
	
	/**
	 * @return the highest fitness across all runs.
	 */
	public double getHighestFitness() {
		double highest = 0;
		for (RunEntry runEntry : runEntries) {
			if (runEntry.getFitness() > highest) {
				highest = runEntry.getFitness();
			}
		}
		return highest;
	}
	
	/**
	 * @return the lowest fitness across all runs.
	 */
	public double getLowestFitness() {
		double lowest = Double.MAX_VALUE;
		for (RunEntry runEntry : runEntries) {
			if (runEntry.getFitness() < lowest) {
				lowest = runEntry.getFitness();
			}
		}
		return lowest;
	}
	
	/**
	 * 
	 * @return the number of runs in which a perfect solution was found.
	 */
	public int getSuccessfulRuns() {
		int count = 0;
		for (RunEntry runEntry : runEntries) {
			// only increment if solution was perfect
			if (runEntry.isSuccessful()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculates the ratio of successful runs (runs where
	 * a perfect solution was found) to total number of runs.
	 * A double-precision value between 0 and 1 is returned, 
	 * where 0 means 0% success rate and 1 means 100% success rate.
	 * 
	 * @return the success rate across all runs.
	 */
	public double getSuccessRate() {
		return getSuccessfulRuns() / (double) runEntries.size();
	}
	
	/**
	 * Calculates the average generation out of successful runs only. 
	 * The generation value in each successful run corresponds to the
	 * generation in which the perfect solution was found. 
	 * 
	 * @return the average number of generations for perfect solutions.
	 */
	public double getAverageSuccessfulGenerations() {
		double average = 0;
		int successfulRuns = getSuccessfulRuns();
		for (RunEntry runEntry : runEntries) {
			// only if solution was perfect
			if (runEntry.isSuccessful()) {
				average += runEntry.getGeneration() / successfulRuns;
			}
		}
		return average;
	}
	
	/**
	 * Calculates the standard deviation of
	 * the average number of generations in
	 * each run where a perfect solution was found.
	 * 
	 * @return the standard deviation of the number of generations in successful runs.
	 */
	public double getAverageSuccessfulGenerationsStdDev() {
		double average = getAverageSuccessfulGenerations();
		double temp, stdDev = 0;
		for (RunEntry runEntry : runEntries) {
			// only if solution was perfect
			if (runEntry.isSuccessful()) {
				temp = runEntry.getGeneration() - average;
				temp = temp * temp;
				stdDev += temp;
			}
		}
		return stdDev;
	}
	
}
