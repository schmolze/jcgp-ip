package jcgp.backend.parameters.monitors;

import jcgp.backend.parameters.BooleanParameter;

/**
 * This is a special type of {@code BooleanParameter} which 
 * cannot be modified in the GUI (if the GUI is in use).
 * 
 * @author Eduardo Pedroni
 *
 */
public class BooleanMonitor extends BooleanParameter {
	
	/**
	 * Creates a new instance of this class, assuming the monitor
	 * is not critical.
	 * 
	 * @param value the initial value for this monitor.
	 * @param name the name of this monitor, for GUI display.
	 */
	public BooleanMonitor(boolean value, String name) {
		super(value, name, true, false);
	}
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param value the initial value for this monitor.
	 * @param name the name of this monitor, for GUI display.
	 * @param critical true if the monitor is critical.
	 */
	public BooleanMonitor(boolean value, String name, boolean critical) {
		super(value, name, true, critical);
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
