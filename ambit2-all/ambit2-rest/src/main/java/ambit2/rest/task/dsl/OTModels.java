package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

import ambit2.rest.OpenTox;
import ambit2.rest.rdf.OT.OTProperty;

public class OTModels extends OTContainers<OTModel> {
	protected OTFeatures predicted;
	
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
	 public static OTModels models() throws Exception  { 
		    return new OTModels();
	 }
	 public OTModels load(OTProperty[] varTypes) throws Exception  {
		 for (OTModel model: items) 
			 model.load(varTypes);
		 return this;
	 }
	 public OTFeatures predictedVariables() throws Exception  {
		 if (items==null) return null;
		 OTFeatures features = OTFeatures.features();
		 for (OTModel model: items)
			 features.add(model.predictedVariables);
		 return features;
	 }
}
