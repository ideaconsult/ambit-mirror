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
			Object id = request.getAttributes().get("dataset_id");
			
			
			if (id != null)  try {
				SourceDataset dataset = new SourceDataset();
				dataset.setId(new Integer(Reference.decode(id.toString())));
				QueryDataset query = new QueryDataset();
				query.setValue(dataset);
				query.setMaxRecords(100);
				return query;
			} catch (Exception x) {
				throw new AmbitException("Invalid dataset id "+id);
			}
			else throw new AmbitException("Invalid dataset id");
			
		} catch (Exception x) {
			throw new AmbitException(x);
		}		
	}

}
