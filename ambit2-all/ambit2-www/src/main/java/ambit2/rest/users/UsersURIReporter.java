package ambit2.rest.users;

import org.restlet.Request;

import ambit2.base.data.AmbitUser;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class UsersURIReporter  <Q extends IQueryRetrieval<AmbitUser>> extends QueryURIReporter<AmbitUser, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public UsersURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public UsersURIReporter(ResourceDoc doc) {
		this(null,doc);
	}	

	@Override
	public String getURI(String ref, AmbitUser item) {
		return String.format("%s%s/%s",ref,UserResource.resource,item.getName());
	}

}