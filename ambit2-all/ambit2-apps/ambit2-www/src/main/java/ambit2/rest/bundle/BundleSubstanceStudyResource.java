package ambit2.rest.bundle;

import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.db.update.bundle.effects.BundleStudyFacetQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.facet.AmbitFacetResource;

public class BundleSubstanceStudyResource<Q extends IQueryRetrieval<SubstanceByCategoryFacet>> extends AmbitFacetResource<SubstanceByCategoryFacet,Q>  {
	public final static String resource = "/study";
	protected SubstanceEndpointsBundle bundle;
	
	public BundleSubstanceStudyResource() {
		super();
		setHtmlbyTemplate(false);
	}
	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle!=null) {
			try {
				bundle = new SubstanceEndpointsBundle(new Integer(Reference.decode(idbundle.toString())));
				BundleStudyFacetQuery q = new BundleStudyFacetQuery(
						String.format("%s%s/%s/study",getRootRef(),OpenTox.URI.bundle.getURI(),idbundle)
						);
				q.setFieldname(bundle);
				return (Q)q;
			} catch (Exception x) {	}
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		return new BundleStudyJSONReporter(request,jsonp,bundle);
	}	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Substance study summary");
		map.put("facet_group","Structure type");
		map.put("facet_count","Number of structures");

		map.put("facet_tooltip","");
		map.put("facet_subgroup","");
	}
}
