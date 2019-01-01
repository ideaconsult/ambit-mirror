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

	

}
