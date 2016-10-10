package jcgp.gui.settings.parameters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import jcgp.backend.parameters.BooleanParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.gui.constants.Constants;
import jcgp.gui.settings.SettingsPane;

/**
 * This extension of @code{GUIParameter} uses a @code{CheckBox} to display
 * the value of a @code{BooleanParameter}. It cannot be constructed
 * directly - instead, use @code{GUIParameter.create()}.
 * <br><br>
 * See {@link GUIParameter} for more information.
 * 
 * @author Eduardo Pedroni
 */
public class GUIBooleanParameter extends GUIParameter<Boolean> {

	private CheckBox checkBox;
	
	/**
	 * This protected constructor is intended for use
	 * by the factory method only.
	 * 
	 */
	protected GUIBooleanParameter(BooleanParameter parameter, SettingsPane sp) {
		super(parameter, sp);
	}
	
	@Override
	protected Control makeControl() {
		checkBox = new CheckBox();
		checkBox.setSelected(parameter.get());
		
		return checkBox;
	}
	
	@Override
	protected void setControlListeners() {
		/* pass the CheckBox value back to the parameter whenever it gets
		 * modified, provided the experiment isn't running */
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(
					ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (!settingsPane.isExperimentRunning()) {
					parameter.set(newValue);
					settingsPane.revalidateParameters();
				}
			}
		});
	}
	
	@Override
	protected void setValidityStyle() {
		// update the Control's style and tooltip based on the status of the parameter
		if (parameter.getStatus() == ParameterStatus.INVALID) {
			checkBox.setStyle(Constants.BASE_CHECKBOX_STYLE + Constants.INVALID_PARAMETER_STYLE);
			checkBox.setTooltip(tooltip);
			tooltip.setText(parameter.getStatus().getDetails());
		} else if (parameter.getStatus() == ParameterStatus.WARNING 
				|| parameter.getStatus() == ParameterStatus.WARNING_RESET) {
			checkBox.setStyle(Constants.BASE_CHECKBOX_STYLE + Constants.WARNING_PARAMETER_STYLE);
			checkBox.setTooltip(tooltip);
			tooltip.setText(parameter.getStatus().getDetails());
		} else {
			checkBox.setStyle(Constants.BASE_CHECKBOX_STYLE + Constants.VALID_PARAMETER_STYLE);
			checkBox.setTooltip(null);
		}
	}

	@Override
	public void refreshValue() {
		checkBox.setSelected(parameter.get());
	}
	
}
