package org.opentox.aa.exception;

import org.restlet.data.Status;

public class AAServiceNotAvailable extends AAException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4544960474904076072L;

	public AAServiceNotAvailable(String service,Throwable cause) {
		super(Status.CLIENT_ERROR_NOT_FOUND,String.format("%s not available",service),
				cause);
	}
}
