package ambit2.rest.task.dsl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public abstract class OTContainers<T extends OTObject>  extends OTProcessingResource {
	protected List<T> items;

	 public synchronized OTContainers<T> add(T item) throws Exception  { 
		 	if (item==null) return this;
			if (items==null) items = new ArrayList<T>();
			if (items.indexOf(item)<0)
				items.add(item);
			return this;
	 }
	 public synchronized OTContainers<T> add(OTContainers<T> newitems) throws Exception  { 
		 	if (newitems==null) return this;
			if (items==null) items = new ArrayList<T>();
			for (T item : newitems.items)
				add(item);
			return this;
	 }	 
	 /*
	 public synchronized T commit() throws Exception  { 
		    long now = System.currentTimeMillis();
		    if ((items==null) || (items.size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No dataset uri to add");
			Form params = new Form();
			for (T item : items)
				params.add(getParamName(),item.toString());
			if (dataset_service!=null)
				params.add(OpenTox.params.dataset_service.toString(),dataset_service.toString());
			
			RemoteTask task = null;
			if (uri==null)
				task = new RemoteTask(dataset_service,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST,authentication);
			else
				task = new RemoteTask(uri,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.PUT,authentication);
			task = wait(task,now);
			Reference ref = task.getResult();
			items.clear();
			return createItem(ref); 
			
	 }	
	 */ 
	 protected abstract  String getParamName() throws Exception ;
	 public T createItem(String uri) throws Exception {
		 return createItem(new Reference(uri));
	 }
	 public abstract T createItem(Reference uri) throws Exception;

	 public abstract OTContainers<T> delete(T item) throws Exception ;
	 public OTContainers<T> withDatasetService(Reference uri) throws Exception { 
		  this.dataset_service = uri;
		  return this; 
	 }	
	 public OTContainers<T>  withDatasetService(String uri) throws Exception { 
		  return withDatasetService(new Reference(uri));
	 }		 
	 
	 public OTContainers<T> remove(T dataset) throws Exception {
		 items.remove(dataset);
		 return this;
	 }
	 
	 public OTContainers<T> remove(String uri) throws Exception {
		 for (int i=size()-1; i>=0; i--)
			 if (items.get(i).uri.toString().equals(uri)) {
				 items.remove(i);
				 break;
			 }
		 return this;
	 }	 
	 public int size() {
		 return items==null?0:items.size();
	 }
	 public Iterable<T> getItems() {
		 return items;
	 }

	 public void clear() {
		 if (items!=null) items.clear();
	 }
	 @Override
	public String toString() {
		if ((items==null) || (items.size()==0)) return "";
		StringBuilder b = new StringBuilder();
		for (T item: items) {
			b.append(item.toString());
			b.append("\n");
		}
		return b.toString();
	}
	 
		public OTContainers<T> read(String uri) throws Exception {
			ClientResource client = null;
			Representation r = null;
			try {
		
				client = new ClientResource(new Reference(uri));
						
				r = client.get(MediaType.TEXT_URI_LIST);
				BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));
				String line = null;
				while ((line = reader.readLine())!=null) {
					add(createItem(line.trim()));
				}					
				return this;
			} catch (Exception x) {
				throw new Exception(x.getMessage());
			} finally {
				try { r.release();} catch (Exception x) {}
				try { client.release();} catch (Exception x) {}
			}
		}
		
		public T getItem(int index) {
			return items==null?null:items.get(index);
		}
		
}
