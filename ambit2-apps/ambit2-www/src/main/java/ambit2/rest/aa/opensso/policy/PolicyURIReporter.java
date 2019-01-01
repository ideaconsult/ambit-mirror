package ambit2.rest.aa.opensso.policy;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.rest.admin.AdminResource;
import ambit2.rest.reporters.CatalogURIReporter;

/**
 * Generates URI of type /opentoxuser/{id}/policy/{policyid}
 * 
 * @author nina
 * 
 */
public class PolicyURIReporter extends CatalogURIReporter<Policy> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3861530813327878373L;

    public PolicyURIReporter() {
    }

    public PolicyURIReporter(Request request) {
	super(request);
    }

    public String getURI(String ref, Policy item) {
	return String.format("%s/%s/%s/%s", ref, AdminResource.resource, OpenSSOPoliciesResource.resource,
		item == null ? "" : Reference.encode(item.getId()));
    }
}
