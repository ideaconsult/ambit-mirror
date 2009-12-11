package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryField;
import ambit2.rest.propertyvalue.PropertyValueResource;

/**
 * Search for structures, given a property value
 * @author nina
 *
 */
public class PropertyQueryResource extends StructureQueryResource<QueryField> {
	public final static String property =  String.format("%s%s/{condition}/{value}",
			QueryResource.query_resource,PropertyValueResource.featureKey);
	
	protected String dataset_id;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			this.dataset_id = Reference.decode(getRequest().getAttributes().get("dataset_id").toString());
		} catch (Exception x) {
			this.dataset_id = null;
		}		
	}

	@Override
	protected QueryField createQuery(Context context, Request request,
			Response response) throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		QueryField q =  new QueryField();
		q.setChemicalsOnly(true);
        try {
        	Object value = request.getAttributes().get("value");
        	if (value == null)
    			throw new ResourceException(
    					Status.SERVER_ERROR_INTERNAL,"Invalid search criteria"
    					);
        	else
        		q.setValue(Reference.decode(value.toString()));

        } catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
        }			
        StringCondition condition = StringCondition.getInstance(StringCondition.C_EQ);
        try {
        	condition = StringCondition.getInstance(Reference.decode(request.getAttributes().get("condition").toString()));
        } catch (Exception x) {
        	condition = StringCondition.getInstance(StringCondition.C_EQ);
        } finally {
        	q.setCondition(condition);
        }
        return q;
	}

}
