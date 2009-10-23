package ambit2.rest.model;

import java.io.Writer;

import org.restlet.data.Request;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

public class ModelURIReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends QueryURIReporter<ModelQueryResults, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public ModelURIReporter(Request baseRef) {
		super(baseRef);
	}
	public ModelURIReporter() {
	}	
	@Override
	public String getURI(String ref, ModelQueryResults model) {
		return
		String.format("%s%s/%d", 
				ref,
				ModelResource.resource,model==null?"":model.getId());
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	