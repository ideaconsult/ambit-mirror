package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Generates HTML representation of a resource
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryHTMLReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	protected QueryURIReporter uriReporter;
	/**
	 * 
	 */
	private static final long serialVersionUID = 16821411854045588L;
	
	/**
	 * 
	 */
	public QueryHTMLReporter() {
		this(null);
	}
	public QueryHTMLReporter(Reference baseRef) {
		uriReporter =  createURIReporter(baseRef);
	}
	protected abstract QueryURIReporter createURIReporter(Reference reference);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.baseReference);
		} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.baseReference);
			output.flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
}
