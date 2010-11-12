package org.opentox.aa.exception;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class AATokenValidationException extends AAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AATokenValidationException(String ref, ResourceException x) {
		super(x.getStatus(),ref,x);
	}
	public AATokenValidationException(String ref, Exception x) {
		super(Status.SERVER_ERROR_BAD_GATEWAY,ref,x);
	}
}
