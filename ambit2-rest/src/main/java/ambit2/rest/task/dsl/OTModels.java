package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

import ambit2.rest.OpenTox;

public class OTModels extends OTContainers<OTModel> {

	@Override
	public OTModel createItem(Reference uri) throws Exception {
		return OTModel.model(uri);
	}

	@Override
	public OTContainers<OTModel> delete(OTModel item) throws Exception {
		throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.model_uri.toString();
	}

}
