package nplugins.test.shell.application;


import nplugins.shell.NanoPluginsManager;
import nplugins.shell.application.NPluginsApplication;

import org.junit.After;
import org.junit.Before;

public class NPluginsApplicationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	public static void main(String[] args) throws Exception {
		NPluginsApplication app = new NPluginsApplication("Test",100,100,args) {
			protected void addPlugins(NanoPluginsManager manager) {
				try {
					manager.addPackage("nplugins.demo.DemoPlugin");
					manager.addPackage("nplugins.demo.DemoPlugin",new String[] {"1"},"Demo",null);
					manager.addPackage("nplugins.demo.DemoPlugin",new String[] {"2"},"Demo",null);
					manager.addPackage("nplugins.workflow.MWorkflowPlugin",new String[] {"2"},"MWorkflow",null);
					
				} catch (Exception x) {
		            x.printStackTrace();
					logger.severe(x.getMessage());
				}		
			}			
		};

	}
	
}
