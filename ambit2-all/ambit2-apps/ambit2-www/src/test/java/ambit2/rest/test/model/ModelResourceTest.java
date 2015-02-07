package ambit2.rest.test.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.OTDataset;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.base.data.Property;
import ambit2.rest.OpenTox;
import ambit2.rest.model.ModelResource;
import ambit2.rest.test.ResourceTest;

public class ModelResourceTest extends ResourceTest {
    @Override
    public String getTestURI() {
	return String.format("http://localhost:%d%s/1", port, ModelResource.resource);
    }

    @Test
    public void testHTML() throws Exception {
	testGet(getTestURI(), MediaType.TEXT_HTML);
    }

    @Override
    public boolean verifyResponseHTML(String uri, MediaType media, InputStream in) throws Exception {
	BufferedReader r = new BufferedReader(new InputStreamReader(in));
	String line = null;
	int count = 0;
	while ((line = r.readLine()) != null) {
	    // Assert.assertEquals("1530-32-1 ", line);
	    System.out.println(line);
	    count++;
	}
	return count > 1;
    }

    @Test
    public void testRDFXML() throws Exception {
	testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);
    }

    @Test
    public void testURI() throws Exception {
	testGet(getTestURI(), MediaType.TEXT_URI_LIST);
    }

    @Override
    public boolean verifyResponseURI(String uri, MediaType media, InputStream in) throws Exception {
	BufferedReader r = new BufferedReader(new InputStreamReader(in));
	String line = null;
	int count = 0;
	while ((line = r.readLine()) != null) {
	    Assert.assertEquals(String.format("http://localhost:%d/model/1", port), line);
	    count++;
	}
	return count == 1;
    }

    @Test
    public void testPostDataset() throws Exception {
	Form headers = new Form();
	String dataset = String.format("http://localhost:%d/dataset/1", port);
	headers.add(OpenTox.params.dataset_uri.toString(), dataset);
	testAsyncTask(
		getTestURI(),
		headers,
		Status.SUCCESS_OK,
		String.format("%s?%s=%s", dataset, OpenTox.params.feature_uris.toString(),
			Reference.encode(String.format("%s/predicted", getTestURI()))));
    }

    @Test
    public void testPostDataset1() throws Exception {
	Form headers = new Form();
	String dataset = String.format("http://localhost:%d/dataset/1?max=1", port);
	headers.add(OpenTox.params.dataset_uri.toString(), dataset);
	testAsyncTask(
		getTestURI(),
		headers,
		Status.SUCCESS_OK,
		String.format("%s&%s=%s", dataset, OpenTox.params.feature_uris.toString(),
			Reference.encode(String.format("%s/predicted", getTestURI()))));
    }

    @Test
    public void testPostForeignCompound() throws Exception {
	Form headers = new Form();
	String dataset = "http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1";
	headers.add(OpenTox.params.dataset_uri.toString(), dataset);

	testAsyncTask(
		getTestURI(),
		headers,
		Status.SUCCESS_OK,
		String.format("%s?feature_uris[]=%s", dataset,
			Reference.encode(String.format("%s/predicted", getTestURI()))));
	Assert.fail("Results are not written if the compound is not in the local database");
    }

    @Test
    public void testPostCompound() throws Exception {
	Form headers = new Form();
	String dataset = String.format("http://localhost:%s/compound/7", port);
	headers.add(OpenTox.params.dataset_uri.toString(), dataset);

	testAsyncTask(
		getTestURI(),
		headers,
		Status.SUCCESS_OK,
		String.format("%s?feature_uris[]=%s", dataset,
			Reference.encode(String.format("%s/predicted", getTestURI()))));

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT name,value,idstructure,idchemical FROM values_string join structure using(idstructure) where name='toxTree.tree.cramer.CramerRules' and idchemical=7");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("High (Class III)", table.getValue(0, "value"));

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT name,value,idstructure,idchemical FROM values_string join structure using(idstructure) where name='toxTree.tree.cramer.CramerRules#explanation' and idchemical=7");
	Assert.assertEquals(1, table.getRowCount());

	c.close();
    }

    @Test
    public void testClustering() throws Exception {
	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
		port, port, port),
		null,
		String.format(
			"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
			port, port, port), String.format("http://localhost:%d/algorithm/SimpleKMeans", port));
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED",
		"SELECT name,value_num FROM property_values join properties using(idproperty) where name='Cluster'");
	Assert.assertEquals(4, table.getRowCount());
	c.close();
    }

    @Test
    public void testJ48Test() throws Exception {
	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/4",
		port, port, port, port),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/dataset/2?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/3",
			port, port, port), String.format("http://localhost:%d/algorithm/J48", port));
	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT name,value_num FROM property_values join properties using(idproperty) join struc_dataset using(idstructure) where id_srcdataset=2 and idproperty>4");
	Assert.assertEquals(4, table.getRowCount());
	c.close();
    }

    @Test
    public void testJ48_R() throws Exception {
	setUpDatabase("src/test/resources/src-datasets_model.xml");

	OTDataset dataset = OTDataset
		.dataset(String
			.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/4",
				port, port, port, port));
	dataset.withDatasetService(String.format("http://localhost:%d/dataset", port));
	OTDataset newDataset = dataset.copy();

	System.out.println(newDataset);
	predict(newDataset.getUri().toString(),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
			port, port, port, port), String.format("http://localhost:%d/algorithm/J48", port));

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT id_srcdataset,idstructure,idproperty,name,value_string,value_number FROM values_all join struc_dataset using(idstructure) where id_srcdataset=1 and idproperty>4 order by idstructure");
	Assert.assertEquals(4, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT dependent,idproperty from models join template_def t on t.idtemplate=models.dependent where models.name regexp 'J48'");
	Assert.assertEquals(1, table.getRowCount());

	c.close();

    }

    @Test
    public void testWafflesRandomForest() throws Exception {

	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/4&feature_uris[]=http://localhost:%d/feature/2",
		port, port, port, port),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/7",
			port, port, port, port), String.format("http://localhost:%d/algorithm/WafflesRandomForest",
			port), "10"); // 10 trees in the random forest

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT id_srcdataset,idstructure,idproperty,properties.name,value_string,value_number FROM values_all join struc_dataset using(idstructure) "
				+ "	join properties using(idproperty) join catalog_references r on properties.idreference=r.idreference "
				+ " where id_srcdataset=1 and properties.name='Complex Endpoint' and type='Model' order by idstructure");
	Assert.assertEquals(4, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT dependent,idproperty from models join template_def t on t.idtemplate=models.dependent where models.name regexp 'RandomForest'");
	Assert.assertEquals(1, table.getRowCount());

	// table = c.createQueryTable("EXPECTED",
	// String.format("SELECT * from properties where comments='%s'",Property.opentox_ConfidenceFeature));
	// Assert.assertEquals(1,table.getRowCount());
	/*
	 * table = c.createQueryTable("EXPECTED", String.format(
	 * "SELECT * from property_values join properties using(idproperty) where comments='%s' and value_num is not null"
	 * ,Property.opentox_ConfidenceFeature));
	 * Assert.assertEquals(4,table.getRowCount());
	 */
	table = c.createQueryTable("EXPECTED", String.format(
		"SELECT * from property_values join properties using(idproperty) where comments='%s'",
		"http://www.opentox.org/api/1.1#Test+endpoint"));
	Assert.assertEquals(4, table.getRowCount());
	c.close();

    }

    @Test
    public void testWafflesRandomForestPredictNew() throws Exception {

	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/4&feature_uris[]=http://localhost:%d/feature/2",
		port, port, port, port),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/compound/10/conformer/999?feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/1",
			port, port, port, port), String.format("http://localhost:%d/algorithm/WafflesRandomForest",
			port), "10"); // 10 trees in the random forest

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idstructure,idproperty,properties.name,value_string,value_number FROM values_all "
				+ "	join properties using(idproperty) join catalog_references r on properties.idreference=r.idreference "
				+ " where idstructure=999 and properties.name='Complex Endpoint' and type='Model' order by idstructure");
	Assert.assertEquals(1, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT dependent,idproperty from models join template_def t on t.idtemplate=models.dependent where models.name regexp 'RandomForest'");
	Assert.assertEquals(1, table.getRowCount());

	// table = c.createQueryTable("EXPECTED",
	// String.format("SELECT * from properties where comments='%s'",Property.opentox_ConfidenceFeature));
	// Assert.assertEquals(1,table.getRowCount());
	/*
	 * table = c.createQueryTable("EXPECTED", String.format(
	 * "SELECT * from property_values join properties using(idproperty) where comments='%s' and value_num is not null"
	 * ,Property.opentox_ConfidenceFeature));
	 * Assert.assertEquals(4,table.getRowCount());
	 */
	table = c.createQueryTable("EXPECTED", String.format(
		"SELECT * from property_values join properties using(idproperty) where comments='%s'",
		"http://www.opentox.org/api/1.1#Test+endpoint"));
	Assert.assertEquals(1, table.getRowCount());
	c.close();

    }

    @Test
    public void testJ48() throws Exception {

	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/4",
		port, port, port, port),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
			port, port, port, port), String.format("http://localhost:%d/algorithm/J48", port));

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT id_srcdataset,idstructure,idproperty,properties.name,value_string,value_number FROM values_all join struc_dataset using(idstructure) "
				+ "	join properties using(idproperty) join catalog_references r on properties.idreference=r.idreference "
				+ " where id_srcdataset=1 and properties.name='Complex Endpoint' and type='Model' order by idstructure");
	Assert.assertEquals(4, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT dependent,idproperty,content from models join template_def t on t.idtemplate=models.dependent where models.name regexp 'J48'");
	Assert.assertEquals(1, table.getRowCount());

	table = c.createQueryTable("EXPECTED",
		String.format("SELECT * from properties where comments='%s'", Property.opentox_ConfidenceFeature));
	Assert.assertEquals(1, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			String.format(
				"SELECT * from property_values join properties using(idproperty) where comments='%s' and value_num is not null",
				Property.opentox_ConfidenceFeature));
	Assert.assertEquals(4, table.getRowCount());

	c.close();

    }

    @Test
    public void testRandomForest() throws Exception {

	setUpDatabase("src/test/resources/src-datasets_model.xml");
	predict(String.format(
		"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/4",
		port, port, port, port),
		String.format("http://localhost:%d/feature/4", port),
		String.format(
			"http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
			port, port, port, port), String.format("http://localhost:%d/algorithm/RandomForest", port));

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT id_srcdataset,idstructure,idproperty,properties.name,value_string,value_number FROM values_all join struc_dataset using(idstructure) "
				+ "	join properties using(idproperty) join catalog_references r on properties.idreference=r.idreference "
				+ " where id_srcdataset=1 and properties.name='Complex Endpoint' and type='Model' order by idstructure");
	Assert.assertEquals(4, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT dependent,idproperty,content from models join template_def t on t.idtemplate=models.dependent where models.name regexp 'RandomForest'");
	Assert.assertEquals(1, table.getRowCount());

	table = c.createQueryTable("EXPECTED",
		String.format("SELECT * from properties where comments='%s'", Property.opentox_ConfidenceFeature));
	Assert.assertEquals(1, table.getRowCount());

	table = c
		.createQueryTable(
			"EXPECTED",
			String.format(
				"SELECT * from property_values join properties using(idproperty) where comments='%s' and value_num is not null",
				Property.opentox_ConfidenceFeature));
	Assert.assertEquals(4, table.getRowCount());

	c.close();

    }

    public void predict(String dataset, String target, String datasetTest, String algorithmURI) throws Exception {
	predict(dataset, target, datasetTest, algorithmURI, null);
    }

    public void predict(String dataset, String target, String datasetTest, String algorithmURI, String parameter)
	    throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT id_srcdataset,idstructure,idproperty,name,value_string,value_number FROM values_all join struc_dataset using(idstructure) where id_srcdataset=1 and idproperty=4 order by idstructure");
	Assert.assertEquals(3, table.getRowCount());
	c.close();
	// First create a model
	Form headers = new Form();
	headers.add(OpenTox.params.dataset_uri.toString(), dataset);
	if (target != null)
	    headers.add(OpenTox.params.target.toString(), target);
	if (parameter != null)
	    headers.add(OpenTox.params.parameters.toString(), parameter);

	String wekaURI = String.format("http://localhost:%d%s/3", port, ModelResource.resource);

	testAsyncTask(algorithmURI, headers, Status.SUCCESS_OK, wekaURI);

	headers = new Form();
	if (target != null)
	    headers.add(OpenTox.params.target.toString(), target);
	headers.add(OpenTox.params.dataset_uri.toString(), datasetTest);
	testAsyncTask(
		wekaURI,
		headers,
		Status.SUCCESS_OK,
		String.format("%s%sfeature_uris[]=%s", datasetTest, datasetTest.indexOf("?") > 0 ? "&" : "?",
			Reference.encode(String.format("%s/predicted", wekaURI))));

    }

}