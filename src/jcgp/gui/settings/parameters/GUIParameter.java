package jcgp.gui.settings.parameters;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.DoubleParameter;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.Parameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.gui.settings.SettingsPane;

/**
 * 
 * This is the base class for all @code{GUIParameter}s. Using the factory method @code{GUIParameter.create()}
 * generates an appropriate instance of this class for the specified parameter.
 * <br><br>
 * A @code{GUIParameter} is an @code{HBox} containing a @code{Text} for the parameter name 
 * and a @code{Control} for interaction.
 * It stores an instance of its associated @code{Parameter} object and also contains a @code{Tooltip} for 
 * displaying status information.
 * <br><br>
 * Monitor parameters are updated automatically and have their @code{Control} disabled so
 * that no changes can be made via the GUI.
 * Non-monitor parameters are updated automatically as well, but may be changed by the user
 * if the program is not evolving.
 * 
 * @see Parameter
 * @author Eduardo Pedroni
 * @param <T> the parameter data type
 */
public abstract class GUIParameter<T> extends HBox {
	
	private Label name;
	private Control valueControl;
	
	protected SettingsPane settingsPane;
	protected Tooltip tooltip;
	protected Parameter<T> parameter;
	
	/** This is the lock used to prevent more than one update task to be scheduled
	 *  at the same time on the same GUIParameter. */
	private AtomicBoolean updateLock = new AtomicBoolean(false);
	
	/**
	 * This value is used to assert whether the control has changed values since 
	 * the program last ran. Therefore, it is updated whenever a generation occurs
	 * or the experiment is reset.
	 */
	private T referenceValue;
	
	/**
	 * This protected template constructor contains the common elements to all
	 * @code{GUIParameter}s and should be invoked by any subclasses using @code{super()}. It
	 * defers the creation of the parameter {@code Control} object to the subclass
	 * currently being built (which in turn is defined by the factory method).
	 * 
	 * @param parameter a @code{Parameter} for which to generate a @code{GUIParameter}.
	 * @param sp a reference to the @code{SettingsPane}.
	 */
	protected GUIParameter(Parameter<T> parameter, final SettingsPane settingsPane) {
		this.parameter = parameter;
		this.referenceValue = parameter.get();
		
		this.settingsPane = settingsPane;
		
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(5);
		
		name = new Label(parameter.toString());
		// set text width to half of the total width of the GUIParameter
		name.prefWidthProperty().bind(widthProperty().divide(2));
		
		// the tooltip is the hover-over label containing status information, when appropriate
		tooltip = new Tooltip();
		tooltip.setSkin(null);
		
		valueControl = makeControl();
		
		// if the parameter is a monitor, it should be permanently disabled
		valueControl.setDisable(parameter.isMonitor());
		
		// bind to parameter value property in a thread-safe way
		makeThreadSafeBinding();
		
		// if parameter is not a monitor, make sure the control is constrained appropriately
		if (!parameter.isMonitor()) {
			setControlListeners();
		}
		
		getChildren().addAll(name, valueControl);
	}
	
	/**
	 * Factory method to create @code{GUIParameter}s from @code{Parameter}s. 
	 * Use this to create an appropriate @code{GUIParameter} from any instance of @code{Parameter},
	 * rather than manually downcasting the @code{Parameter} object every time.
	 * 
	 * @param parameter a parameter for which to generate a @code{GUIParameter}.
	 * @param sp a reference to the @code{SettingsPane}.
	 * @return an appropriate instance of @code{GUIParameter}.
	 */
	public static GUIParameter<?> create(Parameter<?> parameter, SettingsPane sp) {
		if (parameter instanceof IntegerParameter) {
			return new GUIIntegerParameter((IntegerParameter) parameter, sp);
		} else if (parameter instanceof DoubleParameter) {
			return new GUIDoubleParameter((DoubleParameter) parameter, sp);
		} else if (parameter instanceof BooleanParameter) {
			return new GUIBooleanParameter((BooleanParameter) parameter, sp);
		} else {
			throw new ClassCastException("No GUIParameter subclass exists for argument of type " + parameter.getClass());
		}
	}

	/**
	 * Parameters are intended to communicate information from the experiment
	 * to the GUI. Since the experiment runs on a separate threads and it is illegal
	 * to modify JavaFX objects from outside the JavaFX Application thread, this
	 * special ChangeListener updates the GUIParameter in a safe way.
	 * <br><br>
	 * Note that this is applied to all parameters regardless of whether they are 
	 * monitors or not; the only difference between monitor and non-monitor parameters
	 * is that monitor parameters cannot be modified from the GUI.
	 */
	private void makeThreadSafeBinding() {
		parameter.valueProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(
					ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// only do this if the experiment is running
				if (settingsPane.isExperimentRunning() || !isFocused()) {
					/* here's the catch - atomically get the lock state and set it to true
					 * the lock will only be false again when the runnable is finished executing, 
					 * preventing multiple runnables to concurrently update the same GUIParameter
					 */
					if (!updateLock.getAndSet(true)) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								refreshValue();
								updateLock.set(false);
							}
						});
					}
				}
			}
		});
	}
	
	/**
	 * @return true if the current value of the parameter does not prevent the 
	 * experiment from running.
	 */
	public boolean isValid() {
		return parameter.getStatus() != ParameterStatus.INVALID;
	}
	
	/**
	 * Force the parameter to validate its current value, and apply the associated
	 * style to the @code{GUIParameter}.
	 */
	public void validate() {
		parameter.validate(parameter.get());
		setValidityStyle();
	}
	
	/**
	 * Certain parameter changes might require the experiment to be reset, either
	 * because the parameter is critical or because its status requires a reset.
	 * 
	 * @return true if an experiment reset is required due to this parameter changing.
	 */
	public boolean requiresReset() {
		return (parameter.isCritical() && !parameter.get().equals(referenceValue))
				|| parameter.getStatus() == ParameterStatus.WARNING_RESET;
	}
	
	/**
	 * Set the current parameter value as the reference value of the @code{GUIParameter}.
	 * The new reference value will be used to determine the validity of the parameter, 
	 * should its value change.
	 */
	public void applyValue() {
		referenceValue = parameter.get();
	}
	
	/* 
	 * The following prototypes are instance-dependent and are called from
	 * GUIParameter() as necessary.
	 */
	/**
	 * This method returns the @code{Control} object used to control the parameter.
	 * <br><br>
	 * Implementations of @code{GUIParameter} must override this method and return 
	 * a @code{Control} appropriate to the type of parameter. This will typically be
	 * done by referencing the protected field @code{GUIParameter.parameter}.
	 * 
	 * @return the Control object to be added to the GUIParameter.
	 */
	protected abstract Control makeControl();
	
	/**
	 * Adds the necessary handlers to the @code{Control} object in order to modify
	 * the underlying parameter. This will typically consist of filtering key
	 * presses to ensure no invalid characters are inserted, applying the new 
	 * value to the underlying parameter and revalidating the parameters to
	 * reflect the changes made.
	 */
	protected abstract void setControlListeners();
	
	/**
	 * This method is called to style the @code{GUIParameter} according to the status of
	 * the parameter, which can be obtained with @code{parameter.getStatus()}. While the
	 * subclass is free to style itself in any way, the CSS strings defined here 
	 * (INVALID_PARAMETER_STYLE, WARNING_PARAMETER_STYLE, VALID_PARAMETER_STYLE)
	 * provide a way to keep the GUI consistent.
	 * 
	 * @see ParameterStatus
	 */
	protected abstract void setValidityStyle();
	
	/**
	 * Update the control so it shows the correct value of the parameter. This method
	 * is used exclusively by the thread-safe binding created if the module is a monitor.
	 */
	protected abstract void refreshValue();
}
