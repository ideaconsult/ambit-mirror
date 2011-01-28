package ambit2.rest.property;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.PropertiesByDataset;
import ambit2.db.search.property.PropertiesByQuery;
import ambit2.rest.ResourceDoc;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;

/**
 * Retrieves feature definitions by dataset 
<pre>
	/dataset/{id}/feature
</pre>
 * @author nina
 *
 */
public class PropertiesByDatasetResource extends PropertyResource {
	//public final static String DatasetFeaturedefID = String.format("%s%s/{%s}",DatasetsResource.datasetID,featuredef,idfeaturedef);
	//public final static String DatasetFeaturedef = String.format("%s%s",DatasetsResource.datasetID,featuredef);

	public PropertiesByDatasetResource() {
		super();
		setDocumentation(new ResourceDoc("Feature","Feature"));
	}
	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Object id = request.getAttributes().get(DatasetResource.datasetKey);
		collapsed = true;

		IQueryRetrieval<Property>  q = null;
		if (id != null) try {
			q = getQueryById(new Integer(Reference.decode(id.toString())));
			collapsed = false;
		} catch (NumberFormatException x) {
			q = getQueryById(Reference.decode(id.toString()));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		return q;
	}
	
	protected IQueryRetrieval<Property> getQueryById(Integer key) throws ResourceException {

		try {
			PropertiesByDataset q = new PropertiesByDataset();
			q.setFieldname(null);
			SourceDataset dataset = new SourceDataset();			
			dataset.setId(key);
			q.setValue(dataset);
			return q;
		} catch (Exception x) { throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,key.toString(),x);}

	}
	
	protected IQueryRetrieval<Property> getQueryByName(String key) throws ResourceException {

		try {
			PropertiesByDataset q = new PropertiesByDataset();
			q.setFieldname(null);
			SourceDataset dataset = new SourceDataset(key);			
			q.setValue(dataset);
			return q;
		} catch (Exception x) { throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,key.toString(),x);}

	}
	
	protected IQueryRetrieval<Property> getQueryById(String key) throws ResourceException {
		int queryResultsID = -1;
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				return getQueryByName(key);
			}
		} else return getQueryByName(key);
		
		PropertiesByQuery q = new PropertiesByQuery();
		q.setChemicalsOnly(true);
		q.setValue(new StoredQuery(queryResultsID));
		q.setFieldname(null);
		return q;
	}
}
