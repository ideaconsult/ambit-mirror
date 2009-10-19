package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.rest.StatusException;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.structure.CompoundResource;

/**
 * All compounds from a dataset
 * @author nina
 *
 */
public class DatasetStructuresResource extends StructureQueryResource<QueryDatasetByID> {
	public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compound);



	@Override
	protected QueryDatasetByID createQuery(Context context, Request request,
			Response response) throws StatusException {
		try {
			Object id = request.getAttributes().get("dataset_id");
			
			
			if (id != null)  try {
				QueryDatasetByID query = new QueryDatasetByID();
				query.setValue(new Integer(Reference.decode(id.toString())));
				query.setMaxRecords(1000);
				return query;
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(id);
			} catch (Exception x) {
				throw new InvalidResourceIDException(id);
			}
			else throw new InvalidResourceIDException("");
			
		} catch (Exception x) {
			throw new StatusException(
					new Status(Status.SERVER_ERROR_INTERNAL,x,x.getMessage())
					);
		}		
	}

}
