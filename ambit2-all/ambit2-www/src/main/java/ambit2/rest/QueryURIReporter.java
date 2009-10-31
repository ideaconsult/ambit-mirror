package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;
import org.restlet.data.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Reports query results in text/uri-list 
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryURIReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected String delimiter = "";
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}	
	protected Request request;
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}
	protected QueryURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	public QueryURIReporter(Request request) {
		this(request==null?null:request.getRootRef());
		setRequest(request);
	}	
	protected QueryURIReporter() {
	}	
	@Override
	public void processItem(T item) throws AmbitException {
		try {
			String o = getURI(item);
			if (o != null) 	output.write(o);
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
	}	
	public abstract String getURI(String ref, T item);
	
	public String getURI(T item) {
		String ref = baseReference==null?"":baseReference.toString();
		if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);	
		return getURI(ref,item);
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}
