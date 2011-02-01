package ambit2.rest.task.dsl;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.dsl.interfaces.IOTObject;

public class OTObject implements Comparable<OTObject>, IOTObject{
	 protected Reference service = null;
	 protected Reference dataset_service;
	 protected final Reference uri ;
	 protected String name;
	 public String getName() {
		return name;
	}
	 public static OTObject object(Reference datasetURI) throws Exception  { 
		    return new OTObject(datasetURI);
	 }

	 public static OTObject object(String datasetURI) throws Exception  { 
		    return new OTObject(datasetURI);
	 }
	public OTObject withName(String name) {
		this.name = name;
		return this;
	}

	protected Form form;
	 
	protected OTObject(Reference ref) {
			this.uri = ref==null?null:ref.clone();
	}
	protected OTObject(String ref) {
		this.uri = ref==null?null:new Reference(ref);
	}	 
	 public Reference getUri() {
		return uri;
	}

	
	 
		protected long pollInterval = 1500;
		protected long pollTimeout = 1000L*60L*5L; //5 min
	 
	 protected OTObject() {
		 uri = null;
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
				if(task.getError() instanceof ResourceException)
					throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
							String.format("%s %d %s",result==null?task.getUrl():result,
							((ResourceException)task.getError()).getStatus().getCode(),
							task.getError().getMessage()),
							task.getError());
				else
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
		 if (form==null) form = new Form();
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
	
	public OTObject readTextLineAsName() throws Exception {
		ClientResourceWrapper client = new ClientResourceWrapper(uri);
		Representation r=null;
		try {
			r =	client.get(MediaType.TEXT_PLAIN);
			this.name = r.getText().trim();
		} catch (Exception x) {
			
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
		return this;
	}
	
	public OTObject create() throws Exception {
		if (service==null)
			throw new Exception("No service available");
		throw new Exception("Not implemented");
	}
	
	 static Reference getPagedReference(Reference uri, int page,int pageSize) throws Exception {
		 Reference ref = uri.clone();
		 Form form = ref.getQueryAsForm();
		 form.removeAll(OpenTox.params.page.toString());
		 form.removeAll(OpenTox.params.pagesize.toString());
		 form.add(OpenTox.params.page.toString(),Integer.toString(page));
		 form.add(OpenTox.params.pagesize.toString(),Integer.toString(pageSize));
		 ref.setQuery(form.getQueryString());
		 return ref;
	 }		
}
