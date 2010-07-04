package ambit2.rest.reporters;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.rest.task.Task;

/**
 * URI of the task
 * @author nina
 *
 * @param <USERID>
 */
public class TaskURIReporter<USERID> extends CatalogURIReporter<Task<Reference,USERID>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9136099541989811170L;
	public TaskURIReporter() {
	}
	public TaskURIReporter(Request request) {
		super(request);
	}
	protected TaskURIReporter(Reference baseRef) {
		super(baseRef);
	}	
	public String getURI(String ref, Task<Reference,USERID> task) {

		return task.getUri().toString();

	}
}
