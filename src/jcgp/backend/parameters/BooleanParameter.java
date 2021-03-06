package jcgp.backend.parameters;

import javafx.beans.property.SimpleBooleanProperty;
import jcgp.backend.parameters.monitors.BooleanMonitor;

/**
 * Parameter subclass for the boolean type. Most of the
 * functionality is already implemented in {@code Parameter}, 
 * leaving only construction and type definition to the
 * subclasses.
 * <br><br>
 * This class contains three constructors, two of which are public.
 * One assumes the parameter is not critical and only takes a name
 * and initial value, while the other allows the critical flag
 * to be set as well. The third constructor is protected and allows
 * the monitor flag to be set as well, allowing subclasses of this class
 * to be used as monitors. See {@link BooleanMonitor} for an example
 * of this usage.
 * <br><br>
 * The validate method is overridden here and left blank since not all
 * parameters actually require validation, but where validation is
 * required this method can be anonymously overridden on an instance-to-instance
 * basis.
 * 
 * @author Eduardo Pedroni
 *
 */
public class BooleanParameter extends Parameter<Boolean> {
	
	/**
	 * Creates a new instance of this class, assuming the parameter
	 * is not critical.
	 * 
	 * @param value the initial value for this parameter.
	 * @param name the name of this parameter, for GUI display.
	 */
	public BooleanParameter(boolean value, String name) {
		super(name, false, false);
		this.valueProperty = new SimpleBooleanProperty(value);
	}
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param value the initial value for this parameter.
	 * @param name the name of this parameter, for GUI display.
	 * @param critical true if the parameter is critical.
	 */
	public BooleanParameter(boolean value, String name, boolean critical) {
		super(name, false, critical);
		this.valueProperty = new SimpleBooleanProperty(value);
	}
	
	/**
	 * For use by subclasses only, this constructor allows the monitor flag to be set.
	 * 
	 * @param value the initial value for this parameter.
	 * @param name the name of this parameter, for GUI display.
	 * @param monitor true if the parameter is a monitor.
	 * @param critical true if the parameter is critical.
	 */
	protected BooleanParameter(boolean value, String name, boolean monitor, boolean critical) {
		super(name, monitor, critical);
		this.valueProperty = new SimpleBooleanProperty(value);
	}
	
	@Override
	public Boolean get() {
		return super.get().booleanValue();
	}

	@Override
	public void validate(Boolean newValue) {
		/*
		 *  Blank by default.
		 *  Instances should override this as necessary.
		 *  
		 */
	}
}
