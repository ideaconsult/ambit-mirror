package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.db.search.structure.QueryDatasetByID;


/**
 * Dataset resource - A set of chemical compounds and assigned features
 * 
 * http://opentox.org/development/wiki/dataset
 * 
 * Supported operations:
 * <ul>
 * <li>GET /dataset  ; returns text/uri-list or text/xml or text/html
 * <li>POST /dataset ; accepts chemical/x-mdl-sdfile or multipart/form-data (SDF,mol, txt, csv, xls,all formats supported in Ambit)
 * <li>GET 	 /dataset/{id}  ; returns text/uri-list or text/xml
 * <li>PUT and DELETE not yet supported
 * </ul>
 * 
 * @author nina
 *
 */
public class DatasetResource extends DatasetStructuresResource {
	
	

	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(datasetKey);
		if (id != null)
			return String.format("riap://application/dataset/%s/feature_definition",id);
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	@Override
	protected QueryDatasetByID createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		try {
			QueryDatasetByID q = super.createQuery(context, request, response);
			setTemplate(createTemplate(context, request, response));
			return q;
			/*
			final Template profile = new Template(null);
			profile.setId(-1);				
			
			Form form = request.getResourceRef().getQueryAsForm();
			String[] featuresURI =  form.getValuesArray("features");

			for (String featureURI:featuresURI) {
				readFeatures(featureURI, profile);
			}
			
			if (q.getValue() > 0) {
				readFeatures(String.format("riap://application/dataset/%d/feature_definition",q.getValue()), profile);
				setTemplate(profile);
			}
			try {
				 Preferences.setProperty(Preferences.MAXRECORDS,
						 form.getFirstValue("max"));
			} catch (Exception x) {
				
			}

			return q;
			*/
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}
}
