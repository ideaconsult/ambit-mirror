package ambit2.rest.facet;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.facets.datasets.DatasetByPrefixNameFacetQuery;
import ambit2.db.facets.datasets.DatasetPrefixFacet;
import ambit2.rest.query.QueryResource;

/**
 * 

 * @author nina
 *
 */
public class DatasetsByNamePrefixResource  extends AmbitFacetResource<DatasetPrefixFacet,DatasetByPrefixNameFacetQuery>  {
	public static final String resource = "/ndatasets_nameprefix";
	
	@Override
	protected DatasetByPrefixNameFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		DatasetByPrefixNameFacetQuery q = new DatasetByPrefixNameFacetQuery(getResourceRef(getRequest()).toString());
		try {
			q.setValue(request.getResourceRef().getQueryAsForm().getFirstValue(QueryResource.search_param));
		} catch (Exception x) {
			q.setValue(null);
		}
		try {
			q.setFieldname(getStructure());
		} catch (Exception x) {
			q.setFieldname(null);
		}		
		return q;
		
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Datasets");
	}
}
