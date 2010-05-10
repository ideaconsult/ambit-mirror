package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

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
 	
}
