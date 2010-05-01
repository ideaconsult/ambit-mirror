package ambit2.rest.task;

import java.util.concurrent.Callable;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

/**
 * Mockup class.
 * Does nothing , just sleeps for delay ms (default 1min) and returns the URI specified in the constructor.
 * 
 * For testing purposes.
 * @author nina
 *
 */
public class CallableMockup implements Callable<Reference> {
	protected long delay;
	protected Object resultURI;
	protected Exception error;
	public CallableMockup(Form form) {
		try {
			this.delay = Long.parseLong(OpenTox.params.delay.getFirstValue(form).toString());
		} catch (Exception x) {
			this.delay = 30000;
		}
		try {
			this.error = new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.error.getFirstValue(form).toString());
		} catch (Exception x) {
			this.error = null;
		}		
		resultURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (resultURI==null) resultURI = OpenTox.params.model_uri.getFirstValue(form);
	}
	public Reference call() throws Exception {
		Thread.sleep(delay);
		Thread.yield();
		if (error != null) throw error;
		if (resultURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No result URI specified");
		else return new Reference(resultURI.toString());
	}
}
