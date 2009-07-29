package ambit2.rest.dataset;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

public class DatasetResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	public final static String datasetID =  String.format("%s%s",DatasetsResource.datasets,"/{dataset_id}");
	public DatasetResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));		
	}
	
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		RetrieveDatasets query = new RetrieveDatasets();
		
		query.setValue(new SourceDataset(Reference.decode(
				request.getAttributes().get("dataset_id").toString())));
		System.out.println(Reference.decode(
				request.getAttributes().get("dataset_id").toString()));
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new DatasetHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest().getRootRef()) {
				@Override
				public void processItem(SourceDataset dataset, Writer output) {
					super.processItem(dataset, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
		} else 
			return new OutputStreamConvertor(
					new DatasetHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
	}		
		//return new DocumentConvertor(new DatasetReporter(getRequest().getRootRef()));


}
