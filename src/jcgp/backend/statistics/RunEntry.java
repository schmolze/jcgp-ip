package jcgp.backend.statistics;

/**
 * This class encapsulates the data contained in a log entry.
 * <br><br>
 * Once constructed, data can only be retrieved. Note that
 * the generation argument in the constructor (and consequently
 * the value returned by {@code getGeneration()} refer to the 
 * last generation when improvement occurred.
 * 
 * @see StatisticsLogger
 * @author Eduardo Pedroni
 *
 */
public class RunEntry {
	
	private int generation, activeNodes;
	private double bestFitness;
	private boolean successful;
	
	/**
	 * Creates a new run entry for a logger. 
	 * 
	 * @param generation the generation when fitness improvement last occurred.
	 * @param fitness the best fitness achieved.
	 * @param active the number of active nodes in the best solution found.
	 * @param successful whether or not the run found a perfect solution.
	 */
	public RunEntry(int generation, double fitness, int active, boolean successful) {
		this.generation = generation;
		this.bestFitness = fitness;
		this.activeNodes = active;
		this.successful = successful;
	}
	
	/**
	 * @return the generation when improvement last occurred.
	 */
	public int getGeneration() {
		return generation;
	}
	/**
	 * @return the best fitness achieved during the run.
	 */
	public double getFitness() {
		return bestFitness;
	}
	/**
	 * @return true if the run was successful.
	 */
	public boolean isSuccessful() {
		return successful;
	}
	
	/**
	 * @return the number of active nodes in the best solution found. 
	 */
	public int getActiveNodes() {
		return activeNodes;
	}

}
