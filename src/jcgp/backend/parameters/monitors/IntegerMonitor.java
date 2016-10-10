package jcgp.backend.parameters.monitors;

import jcgp.backend.parameters.IntegerParameter;

/**
 * This is a special type of {@code IntegerParameter} which 
 * cannot be modified in the GUI (if the GUI is in use).
 * 
 * @author Eduardo Pedroni
 *
 */
public class IntegerMonitor extends IntegerParameter {
	
	/**
	 * Creates a new instance of this class, assuming the monitor
	 * is not critical.
	 * 
	 * @param value the initial value for this monitor.
	 * @param name the name of this monitor, for GUI display.
	 */
	public IntegerMonitor(int value, String name) {
		super(value, name, true, false);
	}
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param value the initial value for this monitor.
	 * @param name the name of this monitor, for GUI display.
	 * @param critical true if the monitor is critical.
	 */
	public IntegerMonitor(int value, String name, boolean critical) {
		super(value, name, true, critical);
	}

	@Override
	public void validate(Number newValue) {
		/*
		 *  Blank by default.
		 *  Instances should override this as necessary.
		 *  
		 */
	}
}
