package ambit2.rest.aa;

import org.opentox.aa.exception.AAException;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Error when communication with Authn service
 * @author nina
 *
 */
public class AuthenticationServiceException extends AAException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7898064345647477509L;
	public AuthenticationServiceException(String ref, ResourceException x) {
		super(x.getStatus().getCode(),ref,x);
	}
	public AuthenticationServiceException(String ref, Exception x) {
		super(Status.SERVER_ERROR_BAD_GATEWAY.getCode(),ref,x);
	}
}
