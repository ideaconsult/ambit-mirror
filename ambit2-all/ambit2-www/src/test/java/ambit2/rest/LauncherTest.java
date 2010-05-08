package ambit2.rest;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.test.ResourceTest;

public class LauncherTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/launcher",port);
	}
	
	@Test
	public void testPostDataset() throws Exception {
		Form headers = new Form();
		String dataset = String.format("http://localhost:%d/dataset/1", port);
		String model = String.format("http://localhost:%d/model/2", port);
		
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		headers.add(OpenTox.params.model_uri.toString(), model);
		testAsyncTask(getTestURI(), headers, Status.SUCCESS_OK, String.format(
				"%s?%s=%s",
				dataset, 
				OpenTox.params.feature_uris.toString(),
				Reference.encode(String
						.format("%s/predicted", getTestURI()))));
	}
	
	@Test
	public void testSuperServiceSimpleModel() throws Exception {
		Form headers = new Form();
		String dataset = String.format("http://localhost:%d/dataset/1", port);
		String model = String.format("http://localhost:%d/model/2", port);
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		headers.add("url", model);

		testAsyncTask(superservice, headers, Status.SUCCESS_OK, String.format(
				"%s?%s=%s",
				dataset, 
				OpenTox.params.feature_uris.toString(),
				Reference.encode(String
						.format("%s/predicted", model))));
	}
	//http://apps.ideaconsult.net:8080/ambit2/model/33
		//http://apps.ideaconsult.net:8080/ambit2/compound/100
	
	@Test
	public void testSuperServiceMultiAlgorithmsModel() throws Exception {
		Form headers = new Form();
		String dataset_service = String.format("http://localhost:%d/dataset", port);
		String dataset = String.format("http://localhost:%d/dataset/1", port);
		String algo1 = String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", port);
		String algo2 = String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor", port);
		String algo3 = String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor", port);
		
		String algo4 = String.format("http://localhost:%d/algorithm/ambit2.descriptors.MolecularWeight", port);
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		//headers.add(OpenTox.params.dataset_service.toString(), dataset_service);
		headers.add("url", algo1);
		headers.add("url", algo2);
		headers.add("url", algo3);
		headers.add("url", algo4);

		testAsyncTask(superservice, headers, Status.SUCCESS_OK, String.format(
				"%s/%s",
				dataset_service,
				"R3"));
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM query where idquery=3");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join template_def using(idtemplate) where idquery=3");
		Assert.assertEquals(32,table.getRowCount());		
		c.close();	
		

	}	
	
	@Test
	public void testSuperServiceRemoteDescriptors() throws Exception {
		Form headers = new Form();
		String dataset_service = String.format("http://localhost:%d/dataset", port);
		String dataset = String.format("http://localhost:%d/dataset/1?max=1", port);
		String algo1 = "http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem/ChiPathDescriptor";
		String algo2 = "http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem/KappaShapeIndicesDescriptor";
		String algo3 = "http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem/PetitjeanShapeIndexDescriptor";
		
		String algo4 = String.format("http://localhost:%d/algorithm/ambit2.descriptors.MolecularWeight", port);
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		headers.add(OpenTox.params.dataset_service.toString(), dataset_service);
		headers.add("url", algo1);
		headers.add("url", algo2);
		headers.add("url", algo3);
		headers.add("url", algo4);

		testAsyncTask(superservice, headers, Status.SUCCESS_OK, String.format(
				"%s/%s",
				dataset_service,
				"R3"));
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM query where idquery=3");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join template_def using(idtemplate) where idquery=3");
		Assert.assertEquals(4,table.getRowCount());		
		c.close();		
	}		
}
