package ambit2.rest.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.aa.opensso.policy.OpenSSOPoliciesResource;
import ambit2.rest.admin.fingerprints.FingerprintResource;
import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.dataset.filtered.StatisticsResource;
import ambit2.rest.dataset.filtered.StatisticsResource.StatsMode;

public class AdminResource  extends CatalogResource<String> {
	public static final String resource = "admin";
	protected List<String> topics = new ArrayList<String>();

	public AdminResource() {
		super();
		topics.add(String.format("%s/%s",resource,DatabaseResource.resource));
		topics.add(String.format("%s/%s",resource,OpenSSOPoliciesResource.resource));

		topics.add(String.format("%s%s",resource,StatisticsResource.resource));
		for (StatsMode mode: StatsMode.values())
			topics.add(String.format("%s%s/%s",resource,StatisticsResource.resource,mode.name()));
		
		topics.add(String.format("%s%s/%s",resource,FingerprintResource.resource,FPTable.fp1024.name()));
		topics.add(String.format("%s%s/%s",resource,FingerprintResource.resource,FPTable.sk1024.name()));	
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "admin.ftl";
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {

		return topics.iterator();
	}
	

}
