package org.opentox.aa.exception;

import org.restlet.data.Status;

public class AuthzException extends AAException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8824211189407043701L;

	public AuthzException(String service,Throwable cause) {
		super(Status.CLIENT_ERROR_NOT_FOUND,String.format("%s not available",service),
				cause);
	}
}

