package ambit2.rest.facet;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.db.facets.datasets.EndpointCompoundFacet;
import ambit2.db.facets.datasets.EndpointCompoundFacetQuery;
import ambit2.db.search.StringCondition;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.query.QueryResource;

public class DatasetsByEndpoint extends AmbitFacetResource<EndpointCompoundFacet,EndpointCompoundFacetQuery> {
	public static final String resource = "/ndatasets_endpoint";
	

	@Override
	protected EndpointCompoundFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		

		
				
		String endpoint = getParams().getFirstValue(MetadatasetResource.search_features.feature_sameas.toString());
		EndpointCompoundFacetQuery q = new EndpointCompoundFacetQuery(getResourceRef(getRequest()).toString());
		Property p = null;
		if (endpoint != null) {
			p  = new Property("");
			p.setLabel(endpoint);
			q.setFieldname(p);
		}
		q.setValue(getStructure());
		
		StringCondition c = StringCondition.getInstance(StringCondition.C_REGEXP);
		String param = getParams().getFirstValue(QueryResource.condition.toString());
		try {
			if (param != null)	{
				if ("startswith".equals(param.toLowerCase()))
					q.setCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
				else
					c = StringCondition.getInstance(param);
			}
		} catch (Exception x) {	
			
		} finally {
			q.setCondition(c);
		}
		return q;
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Datasets per endpoint");

	}
}
