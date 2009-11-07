package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryCombined.COMBINE;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.rest.structure.CompoundResource;

/**
 * Compound from a dataset; if id is valid, but the compound doesn't belong to the dataset , it will not be a hit
 * 
 * @author nina
 *
 */
public class DatasetCompoundResource extends CompoundResource {
	//public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID);

	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(DatasetResource.datasetKey);
		if (id != null)
			return String.format("riap://application/dataset/%s/feature_definition",id);
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		IQueryRetrieval<IStructureRecord> q = super.createQuery(context, request, response);
		if (q == null) return null;
		
		DatasetStructuresResource ds = new DatasetStructuresResource();
		ds.init(context, request, response);
		IQueryRetrieval<IStructureRecord> datasetQuery = ds.createQuery(context, request, response);
		if (datasetQuery == null) return null;
		QueryCombinedStructure combinedQuery = new QueryCombinedStructure();
		combinedQuery.add(q);
		combinedQuery.add(datasetQuery);
		combinedQuery.setCombine_queries(COMBINE.AND);
		return combinedQuery;
	}
}
