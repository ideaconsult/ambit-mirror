package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FunctionalGroup;
import ambit2.rest.dataset.DatasetsResource;

/**
 * Query by smarts
 * @author nina
 *
 */
public class SmartsQueryResource  extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String smartsKey =  "smarts";
	public final static String smarts_resource =  String.format("%s/%s/{%s}",query_resource,smartsKey,smartsKey);
	public final static String smartsID =  String.format("%s/%s",query_resource,smartsKey);
	public final static String dataset_smarts_resource =  String.format("%s%s%s",DatasetsResource.datasetID,query_resource,"/smarts/{smarts}");
	
	public SmartsQueryResource(Context context, Request request,
			Response response) {
		super(context, request, response);
	}
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws AmbitException {
		try {
			Object key = request.getAttributes().get(smartsKey);
			if (key==null) {
				throw new AmbitException("Empty smarts");
			}
			String smarts = Reference.decode(key.toString());
			QuerySMARTS query = new QuerySMARTS();
			query.setChemicalsOnly(true);
			query.setValue(new FunctionalGroup(smarts,smarts,smarts));
			
			Object id = request.getAttributes().get("dataset_id");
			if (id != null) try {
				QueryDatasetByID scope = new QueryDatasetByID();
				scope.setValue(new Integer(Reference.decode(id.toString())));
				
				QueryCombinedStructure combined = new QueryCombinedStructure();
				combined.add(query);
				combined.setScope(scope);
				return combined;
			} catch (Exception x) {
				return query;
			}
			return query;

		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}		
	@Override
	public boolean allowPost() {
		return true;
	}
}
