package ambit2.rest.test.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.rest.AmbitApplication;
import ambit2.rest.test.ResourceTest;

public class TaskResourceTest extends ResourceTest {
	@Override
	protected Application createApplication(Context context) {
		Application app = super.createApplication(context);
		Assert.assertTrue(app instanceof AmbitApplication);

		return app;

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
		((AmbitApplication)app).addTask(c,new Reference(String.format("http://localhost:%d", port)));
				
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
		Reference completedTaskURI = ((AmbitApplication)app).addTask(c,new Reference(String.format("http://localhost:%d", port)));
				
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
		String longTaskURI = ((AmbitApplication)app).addTask(c,new Reference(String.format("http://localhost:%d", port))).toString();
			
		Response response = testGet(longTaskURI,MediaType.TEXT_URI_LIST,Status.SUCCESS_ACCEPTED);
		//((AmbitApplication)app).removeTasks();
	}	
}
