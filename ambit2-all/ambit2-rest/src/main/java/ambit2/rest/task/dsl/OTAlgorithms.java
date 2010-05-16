package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

import ambit2.rest.OpenTox;

public class OTAlgorithms extends OTProcessingContainers<OTAlgorithm> {

	 public static OTAlgorithms algorithms() throws Exception  { 
		    return new OTAlgorithms();
	 }
	@Override
	public OTAlgorithm createItem(Reference uri) throws Exception {
		return OTAlgorithm.algorithm(uri);
	}

	@Override
	public OTContainers<OTAlgorithm> delete(OTAlgorithm item) throws Exception {
		throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.algorithm_uri.toString();
	}

}
