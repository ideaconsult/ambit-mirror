package ambit2.rest.task;

import org.opentox.dsl.OTAlgorithm;
import org.opentox.dsl.OTAlgorithms;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTModel;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.rest.OpenTox;

/**
 * 
<pre>
http://opentox.org/dev/apis/api-1.2/Algorithm

Superservice
URL parameters:
dataset_uri
feature_uris[]
feature_calculation
feature_selection
model_learning
applicability_domain
validation_service
dataset_service
parameter

TP header:
subjectid 
</pre>
 * @author nina
 *
 * @param <USERID>
 */
public class CallableBuilder<USERID> extends CallablePOST<USERID> {

	public CallableBuilder(Form form,Reference root,USERID token) throws Exception {
		this(MediaType.TEXT_URI_LIST,form.getWebRepresentation(),root,token);
	}	
	

	public CallableBuilder(MediaType media, 
			  Representation input,
			  Reference root,
			  USERID token) {
		this(media,input,1500,root,token);
	}
	public CallableBuilder(MediaType media, 
			  Representation input,
			  long pollInterval,
			  Reference root,
			  USERID token) {
		super(media,input,pollInterval,root,token);
	}
	
	protected String getPredictionFeature(Form form) {
		String target = form.getFirstValue(OpenTox.params.target.toString());
		if (target!=null) target = target.trim();
		return target;
	}	
	
	@Override
	public TaskResult doCall() throws Exception {
		Form form = new Form(input);
		String dataset_service = getDatasetService(form);
		String dataset_uri = getDatasetURI(form);
		String target = getPredictionFeature(form);
		OTFeature prediction_feature = target == null?null:OTFeature.feature(target);
		
		String[] feature_calculation = getAlgorithms(form, OpenTox.params.feature_calculation.toString());
		
		OTDataset dataset = dataset_uri!=null?OTDataset.dataset(dataset_uri).withDatasetService(dataset_service):null;
		if (target != null) {
			if (dataset == null) throw new Exception("No dataset!");
			dataset = dataset.removeColumns();
			dataset = dataset.addColumns(prediction_feature);
			dataset = dataset.copy();
		}
		
		
		//we have some descriptors to calculate before building a model
		if ((feature_calculation!=null) && (feature_calculation.length>0)) {
			if (dataset == null) throw new Exception("No dataset!");
			dataset = buildDataset(dataset.getUri().toString(), dataset_service, feature_calculation, form);
		}
		
		String[] model_learning = getAlgorithms(form, OpenTox.params.model_learning.toString());
		if ((model_learning!=null) && (model_learning.length>0)) { //model
			OTAlgorithm algorithm = OTAlgorithm.algorithm(model_learning[0]);
			try {
				OTModel model = algorithm.process(dataset, prediction_feature);
				return new TaskResult(model.getUri().toString());
			} catch (Exception x) {
				throw x;
			}
		} else {
			if (dataset==null)  throw new Exception("No dataset!");
			return new TaskResult(dataset.getUri().toString());
		}
	}
	
	protected OTDataset buildDataset(String datasetURI, 
				String dataset_service, 
				String[] feature_calculation,
				Form form) throws Exception {
		
		OTAlgorithms algorithms = OTAlgorithms.algorithms();
		algorithms.withDatasetService(dataset_service);
		
		for (String algoUri : feature_calculation)
			if (algoUri!=null) 
				algorithms.add(OTAlgorithm.algorithm(algoUri.trim()).withParams(form));
		
		return algorithms.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service),true);
	}
}
