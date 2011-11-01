package ambit2.rest.test.structure;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.OpenTox;



public class ConformerResourceTest extends CompoundResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/10/conformer/100214", port);
	}

	@Test
	public void testDeleteCompound() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=10");
		Assert.assertEquals(2,table.getRowCount());			
		c.close();
		
		Response response =  testDelete(
					String.format("http://localhost:%d/compound/10/conformer/999", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where idstructure = 999 and id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=10");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=10");
		Assert.assertEquals(1,table.getRowCount());			
		c.close();
				
		
	}	
	
	@Test
	public void testDeleteConformerAndProperties() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=11");
		Assert.assertEquals(1,table.getRowCount());			
		c.close();
		
		Response response =  testDelete(
					String.format("http://localhost:%d/compound/11/conformer/100215", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where idstructure = 100215 and id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=11");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=11");
		Assert.assertEquals(0,table.getRowCount());			
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idstructure = 100215");
		Assert.assertEquals(0,table.getRowCount());				
		c.close();
				
		
	}		
	
	@Test
	public void testDeleteConformerFromDataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=11");
		Assert.assertEquals(1,table.getRowCount());			
		c.close();
		
		Form form = new Form();
		form.add(OpenTox.params.compound_uris.toString(),
					String.format("http://localhost:%d/compound/11/conformer/100215",port));
		Reference target = new Reference(String.format("http://localhost:%d/dataset/1", port));
		target.setQuery(form.getQueryString());
		//delete doesn't allow passing body content
		Response response =  testDelete(target.toString(),
					MediaType.APPLICATION_WWW_FORM,
					null);
		
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where idstructure = 100215 and id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		//only removed from struc_dataset
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=11");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=11");
		Assert.assertEquals(1,table.getRowCount());			
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idstructure = 100215");
		Assert.assertEquals(1,table.getRowCount());				
		c.close();
				
		
	}		
	

}
