package jcgp.gui.settings.parameters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import jcgp.backend.parameters.IntegerParameter;
import jcgp.backend.parameters.ParameterStatus;
import jcgp.gui.constants.Constants;
import jcgp.gui.settings.SettingsPane;

/**
 * This extension of @code{GUIParameter} uses a @code{TextField} to display
 * the value of a @code{IntegerParameter}. It cannot be constructed
 * directly - instead, use @code{GUIParameter.create()}.
 * <br><br>
 * See {@link GUIParameter} for more information.
 * 
 * @author Eduardo Pedroni
 */
public class GUIIntegerParameter extends GUIParameter<Number> {
	
	private TextField textField;
	
	/**
	 * This protected constructor is intended for use
	 * by the factory method only.
	 * 
	 */
	protected GUIIntegerParameter(IntegerParameter parameter, SettingsPane sp) {
		super(parameter, sp);
	}

	@Override
	protected Control makeControl() {
		// this uses a text field
		textField = new TextField(String.valueOf(parameter.get()));
		textField.setStyle(Constants.VALID_PARAMETER_STYLE);
		textField.setAlignment(Pos.CENTER_RIGHT);
		textField.prefWidthProperty().bind(widthProperty().divide(2));
		
		return textField;
	}

	@Override
	protected void setControlListeners() {
		/* pass the TextField value back to the parameter whenever it gets
		 * modified, provided it is not empty, the experiment isn't running
		 * and it matches the integer regex pattern */
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!settingsPane.isExperimentRunning()) {
					if (newValue.matches("[0-9]*")) {
						if (!newValue.isEmpty()) {
							int value = Integer.parseInt(newValue);
							parameter.set(value);
							settingsPane.revalidateParameters();
						}
					} else {
						refreshValue();
					}
				}
			}
		});
		/* if the TextField loses focus and is empty, set it to the current
		 * value of the parameter */
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(
					ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					refreshValue();
				}
			}
		});
	}
	
	@Override
	protected void setValidityStyle() {
		// update the Control's style and tooltip based on the status of the parameter
		if (parameter.getStatus() == ParameterStatus.INVALID) {
			textField.setStyle(Constants.BASE_TEXT_STYLE + Constants.INVALID_PARAMETER_STYLE);
			textField.setTooltip(tooltip);
			tooltip.setText(parameter.getStatus().getDetails());
		} else if (parameter.getStatus() == ParameterStatus.WARNING 
				|| parameter.getStatus() ==  ParameterStatus.WARNING_RESET) {
			textField.setStyle(Constants.BASE_TEXT_STYLE + Constants.WARNING_PARAMETER_STYLE);
			textField.setTooltip(tooltip);
			tooltip.setText(parameter.getStatus().getDetails());
		} else {
			textField.setStyle(Constants.BASE_TEXT_STYLE + Constants.VALID_PARAMETER_STYLE);
			textField.setTooltip(null);
		}
	}

	@Override
	public void refreshValue() {
		if (!textField.isFocused()) {
			textField.setText(parameter.get().toString());
		}
	}
}
