package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryCombined.COMBINE;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.rest.StatusException;
import ambit2.rest.structure.CompoundResource;

/**
 * Compound from a dataset
 * 
 * @author nina
 *
 */
public class DatasetCompoundResource extends CompoundResource {
	public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID);

	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws StatusException {
		IQueryRetrieval<IStructureRecord> q = super.createQuery(context, request, response);
		if (q == null) return null;
		
		DatasetStructuresResource ds = new DatasetStructuresResource();
		ds.init(context, request, response);
		QueryDatasetByID datasetQuery = ds.createQuery(context, request, response);
		if (datasetQuery == null) return null;
		QueryCombinedStructure combinedQuery = new QueryCombinedStructure();
		combinedQuery.add(q);
		combinedQuery.add(datasetQuery);
		combinedQuery.setCombine_queries(COMBINE.AND);
		return combinedQuery;
	}
}
