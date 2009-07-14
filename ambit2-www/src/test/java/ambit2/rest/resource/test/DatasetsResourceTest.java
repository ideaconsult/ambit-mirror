package ambit2.rest.resource.test;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Response;

import ambit2.rest.AmbitApplication;

public class DatasetsResourceTest extends ResourceTest {

	@Test
	public void getDatasets() throws Exception {
		
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get("http://localhost:8080"+AmbitApplication.datasets);
		String out = response.getEntity().getText();
		System.out.println(out);
		//<?xml version="1.0" encoding="UTF-8"?><datasets xmlns="http://opentox.org/1.0"><link href="http://localhost:8080/dataset/Dataset+1"/><link href="http://localhost:8080/dataset/Dataset+2"/><link href="http://localhost:8080/dataset/Dbunit+dataset"/></datasets>
/*
	    // parse an XML document into a DOM tree
	    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = parser.parse("http://localhost:8080"+AmbitApplication.datasets);

	    // create a SchemaFactory capable of understanding WXS schemas
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	    // load a WXS schema, represented by a Schema instance
	    Source schemaFile = new StreamSource(getClass().getClassLoader().getResourceAsStream("schema/datasets.xsd"));
	    Schema schema = factory.newSchema(schemaFile);

	    // create a Validator instance, which can be used to validate an instance document
	    Validator validator = schema.newValidator();

	    // validate the DOM tree
        validator.validate(new DOMSource(document));
*/
	}
	@Test
	public void getDataset() throws Exception {
		Client client = new Client(Protocol.HTTP);
		
		Response response =	client.get("http://localhost:8080"+AmbitApplication.datasets + "/"+Reference.encode("Dataset 2"));
		String out = response.getEntity().getText();
		System.out.println(out);
		
		//<?xml version="1.0" encoding="UTF-8"?><dataset id="3" name="Dataset 2" xmlns="http://opentox.org/1.0"><link href="http://localhost:8080/dataset/Dataset+2"/></dataset>
		/*
		Diff diff = new Diff(new FileReader(
			new File("./etc/control-xml/control-web-races.xml")),
			new StringReader(response.getEntity().getText()));
		assertTrue(diff.toString(), diff.identical());
		*/
	}	
	
}
