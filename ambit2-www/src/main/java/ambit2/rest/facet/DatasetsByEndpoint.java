package ambit2.rest.facet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.datasets.EndpointCompoundFacetQuery;
import ambit2.db.search.StringCondition;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.query.QueryResource;

public class DatasetsByEndpoint extends FacetResource<EndpointCompoundFacetQuery> {
	public static final String resource = "/ndatasets_endpoint";
	
	@Override
	protected EndpointCompoundFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		String uri = getParams().getFirstValue(OpenTox.params.compound_uri.toString());
		int compoundid  = -1;
		if (uri!= null) {
			Object id = OpenTox.URI.compound.getId(uri,getRequest().getRootRef());
			if (id == null) {
				Object[] ids;
				ids = OpenTox.URI.conformer.getIds(uri,getRequest().getRootRef());
				compoundid  = ((Integer) ids[0]).intValue();
			} else 
				compoundid = ((Integer)id).intValue();
		}
		
				
		String endpoint = getParams().getFirstValue(MetadatasetResource.search_features.feature_sameas.toString());
		EndpointCompoundFacetQuery q = new EndpointCompoundFacetQuery();
		if (endpoint != null) {
			Property p  = new Property("");
			p.setLabel(endpoint);
			p.setLabel(endpoint);
		}
		if (compoundid>0) {
			IStructureRecord record = new StructureRecord(compoundid,-1,null,null);
			q.setValue(record);
		}
		StringCondition c = StringCondition.getInstance(StringCondition.C_REGEXP);
		String param = getParams().getFirstValue(QueryResource.condition.toString());
		try {
			if (param != null)	c = StringCondition.getInstance(param);
		} catch (Exception x) {	
		} finally {
			q.setCondition(c);
		}
		return q;
	}
}
