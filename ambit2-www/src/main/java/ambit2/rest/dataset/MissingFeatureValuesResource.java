package ambit2.rest.dataset;

import java.util.Iterator;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryMissingDescriptor;
import ambit2.rest.OpenTox;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * Returns structures with missing values from a given dataset, or globally.
 * Dataset is expected in dataset_uri parameter.
 * Features are accepted in feature_uris[] parameters.
 * @author nina
 *
 */
public class MissingFeatureValuesResource extends DatasetStructuresResource<QueryCombinedStructure> {
	public static final String resource = "/missingValues";
	@Override
	protected QueryCombinedStructure createQuery(Context context, Request request, Response response)
			throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		if ((getTemplate()==null) || (getTemplate().size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No features set!");
		
		QueryCombinedStructure query = new QueryCombinedStructure();
		
		Form form = getRequest().getResourceRef().getQueryAsForm();
		setPaging(form, query);
		
		query.setCombine_as_and(false);
		Iterator<Property> i = getTemplate().getProperties(true);
		while (i.hasNext()) {
			try {
				Property p = i.next();
				if (p.isEnabled()) {
					QueryMissingDescriptor q = new QueryMissingDescriptor();
					q.setFieldname(p.getReference());
					q.setValue(p.getName());
					query.add(q);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
			
		}    	

		Object dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (dataset!=null) try {
			Object q = CallableQueryProcessor.getQueryObject(new Reference(dataset.toString()), getRequest().getRootRef());
			if ((q!=null) && (q instanceof AbstractStructureQuery))
				query.setScope((AbstractStructureQuery)q);
			else 
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Processing foreign datasets not implemented!");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
		return query;
	}
	

}
