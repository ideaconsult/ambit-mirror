package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.rest.OpenTox;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.StructureQueryResource;

/**
 * All compounds from a dataset
 * @author nina
 *
 */
public class DatasetStructuresResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	//public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compound);
	public final static String dataset = OpenTox.URI.dataset.getURI();	
	public final static String datasetKey = OpenTox.URI.dataset.getKey();	
	public final static String QR_PREFIX="R";
	protected Integer datasetID;
	protected Integer queryResultsID;


	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			setTemplate(createTemplate(context, request, response));
			Object id = request.getAttributes().get(datasetKey);
			if (id != null)  try {
				
				id = Reference.decode(id.toString());
				return getQueryById(new Integer(id.toString()));

			} catch (NumberFormatException x) {
				return getQueryById(id.toString());
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			else throw new InvalidResourceIDException("");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}		
	}
	
	
	protected Q getQueryById(Integer key) throws ResourceException {
		datasetID = key;
		QueryDatasetByID query = new QueryDatasetByID();
		query.setValue(key);
		try {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			query.setMaxRecords(Long.parseLong(form.getFirstValue(max_hits).toString()));
		} catch (Exception x) {}
		return (Q)query;
	}
	
	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(QR_PREFIX)) {
			key = key.substring(QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
		} else throw new InvalidResourceIDException(key);
		
		QueryStoredResults q = new QueryStoredResults();
		q.setChemicalsOnly(true);
		q.setFieldname(new StoredQuery(Integer.parseInt(key.toString())));
		q.setValue(null);
		return (Q)q;
	}

}
