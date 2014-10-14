package ambit2.rest.error;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Reports invalid resource id
 * @author nina
 *
 */
public class InvalidResourceIDException extends ResourceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1811762282496306586L;
	protected static final String msg = "Invalid resource id %s";
	
	public InvalidResourceIDException(Object id) {
		super(Status.CLIENT_ERROR_BAD_REQUEST,getMessage(id));
	}

	public static String getMessage(Object id) {
		return String.format(msg, id==null?"":id);
	}
}
