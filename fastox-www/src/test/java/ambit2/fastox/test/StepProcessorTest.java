package ambit2.fastox.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import ambit2.fastox.FastoxComponent;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.fastox.wizard.WizardStep;


public abstract class StepProcessorTest {
	protected WizardStep step;
	protected WizardMode mode = WizardMode.A;
	protected abstract WizardStep getStep();
	
	protected Component component=null;

	protected int port = 8181;
	@Before
	public void setUp() throws Exception {

        Context context = new Context();
        
        // Create a component
        component = new FastoxComponent(context);
        component.getServers().add(Protocol.HTTP, port);
        component.getServers().add(Protocol.HTTPS, port);        
        component.start();        
	}


	@After
	public void tearDown() throws Exception {
		if (component != null)
			component.stop();
	}	
	@Test
	public void testStepExist() throws Exception {
		Assert.assertNotNull(getStep());
	}
	
	public StepProcessor getStepProcessor() throws Exception {
		return getStep().getProcessor();
	}
	
	
}
