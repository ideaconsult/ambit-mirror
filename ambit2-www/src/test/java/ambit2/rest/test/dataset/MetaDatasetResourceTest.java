package ambit2.rest.test.dataset;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Reference;

import ambit2.base.data.SourceDataset;
import ambit2.rest.rdf.RDFMetaDatasetIterator;
import ambit2.rest.test.ResourceTest;

public class MetaDatasetResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1/metadata", port);
	}

	@Test
	public void testRDFXML() throws Exception {
		Reference ref = new Reference(
				String.format("http://localhost:%d/dataset/1/metadata", 
						port
						));
						
		RDFMetaDatasetIterator iterator = new RDFMetaDatasetIterator(ref);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		int count = 0;
		while (iterator.hasNext()) {
			SourceDataset target = iterator.next();
			Assert.assertEquals(1,target.getId());
			Assert.assertEquals("Dbunit dataset", target.getName());
			Assert.assertEquals("XLogP", target.getTitle());
			Assert.assertEquals("XLogP", target.getURL());
			count++;
		}
		Assert.assertEquals(1,count);
		iterator.close();
	}	
}
