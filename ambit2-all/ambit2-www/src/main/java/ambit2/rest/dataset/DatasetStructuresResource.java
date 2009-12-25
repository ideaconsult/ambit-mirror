package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryDatasetByID;
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
	


	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			setTemplate(createTemplate(context, request, response));
			Object id = request.getAttributes().get(datasetKey);
			if (id != null)  try {
				
				QueryDatasetByID query = new QueryDatasetByID();
				query.setValue(new Integer(Reference.decode(id.toString())));

				return (Q)query;
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(id);
			} catch (Exception x) {
				throw new InvalidResourceIDException(id);
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

}
