package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryField;

public class PropertyQueryResource extends StructureQueryResource<QueryField> {

	protected String dataset_id;
	public PropertyQueryResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			this.dataset_id = Reference.decode(request.getAttributes().get("dataset_id").toString());
		} catch (Exception x) {
			this.dataset_id = null;
		}

	}	
	@Override
	protected QueryField createQuery(Context context, Request request,
			Response response) throws AmbitException {
		QueryField q =  new QueryField();
        try {
        	q.setValue(Reference.decode(request.getAttributes().get("value").toString()));

        } catch (Exception x) {
        	throw new AmbitException(x);
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
