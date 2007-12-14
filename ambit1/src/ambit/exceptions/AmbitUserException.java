package ambit.exceptions;

public class AmbitUserException extends AmbitException {
	public static final String msg_notallowed = "User not allowed";
	public AmbitUserException() {
		super();
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
