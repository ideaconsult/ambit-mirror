package ambit2.rest.legacy;

import java.io.StringWriter;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

import com.hp.hpl.jena.rdf.model.Model;

@Deprecated
public class OTObject implements Comparable<OTObject>, IOTObject {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1085797924363749090L;

	protected transient final Reference uri ;
	 
	protected transient Reference service = null;
	protected transient Reference dataset_service;
	protected boolean selected = true;
	protected boolean forbidden = false;
	protected String name;
	 public void setName(String name) {
		this.name = name;
	}
	public String getName() {

		return name;
	}
		public Reference getDataset_service() {
			return dataset_service;
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
		protected long pollTimeout = 10000L*60L*5L; //50 min
	 
	 protected OTObject() {
		 uri = null;
	 }
		

	 @Override
	public String toString() {
		return uri==null?null:uri.toString();
	}
	 
	protected OTRemoteTask wait(OTRemoteTask task, long now) throws Exception {
			if (task.getError()!=null) throw task.getError();
			if (task.getResult()==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("%s returns empty contend instead of URI"));
			String result = task.getResult().toString();
			OTFibonacciSequence sequence = new OTFibonacciSequence();
			while (!task.poll()) {
				if (task.getError()!=null) throw task.getError();
				Thread.sleep(sequence.sleepInterval(pollInterval,true,1000 * 60 * 5)); 				
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
	
	public static Reference getPagedReference(Reference uri, int page,int pageSize) throws Exception {
		 Reference ref = uri.clone();
		 Form form = ref.getQueryAsForm();
		 int max = -1;
		 try {
			 String maxRecords = form.getFirstValue("max");
			 if (maxRecords!=null) {
				 form.removeAll("max");
				 max = Integer.parseInt(maxRecords);
			 }
		 } catch (Exception x) {max = -1; }
		 
		 if ((max>0) && ((page+1)*pageSize > max)) {
			 page = max / pageSize;
		 }
		 
		 form.removeAll(OpenTox.params.page.toString());
		 form.removeAll(OpenTox.params.pagesize.toString());
		 form.add(OpenTox.params.page.toString(),Integer.toString(page));
		 form.add(OpenTox.params.pagesize.toString(),Integer.toString(pageSize));
		 ref.setQuery(form.getQueryString());
		 return ref;
	 }		
	 public boolean isSelected() {
		return selected;
	}
	 public void setSelected(boolean value) {
		selected = value;
		
	}
	 @Override
	 public void get(String mediaType) throws Exception {
		 throw new Exception("Not Implemented");
	 }
	 protected OTObject put(Model jenaModel) throws Exception {
		 return send(jenaModel, false);
	 }
	 public OTObject post(Model jenaModel) throws Exception {
		 return send(jenaModel, true);
	 }
	 public OTObject post(Representation representation, MediaType mediatype) throws Exception {
		 return send(representation,mediatype, true);
	 }	 
	 /**
	  * 
	  * @param jenaModel
	  * @param post true POST , false PUT
	  * @return
	  * @throws Exception
	  */
	 protected OTObject send(Model jenaModel, boolean post) throws Exception {
		 StringWriter writer = new StringWriter();
		 jenaModel.write(writer);
		 Representation r=null;
		 ClientResourceWrapper client = new ClientResourceWrapper(getUri());
		 try {
			 r = new StringRepresentation(writer.toString(),MediaType.APPLICATION_RDF_XML);
			 if (post) client.post(r); else client.put(r);
		 } catch (Exception x) {
			 throw x;
		 } finally {
			 try {r.release(); } catch (Exception x) {}
			 try {client.release(); } catch (Exception x) {}
			 
		 }
		 return this;
	 }
	 
	 /**
	  * 
	  * @param jenaModel
	  * @param post true POST , false PUT
	  * @return
	  * @throws Exception
	  */
	 protected OTObject send(Representation r, MediaType mediatype, boolean post) throws Exception {

		 Representation newr = null;
		 ClientResourceWrapper client = new ClientResourceWrapper(getUri());
		 try {
			 if (post) 
				 newr = client.post(r,MediaType.TEXT_URI_LIST); 
			 else 
				 newr = client.put(r,MediaType.TEXT_URI_LIST);
			 return OTCompound.compound(newr.getText().trim());
		 } catch (Exception x) {
			 throw x;
		 } finally {
			 try {r.release(); } catch (Exception x) {}
			 try {client.release(); } catch (Exception x) {}
			 
		 }
		 
	 }	 
	 


		public boolean delete() throws Exception {
			 ClientResourceWrapper client = new ClientResourceWrapper(getUri());
			 Representation r = null;
			 try {
				 r = client.delete();
				 return Status.SUCCESS_OK.equals(client.getStatus());

			 } finally {
				 try {r.release(); } catch (Exception x) {}
				 try {client.release(); } catch (Exception x) {}
				 
			 }
		}
		
		@Override
		public boolean isForbidden() {
			return forbidden;
		}
		
		@Override
		public IOTObject getPage(int page, int pageSize) throws Exception {
			throw new UnsupportedOperationException();
		}
}
