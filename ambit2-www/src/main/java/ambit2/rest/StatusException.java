package ambit2.rest;

import org.restlet.data.Status;

import ambit2.base.exceptions.AmbitException;

/**
 * An exception, storing HTTP status code. TODO - replace by ResourceException
 * @author nina
 *
 */
public class StatusException extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2612739552222906623L;
	/**
	 * 
	 */
	
	protected Status status;
	public Status getStatus() {
		return status;
	}
	public StatusException(Status status) {
		super(status.getDescription());
		this.status = status;
	}
	@Override
	public String getMessage() {
		return String.format("%d %s (%s)", status.getCode(),status.getName(),status.getDescription());
	}


	
}
