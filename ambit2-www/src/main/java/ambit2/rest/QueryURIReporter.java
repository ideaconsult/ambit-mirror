package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public abstract class QueryURIReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}
	public QueryURIReporter(Reference baseRef) {
		this.baseReference = baseRef;
	}
	public QueryURIReporter() {
	}	
	@Override
	public void processItem(T item, Writer output) {
		try {
			output.write(getURI(item));
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
