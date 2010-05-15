package ambit2.rest.task.dsl;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.task.RemoteTask;

public class OTObject implements Comparable<OTObject>{
	 protected Reference uri = null;
	 protected Form form;
	 
	 public Reference getUri() {
		return uri;
	}

	protected Reference dataset_service;
	 
	 protected ChallengeResponse authentication;
		protected long pollInterval = 1500;
		protected long pollTimeout = 1000L*60L*5L; //5 min
	 
	 protected OTObject() {
		 
	 }
	 public OTObject withUri(String uri) throws Exception { 
		  return withUri(new Reference(uri));
	 }	
	 public OTObject withUri(Reference uri) throws Exception { 
		  this.uri = uri;
		  return this; 
	 }			
	 public OTObject withAuthentication(ChallengeResponse authentication) throws Exception { 
		  this.authentication = authentication;
		  return this; 
	 }		 
	 @Override
	public String toString() {
		return uri==null?null:uri.toString();
	}
	 
	protected RemoteTask wait(RemoteTask task, long now) throws Exception {
			if (task.getError()!=null) throw task.getError();
			if (task.getResult()==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("%s returns empty contend instead of URI"));
			String result = task.getResult().toString();
			while (!task.poll()) {
				if (task.getError()!=null) throw task.getError();
				Thread.sleep(pollInterval);
				Thread.yield();
				if ((System.currentTimeMillis()-now) > pollTimeout) 
					throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT,
							String.format("%s %s ms > %s ms",result==null?task.getUrl():result,System.currentTimeMillis()-now,pollTimeout));
			}
			
			if (task.getError()!=null) 
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("%s %s",result==null?task.getUrl():result,task.getError().getMessage()),
						task.getError());
			return task;
	}	 
	public OTObject withParams(Form form) throws Exception { 
		 this.form = form;
		 return this;
	 }
	 public OTObject withParams(String name,String value) throws Exception { 
		 if (form!=null) form = new Form();
		 form.add(name, value);
		 return this;
	 }
	public int compareTo(OTObject o) {
		return toString().compareTo(o.toString());
	}
	@Override
	public boolean equals(Object obj) {
		return uri==null?false:toString().equals(obj.toString());
	}
}
