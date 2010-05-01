package ambit2.rest.task;

import java.util.concurrent.Callable;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST implements Callable<Reference>{
	protected Reference url;
	protected MediaType media; 
	protected Representation input;
	protected ChallengeResponse authentication;
	protected ReferenceList results;
	protected Status status;
	protected long pollInterval = 1500;
	protected long pollTimeout = 1000L*60L*5L; //5 min
	
	public long getPollTimeout() {
		return pollTimeout;
	}
	public void setPollTimeout(long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}
	public long getPollInterval() {
		return pollInterval;
	}
	public void setPollInterval(long pollInterval) {
		this.pollInterval = pollInterval;
	}
	
	public CallablePOST(Form form) throws Exception {
		this(new Reference(form.getFirstValue("url")),MediaType.TEXT_URI_LIST,form.getWebRepresentation());
	}	
	
	public CallablePOST(Reference url,MediaType media, 
			  Representation input) {
		this(url,media,input,null);
	}		
	public CallablePOST(Reference url,MediaType media, 
			  Representation input,
			  ChallengeResponse authentication) {
		this(url,media,input,authentication,1500);
	}
	public CallablePOST(Reference url,MediaType media, 
			  Representation input,
			  ChallengeResponse authentication, long pollInterval) {
		this.url = url;
		this.media = media;
		this.input = input;
		this.pollInterval = pollInterval;
	}
	public Reference call() throws Exception {
		System.out.println(url);
		long now = System.currentTimeMillis();
		RemoteTask task = new RemoteTask(url,media,input,Method.POST,authentication);
		String result = task.getResult().toString();
		while (!task.poll()) {
			if (task.error!=null) throw task.error;
			Thread.sleep(pollInterval);
			Thread.yield();
			if ((System.currentTimeMillis()-now) > pollTimeout) 
				throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT,
						String.format("%s %s ms > %s ms",result==null?url.toString():result,System.currentTimeMillis()-now,pollTimeout));
		}
		
		if (task.error!=null) 
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					String.format("%s %s",result==null?url.toString():result,task.error.getMessage()),
					task.error);
		return task.getResult();
	}

	
}
