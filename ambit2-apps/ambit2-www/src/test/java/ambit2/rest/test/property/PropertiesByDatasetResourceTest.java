package ambit2.rest.test.property;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.rest.AbstractResource;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.test.ResourceTest;

public class PropertiesByDatasetResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/R2%s", port,OpenTox.URI.dataset.getURI(),PropertyResource.featuredef);
	}
	
	@Test
	public void testRDFXML_Query() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		int count = 0;
		while (iterator.hasNext()) {
			
			Property p = iterator.next();
			Assert.assertEquals("CAS",p.getName());
			Assert.assertEquals(3,p.getId());
			count++;
		}
		iterator.close();
		Assert.assertEquals(1,count);
	}	
	
	@Test
	public void testRDFXML_Dataset() throws Exception {
		String dataset = String.format("http://localhost:%d%s/2?%s=%d", port,OpenTox.URI.dataset.getURI(),AbstractResource.max_hits,1);
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(dataset));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		int count = 0;
		while (iterator.hasNext()) {
			
			Property p = iterator.next();
			System.out.println(p);
			Assert.assertEquals("CAS",p.getName());
			Assert.assertEquals(3,p.getId());
			count++;
		}
		iterator.close();
		Assert.assertEquals(1,count);
	}		
}
