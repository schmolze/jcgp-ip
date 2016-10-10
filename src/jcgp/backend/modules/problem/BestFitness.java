package jcgp.backend.modules.problem;

/**
 * Enum type to allow problems to indicate their fitness
 * orientation.
 * <br><br>
 * {@code BestFitness.HIGH} means high fitness values are
 * better than low. Conversely, {@code BestFitness.LOW}
 * signals that low fitness values indicate better fitness
 * than high values. 
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public enum BestFitness {
	HIGH, LOW;
}
