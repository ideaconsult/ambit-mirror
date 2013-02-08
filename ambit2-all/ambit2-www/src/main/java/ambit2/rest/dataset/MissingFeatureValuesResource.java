package ambit2.rest.dataset;

import java.util.Iterator;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.structure.QueryMissingProperty;
import ambit2.rest.OpenTox;

/**
 * Returns structures with missing values from a given dataset, or globally.
 * Dataset is expected in dataset_uri parameter.
 * Features are accepted in feature_uris[] parameters.
 * @author nina
 *
 */
public class MissingFeatureValuesResource extends DatasetStructuresResource<QueryMissingProperty> {
	public static final String resource = "/missingValues";
	@Override
	protected QueryMissingProperty createQuery(Context context, Request request, Response response)
			throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		if ((getTemplate()==null) || (getTemplate().size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No features set!");
		
		QueryMissingProperty query = new QueryMissingProperty();
		
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, query);
		
		Iterator<Property> i = getTemplate().getProperties(true);
		while (i.hasNext()) {
			try {
				Property p = i.next();
				if (p.isEnabled()) {
					query.setValue(p);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
			
		}    	
		Object dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (dataset!=null) 
		try {
			String baseRef = getRootRef().toString()+"/dataset/";
			int p = dataset.toString().indexOf(baseRef);
			if (p >= 0) {
				ISourceDataset adataset = new SourceDataset();
				String number = dataset.toString().substring(p+ baseRef.length());
				adataset.setID(Integer.parseInt(number));
				query.setFieldname(adataset);
				return query;
			} else
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Processing foreign datasets not implemented!");
		} catch (NumberFormatException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid dataset id",x);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid dataset URI");
	}
	

}
