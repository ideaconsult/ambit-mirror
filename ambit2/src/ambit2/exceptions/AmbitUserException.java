package ambit2.exceptions;

/**
 * User not allowed exception
 * @author nina
 *
 */
public class AmbitUserException extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1762344484744030925L;

	
	public AmbitUserException() {

	}

	public AmbitUserException(String arg0) {
		super(arg0);
	}

	public AmbitUserException(Throwable arg0) {
		super(arg0);
	}

	public AmbitUserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
