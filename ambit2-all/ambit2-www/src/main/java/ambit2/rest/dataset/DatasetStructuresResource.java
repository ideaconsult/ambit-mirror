package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.search.structure.QueryDataset;
import ambit2.rest.query.StructureQueryResource;

public class DatasetStructuresResource extends StructureQueryResource<QueryDataset> {
	
	public DatasetStructuresResource(Context context, Request request, Response response) {
		super(context,request,response);
		
	}

	@Override
	protected QueryDataset createQuery(Context context, Request request,
			Response response) throws AmbitException {
		try {
			QueryDataset query = new QueryDataset();
			query.setValue(new SourceDataset(Reference.decode(request.getAttributes().get("dataset_id").toString())));
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}		
	}

}
