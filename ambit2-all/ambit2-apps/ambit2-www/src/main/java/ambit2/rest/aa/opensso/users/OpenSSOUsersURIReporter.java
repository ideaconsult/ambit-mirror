package ambit2.rest.aa.opensso.users;

import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;

import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.reporters.CatalogURIReporter;

public class OpenSSOUsersURIReporter  extends CatalogURIReporter<OpenSSOUser> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public OpenSSOUsersURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public OpenSSOUsersURIReporter(ResourceDoc doc) {
		this(null,doc);
	}	

	@Override
	public String getURI(String ref, OpenSSOUser item) {
		return String.format("%s/%s/%s",ref,OpenSSOUserResource.resource,
				(item==null)||(item.getUsername()==null)?"":item.getUsername());
	}

}