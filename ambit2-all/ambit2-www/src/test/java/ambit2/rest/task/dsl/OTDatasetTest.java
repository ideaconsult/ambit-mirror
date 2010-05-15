package ambit2.rest.task.dsl;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Test;

import ambit2.rest.test.ResourceTest;

public class OTDatasetTest extends ResourceTest {
	
	@Test
	public void testIsNonEmpty() throws Exception {
		OTDataset dataset = OTDataset.dataset().withUri(String.format("http://localhost:%d/dataset/1", port));
		Assert.assertFalse(dataset.isEmpty(false));
	}
	@Test
	public void testIsEmpty() throws Exception {
		OTDataset dataset = OTDataset.dataset().withUri(String.format("http://localhost:%d/dataset/100", port));
		Assert.assertTrue(dataset.isEmpty(false));
	}	
	
	@Test
	public void testEmptyFeatureValues() throws Exception {
		OTDataset dataset = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/compound/10",port));
		OTFeature feature = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeatures features = OTFeatures.features();
		features.add(feature);

		OTDataset subset = dataset.filteredSubsetWithFeatures(features);
		
		Assert.assertTrue(subset.isEmpty());
	}		
	@Test
	public void testHasFeatureValues() throws Exception {
		OTDataset dataset = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/compound/11",port));
		OTFeature feature = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeatures features = OTFeatures.features();
		features.add(feature);

		OTDataset subset = dataset.filteredSubsetWithoutFeatures(features);
		Assert.assertTrue(subset.isEmpty());
	}		
	
	@Test
	public void testAddColumn() throws Exception {
		OTFeature feature1 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/dataset/1",port)).
			addColumns(feature1).
			addColumns(feature2);

		Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1",dataset);

		Assert.fail("verify if the features are there");
	}		
	
	@Test
	public void testRemoveColumn() throws Exception {
		OTFeature feature1 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/dataset/1",port)).
			addColumns(feature1).
			addColumns(feature2);

		Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1",dataset.toString());

		dataset.removeColumns();
		Assert.assertEquals("http://localhost:8181/dataset/1",dataset.toString());
	}		
	@Test
	public void testMerge() throws Exception {
		OTFeature feature1 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset1 = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/dataset/2",port)).
			addColumns(feature1);
		
		OTDataset dataset2 = OTDataset.dataset().
		withDatasetService(String.format("http://localhost:%d/dataset",port)).
		withUri(String.format("http://localhost:%d/dataset/3",port)).
		addColumns(feature2);
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(String.format("http://localhost:%d/dataset",port)).add(dataset1).add(dataset2);
		OTDataset dataset = datasets.merge();
		Assert.assertEquals("http://localhost:8181/dataset/R3",dataset.toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from query_results where idquery=3");
		Assert.assertEquals(4,table.getRowCount());		
	}		
	
	
	@Test
	public void testMergeRemote() throws Exception {
		String dataset_service = "http://ambit.uni-plovdiv.bg:8080/ambit2/dataset";
		OTFeature feature1 = OTFeature.feature().withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/1");
		OTFeature feature2 = OTFeature.feature().withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/29683");
		
		OTDataset dataset1 = OTDataset.dataset().
			withDatasetService(dataset_service).
			withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1").
			addColumns(feature1);
		
		OTDataset dataset2 = OTDataset.dataset().
		withDatasetService(dataset_service).
		withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/compound/2").
		addColumns(feature2);
		
		OTDataset dataset3 = OTDataset.dataset().
		withDatasetService(dataset_service).
		withUri("http://ambit.uni-plovdiv.bg:8080/ambit2/compound/200").
		addColumns(feature1);		
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(dataset_service).add(dataset1).add(dataset2).add(dataset3);
		OTDataset dataset = datasets.merge();
		Assert.assertTrue(dataset.getUri().toString().startsWith("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/R"));
		Assert.fail("verify there are 3 compounds and 2 features");
	}		
	
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	
	@Test
	public void testCopy() throws Exception {
		OTFeature feature1 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature().withUri(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset = OTDataset.dataset().
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			withUri(String.format("http://localhost:%d/dataset/1?max=1",port)).
			addColumns(feature1).
			addColumns(feature2);

		//Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1",dataset.toString());

		OTDataset dataset1 = OTDataset.dataset().withDatasetService(String.format("http://localhost:%d/dataset",port)).copy(dataset);
		Assert.assertEquals("http://localhost:8181/dataset/R3",dataset1.toString());
	}		
}
