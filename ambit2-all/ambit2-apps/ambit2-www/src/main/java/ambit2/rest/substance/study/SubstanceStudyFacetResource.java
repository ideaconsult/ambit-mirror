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
import org.restlet.resource.ResourceException;

import ambit2.db.substance.study.facet.SubstanceStudyFacet;
import ambit2.db.substance.study.facet.SubstanceStudyFacetQuery;
import ambit2.rest.facet.AmbitFacetResource;
import ambit2.rest.substance.SubstanceResource;

public class SubstanceStudyFacetResource<Q extends IQueryRetrieval<SubstanceStudyFacet>> extends AmbitFacetResource<SubstanceStudyFacet,Q>  {
	public final static String resource = "studysummary";
	
	public SubstanceStudyFacetResource() {
		super();
		setHtmlbyTemplate(false);
	}
	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {

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
				Reference puri = new Reference(property_uri.endsWith("/")?property_uri.substring(0, property_uri.length()-2):property_uri);
				//the very last segment denotes protocol, then study type, then one is the endpoint hash
				if (puri.getSegments().get(puri.getSegments().size()-1).indexOf("-") > 0) //this is the protocol
				    property=puri.getSegments().get(puri.getSegments().size()-3);
				else    
				    property=puri.getSegments().get(puri.getSegments().size()-2);
				if (property.length()!=40) property = null;
			} catch (Exception x) {}
			if (property!=null) {
				q.setValue(property);
			}		
			return (Q)q;
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
