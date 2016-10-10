package jcgp.backend.parameters;

/**
 * Enum type containing all possible states for parameters.
 * <br>
 * <ul>
 * <li>INVALID: the new parameter value is not valid,
 * and the experiment will not be allowed to run.</li>
 * <li>WARNING: the new parameter value is technically valid,
 * though it might lead to undesirable behaviour.</li>
 * <li>WARNING_RESET: the new parameter value is technically valid
 * but will require a reset.</li>
 * <li>VALID: the new value is valid.</li>
 * </ul>
 * <br><br>
 * The above definitions are final in the sense that they outline
 * how parameters are treated by the program depending on their 
 * status (e.g. if any parameters are set to WARNING_RESET, a reset 
 * will automatically be performed when the experiment is run).
 * <br>
 * In addition to the status itself, this class includes a field
 * to contain details about the current status. If a GUI is in use, 
 * the contents of the field should be displayed to the user, as well
 * as some visual indication of the status itself. Both the status
 * and the message should be updated by each parameter when {@code validate()}
 * is called.
 * 
 * @see Parameter
 * @author Eduardo Pedroni
 *
 */
public enum ParameterStatus {
	INVALID, WARNING, WARNING_RESET, VALID;

	private String details;

	/**
	 * Sets a new string containing details about the current status.
	 * This should be displayed by the GUI, if one is in use.
	 * 
	 * @param details an explanation of the current status.
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * @return the string containing details about the current status.
	 */
	public String getDetails() {
		return details;
	}

}
