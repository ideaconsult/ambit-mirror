package ambit2.rest.legacy;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

/**
 * List of {@link OTDataset}
 * @author nina
 *
 */
@Deprecated
public class OTDatasets extends OTContainers<OTDataset> {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 7638982655806506900L;
	
	public static OTDatasets datasets(String uri,String referer) throws Exception  { 
	    return new OTDatasets(uri,referer);
	}

	public OTDatasets(String uri,String referer) {
		super(uri,referer);
	}
	@Override
	public OTDataset createItem(Reference uri) throws Exception {
		return OTDataset.dataset(uri).withDatasetService(dataset_service);
	}

	@Override
	public OTContainers<OTDataset> delete(OTDataset item) throws Exception {
		 throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.dataset_uri.toString();
	}
	public OTDataset merge() throws Exception {
		if (size()== 0) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("No datasets! %s ",getUri().toString()));
		OTDataset result = null;
		
		OTDatasets datasets = OTDatasets.datasets(null,referer);
		datasets.withDatasetService(dataset_service);
		for (OTDataset dataset: getItems()) 
			if (result==null) 
				result = dataset.withDatasetService(dataset_service).copy();
			else 
				datasets.add(dataset.withDatasetService(dataset_service));
		if (datasets.size()==0) return result;
		else return result.put(datasets);
	}
	
}
