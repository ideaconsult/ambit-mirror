package ambit2.rest.test;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitApplication;
import ambit2.rest.ChemicalMediaType;

public abstract class ResourceTest extends DbUnitTest {
	protected Component component=null;
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
        component.getDefaultHost().attach(new AmbitApplication(context));
        component.start();        
	}

	@After
	public void tearDown() throws Exception {
		if (component != null)
			component.stop();
	}
	
	public void testGet(String uri, MediaType media) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(uri);
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		Response response = client.handle(request);
		Assert.assertEquals(200,response.getStatus().getCode());
		Assert.assertTrue(response.isEntityAvailable());
		InputStream in = response.getEntity().getStream();
		Assert.assertTrue(verifyResponse(uri,media,in));
		in.close();	
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
	public boolean verifyResponse(String uri, MediaType media,InputStream in) throws Exception {
		if (MediaType.APPLICATION_PDF.equals(media))
			return verifyResponsePDF(uri, media, in);
		else if (MediaType.TEXT_HTML.equals(media))
			return verifyResponseHTML(uri, media, in);
		else if (MediaType.TEXT_XML.equals(media))
			return verifyResponseXML(uri, media, in);
		else if (MediaType.TEXT_URI_LIST.equals(media))
			return verifyResponseURI(uri, media, in);		
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
}
