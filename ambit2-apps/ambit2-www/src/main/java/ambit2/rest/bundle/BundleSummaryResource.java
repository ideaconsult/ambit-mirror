package ambit2.rest.bundle;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.facets.bundle.BundleSummaryFacet;
import ambit2.db.facets.bundle.BundleSummaryQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.facet.AmbitFacetResource;

public class BundleSummaryResource extends AmbitFacetResource<BundleSummaryFacet,BundleSummaryQuery> {
	public static final String resource = "/summary";
	

	@Override
	protected BundleSummaryQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Object id = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		
		if (id!=null) {
			try {
					Integer idnum = new Integer(Reference.decode(id.toString()));
					SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
					dataset.setID(idnum);
					BundleSummaryQuery q = new BundleSummaryQuery(getRequest().getRootRef()+"/bundle/"+idnum);
					q.setFieldname(dataset);
					
					return q;
			} catch (NumberFormatException x) {
			}
		}		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Bundle summary");

	}
	public String getDefaultUsersDB() {
		String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		return usersdbname==null?"ambit_users":usersdbname;
	}
}
