package ambit2.rest.task.dsl;

import junit.framework.Assert;

import org.junit.Test;
import org.opentox.dsl.OTAlgorithms;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTModel;
import org.opentox.dsl.OTSuperModel;

import ambit2.rest.test.ResourceTest;

public class OTRemoteModelTest extends ResourceTest {
	@Test
	public void testModelVarsTUM() throws Exception {
		
		OTModel model = OTSuperModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_8").
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port));
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(264,features.size());

		OTDataset result  = model.process(OTDataset.dataset(String.format("http://194.141.0.136:%d/dataset/1", port)).
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port)));
		
		result.getUri().toString().equals(
				"http://194.141.0.136:8181/dataset/1?feature_uris[]=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fambit2%2Fmodel%2F16%2Fpredicted"
				);

	}		
	
	@Test
	public void testModelTUM() throws Exception {
		
		OTModel model = OTSuperModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_22").
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset");
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(9,features.size());
		/*
		OTAlgorithms algorithms = OTAlgorithms.algorithms();
		for (OTFeature feature : features.getItems())
			if (feature!=null) 
				algorithms.add(feature.algorithm().getAlgorithm());

		Assert.assertEquals(9,algorithms.size());		
	*/
		OTDataset result  = model.process(OTDataset.dataset("http://apps.ideaconsult.net:8080/ambit2/dataset/R864").
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset"));
		
		System.out.println(result);
		/*
		result.getUri().toString().equals(
				"http://194.141.0.136:8181/dataset/1?feature_uris[]=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fambit2%2Fmodel%2F16%2Fpredicted"
				);
				*/

	}		
	@Test
	public void testTUMPrecalculatedDescriptors() throws Exception {
		
		OTModel model = OTModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_10").
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port));
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(264,features.size());

		OTDataset result  = model.process(OTDataset.dataset(String.format("http://194.141.0.136:%d/dataset/1", port)).
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port)));
		
		result.getUri().toString().equals(
				"http://194.141.0.136:8181/dataset/1?feature_uris[]=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fambit2%2Fmodel%2F16%2Fpredicted"
				);

	}	
	@Test
	/**
	 * 
1) model  (MLR)
http://opentox.ntua.gr:3003/model/195
training dataset
http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/R7798
prediction feature
http://ambit.uni-plovdiv.bg:8080/ambit2/feature/255510
	 */
	public void testModelVarsNTUA() throws Exception {
		
		OTModel model = OTSuperModel.model("http://opentox.ntua.gr:3003/model/195").
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port));
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(4,features.size());

		OTDataset result  = model.process(OTDataset.dataset(String.format("http://194.141.0.136:%d/dataset/1", port)).
					withDatasetService(String.format("http://194.141.0.136:%d/dataset", port)));
		
		System.out.println(result.getUri());

	}	
	@Test
	public void testModelVarsEos() throws Exception {

		OTModel model = OTSuperModel.model("http://apps.ideaconsult.net:8080/ambit2/model/33").
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset");
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(4,features.size());
		
		OTAlgorithms algorithms = OTAlgorithms.algorithms();
		
		for (OTFeature feature : features.getItems())
			if (feature!=null) 
				algorithms.add(feature.algorithm().getAlgorithm());

		Assert.assertEquals(3,algorithms.size());
		
		OTDataset result  = model.process(OTDataset.dataset(String.format("http://apps.ideaconsult.net:8080/ambit2/compound/684", port)).
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset"));
		
		System.out.println(result.getUri().toString());
		

	}		
}
