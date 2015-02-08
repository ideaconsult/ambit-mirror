package ambit2.rest.test.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ISourceDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.test.ProtectedResourceTest;

public class DatasetsResourceTest extends ProtectedResourceTest {

    @Override
    public String getTestURI() {
	return String.format("http://localhost:%d/dataset", port);
    }

    @Test
    public void testURI() throws Exception {
	testGet(getTestURI(), MediaType.TEXT_URI_LIST);
    }

    @Override
    public boolean verifyResponseURI(String uri, MediaType media, InputStream in) throws Exception {
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	int count = 0;
	while ((line = reader.readLine()) != null) {
	    System.out.println(line);
	    count++;
	}
	return count == 3;
    }

    @Test
    public void testHTML() throws Exception {
	testGet(getTestURI(), MediaType.TEXT_HTML);
    }

    @Override
    public boolean verifyResponseHTML(String uri, MediaType media, InputStream in) throws Exception {
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	int count = 0;
	while ((line = reader.readLine()) != null) {
	    System.out.println(line);
	    count++;
	}
	return count > 0;
    }

    public Response testPost(String uri, MediaType media, String content) throws Exception {

	Request request = new Request();
	Client client = new Client(Protocol.HTTP);

	request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "guest", "guest"));
	request.setResourceRef(uri);
	request.setMethod(Method.POST);
	request.setEntity(content, media);
	// request.getAttributes().put("org.restlet.http.headers", headers);
	request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
	return client.handle(request);

    }

    /*
     * public Response testPostFile(String uri, MediaType media, Representation
     * file) throws Exception { Request request = new Request(); Client client =
     * new Client(Protocol.HTTP); ChallengeScheme scheme =
     * ChallengeScheme.HTTP_BASIC; ChallengeResponse authentication = new
     * ChallengeResponse(scheme, "guest", "guest");
     * request.setChallengeResponse(authentication);
     * request.setResourceRef(uri); request.setMethod(Method.POST);
     * request.setEntity(file);
     * request.getClientInfo().getAcceptedMediaTypes().add(new
     * Preference<MediaType>(media)); return client.handle(request); }
     */
    @Test
    public void testCreateEntry() throws Exception {

	InputStream in = getClass().getClassLoader().getResourceAsStream("input.sdf");

	StringBuilder b = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	while ((line = reader.readLine()) != null) {
	    b.append(line);
	    b.append('\n');
	}

	testAsyncPoll(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_MDLSDF,
		new StringRepresentation(b.toString(), ChemicalMediaType.CHEMICAL_MDLSDF), Method.POST, new Reference(
			String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(12, table.getRowCount());
	c.close();

    }

    @Test
    public void testCreateEntryCML() throws Exception {

	InputStream in = getClass().getClassLoader().getResourceAsStream("diamantane.cml");

	StringBuilder b = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	while ((line = reader.readLine()) != null) {
	    b.append(line);
	    b.append('\n');
	}

	testAsyncPoll(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_CML,
		new StringRepresentation(b.toString(), ChemicalMediaType.CHEMICAL_CML), Method.POST, new Reference(
			String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(8, table.getRowCount());
	c.close();

    }

    public void testCreateEntryCMLNano() throws Exception {

	InputStream in = getClass().getClassLoader().getResourceAsStream("nano.nmx");

	StringBuilder b = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	while ((line = reader.readLine()) != null) {
	    b.append(line);
	    b.append('\n');
	}

	testAsyncPoll(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_CML,
		new StringRepresentation(b.toString(), ChemicalMediaType.NANO_CML), Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,formula FROM structure join chemicals using(idchemical) where type_structure='NANO' order by idchemical");
	Assert.assertEquals(2, table.getRowCount());
	Assert.assertNotNull(table.getValue(1, "formula"));
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT * FROM properties join catalog_references using(idreference) where name='Size' and units='nm' and title='NanoMaterialMeasurement'");
	Assert.assertEquals(1, table.getRowCount());
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT * FROM properties join catalog_references using(idreference) where name='MaterialType' and title='NanoMaterialMeasurement'");
	Assert.assertEquals(1, table.getRowCount());
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT * FROM properties join catalog_references using(idreference) where name='Name' and title regexp '^NanoMaterialLabel'");
	Assert.assertEquals(2, table.getRowCount());
	c.close();
    }

    @Test
    public void testCreateEntry_TUM500() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("dataset_tum_500.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, table.getRowCount());

	table = c
		.createQueryTable(
			"ANNOTATIONS",
			"SELECT name,rdf_type,predicate,object FROM property_annotation join properties using(idproperty)\n"
				+ "where name='TUM_CDK_ATSc4' and rdf_type='ModelConfidenceFeature' and predicate regexp \"confidenceOf$\" ");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertTrue(table.getValue(0, "object").toString().startsWith("/feature/"));
	table = c
		.createQueryTable(
			"ANNOTATIONS",
			"SELECT name,rdf_type,predicate,object FROM property_annotation join properties using(idproperty)\n"
				+ "where name='TUM_CDK_khs.ssssB' and rdf_type='ModelConfidenceFeature' and predicate regexp \"confidenceOf$\" and\n"
				+ "object='http://opentox.tum.de/feature/XXX'");
	Assert.assertEquals(1, table.getRowCount());

	c.close();
    }

    @Test
    public void testCreateEntryRDF() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("import_dataset2.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();
    }

    @Test
    public void testPostNewQueryResult() throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED",
		"SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
	Assert.assertEquals(2, table.getRowCount());
	c.close();

	Form form = new Form();
	form.add(OpenTox.params.dataset_uri.toString(), String.format("http://localhost:%d/dataset/R1", port));

	testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset", port)), MediaType.TEXT_URI_LIST,
		form.getWebRepresentation(), Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/R3", port)));

	c = getConnection();
	table = c.createQueryTable("EXPECTED",
		"SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
	Assert.assertEquals(3, table.getRowCount());
	table = c
		.createQueryTable("EXPECTED",
			"SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where title='temp'");
	Assert.assertEquals(3, table.getRowCount());
	c.close();

	CreateEntryRDF("R3_descriptors.rdf");

	Assert.fail("verify");

    }

    @Test
    public void testCreateEntryRDF1() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("37.rdf");

	// CreateEntryRDF("FeatureGenerationExample.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, table.getRowCount());
	c.close();
    }

    @Test
    public void testCreateEntryRDF2() throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("2765_2.rdf");

	// CreateEntryRDF("FeatureGenerationExample.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(6, table.getRowCount());
	c.close();
    }

    public void CreateEntryRDF(String name) throws Exception {

	InputStream in = getClass().getClassLoader().getResourceAsStream(name);

	StringBuilder b = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	String line = null;
	while ((line = reader.readLine()) != null) {
	    b.append(line);
	    b.append('\n');
	}

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, new StringRepresentation(b.toString(),
		MediaType.APPLICATION_RDF_XML), Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/4", port)));

    }

    public void testCreateEntryFromFileRDF() throws Exception {
	URL url = getClass().getClassLoader().getResource("input.rdf");
	FileRepresentation rep = new FileRepresentation(url.getFile(), MediaType.APPLICATION_RDF_XML, 0);
	// EncodeRepresentation encodedRep = new
	// EncodeRepresentation(Encoding.GZIP,rep);

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, rep, Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(527, table.getRowCount());
	c.close();

    }

    @Test
    public void testCreateEntryFromFileRDF2() throws Exception {
	URL url = getClass().getClassLoader().getResource("dataset_mna_multi.rdf");
	FileRepresentation rep = new FileRepresentation(url.getFile(),
	// "E:/src/ambit2-all/src/test/resources/endpoints/skin_sensitisation/LLNA_3D.sdf",
		MediaType.APPLICATION_RDF_XML, 0);
	// EncodeRepresentation encodedRep = new
	// EncodeRepresentation(Encoding.GZIP,rep);

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, rep, Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, table.getRowCount());

	table = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join struc_dataset using(id_srcdataset) where id_srcdataset=4");
	Assert.assertEquals(2, table.getRowCount());
	c.close();

    }

    @Test
    public void testCreateEntryFromMultipartWeb() throws Exception {
	URL url = getClass().getClassLoader().getResource("input.sdf");
	File file = new File(url.getFile());
	Representation rep = getMultipartWebFormRepresentation(file);

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	// curl -H "Content-type:chemical/x-mdl-sdfile/
	testAsyncPoll(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_MDLSDF, rep, Method.POST, new Reference(
		String.format("http://localhost:%d/dataset/4", port)));

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(12, table.getRowCount());
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT name,title,url,licenseURI FROM src_dataset join catalog_references using(idreference) where name='new-file-name' and id_srcdataset=4");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("junit test on input.sdf", table.getValue(0, "title"));
	Assert.assertEquals("http://ambit.sourceforge.net", table.getValue(0, "url"));
	Assert.assertEquals(ISourceDataset.license.CC0_1_0.getURI(), table.getValue(0, "licenseURI"));
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT d.name,title,url,licenseURI FROM src_dataset d join template_def using(idtemplate) join properties p using(idproperty)"
				+ "join catalog_references r on p.idreference=r.idreference where d.name='new-file-name' and id_srcdataset=4");
	Assert.assertEquals(72, table.getRowCount());
	c.close();
    }

    protected Representation getMultipartWebFormRepresentation(File file) throws Exception {
	String docPath = file.getAbsolutePath();
	StringBuffer str_b = new StringBuffer();
	final String bndry = "XCVBGFDS";
	String paramName = "file";
	String fileName = file.getName();
	final MediaType type = new MediaType(String.format("multipart/form-data; boundary=%s", bndry));
	file = new File(docPath);

	/**
	 * WRITE THE fields
	 */
	String disptn = String.format("--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n", bndry,
		"title", "new-file-name");
	str_b.append(disptn);
	disptn = String.format("--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n", bndry,
		AbstractDataset._props.seeAlso, "http://ambit.sourceforge.net");
	str_b.append(disptn);
	// source
	disptn = String.format("--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n", bndry,
		AbstractDataset._props.source, "junit test on input.sdf");
	str_b.append(disptn);
	// license
	disptn = String.format("--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n", bndry,
		AbstractDataset._props.license, ISourceDataset.license.CC0_1_0.getURI());
	str_b.append(disptn);
	/**
	 * WRITE THE FIRST/START BOUNDARY
	 */
	disptn = String.format(
		"--%s\r\nContent-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\nContent-Type: %s\r\n\r\n",
		bndry, paramName, fileName, ChemicalMediaType.CHEMICAL_MDLSDF);
	str_b.append(disptn);
	/**
	 * WRITE THE FILE CONTENT
	 */
	FileInputStream is;
	byte[] buffer = new byte[4096];
	int bytes_read;
	try {
	    is = new FileInputStream(file);
	    while ((bytes_read = is.read(buffer)) != -1) {

		str_b.append(new String(buffer, 0, bytes_read));
	    }
	    is.close();
	} catch (IOException e) {
	    throw e;
	}
	/**
	 * WRITE THE CLOSING BOUNDARY
	 */
	String boundar = String.format("\r\n--%s--", bndry);
	str_b.append(boundar); // another 2 new lines
	StringRepresentation st_b = new StringRepresentation(str_b.toString(), type);
	// PUT
	return st_b;

    }

    @Test
    public void testCreateEntryFromFile() throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	URL url = getClass().getClassLoader().getResource("input.sdf");
	FileRepresentation rep = new FileRepresentation(url.getFile(), ChemicalMediaType.CHEMICAL_MDLSDF, 0);
	// curl -H "Content-type:chemical/x-mdl-sdfile/
	testAsyncPoll(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_MDLSDF, rep, Method.POST, new Reference(
		String.format("http://localhost:%d/dataset/4", port)));

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(12, table.getRowCount());
	c.close();

    }

    @Test
    public void testCreate2Entries() throws Exception {

	testCreateEntryFromFile();

	URL url = getClass().getClassLoader().getResource("PK-test.csv");
	FileRepresentation rep = new FileRepresentation(url.getFile(),
	// "E:/src/ambit2-all/src/test/resources/endpoints/skin_sensitisation/LLNA_3D.sdf",
		MediaType.TEXT_CSV, 0);
	// EncodeRepresentation encodedRep = new
	// EncodeRepresentation(Encoding.GZIP,rep);

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, rep, Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/5", port)));
    }

    @Test
    public void testCreateEntryFromCSVFile() throws Exception {
	URL url = getClass().getClassLoader().getResource("PK-test.csv");
	FileRepresentation rep = new FileRepresentation(url.getFile(),
	// "E:/src/ambit2-all/src/test/resources/endpoints/skin_sensitisation/LLNA_3D.sdf",
		MediaType.TEXT_CSV, 0);
	// EncodeRepresentation encodedRep = new
	// EncodeRepresentation(Encoding.GZIP,rep);

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, rep, Method.POST,
		new Reference(String.format("http://localhost:%d/dataset/4", port)));

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED",
		"SELECT * FROM structure join struc_dataset using(idstructure) where id_srcdataset=4");
	Assert.assertEquals(1, table.getRowCount());

	c.close();

	Form headers = new Form();
	headers.add(OpenTox.params.dataset_uri.toString(), String.format("http://localhost:%d/dataset/4", port));
	Reference result = testAsyncTask(String.format("http://localhost:%d/model/pKa", port), headers,
		Status.SUCCESS_OK,
		"http://localhost:8181/dataset/4?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F2%2Fpredicted");
	System.out.println(result);
    }

    @Test
    public void testCreateEntryTUMRDFRemote() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("descriptor_service_output.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, table.getRowCount());
	c.close();

    }

    @Test
    public void testCreateEntryLazarRDFLocal() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("lazar/test-lazar.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(6, table.getRowCount());
	c.close();

    }

    public void testCreateEntryTUMRDFLocal() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("ambit2_4829261780582040994.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, table.getRowCount());
	c.close();

    }

    @Test
    public void testSameAs() throws Exception {
	Assert.fail("Same as is always the same as Name, like here http://ambit.uni-plovdiv.bg:8080/ambit2/feature/82119 or here http://ambit.uni-plovdiv.bg:8080/ambit2/feature/82138. Maybe this is a bug while parsing an rdf or sdf file.");
    }

    @Test
    public void testPostEmptyDataset() throws Exception {
	Assert.fail("Post empty dataset");
    }

    @Test
    public void testNTUA() throws Exception {
	/*
	 * > >From first sight noticed > > 1) It returns dataset content itself,
	 * not a dataset or task URI , as > > in the specification I have tried
	 * to POST the content to your server using this command:
	 * 
	 * curl -H 'Content-type:application/rdf+xml' -X POST --data-binary
	 * 
	 * @/path/to/my/ds.rdf http://ambit.uni-plovdiv.bg:8080/ambit2/dataset
	 * -v
	 * 
	 * This seems to work properly and produces a task resource. The task
	 * completes and returns a dataset uri which is
	 * http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/30 but its content is
	 * different from the dataset I posted. So I return to the client the
	 * content of the dataset itself rather than the URI of a created
	 * dataset. Is it possible for you to solve this problem?
	 */
	Assert.fail("POST from NTUA");
    }

    /*
     * public void testPostFileWithoutRestlet() throws Exception{
     * postFileWithoutRestlet
     * ("E:/src/ambit2-all/ambit2-www/src/test/resources/input.sdf");
     * 
     * } //should set name field public void postFileWithoutRestlet(String
     * filename) throws Exception{ String NEWLINE = "\r\n";
     * 
     * String PREFIX = "--";
     * 
     * String userPassword = "guest:guest";
     * 
     * String encoding = new sun.misc.BASE64Encoder().encode
     * (userPassword.getBytes());
     * 
     * URL url = new URL(getTestURI());
     * 
     * 
     * // create a boundary string
     * 
     * String boundary = "--------------------" +
     * Long.toString(System.currentTimeMillis(), 16);
     * 
     * 
     * URLConnection urlConn = url.openConnection(); if(urlConn instanceof
     * HttpURLConnection) { HttpURLConnection httpConn =
     * (HttpURLConnection)urlConn; httpConn.setRequestMethod("POST"); }
     * urlConn.setDoInput(true); urlConn.setDoOutput(true);
     * urlConn.setUseCaches(false); urlConn.setDefaultUseCaches(false);
     * 
     * urlConn.setRequestProperty ("Authorization", "Basic " + encoding);
     * 
     * 
     * urlConn.setRequestProperty("Content-Type",
     * "multipart/form-data; boundary=" + boundary);
     * 
     * // set some other request headers...
     * 
     * urlConn.setRequestProperty("Connection", "Keep-Alive");
     * urlConn.setRequestProperty("Cache-Control", "no-cache");
     * 
     * 
     * DataOutputStream os = new DataOutputStream(urlConn.getOutputStream());
     * 
     * 
     * os.writeBytes(PREFIX); os.writeBytes(boundary); os.writeBytes(NEWLINE);
     * // write content header
     * //os.writeBytes("Content-Disposition: form-data; name=\"datafile" + //
     * "\"; filename=\"" + args[0] + "\"");
     * os.writeBytes("Content-Disposition: form-data; name=\"file\"");
     * 
     * os.writeBytes(NEWLINE); //if(mimeType != null) { //
     * os.writeBytes("Content-Type: " + mimeType); // os.writeBytes(NEWLINE);
     * //} os.writeBytes(NEWLINE); // write content byte[] data =new byte[1024];
     * FileInputStream is = new FileInputStream(new File(filename)); int r = 0;
     * while((r = is.read(data, 0, data.length)) != -1) { os.write(data, 0, r);
     * } // close input stream, but ignore any possible exception for it try {
     * is.close(); } catch(Exception e) {} os.writeBytes(NEWLINE); os.flush();
     * 
     * os.writeBytes(PREFIX); os.writeBytes(boundary); os.writeBytes(PREFIX);
     * os.writeBytes(NEWLINE); os.flush(); os.close();
     * 
     * 
     * 
     * // read response from server
     * 
     * 
     * BufferedReader in = new BufferedReader( new
     * InputStreamReader(urlConn.getInputStream())); String line = "";
     * while((line = in.readLine()) != null) { System.out.println(line); }
     * in.close();
     * 
     * }
     */

    @Test
    public void testAddCompoundsToDataset() throws Exception {
	// String dataset =
	// "http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/288";
	String dataset = String.format("http://localhost:%d/dataset/1", port);

	URL url = getClass().getClassLoader().getResource("test.sdf");
	Reference reference = upload(new Reference(dataset), new FileRepresentation(url.getFile(),
		ChemicalMediaType.CHEMICAL_MDLSDF), new Variant(MediaType.TEXT_URI_LIST), false);
	Assert.assertEquals(dataset, reference.toString());

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset where id_srcdataset=1");
	Assert.assertEquals(14, table.getRowCount());
	c.close();

    }

    protected Reference upload(Reference datasetURI, Representation entity, Variant variant, boolean newDataset)
	    throws ResourceException {
	try {
	    // factory.setSizeThreshold(100);
	    RemoteTask task = new RemoteTask(datasetURI, variant.getMediaType(), entity, newDataset ? Method.POST
		    : Method.PUT);
	    // task.poll() returns true if completed
	    while (!task.poll()) {
		Thread.sleep(1500);
		System.out.println(task);
	    }
	    if (Status.SUCCESS_OK.equals(task.getStatus())) {
		return task.getResult();
	    } else if (Status.CLIENT_ERROR_NOT_FOUND.equals(task.getStatus())) {
		throw new ResourceException(task.getStatus());
	    } else
		throw new ResourceException(task.getStatus(), task.getResult() == null ? task.getStatus()
			.getDescription() : task.getResult().toString());

	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(x);
	} finally {

	}
    }

    protected Reference delete(Reference datasetURI, Representation entity, Variant variant) throws ResourceException {
	try {
	    // factory.setSizeThreshold(100);
	    RemoteTask task = new RemoteTask(datasetURI, variant.getMediaType(), entity, Method.DELETE);
	    // task.poll() returns true if completed
	    while (!task.poll()) {
		Thread.sleep(1500);
		System.out.println(task);
	    }
	    if (Status.SUCCESS_OK.equals(task.getStatus())) {
		return task.getResult();
	    } else if (Status.CLIENT_ERROR_NOT_FOUND.equals(task.getStatus())) {
		throw new ResourceException(task.getStatus());
	    } else
		throw new ResourceException(task.getStatus(), task.getResult() == null ? task.getStatus()
			.getDescription() : task.getResult().toString());

	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(x);
	} finally {

	}
    }

    public void testCreateEntryBBRCRDFLocal() throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, table.getRowCount());
	c.close();

	CreateEntryRDF("bbrc/bbrc_featurepaths.rdf");

	c = getConnection();
	table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(2339, table.getRowCount());
	c.close();

    }

    @Test
    public void testDeleteStructuresFromDataset() throws Exception {
	IDatabaseConnection c = getConnection();
	String dataset = String.format("http://localhost:%d/dataset/2", port);
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset where id_srcdataset=2");
	Assert.assertEquals(2, table.getRowCount());
	table = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idproperty,idtemplate FROM src_dataset join template_def using(idtemplate) join properties using(idproperty) where id_srcdataset=2");
	Assert.assertEquals(1, table.getRowCount());
	Object idproperty = table.getValue(0, "idproperty");
	Assert.assertEquals("3", idproperty.toString());
	Object idtemplate = table.getValue(0, "idtemplate");
	Assert.assertEquals("103", idtemplate.toString());

	try {
	    Reference reference = delete(new Reference(dataset), null, new Variant(MediaType.TEXT_URI_LIST));
	    Assert.assertEquals(String.format("http://localhost:%d/dataset", port), reference.toString());
	} catch (Exception x) {
	    x.printStackTrace();
	}
	table = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where id_srcdataset=2");
	Assert.assertEquals(0, table.getRowCount());
	table = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset where id_srcdataset=2");
	Assert.assertEquals(0, table.getRowCount());
	table = c.createQueryTable("EXPECTED", "SELECT * FROM template where idtemplate=103");
	Assert.assertEquals(0, table.getRowCount());
	table = c.createQueryTable("EXPECTED", "SELECT * FROM properties where idproperty=3");
	Assert.assertEquals(0, table.getRowCount());
	c.close();
    }

    public void testDeleteStructuresFromRDataset() throws Exception {
	Assert.fail("add test");
    }
}