package ambit2.rest.task.dsl;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.rest.legacy.OTAlgorithms;
import ambit2.rest.legacy.OTDataset;
import ambit2.rest.legacy.OTFeature;
import ambit2.rest.legacy.OTFeatures;
import ambit2.rest.legacy.OTModel;
import ambit2.rest.legacy.OTSuperModel;
import ambit2.rest.test.ResourceTest;

public class OTRemoteModelTest extends ResourceTest {
	@Test
	public void testModelVarsTUM() throws Exception {
		
		OTModel model = OTSuperModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_8","test").
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
		
		OTModel model = OTSuperModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_22","test").
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
		
		OTModel model = OTModel.model("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_j48_10","test").
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
	public void testModelVarsEos() throws Exception {

		OTModel model = OTSuperModel.model("http://apps.ideaconsult.net:8080/ambit2/model/33","test").
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset");
		
		OTFeatures features = model.load().getIndependentVariables();
		Assert.assertEquals(4,features.size());
		
		OTAlgorithms algorithms = OTAlgorithms.algorithms(null,"test");
		
		for (OTFeature feature : features.getItems())
			if (feature!=null) 
				algorithms.add(feature.algorithm().getAlgorithm());

		Assert.assertEquals(3,algorithms.size());
		
		OTDataset result  = model.process(OTDataset.dataset(String.format("http://apps.ideaconsult.net:8080/ambit2/compound/684", port)).
					withDatasetService("http://apps.ideaconsult.net:8080/ambit2/dataset"));
		
		System.out.println(result.getUri().toString());
		

	}		
}
