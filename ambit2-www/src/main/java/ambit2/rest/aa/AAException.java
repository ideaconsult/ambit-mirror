package ambit2.rest.aa;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * AA exceptions wrapper
 * @author nina
 *
 */
public class AAException extends ResourceException {

	public AAException(Status status, String description, Throwable cause) {
		super(status, description, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5750422032288902442L;

}
