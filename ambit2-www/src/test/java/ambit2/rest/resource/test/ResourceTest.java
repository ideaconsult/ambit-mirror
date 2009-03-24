package ambit2.rest.resource.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import ambit2.base.config.Preferences;
import ambit2.rest.AmbitApplication;
import ambit2.rest.test.DbUnitTest;

public class ResourceTest extends DbUnitTest {
	protected Component component=null;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase("src/test/resources/src-datasets.xml");
        // Create a component
        component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
       
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
	@Test
	public void testGet() throws Exception {
		
	}
}
