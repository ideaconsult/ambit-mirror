package ambit2.rest.dataset;

import java.io.InputStreamReader;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.rest.property.PropertyDOMParser;


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
	
	
	public final static String dataset = "/dataset";	
	public final static String datasetKey = "dataset_id";	
	
	@Override
	protected QueryDatasetByID createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		try {
			QueryDatasetByID q = super.createQuery(context, request, response);
			if (q.getValue() > 0) {
				ClientResource client = new ClientResource(
						String.format("riap://application/dataset/%d/feature_definition",q.getValue()));
				client.setClientInfo(getRequest().getClientInfo());
				client.setReferrerRef(getRequest().getOriginalRef());
				Representation r = client.get(MediaType.TEXT_XML);
				final Template profile = new Template(null);
				profile.setId(-1);				
				PropertyDOMParser parser = new PropertyDOMParser() {
					@Override
					public void processItem(Property property)
							throws AmbitException {
						if (property!= null) {
							property.setEnabled(true);
							profile.add(property);
						}
					}
				};
				setTemplate(profile);
				parser.parse(new InputStreamReader(r.getStream()));
				r.getStream().close();
			}
			return q;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}
}
