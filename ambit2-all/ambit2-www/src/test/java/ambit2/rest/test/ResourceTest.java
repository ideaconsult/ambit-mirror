package ambit2.rest.test;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.restlet.Application;
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
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;

public abstract class ResourceTest extends DbUnitTest {
	protected Component component=null;
	protected Application app;
	protected int port = 8181;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase("src/test/resources/src-datasets.xml");
        // Create a component
        component = new Component();
        component.getServers().add(Protocol.HTTP, port);
       
        Context context = component .getContext().createChildContext(); 
        context.getParameters().add(Preferences.DATABASE, getDatabase());
        context.getParameters().add(Preferences.USER, getUser());
        context.getParameters().add(Preferences.PASSWORD, getPWD());
        context.getParameters().add(Preferences.PORT, getPort());
        context.getParameters().add(Preferences.HOST, getHost());
        app = createApplication(context);
        component.getDefaultHost().attach(app);
        component.start();        
	}
	
	protected Application createApplication(Context context) {
		Application app = new AmbitApplication();
		app.setContext(context);
		return app;
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
