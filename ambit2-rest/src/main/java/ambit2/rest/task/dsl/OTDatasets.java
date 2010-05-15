package ambit2.rest.task.dsl;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

public class OTDatasets extends OTContainers<OTDataset> {

	 public static OTDatasets datasets() throws Exception  { 
		    return new OTDatasets();
	 }
	@Override
	public OTDataset createItem(Reference uri) throws Exception {
		return OTDataset.dataset().withUri(uri);
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
		if (size()== 0) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("No datasets! %s ",uri.toString()));
		OTDataset result = null;
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(dataset_service);
		for (OTDataset dataset: getItems()) 
			if (result==null) 
				result = OTDataset.dataset().withDatasetService(dataset_service).copy(dataset);
			else 
				datasets.add(dataset);
		if (datasets.size()==0) return result;
		else return result.put(datasets);
	}
	
}
