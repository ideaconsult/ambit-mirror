package ambit2.rest.task.dsl;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.rest.task.RemoteTask;
import ambit2.rest.test.ResourceTest;

public class OTModelTest extends ResourceTest {
	@Override
	public void testGetJavaObject() throws Exception {
	}
	@Test
	public void testCalculateDescriptorsRemote() throws Exception {
		String dataset_service = "http://ambit.uni-plovdiv.bg:8080/ambit2/dataset";
		String prefix = "http://ambit.uni-plovdiv.bg:8080/ambit2";
		OTModel model = OTModel.model().withDatasetService(dataset_service);
		
		OTAlgorithm alg = OTAlgorithm.algorithm().withUri(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",prefix));
		OTFeature feature = OTFeature.feature().withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11389").withAlgorithm(alg);
		
		OTAlgorithm alg1 = OTAlgorithm.algorithm().withUri(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",prefix));
		OTFeature feature1 = OTFeature.feature().withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/296298").withAlgorithm(alg1);
		
		OTFeatures features = OTFeatures.features();
		features.withDatasetService(dataset_service).add(feature).add(feature1);
		
		OTDataset result = model.calculateDescriptors(
				features,
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri(String.format("%s/compound/1",prefix))
				);
		Assert.fail("verify content");
	}	
	
	@Test
	public void testCalculateDescriptorsLocal() throws Exception {
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTModel.model().withDatasetService(dataset_service);
		
		OTAlgorithm alg = OTAlgorithm.algorithm().withUri(String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",port));
		OTFeature feature = OTFeature.feature().withAlgorithm(alg);
		
		OTAlgorithm alg1 = OTAlgorithm.algorithm().withUri(String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",port));
		OTFeature feature1 = OTFeature.feature().withAlgorithm(alg1);
		
		OTFeatures features = OTFeatures.features();
		features.withDatasetService(dataset_service).add(feature).add(feature1);
		
		OTDataset result = model.calculateDescriptors(
				features,
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri(String.format("http://localhost:%d/dataset/1",port))
				);
		Assert.assertEquals("http://localhost:8181/dataset/R3", result.toString());
	}	
	
	@Test
	public void testCalculateModel() throws Exception {
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTModel.model().withDatasetService(dataset_service).
			withUri(String.format("http://localhost:%d/model/1",port));
		
		OTDataset result = model.process(
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri(String.format("http://localhost:%d/dataset/1",port))
				);
		Assert.assertEquals("http://localhost:8181/dataset/R3?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F1%2Fpredicted", result.toString());
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2
	}	
	
	@Test
	public void testCreateRegressionModel() throws Exception {

		String dataset_service = String.format("http://localhost:%d/dataset",port);
		String prefix = String.format("http://localhost:%d",port);
		
		OTDataset inputDataset = OTDataset.dataset().withDatasetService(dataset_service).withUri(String.format("%s/dataset/3", prefix));
		OTAlgorithm alg1 = OTAlgorithm.algorithm().withUri(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",prefix));
		OTDataset calculated1 = alg1.process(inputDataset);
		
		OTAlgorithm alg2 = OTAlgorithm.algorithm().withUri(String.format("%s/algorithm/org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor",prefix));
		OTDataset calculated2 = alg2.process(inputDataset);
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(dataset_service).add(calculated1).add(calculated2);
		OTDataset dataset = datasets.merge();

		OTAlgorithm lr = OTAlgorithm.algorithm().withUri(String.format("%s/algorithm/LR",prefix));
		OTModel model = lr.process(dataset, OTFeature.feature().withUri(String.format("%s/feature/4", prefix)));
		
		OTModel supermodel = OTSuperModel.model().withUri(model.uri).withDatasetService(dataset_service);
		
		System.out.println(supermodel.process(dataset));
		
		OTDataset result = supermodel.process(
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri(String.format("http://localhost:%d/dataset/1?max=5",port))
				);
		System.out.println(result);
		
	}		
	
	@Test
	public void testCreateAndCalculateModel() throws Exception {

		RemoteTask task = new RemoteTask(new Reference(String.format("http://localhost:%d/algorithm/toxtreeskinirritation",port)),
				MediaType.TEXT_URI_LIST,
				null,
				Method.POST,
				null
				);
		while (!task.poll()) ;
		
		String dataset_service = String.format("http://localhost:%d/dataset",port);
		OTModel model = OTSuperModel.model().withDatasetService(dataset_service).
			withUri(task.getResult());
		
		OTDataset result = model.process(
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri(String.format("http://localhost:%d/dataset/1?max=2",port))
				);
		Assert.assertEquals("http://localhost:8181/compound/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F12&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F11", result.toString());
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2
	}			
	@Test
	public void testCalculateModelRemote() throws Exception {
		String dataset_service = "http://ambit.uni-plovdiv.bg:8080/ambit2/dataset";
		OTModel model = OTModel.model().withDatasetService(dataset_service).
			withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/model/259260");
		
		OTDataset result = model.process(
				OTDataset.dataset().withDatasetService(dataset_service).
				withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/compound/112")
				);
		System.out.println(result);
	}		
}
