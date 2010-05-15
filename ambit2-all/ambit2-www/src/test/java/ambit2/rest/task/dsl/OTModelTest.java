package ambit2.rest.task.dsl;

import junit.framework.Assert;

import org.junit.Test;

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
