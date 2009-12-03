package ambit2.rest.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.jmol.util.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitComponent;
import ambit2.rest.ChemicalMediaType;

public abstract class ResourceTest extends DbUnitTest {
	protected Component component=null;

	protected int port = 8181;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		Logger.setLogLevel(Logger.LEVEL_WARN);
		setUpDatabase("src/test/resources/src-datasets.xml");


        Context context = new Context();
        context.getParameters().add(Preferences.DATABASE, getDatabase());
        context.getParameters().add(Preferences.USER, getUser());
        context.getParameters().add(Preferences.PASSWORD, getPWD());
        context.getParameters().add(Preferences.PORT, getPort());
        context.getParameters().add(Preferences.HOST, getHost());
        
        // Create a component
        component = new AmbitComponent(context);
        component.getServers().add(Protocol.HTTP, port);
        component.getServers().add(Protocol.HTTPS, port);        
        component.start();        
	}


	@After
	public void tearDown() throws Exception {
		if (component != null)
			component.stop();
	}
	public void testGet(String uri, MediaType media) throws Exception {
		testGet(uri, media,Status.SUCCESS_OK);
	}
	public Response testPost(String uri, MediaType media, Form headers) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
		ChallengeResponse authentication = new ChallengeResponse(scheme,  
		         "guest", "guest");  
		request.setChallengeResponse(authentication);  		
		request.setResourceRef(uri);
		request.setMethod(Method.POST);
		request.getAttributes().put("org.restlet.http.headers", headers);		
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		return client.handle(request);
	}
	
	
	public void testAsyncTask(String uri,Form headers,Status expected, String uriExpected) throws Exception {

		Response response  =  testPost(uri,MediaType.TEXT_URI_LIST,headers);
		Status status = response.getStatus();
		Assert.assertEquals(Status.REDIRECTION_SEE_OTHER,status);
		
		Assert.assertNotNull(response.getLocationRef());
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(response.getLocationRef());
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.TEXT_URI_LIST));
		Reference ref = response.getLocationRef();
		while (status.equals(Status.REDIRECTION_SEE_OTHER) || status.equals(Status.SUCCESS_ACCEPTED)) {
			System.out.println(status);
			System.out.println(ref);
			//System.out.println("poll");
			Response response1 = client.handle(request);
			status = response1.getStatus();
			if (Status.REDIRECTION_SEE_OTHER.equals(status)) {
				ref = response1.getLocationRef();
				request.setResourceRef(ref);
			} 
		}
		Assert.assertEquals(uriExpected,ref.toString());
		Assert.assertEquals(expected, status);

	}	
	public Response testGet(String uri, MediaType media, Status expectedStatus) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(uri);
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		Response response = client.handle(request);
		Assert.assertEquals(expectedStatus.getCode(),response.getStatus().getCode());
		if (expectedStatus.equals(Status.SUCCESS_OK)) {
			Assert.assertTrue(response.isEntityAvailable());
			InputStream in = response.getEntity().getStream();
			Assert.assertTrue(verifyResponse(uri,media,in));
			in.close();	
		}
		return response;
	}
	
	public void testGetRepresentation(String uri, MediaType media) throws Exception {
		ClientResource client = new ClientResource(uri);
		Assert.assertTrue(verifyRepresentation(uri,media,client.get(media)));
	}	
	
	public boolean verifyRepresentation(String uri, MediaType media,Representation representation) throws Exception {
		throw new Exception("Not implemented");
	}
	public Status testHandleError(String uri, MediaType media) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(uri);
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		Response response = client.handle(request);
		return response.getStatus();
	}	
	public String getTestURI() { return null; }
	
	public boolean verifyResponsePDF(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}
	public boolean verifyResponseHTML(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}	
	public boolean verifyResponseXML(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseCML(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseMOL(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseSDF(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseSMILES(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponsePNG(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseURI(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}	
	public boolean verifyResponseTXT(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}	
	public boolean verifyResponseCSV(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}		
	public boolean verifyResponseARFF(String uri, MediaType media,InputStream in) throws Exception {
		throw new Exception("Not implemented");
	}			

	public boolean verifyResponseRDFXML(String uri, MediaType media, InputStream in)
			throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return false;

	}	
	
	public boolean verifyResponseRDFTurtle(String uri, MediaType media,InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return false;
	}		
	public boolean verifyResponse(String uri, MediaType media,InputStream in) throws Exception {
		if (MediaType.APPLICATION_PDF.equals(media))
			return verifyResponsePDF(uri, media, in);
		else if (MediaType.TEXT_HTML.equals(media))
			return verifyResponseHTML(uri, media, in);
		else if (MediaType.TEXT_XML.equals(media))
			return verifyResponseXML(uri, media, in);
		else if (MediaType.TEXT_URI_LIST.equals(media))
			return verifyResponseURI(uri, media, in);		
		else if (MediaType.TEXT_PLAIN.equals(media))
			return verifyResponseTXT(uri, media, in);			
		else if (MediaType.IMAGE_PNG.equals(media))
			return verifyResponsePNG(uri, media, in);
		else if (ChemicalMediaType.CHEMICAL_MDLMOL.equals(media))
			return verifyResponseMOL(uri, media, in);
		else if (ChemicalMediaType.CHEMICAL_SMILES.equals(media))
			return verifyResponseSMILES(uri, media, in);
		else if (ChemicalMediaType.CHEMICAL_MDLSDF.equals(media))
			return verifyResponseSDF(uri, media, in);
		else if (ChemicalMediaType.CHEMICAL_CML.equals(media))
			return verifyResponseCML(uri, media, in);
		else if (ChemicalMediaType.WEKA_ARFF.equals(media))
			return verifyResponseARFF(uri, media, in);
		else if (MediaType.TEXT_CSV.equals(media))
			return verifyResponseCSV(uri, media, in);
		else if (MediaType.APPLICATION_RDF_XML.equals(media))
			return verifyResponseRDFXML(uri, media, in);
		else if (MediaType.APPLICATION_RDF_TURTLE.equals(media))
			return verifyResponseRDFTurtle(uri, media, in);				
		
		else throw new Exception("Unknown format "+media);
	}
	protected Document createDOM(InputSource in) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);     
        return doc;
	}
	protected Document createDOM(InputStream in) throws Exception {
        return createDOM(new InputSource(in));     
	}
	protected Document createDOM(Reader reader) throws Exception {
        return createDOM(new InputSource(reader));
	}
}
