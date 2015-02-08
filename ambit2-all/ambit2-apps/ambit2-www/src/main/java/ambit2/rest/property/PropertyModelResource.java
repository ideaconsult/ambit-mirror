package ambit2.rest.property;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.property.ModelTemplates;
import ambit2.rest.DisplayMode;
import ambit2.rest.model.ModelResource;

/**
 * Retrieves properties , associated with a model
 * 
 * @author nina
 * 
 * @param <T>
 */
public class PropertyModelResource extends PropertyResource {
    public static final String resourceKey = "modelvars";
    public static final String resourceID = String.format("/{%s}", resourceKey);

    public PropertyModelResource() {
	super();
	_dmode = DisplayMode.table;
    }

    @Override
    protected IQueryRetrieval<Property> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	Form form = request.getResourceRef().getQueryAsForm();
	try {

	    headless = Boolean.parseBoolean(form.getFirstValue("headless"));
	} catch (Exception x) {
	    headless = false;
	}

	ModelQueryResults model = new ModelQueryResults();
	try {
	    model.setId(Integer.parseInt(getRequest().getAttributes().get(ModelResource.resourceKey).toString()));
	} catch (Exception x) {
	    model.setId(-1);
	    if ("null".equals(getRequest().getAttributes().get(ModelResource.resourceKey).toString()))
		model = null;
	    else
		model.setName(getRequest().getAttributes().get(ModelResource.resourceKey).toString());
	}
	ModelTemplates query = new ModelTemplates();
	try {
	    query.setValue(Reference.decode(getRequest().getAttributes().get(resourceKey).toString()));
	} catch (Exception x) {
	    query.setValue(null);
	}
	query.setFieldname(model);
	return query;

    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }
}
