package org.opentox.aa.exception;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class AAUnauthorizedException extends AAException  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -55874393797163012L;
	public AAUnauthorizedException(String ref, ResourceException x) {
		super(x.getStatus(),ref,x);
	}
	public AAUnauthorizedException(String ref, Exception x) {
		super(Status.CLIENT_ERROR_UNAUTHORIZED,ref,x);
	}
	public AAUnauthorizedException(String ref) {
		super(Status.CLIENT_ERROR_UNAUTHORIZED,ref,null);
	}	
}
