package ambit2.rest.exception;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class ImportSubstanceException extends ResourceException {
	private String details;

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8559378742531504299L;

	public ImportSubstanceException(Status status, String msg) {
        super(status);
        this.details = msg;
    }
	
}
