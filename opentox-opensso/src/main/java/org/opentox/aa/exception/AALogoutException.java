package org.opentox.aa.exception;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class AALogoutException extends AAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2430891169503585895L;
	public AALogoutException(String ref, ResourceException x) {
		super(x.getStatus(),ref,x);
	}
	public AALogoutException(String ref, Exception x) {
		super(Status.SERVER_ERROR_BAD_GATEWAY,ref,x);
	}
}
