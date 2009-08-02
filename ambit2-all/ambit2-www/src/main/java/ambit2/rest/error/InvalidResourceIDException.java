package ambit2.rest.error;

import ambit2.base.exceptions.AmbitException;

/**
 * Reports invalid resource id
 * @author nina
 *
 */
public class InvalidResourceIDException extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1811762282496306586L;
	protected Object id;
	protected final String msg = "Invalid resource id %s";
	public InvalidResourceIDException(Object id) {
		this.id = id;
	}
	@Override
	public String getMessage() {
		return String.format(msg, id==null?"":id);
	}
}
