package ambit2.rest.test.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitApplication;
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
	    component.getInternalRouter().attach("/",app);
	    
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
		((AmbitApplication)app).removeTasks();
	}

	@Test
	public void testURI() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("quickTaskURI");
			}
		};
		((AmbitApplication)app).addTask("Test task",c,new Reference(String.format("http://localhost:%d", port)));
				
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
		
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			//Assert.assertEquals("http://localhost:8181/newURI",line);
			count++;
		}
		return count == 1;
	}	
	
	@Test
	public void testCompletedTaskURI() throws Exception {
		//creating task that completes immediately
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				return new Reference("quickTaskURI");
			}
		};
		Reference completedTaskURI = ((AmbitApplication)app).addTask("Test task",c,new Reference(String.format("http://localhost:%d", port)));
				
		Response response = testGet(completedTaskURI.toString(),MediaType.TEXT_URI_LIST,Status.REDIRECTION_SEE_OTHER);
		Assert.assertEquals(
					String.format("http://localhost:%d/task/quickTaskURI",port),
					response.getLocationRef().toString());
		//((AmbitApplication)app).removeTasks();

	}
	@Test
	public void testRunningTaskURI() throws Exception {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				boolean always = true;
				while (always) {
					
				}
				return new Reference("newURI");
			}
		};
		String longTaskURI = ((AmbitApplication)app).addTask("Test task",c,new Reference(String.format("http://localhost:%d", port))).toString();
			
		Response response = testGet(longTaskURI,MediaType.TEXT_URI_LIST,Status.SUCCESS_ACCEPTED);
		//((AmbitApplication)app).removeTasks();
	}	
}
