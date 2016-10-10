package jcgp.backend.parameters;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 * Specifies an abstract model of a module parameter.
 * <br><br>
 * Parameters are values which control the operation of modules.
 * They can be freely modified and accessed by the module in which
 * they are declared. Additionally, the module may choose to expose
 * some of its parameters to a user interface, so that information
 * is displayed. If that is the case, the parameter can be made
 * read-only by setting the monitor flag (it becomes a parameter
 * monitor). In addition, settings the critical flag indicates to
 * the experiment that any changes to the parameter should result in
 * an experiment-wide reset.
 * <br><br>
 * {@code Parameter} is abstract. A typical implementation defines
 * the data type T and initialises the {@code valueProperty} field
 * with a suitable type. For the sake of clarity, it may not be ideal 
 * for a subclass constructor to expose an argument for the monitor
 * field. Instead, a different class should be created which constructs
 * the parameter as a monitor, so that the distinction between a regular
 * parameter and a parameter monitor is more apparent. The boolean, integer
 * and double implementations of parameter (and their associated monitors)
 * implement this pattern, refer to them for more details. 
 * <br><br>
 * The {@code status} field holds the current status of the parameter, 
 * which should change whenever the parameter value changes.
 * In order for this to happen, {@code validate()} is called whenever
 * the parameter status should be updated. This being the case, it should
 * be overridden on an instance-to-instance basis, as each parameter 
 * will likely have different validity criteria. The type of status is
 * {@link ParameterStatus}, an enum type defining all valid states.
 * 
 * @see jcgp.backend.modules.Module
 * @author Eduardo Pedroni
 * @param <T> the data type stored in the parameter.
 */
public abstract class Parameter<T> {
	
	private boolean monitor, critical;
	protected ParameterStatus status = ParameterStatus.VALID;
	protected String name;
	protected Property<T> valueProperty;
	
	/**
	 * For internal use only. This creates a new instance of the class
	 * requiring a name, monitor and critical information. It should be
	 * invoked using {@code super()} by any implementing constructors.
	 * 
	 * @param name the name of the parameter, to be used by GUIs (if in use).
	 * @param monitor true if the parameter is a monitor, meaning it is not editable via the GUI (if in use).
	 * @param critical true if any changes to this parameter should cause an experiment-wide reset.
	 */
	protected Parameter(String name, boolean monitor, boolean critical) {
		this.name = name;
		this.monitor = monitor;
		this.critical = critical;
	}
	
	/**
	 * @return true if the parameter is a monitor.
	 */
	public boolean isMonitor() {
		return monitor;
	}
	
	/**
	 * @return true if the parameter is critical.
	 */
	public boolean isCritical() {
		return critical;
	}
	
	/**
	 * @return the current status of the parameter.
	 */
	public ParameterStatus getStatus() {
		return status;
	}
		
	/**
	 * This method is intended for bindings only. Changes to the parameter
	 * value should be made using {@code set()}.
	 * 
	 * @return the property which holds the parameter value.
	 */
	public ReadOnlyProperty<T> valueProperty() {
		return valueProperty;
	}
	
	/**
	 * @return the parameter's current value.
	 */
	public T get() {
		return valueProperty.getValue();
	}
	
	/**
	 * Sets the parameter to the specified value, if the property
	 * is not bound.
	 * 
	 * @param newValue the new value for the parameter.
	 */
	public void set(T newValue) {
		if (!valueProperty.isBound()) {
			valueProperty.setValue(newValue);
		}
	}
	
	/**
	 * This is a callback method which gets called whenever changes
	 * to parameters (not only its own instance) are made. This method
	 * is intended to set the {@code status} field according to the
	 * new value, so that the user can be informed if any parameters
	 * are currently set to invalid values.
	 * 
	 * @param newValue the new value.
	 */
	public abstract void validate(T newValue);
	
	@Override
	public String toString() {
		return name;
	}
}
