package ambit2.exceptions;

/**
 * Exception
 * @author nina
 *
 */
public class AmbitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6920776336173719941L;

	public AmbitException() {
	}

	public AmbitException(String arg0) {
		super(arg0);
	}

	public AmbitException(Throwable arg0) {
		super(arg0);
	}

	public AmbitException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
