package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * http://opentox.org/wiki/1/Dataset
 * 

 * @author nina
 *
 */
public class DatasetsResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	
	public DatasetsResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));		
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		RetrieveDatasets query = new RetrieveDatasets();
		query.setValue(null);
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(new DatasetHTMLReporter());	
		} else // (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		*/
		return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));			
		
	}
}
