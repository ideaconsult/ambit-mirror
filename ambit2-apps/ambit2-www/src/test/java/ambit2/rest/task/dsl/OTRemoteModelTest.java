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
