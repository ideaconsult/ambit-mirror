package ambit2.plugin.dbtools.test;

import java.util.Vector;

import org.junit.Before;

import ambit2.base.processors.batch.BatchProcessor;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ui.SilentWorkflowListener;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.Workflow;

public abstract class WorkflowTest<W extends Workflow> extends DbUnitTest {
	protected WorkflowOptionsLauncher contextListener;
	protected DBWorkflowContext context;
	protected boolean completed = false;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		context = new DBWorkflowContext();
		contextListener = new SilentWorkflowListener(null);
		Vector<String> props = new Vector<String>();
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
		props.add(DBWorkflowContext.DATASET);
		props.add(BatchProcessor.PROPERTY_BATCHSTATS);
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(context);
	}	
	
	protected abstract W getWorkflow() throws Exception;
}
