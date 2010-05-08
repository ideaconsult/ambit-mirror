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

import ambit2.rest.OpenTox;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST implements Callable<Reference>{
	protected String[] urls;
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
		this(form.getValuesArray("url"),MediaType.TEXT_URI_LIST,form.getWebRepresentation());
	}	
	
	public CallablePOST(String[] url,MediaType media, 
			  Representation input) {
		this(url,media,input,null);
	}		
	public CallablePOST(String[] url,MediaType media, 
			  Representation input,
			  ChallengeResponse authentication) {
		this(url,media,input,authentication,1500);
	}
	public CallablePOST(String[] url,MediaType media, 
			  Representation input,
			  ChallengeResponse authentication, long pollInterval) {
		this.urls = url;
		this.media = media;
		this.input = input;
		this.pollInterval = pollInterval;
	}
	public Reference call() throws Exception {
		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		
		try {
			if ((urls==null) || (urls.length<1)) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"form.url not defined!");
			} else if(urls.length>1) {
				if (dataset_service==null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset service");

				RemoteTask createDataTask = new RemoteTask(new Reference(dataset_service),media,new Form(input).getWebRepresentation(),Method.POST,authentication);
				createDataTask = wait(createDataTask,now);
				if (createDataTask.error!=null) throw createDataTask.error;
				Reference dataset = createDataTask.getResult();
				
				RemoteTaskPool pool = new RemoteTaskPool();
				for (String url:urls) {
					Form params = new Form();
					params.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
					RemoteTask task = new RemoteTask(new Reference(url),media,params.getWebRepresentation(),Method.POST,authentication);
					pool.add(task);
				}

				while (pool.poll()>0) {

					Thread.sleep(pollInterval);
					Thread.yield();
					if ((System.currentTimeMillis()-now) > pollTimeout)
						break;
				}


				Form params = new Form();
				for (RemoteTask task : pool.pool) {
					if (task.error!=null) 
						throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
								String.format("%s %s",task.getUrl(),task.error.getMessage()));
					params.add(OpenTox.params.dataset_uri.toString(),task.getResult().toString());
				}
				params.add(OpenTox.params.dataset_service.toString(),dataset_service);
				RemoteTask task = new RemoteTask(dataset,media,params.getWebRepresentation(),Method.PUT,authentication);
				task = wait(task,now);
				return task.getResult();
				
			} else {
				RemoteTask task = new RemoteTask(new Reference(urls[0]),media,input,Method.POST,authentication);
				task = wait(task,now);
				return task.getResult();
			}
		} finally {
			System.out.println(String.format("Elapsed %s", System.currentTimeMillis()-now));
		}
	}

	protected RemoteTask wait(RemoteTask task, long now) throws Exception {
		String result = task.getResult().toString();
		while (!task.poll()) {
			if (task.error!=null) throw task.error;
			Thread.sleep(pollInterval);
			Thread.yield();
			if ((System.currentTimeMillis()-now) > pollTimeout) 
				throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT,
						String.format("%s %s ms > %s ms",result==null?urls[0].toString():result,System.currentTimeMillis()-now,pollTimeout));
		}
		
		if (task.error!=null) 
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					String.format("%s %s",result==null?urls[0].toString():result,task.error.getMessage()),
					task.error);
		return task;
	}
	
	protected String[] getFeatures(String model) {
		
	}
}
