package ambit2.rest.task.dsl;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Test;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTDatasets;
import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTFeatures;
import org.opentox.rdf.iterators.RDFFeaturesIterator;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;
import ambit2.rest.test.ResourceTest;

public class OTDatasetTest extends ResourceTest {
	
	@Test
	public void testIsNonEmpty() throws Exception {
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/dataset/1", port));
		Assert.assertFalse(dataset.isEmpty(false));
	}
	@Test
	public void testIsEmpty() throws Exception {
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/dataset/100", port));
		Assert.assertTrue(dataset.isEmpty(false));
	}	
	
	@Test
	public void testEmptyFeatureValues() throws Exception {
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/compound/10",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port));
		OTFeature feature = OTFeature.feature(String.format("http://localhost:%d/feature/3",port));
		OTFeatures features = OTFeatures.features();
		features.add(feature);

		OTDataset subset = dataset.filteredSubsetWithFeatures(features);
		
		Assert.assertTrue(subset.isEmpty());
	}		
	@Test
	public void testHasFeatureValues() throws Exception {
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/compound/11",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port));
		OTFeature feature = OTFeature.feature(String.format("http://localhost:%d/feature/3",port));
		OTFeatures features = OTFeatures.features();
		features.add(feature);

		OTDataset subset = dataset.filteredSubsetWithoutFeatures(features);
		Assert.assertTrue(subset.isEmpty());
	}		
	
	@Test
	public void testAddColumn() throws Exception {
		
		String f3 = String.format("http://localhost:%d/feature/3",port);
		OTFeature feature1 = OTFeature.feature(f3);
		String f1 = String.format("http://localhost:%d/feature/1",port);
		OTFeature feature2 = OTFeature.feature(f1);
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/dataset/1",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			addColumns(feature1).
			addColumns(feature2);

	
		Assert.assertEquals(
				String.format("http://localhost:%d/dataset/1?%s=%s&%s=%s",
						port,
						Reference.encode(OpenTox.params.feature_uris.toString()),
						Reference.encode(f3),
						Reference.encode(OpenTox.params.feature_uris.toString()),
						Reference.encode(f1)),
						dataset.toString());
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1
		//http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1
		int count = 0;
		RDFFeaturesIterator i = new RDFFeaturesIterator(dataset.toString());
		while (i.hasNext()) {
			OTFeature feature = i.next();
			System.out.println(feature);
			if (feature.getUri().toString().equals(f3)) count ++;
			else if (feature.getUri().toString().equals(f1)) count ++;
		}
		Assert.assertEquals(2,count);
	}		
	
	@Test
	public void testRemoveColumn() throws Exception {
		OTFeature feature1 = OTFeature.feature(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/dataset/1",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			addColumns(feature1).
			addColumns(feature2);

		Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1",dataset.toString());

		dataset = dataset.removeColumns();
		Assert.assertEquals("http://localhost:8181/dataset/1",dataset.toString());
	}		
	@Test
	public void testMerge() throws Exception {
		OTFeature feature1 = OTFeature.feature(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset1 = OTDataset.dataset(String.format("http://localhost:%d/dataset/2",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			addColumns(feature1);
		
		OTDataset dataset2 = OTDataset.dataset(String.format("http://localhost:%d/dataset/3",port)).
		withDatasetService(String.format("http://localhost:%d/dataset",port)).
		addColumns(feature2);
		
		OTDatasets datasets = OTDatasets.datasets();
		datasets.withDatasetService(String.format("http://localhost:%d/dataset",port)).add(dataset1).add(dataset2);
		OTDataset dataset = datasets.merge();
		Assert.assertEquals("http://localhost:8181/dataset/R3",dataset.toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from query_results where idquery=3");
		Assert.assertEquals(4,table.getRowCount());		
	}		
	
	
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	
	@Test
	public void testCopy() throws Exception {
		OTFeature feature1 = OTFeature.feature(String.format("http://localhost:%d/feature/3",port));
		OTFeature feature2 = OTFeature.feature(String.format("http://localhost:%d/feature/1",port));
		OTDataset dataset = OTDataset.dataset(String.format("http://localhost:%d/dataset/1?max=1",port)).
			withDatasetService(String.format("http://localhost:%d/dataset",port)).
			addColumns(feature1).
			addColumns(feature2);

		//Assert.assertEquals("http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F3&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1",dataset.toString());

		OTDataset dataset1 = dataset.copy();
		Assert.assertEquals("http://localhost:8181/dataset/R3",dataset1.toString());
	}		
}
