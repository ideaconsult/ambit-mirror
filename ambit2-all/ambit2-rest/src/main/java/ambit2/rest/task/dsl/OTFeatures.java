package ambit2.rest.task.dsl;

import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;


public class OTFeatures  extends OTProcessingContainers<OTFeature> {
	
	 public static OTFeatures features() throws Exception  { 
		    return new OTFeatures();
	 }

	@Override
	public OTFeature createItem(Reference uri) throws Exception {
		return OTFeature.feature(uri);
	}

	@Override
	public OTContainers<OTFeature> delete(OTFeature item) throws Exception {
		throw new Exception("not implemented");
	}

	@Override
	protected String getParamName() throws Exception {
		return OpenTox.params.feature_uris.toString();
	}
	public Form getQuery(Form form) throws Exception {
		for (OTFeature feature: getItems()) {
			if (form == null) form = new Form();
			form.add(getParamName(),feature.uri.toString());
		}
		return form;
	}

}
