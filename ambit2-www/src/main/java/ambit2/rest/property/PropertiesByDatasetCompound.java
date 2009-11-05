package ambit2.rest.property;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryCombined;

/**
 * Retrieve features per compound from a dataset
 * @author nina
 *
 */
public class PropertiesByDatasetCompound extends PropertiesByDatasetResource {
	//public final static String DatasetCompoundFeaturedefID = String.format("%s%s",DatasetsResource.datasetID,CompoundFeaturedef);
	
	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		IQueryRetrieval<Property> q = super.createQuery(context, request, response);
		if (q == null) return null;
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.init(context, request, response);
		IQueryRetrieval<Property> q1 = propertyResource.createQuery(context, request, response);
		if (q == null) return null;
		QueryCombined<Property> qc = new QueryCombined<Property>() {
			@Override
			protected String getScopeSQL() {
				return null;
			}
			public void setMaxRecords(long records) {
				
			}
			public long getMaxRecords() {
				return 0;
			}
			@Override
			protected String joinOn() {
				return "idproperty";
			}
		};
		qc.add(q);
		qc.add(qc);
		qc.setCombine_as_and(true);
		return qc;
		
	}

}
