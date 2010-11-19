package ambit2.rest.exception;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class TaskException extends ResourceException {
	protected String URL;
	protected List<Throwable> subtasksStatus;
	public TaskException(Status status, String description, Throwable cause) {
		super(status, description, cause);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5060840500563021260L;
	
}
