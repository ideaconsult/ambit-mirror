package ambit2.rest.test.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.aa.IAuthToken;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskResult;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.task.RemoteTask;
import org.opentox.dsl.task.RemoteTaskPool;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.Preferences;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.TaskResult;
import ambit2.rest.test.ResourceTest;

public class TaskResourceTest extends ResourceTest {
	protected AmbitApplication app;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase("src/test/resources/src-datasets.xml");
	/*
	 * Context context = new Context();
	 * context.getParameters().add(Preferences.DATABASE, getDatabase());
	 * context.getParameters().add(Preferences.USER, getUser());
	 * context.getParameters().add(Preferences.PASSWORD, getPWD());
	 * context.getParameters().add(Preferences.PORT, getPort());
	 * context.getParameters().add(Preferences.HOST, getHost());
	 * 
	 * // Create a component component = new Component();
	 * 
	 * component.getClients().add(Protocol.FILE);
	 * component.getClients().add(Protocol.HTTP);
	 * component.getClients().add(Protocol.HTTPS);
	 * 
	 * app = new AmbitApplication(); app.setContext(context);
	 * 
	 * // Attach the application to the component and start it
	 * 
	 * component.getDefaultHost().attach(app);
	 * component.getInternalRouter().attach("/", app);
	 * 
	 * component.getServers().add(Protocol.HTTP, port);
	 * component.getServers().add(Protocol.HTTPS, port);
	 * 
	 * component.start();
	 */
	}

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/task", port);
	}

	@After
	public void cleanup() {
		//((AmbitApplication) app).removeTasks();
	}

	@Test
	public void testRDF() throws Exception {
		ICallableTask c = new ICallableTask() {
			protected UUID uuid;
			public ITaskResult call() throws Exception {
				return new TaskResult("http://localhost/newResult");
			}
			@Override
			public UUID getUuid() {
				return uuid;
			}
			@Override
			public void setUuid(UUID uuid) {
				this.uuid = uuid;
			}
			@Override
			public String getTaskCategory() {
				return null;
			}
		};
		app.addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)),getUserToken());

		testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);

	}

	@Test
	public void testURI() throws Exception {
		ICallableTask c = new ICallableTask() {
			
			protected UUID uuid;
			public ITaskResult call() throws Exception {
				return new TaskResult("quickTaskURI");
			}
			@Override
			public UUID getUuid() {
				return uuid;
			}
			@Override
			public void setUuid(UUID uuid) {
				this.uuid = uuid;
			}
			@Override
			public String getTaskCategory() {
				return null;
			}
		};
		app.addTask("Test task", c, new Reference(String
				.format("http://localhost:%d", port)),getUserToken());

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
	
	
	public void testMultipleRunningTasks() throws Exception {

		
		Reference url = new Reference(String.format("http://localhost:%d/algorithm/mockup", port));
		for (int i=0; i < 600; i++) {
			Form form = new Form();  
			form.add(OpenTox.params.dataset_uri.toString(),String.format("dataseturi-%d",i+1));
			form.add(OpenTox.params.delay.toString(),"1000");
			RemoteTask task = new RemoteTask(
					url,
					MediaType.APPLICATION_WWW_FORM,form.getWebRepresentation(),Method.POST);
		}
		

		Reference alltasks = new Reference(String.format("http://localhost:%d/task", port));
		RemoteTask tasks = new RemoteTask(
				alltasks,
				MediaType.TEXT_URI_LIST,null,Method.GET);
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
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, "dataseturi");

		Assert.assertEquals("dataseturi",ref.toString());
				
		
	}
	
	@Test
	public void testSuperServiceMultiAlgorithm() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", port));
		form.add(OpenTox.params.algorithm_uri.toString(),
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor", port)
				);
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, String.format("http://localhost:%s/dataset/R3",port));

		Assert.assertEquals(String.format("http://localhost:%s/dataset/R3",port),ref.toString());
		
	}
	
	@Test
	public void testSuperServiceMultiAlgorithmRemote() throws Exception {

		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		form.add(OpenTox.params.dataset_service.toString(),String.format("http://localhost:%d/dataset", port));
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),
				String.format("http://localhost:%d/ambit2/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", 8080));
		form.add(OpenTox.params.algorithm_uri.toString(),
				String.format("http://localhost:%d/ambit2/algorithm/org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor", 8080)
				);
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, String.format("http://localhost:%s/dataset/R3",port));

		Assert.assertEquals(String.format("http://localhost:%s/dataset/R3",port),ref.toString());
		
	}
	
	@Test
	public void testSuperServiceMultiAlgorithmTUM() throws Exception {

		String host = "ideaconsult.dyndns.org";
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://%s:%d/dataset/1", host,port));
		form.add(OpenTox.params.dataset_service.toString(),String.format("http://%s:%d/dataset",host, port));
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),
				"http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem/ALOGPDescriptor");
		form.add(OpenTox.params.algorithm_uri.toString(),
				"http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/JOELIB2/GeometricalRadius"
				);
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		
		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, String.format("http://%s:%s/dataset/R3",host,port));

		Assert.assertEquals(String.format("http://%s:%s/dataset/R3",host,port),ref.toString());
		
	}
	
	@Test
	public void testSuperServiceWithError() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);

		try {
			Reference ref = testAsyncTask(superservice, form, Status.SERVER_ERROR_BAD_GATEWAY, "dataseturi");
			Assert.fail("Should throw an error");
		} catch (Exception x) {
			ResourceException xx = (ResourceException) x;
			Assert.assertEquals(Status.SERVER_ERROR_BAD_GATEWAY,xx.getStatus());
			Assert.assertTrue(xx.getMessage().indexOf("Mockup error")>0);
		}
		
		
		/*
		ResourceException xx = (ResourceException) x.getCause();
		Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,xx.getStatus());
		Assert.assertEquals("Mockup error",xx.getMessage());
		*/
	}	
	
	//TODO - how to get it to test timeouts?
	@Test
	public void testSuperServiceWithTimeout() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		CallablePOST post = new CallablePOST(
				MediaType.TEXT_URI_LIST,
				form.getWebRepresentation(),
				null,
				null);
		try {
			TaskResult ref = post.call();
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			Assert.assertEquals(Status.SERVER_ERROR_GATEWAY_TIMEOUT,x.getStatus());
		} 
	}	
	

	@Test
	public void testMockup() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);

		Reference ref = testAsyncTask(superservice, form, Status.SUCCESS_OK, "dataseturi");
	}	

	@Test
	public void testMockupError() throws Exception {
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),"dataseturi");
		form.add(OpenTox.params.delay.toString(),"1000");
		form.add(OpenTox.params.error.toString(),"Mockup error");
		form.add(OpenTox.params.algorithm_uri.toString(),String.format("http://localhost:%d/algorithm/mockup", port));
		
		
		String superservice = String.format("http://localhost:%d/algorithm/superservice", port);
		try {
			//OTModel.model().withParams(form).withUri(superservice).process(null);
			Reference ref = testAsyncTask(superservice, form, Status.SERVER_ERROR_BAD_GATEWAY, "dataseturi");
			Assert.fail("Should throw an error");
		} catch (ResourceException x) {
			ResourceException xx = (ResourceException) x;
			Assert.assertEquals(Status.SERVER_ERROR_BAD_GATEWAY,xx.getStatus());
			Assert.assertTrue(xx.getMessage().indexOf("Mockup error")>0);
		} 
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	@Test
	public void testMultiplePOST() throws Exception {
		Preferences.setProperty(Preferences.SMILESPARSER.toString(),SMILES_PARSER.CDK.toString());
		//setUpDatabase("src/test/resources/src-datasets.xml");
		/*
		final Reference url = testAsyncTask(
				String.format("http://localhost:%d/algorithm/pka", port),
				new Form(), Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"2"));		
		*/
		
		final Reference url = new Reference(String.format("http://localhost:%d/algorithm/ambit2.descriptors.InChI", port));
		
		final RemoteTaskPool pool = new RemoteTaskPool();
		ExecutorService xs= Executors.newCachedThreadPool();
		Runnable[] t = new Runnable[5];
		final int batch = 2000;
		for (int j=0; j < t.length; j++) {

			t[j] = new Runnable() {
				@Override
				public void run() {
					for (int i=0; i < batch; i++) {
						Form form = new Form();  
						form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/compound/11", port));
						RemoteTask task = new RemoteTask(
								url,
								MediaType.TEXT_URI_LIST,form.getWebRepresentation(),Method.POST);
						pool.add(task);
						System.out.println(i);
					}
				}
				@Override
				public String toString() {
					return "Task creator";
				}
			};
		
		}
		for (Runnable r: t) xs.submit(r);
		
		
		/*
		Reference alltasks = new Reference(String.format("http://localhost:%d/task", port));
		RemoteTask tasks = new RemoteTask(
				alltasks,
				MediaType.TEXT_URI_LIST,null,Method.GET,null);
		
		System.out.println("Polling!");
		*/
		System.out.println(String.format("Poll %d",pool.size()));
		while (pool.size()<t.length*batch) {
			//System.out.println(String.format("Poll %d",pool.size()));
			Thread.yield();
		}
		int running = pool.poll();
		System.out.println(String.format("Poll %d running %d",pool.size(),running));
		while ((running = pool.poll())>0) {
			//System.out.println(String.format("Poll %d running %d",pool.size(),running));
			Thread.yield();

			/*
			tasks = new RemoteTask(
					alltasks,
					MediaType.TEXT_URI_LIST,null,Method.GET,null);
					*/
			
		}
		
		System.out.println("Done!!!!!!!");
		xs.awaitTermination(1,TimeUnit.SECONDS);
		xs.shutdown();
		System.out.println(pool.size());
		
	}	
	@Test
	public void testSuperServiceRemote() throws Exception {
		//delete from property_values where idchemical=163134 and idproperty in (22127,22137,22213,22252,26056,49790)
		String result = testSuperServiceRemote_("http://ideaconsult.dyndns.org:8080/ambit2","http://opentox.ntua.gr:4000/model/1b2399f5-1f22-4c9c-8b53-d4c392586961");
		Assert.assertEquals("http://ideaconsult.dyndns.org:8080/ambit2/dataset/5760?feature_uris%5B%5D=http%3A%2F%2Fideaconsult.dyndns.org%3A8080%2Fambit2%2Ffeature%2F49790",result);
	}
	
	@Test
	public void testSuperServiceRemote3() throws Exception {
		//delete from property_values where idchemical=163134 and idproperty in (22127,22137,22213,22252,26056,49790)
		String result = testSuperServiceRemote_("http://apps.ideaconsult.net:8080/ambit2","http://opentox.ntua.gr:4000/model/88eb031e-1984-4e7e-bfc7-9af30f0e55d6");
		Assert.assertEquals("http://apps.ideaconsult.net:8080/ambit2/dataset/82872?feature_uris%5B%5D=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fambit2%2Ffeature%2F367815",result);
	}
	
	
	@Test
	public void testSuperServiceRemote1() throws Exception {
		testSuperServiceRemote_("http://ideaconsult.dyndns.org:8080/ambit2","http://ideaconsult.dyndns.org:8080/ambit2/model/17");
	}
	
	@Test
	public void testSuperServiceRemote2() throws Exception {
		//delete from property_values where idchemical=163134 and idproperty in (22127,22137,22213,22252,26056,49790)
		String result = testSuperServiceRemote_("http://nina:8080/ambit2","http://alphaville:4000/model/a97def7b-14ee-4bce-b7ff-604eadfe8699");
		//Assert.assertEquals("http://nina:8080/ambit2/dataset/2765?feature_uris%5B%5D=http%3A%2F%2Fnina%3A8080%2Fambit2%2Ffeature%2F51305",result);
		Assert.assertEquals("http://nina:8080/ambit2/dataset/5760?feature_uris%5B%5D=http%3A%2F%2Fnina%3A8080%2Fambit2%2Ffeature%2F51305",result);
	}	
	public String testSuperServiceRemote_(String myname, String themodel) throws Exception {
		
		AuthTokenFactory tokenFactory = new AuthTokenFactory();
		
		OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
		if (ssoToken.login(
				OpenSSOServicesConfig.getInstance().getTestUser(),
				OpenSSOServicesConfig.getInstance().getTestUserPass()
				)) {
			//ClientResourceWrapper.setTokenFactory(tokenFactory);
		} else
			throw new Exception(String.format("Error logging to SSO (%s)",OpenSSOServicesConfig.getInstance().getTestUser()));

		try {
			Form form = new Form();  
			form.add(OpenTox.params.model_uri.toString(),themodel);
			form.add(OpenTox.params.dataset_uri.toString(),
					//String.format("%s/dataset/2765",myname));
						//String.format("%s/dataset/5760",myname));
					String.format("%s/dataset/82872",myname));
					
			form.add(OpenTox.params.dataset_service.toString(),
					String.format("%s/dataset/",myname));
			
			CallablePOST post = new CallablePOST(
					MediaType.TEXT_URI_LIST,
					form.getWebRepresentation(),
					null,
					ssoToken.getToken());
			TaskResult ref = post.call();
			long now = System.currentTimeMillis();
			System.out.println(ref.toString());
			System.out.println(System.currentTimeMillis()-now);		
			return ref.getUri();
			
		
		} finally {
			try {
				ssoToken.logout();
				ClientResourceWrapper.setTokenFactory(null);
			} catch (Exception x ) {}
			
		}
	}	
	
	public static void main(String[] args) {
		TaskResourceTest test = new TaskResourceTest();
		try {
			
			test.setUp();
			test.testMultiplePOST();
			
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {test.tearDown(); } catch (Exception x) {x.printStackTrace();}
		}
		
	}
	
}

class AuthTokenFactory  implements IAuthToken {
	String token;

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getToken() {

		return token;
	}
};