package jcgp.backend.modules.problem;

import java.io.File;

import jcgp.backend.function.FunctionSet;
import jcgp.backend.modules.Module;
import jcgp.backend.parameters.DoubleParameter;
import jcgp.backend.parameters.monitors.DoubleMonitor;
import jcgp.backend.population.Population;
import jcgp.backend.resources.ModifiableResources;
import jcgp.backend.resources.Resources;

/**
 * Defines the general behaviour of a CGP problem. The primary function of {@code Problem} 
 * is to evaluate a population and assign a fitness value to each chromosome. 
 * <br>
 * Problems are free to define whether better fitness means a higher or lower fitness value.
 * In some problem types, it is more convenient to treat fitness 0 as the best possible value.
 * This can be done by changing the fitness orientation to {@code BestFitness.HIGH} or {@code BestFitness.LOW} as appropriate.
 * Fitness orientation is set to high by default.
 * <br><br>
 * When extending this class, the constructor should call a few methods in order to
 * properly construct the problem type: {@code setFunctionSet()}, {@code setFileExtension()} and {@code setFitnessOrientation()},
 * with the respective arguments. As with all subclasses of {@code Module}, {@code setName()} and
 * {@code registerParameters()} should be used where appropriate as well.
 * <br><br>
 * It is advisable to use {@code Resources.reportln()} and {@code Resources.report()}
 * to print any relevant information. Note that reportln() and report() are affected
 * by the report interval base parameter. Use {@code Resources.println()} and
 * {@code Resources.print()} to print information regardless of the current generation.
 * See {@link Resources} for more information.
 * 
 * @see Module
 * @author Eduardo Pedroni
 *
 */
public abstract class Problem extends Module {

	private FunctionSet functionSet;
	private String fileExtension = ".*";
	
	private BestFitness fitnessOrientation = BestFitness.HIGH;
	
	protected DoubleParameter maxFitness, bestFitness;
	
	/**
	 * Initialises the two problem-wide parameters, maxFitness and bestFitness.
	 *
	 * @param resources a reference to the experiment's resources.
	 */
	protected Problem(Resources resources) {
		super(resources);
		
		maxFitness = new DoubleMonitor(0, "Max fitness");
		bestFitness = new DoubleMonitor(0, "Best fitness");
		registerParameters(maxFitness, bestFitness);
	}
	
	/**
	 * The most important method of the problem type. This is called once
	 * per generation, when the new population has been generated.
	 * <br><br>
	 * The basic functionality of this method is to loop through all chromosomes
	 * in the population and decode them according to the problem type. The
	 * fitness of each chromosome is then calculated using the problem data
	 * or otherwise (subjective problem types such as art generation might
	 * leave fitness evaluations up to the user) and assigned to the appropriate
	 * chromosome. 
	 * <br><br>
	 * In addition, realisations of this method should update the value of 
	 * bestFitness as appropriate, since the value of this parameter is displayed
	 * if a GUI is in use.
	 * 
	 * @param population the population to be evaluated.
	 */
	public abstract void evaluate(Population population);
	
	/**
	 * Used to assert whether a given population contains a perfect solution
	 * to the problem. It is up to the problem to define what qualifies
	 * a perfect solution, as some problems (subject ones such as music and
	 * art evolution, for example) might not have perfect solutions at all.
	 * 
	 * @param population the population to search through for a perfect chromosome.
	 * @return the perfect solution index, if one exits, -1 if no perfect solution was found.
	 */
	public abstract int hasPerfectSolution(Population population);
	
	/**
	 * Used to assert whether a given population has a chromosome that is an improvement over 
	 * the current best chromosome. A typical implementation of this method
	 * will simply compare chromosome fitness values, though the problem type
	 * is free to implement this in any way.
	 * 
	 * @param population the population potentially containing a fitter chromosome.
	 * @return the index of the first chromosome in the population that is an improvement, -1 if none is found.
	 */
	public abstract int hasImprovement(Population population);
	
	/**
	 * Parses the specified file and uses the parsed data to
	 * set up the problem type instance appropriately. Any necessary
	 * resource changes can be performed using the provided {@code ModifiableResources}
	 * instance.
	 * <br><br>
	 * In addition, realisations of this method should update the value of
	 * maxFitness where appropriate, as this may be displayed to the user
	 * if a GUI is in use.
	 * 
	 * @param file the data file to parse.
	 * @param resources a modifiable reference to the experiment's resources.
	 */
	public abstract void parseProblemData(File file, ModifiableResources resources);
	
	/**
	 * For internal use in subclass constructor, sets the functions to be
	 * used for this problem type. See {@link FunctionSet} for more details.
	 * 
	 * @param newFunctionSet the function set to use.
	 */
	protected void setFunctionSet(FunctionSet newFunctionSet) {
		this.functionSet = newFunctionSet;
	}
	
	/**
	 * @return the FunctionSet object used by this problem type.
	 */
	public FunctionSet getFunctionSet() {
		return functionSet;
	}
	
	/**
	 * For internal use in subclass constructors, sets the file extension accepted
	 * by this problem type's parser. This is used by the GUI to filter loaded files
	 * by extension in a file chooser. File extensions should be set in the form ".*", 
	 * so for plain text files, ".txt" would be used.
	 * 
	 * @param fileExtension the accepted file extension.
	 */
	protected void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	/**
	 * @return the file extension accepted by this problem type for problem data files.
	 */
	public String getFileExtension() {
		return fileExtension;
	}
	
	/**
	 * @param newOrientation the new fitness orientation to set.
	 */
	protected void setFitnessOrientation(BestFitness newOrientation) {
		this.fitnessOrientation = newOrientation;
	}
	
	/**
	 * @return the fitness orientation of this particular problem.
	 */
	public BestFitness getFitnessOrientation() {
		return fitnessOrientation;
	}
	
	/**
	 * @return the current best fitness, in other words, the fitness
	 * value of the fittest chromosome in the current generation.
	 */
	public double getBestFitness() {
		return bestFitness.get();
	}
	
	/**
	 * Resets the bestFitness parameter.
	 */
	public void reset() {
		bestFitness.set(0);
	}
}
