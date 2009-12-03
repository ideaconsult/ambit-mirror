package ambit2.rest.fastox;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.update.model.ReadModel;
import ambit2.rest.model.ModelResource;

public class KroesStep2 extends ModelResource {


	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		ReadModel query = new ReadModel();
		query.setValue(null);
		query.setFieldname("ToxTree: ILSI/Kroes decision tree for TTC");
		query.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		collapsed = false;
		return query;
	}
	@Override
	protected ReadModel getModelQuery(Object idmodel) throws ResourceException {
		ReadModel query = super.getModelQuery(idmodel);
		query.setValue(null);
		query.setFieldname("ToxTree: ILSI/Kroes decision tree for TTC");
		query.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		return query;
	}
}
