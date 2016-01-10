package ambit2.rest.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Parent class for sets of OpenTox objects {@link http://opentox.org/dev/apis/api-1.1}
 * @author nina
 *
 * @param <T>
 */
@Deprecated
public abstract class OTContainers<T extends IOTObject>  extends OTProcessingResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -461676329820947445L;
	protected List<T> items;

	public OTContainers(String uri,String referer) {
		super(uri,referer);

	}
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
	 
	 public OTContainers<T> remove(T item) throws Exception {
		 items.remove(item);
		 return this;
	 }
	 
	 public OTContainers<T> remove(String uri) throws Exception {
		 for (int i=size()-1; i>=0; i--)
			 if (items.get(i).getUri().toString().equals(uri)) {
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
	 @Override
	 public void get(String mediaType) throws Exception {
		 if (uri != null) read(getUri().toString());
		 else throw new Exception("URI not defined");
	 }
	 public OTContainers<T> readContent(String uri) throws Exception {
		 throw new UnsupportedOperationException();
	 }
	 public OTContainers<T> read() throws Exception {
		 return read(getUri().toString());
	 }
	 public OTContainers<T> read(String uri) throws Exception {
			HttpURLConnection uc= null;
			InputStream in= null;
			int code = 0;
			try {
				uc = ClientResourceWrapper.getHttpURLConnection(uri, "GET", MediaType.TEXT_URI_LIST.toString());
				
				code = uc.getResponseCode();
				if (HttpURLConnection.HTTP_OK == code) {
					in = uc.getInputStream();		
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while ((line = reader.readLine())!=null) {
						line = line.trim();
						if ("".equals(line)) continue;
						add(createItem(line));
					}		
				}
				return this;
			} catch (IOException x) {
				if (code > 0) throw new ResourceException(new Status(code),x);
				else throw x;
			} catch (Exception x) {
				throw x;
			} finally {
				try { if (in !=null) in.close();} catch (Exception x) {}
				try { if (uc != null) uc.disconnect();} catch (Exception x) {}
			}
		}
		
		public T getItem(int index) {
			return items==null?null:index<items.size()?items.get(index):null;
		}
		@Override
		public void setSelected(boolean value) {
			super.setSelected(value);
			for (int i=0; i < items.size();i++)
				items.get(i).setSelected(value);
		}		
}
