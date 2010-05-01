package ambit2.rest.test.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.test.ResourceTest;

public class TaskResourceTest extends ResourceTest {
	protected AmbitApplication app;

	@Before
	public void setUp() throws Exception {
		setUpDatabase("src/test/resources/src-datasets.xml");

		Context context = new Context();
		context.getParameters().add(Preferences.DATABASE, getDatabase());
		context.getParameters().add(Preferences.USER, getUser());
		context.getParameters().add(Preferences.PASSWORD, getPWD());
		context.getParameters().add(Preferences.PORT, getPort());
		context.getParameters().add(Preferences.HOST, getHost());

		// Create a component
		component = new Component();

		component.getClients().add(Protocol.FILE);
		component.getClients().add(Protocol.HTTP);
		component.getClients().add(Protocol.HTTPS);

		app = new AmbitApplication();
		app.setContext(context);

		// Attach the application to the component and start it

		component.getDefaultHost().attach(app);
		component.getInternalRouter().attach("/", app);

		component.getServers().add(Protocol.HTTP, port);
		component.getServers().add(Protocol.HTTPS, port);

		component.start();
	}

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/task", port);
	}

	@After
	public void cleanup() {
		((AmbitApplication) app).removeTasks();
	}

	@Test
	public void testRDF() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("http://localhost/newResult");
			}
		};
		((AmbitApplication) app).addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)));

		testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);

	}

	@Test
	public void testURI() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("quickTaskURI");
			}
		};
		((AmbitApplication) app).addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)));

		testGet(getTestURI(), MediaType.TEXT_URI_LIST);

	}

	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			// Assert.assertEquals("http://localhost:8181/newURI",line);
			count++;
		}
		return count == 1;
	}

	@Test
	public void testCompletedTaskURI() throws Exception {
		// creating task that completes immediately
	
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"0");
		testAsyncTask(String.format("http://localhost:%d/algorithm/mockup", port), 
				form,Status.SUCCESS_OK,"dataseturi");

	}
	@Test
	public void testRunningTaskURI() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		testAsyncTask(String.format("http://localhost:%d/algorithm/mockup", port), 
				form,Status.SUCCESS_OK,"dataseturi");

	}
	
	@Test
	public void testMultipleRunningTasks() throws Exception {

		
		Reference url = new Reference(String.format("http://localhost:%d/algorithm/mockup", port));
		for (int i=0; i < 10; i++) {
			Form form = new Form();  
			form.add(OpenTox.params.dataset_uri.toString(),String.format("dataseturi-%d",i+1));
			form.add(OpenTox.params.delay.toString(),"1000");
			RemoteTask task = new RemoteTask(
					url,
					MediaType.APPLICATION_WWW_FORM,form.getWebRepresentation(),Method.POST,null);
		}
		

		Reference alltasks = new Reference(String.format("http://localhost:%d/task", port));
		RemoteTask tasks = new RemoteTask(
				alltasks,
				MediaType.TEXT_URI_LIST,null,Method.GET,null);
		while (!tasks.poll()) {
			System.out.println(tasks);
		}
		System.out.println(tasks);
		
	}	
	
	@Test
	public void testSuperService() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add("url",String.format("http://localhost:%d/algorithm/mockup", port));
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/superservice", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		Reference ref = post.call();
		Assert.assertEquals("dataseturi",ref.toString());
				
		
	}
	
	@Test
	public void testSuperServiceWithError() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		form.add("url",String.format("http://localhost:%d/algorithm/mockup", port));
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/superservice", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		try {
			Reference ref = post.call();
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			Assert.assertEquals(Status.SERVER_ERROR_BAD_GATEWAY,x.getStatus());
			ResourceException xx = (ResourceException) x.getCause();
			Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,xx.getStatus());
			Assert.assertEquals("Mockup error",xx.getMessage());
		} 
	}	
	
	
	@Test
	public void testSuperServiceWithTimeout() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add("url",String.format("http://localhost:%d/algorithm/mockup", port));
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/superservice", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		post.setPollTimeout(10);
		try {
			Reference ref = post.call();
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			Assert.assertEquals(Status.SERVER_ERROR_GATEWAY_TIMEOUT,x.getStatus());
		} 
	}	
	@Test
	public void testSuperServiceRemote() throws Exception {
		Form form = new Form();  
		form.add("url","http://ambit.uni-plovdiv.bg:8080/ambit2/algorithm/toxtreecarc");
		//form.add("dataset_uri","http://ambit.uni-plovdiv.bg:8080/ambit2/algorithm/J48");
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/superservice", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		Reference ref = post.call();
		long now = System.currentTimeMillis();
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/model/2",ref.toString());
		System.out.println(System.currentTimeMillis()-now);		
		
	}	
	@Test
	public void testMockup() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/mockup", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		Reference ref = post.call();
		Assert.assertEquals("dataseturi",ref.toString());
	}	

	@Test
	public void testMockupError() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		
		CallablePOST post = new CallablePOST(
				new Reference(String.format("http://localhost:%d/algorithm/mockup", port)),
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation());
		try {
			Reference ref = post.call();
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,x.getStatus());
			Assert.assertEquals("Mockup error",x.getMessage());
		} 
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
}
