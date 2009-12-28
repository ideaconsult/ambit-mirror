package ambit2.rest.algorithm;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.processors.batch.ListReporter;

public class CatalogURIReporter<T> extends ListReporter<T, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2871865183499748866L;
	protected Request request;
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	public CatalogURIReporter(Request request) {
		this(request==null?null:request.getRootRef());
		setRequest(request);
		
	}	
	protected CatalogURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	protected CatalogURIReporter() {
	}	
	@Override
	public void processItem(T item, Writer output) {
		try {
			output.write(getURI(item));
			output.flush();
		} catch (IOException x) {
			logger.warn(x);
		}
	}	
	public String getURI(String ref, T item) {
		return String.format("%s%s%s",ref,"".equals(ref)?"":"/",item.toString());
	}
	
	public String getURI(T item) {
		String ref = baseReference==null?"":baseReference.toString();
		if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);	
		return getURI(ref,item);
	}

	public void close() throws Exception {
	}
	@Override
	public void footer(Writer output, Iterator<T> query) {
		
	}
	@Override
	public void header(Writer output, Iterator<T> query) {
		
	}

}
