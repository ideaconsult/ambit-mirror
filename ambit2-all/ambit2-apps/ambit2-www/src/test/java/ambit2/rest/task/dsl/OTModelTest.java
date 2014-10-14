package ambit2.rest.task.dsl;

import junit.framework.Assert;

import org.junit.Test;
import org.opentox.dsl.OTAlgorithm;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTDatasets;
import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTModel;
import org.opentox.dsl.OTSuperModel;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.rest.test.ResourceTest;

public class OTModelTest extends ResourceTest {
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	@Test
	public void testCalculateDescriptorsLocal() throws Exception {
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTModel.model().withDatasetService(dataset_service);
		
		OTAlgorithm alg = OTAlgorithm.algorithm(String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",port));
		OTFeature feature = OTFeature.feature().withAlgorithm(alg);
		
		OTAlgorithm alg1 = OTAlgorithm.algorithm(String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",port));
		OTFeature feature1 = OTFeature.feature().withAlgorithm(alg1);
		
		OTFeatures features = OTFeatures.features();
		features.withDatasetService(dataset_service).add(feature).add(feature1);
		
		OTDataset result = model.calculateDescriptors(
				features,
				OTDataset.dataset(String.format("http://localhost:%d/dataset/1",port)).withDatasetService(dataset_service)
				);
		Assert.assertEquals("http://localhost:8181/dataset/R3", result.toString());
	}	
	
	@Test
	public void testCalculateModel() throws Exception {
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/1",port)).withDatasetService(dataset_service);

		
		OTDataset result = model.process(
				OTDataset.dataset(String.format("http://localhost:%d/dataset/1",port)).withDatasetService(dataset_service)
				);
		//Assert.assertEquals("http://localhost:8181/dataset/R3?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F1%2Fpredicted", result.toString());
		Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F1%2Fpredicted", result.toString());
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2
	}	
	
	@Test
	public void testCreateRegressionModel() throws Exception {

		String dataset_service = String.format("http://localhost:%d/dataset",port);
		String prefix = String.format("http://localhost:%d",port);
		
		OTDataset inputDataset = OTDataset.dataset(String.format("%s/dataset/3", prefix)).withDatasetService(dataset_service);
		OTAlgorithm alg1 = OTAlgorithm.algorithm(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",prefix));
		OTDataset calculated1 = alg1.process(inputDataset);
		
		OTAlgorithm alg2 = OTAlgorithm.algorithm(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor",prefix));
		OTDataset calculated2 = alg2.process(inputDataset);
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(dataset_service).add(calculated1).add(calculated2);
		OTDataset dataset = datasets.merge();

		OTAlgorithm lr = OTAlgorithm.algorithm(String.format("%s/algorithm/LR",prefix));
		OTModel model = lr.process(dataset, OTFeature.feature(String.format("%s/feature/4", prefix)));
		
		OTModel supermodel = OTSuperModel.model(model.getUri()).withDatasetService(dataset_service);
		
		System.out.println(supermodel.process(dataset));
		
		OTDataset result = supermodel.process(
				OTDataset.dataset(String.format("http://localhost:%d/dataset/1?max=5",port)).withDatasetService(dataset_service)
				);
		System.out.println(result);
		
	}		
	
	@Test
	public void testCreateAndCalculateModel() throws Exception {

		RemoteTask task = new RemoteTask(new Reference(String.format("http://localhost:%d/algorithm/toxtreeskinirritation",port)),
				MediaType.TEXT_URI_LIST,
				null,
				Method.POST
				);
		while (!task.poll()) ;
		
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTSuperModel.model(task.getResult()).withDatasetService(dataset_service);
		Assert.assertEquals(String.format("http://localhost:%d/model/3",port), model.getUri().toString());
		
		OTDataset inputDataset = OTDataset.dataset(String.format("http://localhost:%d/query/substructure?search=c1ccccc1",port));
		inputDataset.withDatasetService(dataset_service);
		inputDataset = inputDataset.copy();
		OTDataset result = model.process(inputDataset).withDatasetService(dataset_service);
				
		Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F12&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F11", result.toString());
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2
	}			
	
}
