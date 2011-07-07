package org.opentox.aa.opensso;

public class UserCancelledException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4258682314814841089L;
	public UserCancelledException() {
		super("Cancelled by the user");
	}
}
