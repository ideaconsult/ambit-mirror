package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
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
	

	
	public QueryURIReporter getUriReporter() {
		return uriReporter;
	}
	public void setUriReporter(QueryURIReporter uriReporter) {
		this.uriReporter = uriReporter;
	}

	protected boolean collapsed = true;
	/**
	 * 
	 */
	private static final long serialVersionUID = 16821411854045588L;
	
	/**
	 * 
	 */
	public QueryHTMLReporter() {
		this(null,true);
	}
	public QueryHTMLReporter(Request request, boolean collapsed) {
		super();
		uriReporter =  createURIReporter(request);
		this.collapsed = collapsed;
		processors.clear();
		/*
		ValuesReader valuesReader = new ValuesReader();
		Profile profile = new Profile<Property>();
		profile.add(Property.getCASInstance());
		valuesReader.setProfile(profile);
		processors.add(valuesReader);
		*/
		processors.add(new DefaultAmbitProcessor<T,T>() {
			public T process(T target) throws AmbitException {
				processItem(target);
				return target;
			};
		});
		
	}	
	protected abstract QueryURIReporter createURIReporter(Request request);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.getRequest());
		} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
}
