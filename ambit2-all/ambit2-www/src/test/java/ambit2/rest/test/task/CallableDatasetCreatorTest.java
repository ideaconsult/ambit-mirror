package ambit2.rest.test.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitComponent;
import ambit2.rest.OpenTox;
import ambit2.rest.task.CallableDatasetCreator;

public class CallableDatasetCreatorTest  {
	protected Component component=null;;
	@Before
	public void setUp() throws Exception {
		
        Context context = new Context();
        context.getParameters().add(Preferences.DATABASE, "ambit2");
        context.getParameters().add(Preferences.USER, "guest");
        context.getParameters().add(Preferences.PASSWORD, "guest");
        context.getParameters().add(Preferences.PORT, "3306");
        context.getParameters().add(Preferences.HOST, "localhost");
        
        // Create a component
        component = new AmbitComponent(context);
        component.getServers().add(Protocol.HTTP, 8080);
        component.getServers().add(Protocol.HTTPS, 8080);        
        component.start();        
	}	
	
	@After
	public void tearDown() throws Exception {
		if (component != null)
			component.stop();
	}	
	@Test
	public void test() throws Exception {
		
		Reference app = new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2");
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(), 
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/10");
				"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/169");
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1/conformer/100205");
				//"http://194.141.0.136:8080/ambit2/dataset/48?feature_uris[]=http://194.141.0.136:8080/ambit2/feature/13592");
		form.add(OpenTox.params.model_uri.toString(), 
				String.format("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/%s","TUMOpenToxModel_kNN_6"));
		
		CallableDatasetCreator c = new CallableDatasetCreator(
				form,
				app,
				new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset"),
				null);
		Reference ref = c.call();
		System.out.println(ref);
	}
	@Test
	public void testNTUA() throws Exception {
		
		Reference app = new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2");
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(), 
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/10");
				"http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1");
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1/conformer/100205");
				//"http://194.141.0.136:8080/ambit2/dataset/48?feature_uris[]=http://194.141.0.136:8080/ambit2/feature/13592");
		form.add(OpenTox.params.model_uri.toString(), 
				String.format("http://opentox.ntua.gr:3000/model/%s","3"));
		
		CallableDatasetCreator c = new CallableDatasetCreator(
				form,
				app,
				new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset"),
				null);
		Reference ref = c.call();
		System.out.println(ref);
	}	
	@Test
	public void testLocal() throws Exception {
		Reference app = new Reference("http://194.141.0.136:8080");
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(), 
				"http://194.141.0.136:8080/compound/10");
		form.add(OpenTox.params.model_uri.toString(), 
				String.format("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/%s","TUMOpenToxModel_kNN_6"));
		
		CallableDatasetCreator cr = new CallableDatasetCreator(
				form,
				app,
				new Reference("http://194.141.0.136:8080/dataset"),
				null);
		Reference ref = cr.call();
		System.out.println(ref);
	}	

}
