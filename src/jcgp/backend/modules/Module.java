package jcgp.backend.modules;

import java.util.ArrayList;

import jcgp.backend.parameters.Parameter;
import jcgp.backend.resources.Resources;

/**
 * This class defines the expected behaviour of a module. Generally, modules
 * are entities which contain parameters; these can be retrieved using
 * {@code getLocalParameters()}. GUIs should make use of this getter
 * to display visual parameter controls to users. Subclasses don't have direct
 * access to the list; instead they must use {@code registerParameter()} (ideally
 * in the constructor) to make sure the parameters are returned.
 * <br>
 * In addition, implementations of {@code Module} should specify a module name
 * in their constructor using {@code setName()}. If a name is not provided, 
 * the simple name of the class will be used.
 * <br>
 * All modules, by definition, contain a reference to the experiment's resources, which
 * must be passed at construction. The resources should be accessed with {@code getResources()}.
 * 
 * @see Parameter
 * @author Eduardo Pedroni
 *
 */
public abstract class Module {
	
	private ArrayList<Parameter<?>> localParameters = new ArrayList<Parameter<?>>();
	private String name = getClass().getSimpleName();
	private Resources resources;
	
	/**
	 * Makes a new instance of this class. This should never
	 * be called directly, and should instead be used by subclasses.
	 * 
	 * @param resources a reference to the experiment's resources.
	 */
	protected Module(Resources resources) {
		this.resources = resources;
	}
		
	/**
	 * This method is used by the GUI in order to build visual
	 * representations of all parameters used by the module.
	 * Therefore, any parameters returned here will be displayed
	 * visually.
	 * 
	 * @return a list of generic parameters exposed by the module.
	 */
	public ArrayList<Parameter<?>> getLocalParameters() {
		return localParameters;
	}
	
	/**
	 * Adds one or more parameters to the list of local parameters.
	 * The same parameter cannot be added twice.
	 * <br><br>
	 * Implementations of {@code Module} should use this module
	 * to register any parameters they wish to expose to the user
	 * if a GUI is in use.
	 * 
	 * @param newParameters the parameter(s) to add to the list.
	 */
	protected void registerParameters(Parameter<?>... newParameters) {
		for (int i = 0; i < newParameters.length; i++) {
			if (!localParameters.contains(newParameters[i])) {
				localParameters.add(newParameters[i]);
			}
		}
	}
	
	/**
	 * Sets the name of the module, for GUI display.
	 * If no name is set, the simple name of the class
	 * is be used instead.
	 * 
	 * @param name the name to set.
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the experiment's resources.
	 */
	protected Resources getResources() {
		return resources;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
