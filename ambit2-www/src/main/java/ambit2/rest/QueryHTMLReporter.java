package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.search.property.ValuesReader;

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
	public QueryHTMLReporter(Reference baseRef, boolean collapsed) {
		super();
		uriReporter =  createURIReporter(baseRef);
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
				processItem(target,output);
				return target;
			};
		});
		
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
