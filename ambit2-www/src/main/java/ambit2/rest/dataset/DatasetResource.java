package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.query.QueryResource;

public class DatasetResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	public DatasetResource(Context context, Request request, Response response) {
		super(context,request,response);
	}

	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		RetrieveDatasets query = new RetrieveDatasets();
		query.setValue(new SourceDataset(Reference.decode(
				request.getAttributes().get("dataset_id").toString())));
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		
		return new DocumentConvertor(new DatasetReporter(getRequest().getRootRef()));
	}


}
