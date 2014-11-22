package ambit2.rest.substance.study;

import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.facet.SubstanceStudyFacet;
import ambit2.db.substance.study.facet.SubstanceStudyFacetQuery;
import ambit2.db.update.bundle.effects.BundleStudyFacetQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.facet.FacetResource;
import ambit2.rest.substance.SubstanceResource;

public class SubstanceStudyFacetResource<Q extends IQueryRetrieval<SubstanceStudyFacet>> extends FacetResource<SubstanceStudyFacet,Q>  {
	public final static String resource = "studysummary";
	
	public SubstanceStudyFacetResource() {
		super();
		setHtmlbyTemplate(false);
	}
	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle==null) {
			Object substanceuuid = request.getAttributes().get(SubstanceResource.idsubstance);
			Form form = getRequest().getResourceRef().getQueryAsForm();
			String property = form.getFirstValue("property");
			String property_uri = form.getFirstValue("property_uri");		
			SubstanceStudyFacetQuery q = new SubstanceStudyFacetQuery(
					String.format("%s%s/%s/study",getRootRef(),SubstanceResource.substance,substanceuuid)
					);
			q.setFieldname(substanceuuid==null?null:substanceuuid.toString());
			if (property_uri!=null) try {
				//not nice REST style, but easiest to parse the URI
				Reference puri = new Reference(property_uri);
				property=puri.getLastSegment();
			} catch (Exception x) {}
			if (property!=null) {
				q.setValue(property);
			}		
			return (Q)q;
		} else {
			try {
				SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(new Integer(Reference.decode(idbundle.toString())));
				BundleStudyFacetQuery q = new BundleStudyFacetQuery(
						String.format("%s%s/%s/study",getRootRef(),OpenTox.URI.bundle.getURI(),idbundle)
						);
				q.setFieldname(bundle);
				return (Q)q;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			}

		}
	}
	@Override
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		return new StudySummaryJSONReporter(request,jsonp);
	};
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Substance study summary");
		map.put("facet_group","Structure type");
		map.put("facet_count","Number of structures");
	}
}
