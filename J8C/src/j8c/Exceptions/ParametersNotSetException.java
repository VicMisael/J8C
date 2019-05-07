package j8c.Exceptions;

public class ParametersNotSetException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParametersNotSetException() {

		super("You need to set the parameters");
	}
}
