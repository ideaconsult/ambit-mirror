package ambit2.rest.model;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.core.data.model.ModelQueryResults;

public class ModelURIReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends QueryURIReporter<ModelQueryResults, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public ModelURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public ModelURIReporter(Request baseRef) {
		super(baseRef,null);
	}
	public ModelURIReporter() {
		super();
	}	
	@Override
	public String getURI(String ref, ModelQueryResults model) {
		return
		String.format("%s%s/%s", 
				ref,
				ModelResource.resource,model==null?"":Reference.encode(model.getQueryID().toString()));
	}

	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	