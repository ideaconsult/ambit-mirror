package ambit2.rest.property;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.PropertiesByDataset;
import ambit2.db.search.property.PropertiesByQuery;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetStructuresResource;

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
		
		Form form = request.getResourceRef().getQueryAsForm();
		Object id = request.getAttributes().get(DatasetResource.datasetKey);
		_dmode = DisplayMode.table;
		
		IQueryRetrieval<Property>  q = null;
		if (id != null) try {
			q = getQueryById(new Integer(Reference.decode(id.toString())),form);
			_dmode = DisplayMode.singleitem;
		} catch (NumberFormatException x) {
			q = getQueryById(Reference.decode(id.toString()),form);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		return q;
	}
	
	protected IQueryRetrieval<Property> getQueryById(Integer key,Form form) throws ResourceException {

		try {
			PropertiesByDataset q = new PropertiesByDataset();
			q.setFieldname(createTemplate(form));
			SourceDataset dataset = new SourceDataset();			
			dataset.setId(key);
			q.setValue(dataset);
			return q;
		} catch (Exception x) { throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,key.toString(),x);}

	}
	
	protected IQueryRetrieval<Property> getQueryByName(String key,Form form) throws ResourceException {

		try {
			PropertiesByDataset q = new PropertiesByDataset();
			q.setFieldname(createTemplate(form));
			SourceDataset dataset = new SourceDataset(key);			
			q.setValue(dataset);
			return q;
		} catch (Exception x) { throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,key.toString(),x);}

	}
	
	protected IQueryRetrieval<Property> getQueryById(String key,Form form) throws ResourceException {
		int queryResultsID = -1;
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				return getQueryByName(key,form);
			}
		} else return getQueryByName(key,form);
		
		PropertiesByQuery q = new PropertiesByQuery();
		q.setChemicalsOnly(true);
		q.setValue(new StoredQuery(queryResultsID));
		q.setFieldname(createTemplate(form));
		return q;
	}
	
	@Override
	protected AbstractUpdate createDeleteObject(Property entry)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	protected Template createTemplate(Form form) throws ResourceException {
		Object featureid = getRequest().getAttributes().get(PropertiesByDatasetResource.idfeaturedef);
		if (featureid != null) try {
			Template profile = new Template(null);
			Property p = new Property(null);
			p.setEnabled(true);
			p.setId(Integer.parseInt(featureid.toString()));
			profile.add(p);
			return profile;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		String[] featuresURI =  OpenTox.params.feature_uris.getValuesArray(form);
		if (featuresURI!=null)
			return createTemplate(getContext(),getRequest(),getResponse(), featuresURI);
		else return null;
	}
}
