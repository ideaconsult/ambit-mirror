package ambit2.plugin.dbtools.test;

import java.beans.PropertyChangeEvent;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.SCOPE;
import ambit2.db.search.structure.ScopeQuery;
import ambit2.dbui.LoginInfoBean;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.ScopeSelection;
import ambit2.workflow.ui.SilentWorkflowListener;
import ambit2.workflow.ui.UserInteractionEvent;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;
import com.microworkflow.process.Workflow;

public class SelectionWorkflowTest extends WorkflowTest {

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
		props.add(DBWorkflowContext.SCOPE);
		
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(context);
	}	
	
	@Override
	protected Workflow getWorkflow() {
		Workflow w = new Workflow();
		w.setDefinition(new ScopeSelection());
		return w;

	}
	
	public void runWorkflow(ScopeQuery scope) throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/dbtools/test/dataset-properties.xml");

		LoginInfoBean li = new LoginInfoBean();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());		
		Workflow wf = getWorkflow();
		context.put(DBWorkflowContext.LOGININFO, li);
		context.put(DBWorkflowContext.SCOPE,scope);
		context.put(DBWorkflowContext.QUERY,new QueryCombinedStructure());

		wf.addPropertyChangeListener(new WorkflowListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(WorkflowEvent.WF_COMPLETE))
					completed = true;

			}
		});

		completed = false;

		wf.executeWith(context);
		while (!completed) {}		
	}
	@Test
	public void testScopeDataset() throws Exception {

		runWorkflow(new ScopeQuery(SCOPE.scope_dataset));

		Object o = context.get(DBWorkflowContext.SCOPE);
		Assert.assertTrue(o instanceof ScopeQuery);
		Assert.assertTrue(((ScopeQuery)o).getValue() instanceof QueryDataset);

		Object q  = context.get(DBWorkflowContext.QUERY);
		Assert.assertTrue(q instanceof QueryCombinedStructure);
		Assert.assertNotNull(((QueryCombinedStructure)q).getScope());	
		Assert.assertTrue(((QueryCombinedStructure)q).getScope() instanceof QueryDataset);
		
	}	

	@Test
	public void testScopeDB() throws Exception {

		runWorkflow(new ScopeQuery(SCOPE.scope_entiredb));

		Object o = context.get(DBWorkflowContext.SCOPE);
		Assert.assertTrue(o instanceof ScopeQuery);
		Assert.assertNull(((ScopeQuery)o).getValue());

		Object q  = context.get(DBWorkflowContext.QUERY);
		Assert.assertTrue(q instanceof QueryCombinedStructure);
		Assert.assertNull(((QueryCombinedStructure)q).getScope());	
		
	}		
	
	@Test
	public void testScopeStoredResults() throws Exception {

		runWorkflow(new ScopeQuery(SCOPE.scope_previousresults));


		Object o = context.get(DBWorkflowContext.SCOPE);
		Assert.assertTrue(o instanceof ScopeQuery);
		Assert.assertTrue(((ScopeQuery)o).getValue() instanceof QueryStoredResults);

		Object q  = context.get(DBWorkflowContext.QUERY);
		Assert.assertTrue(q instanceof QueryCombinedStructure);
		Assert.assertNotNull(((QueryCombinedStructure)q).getScope());	
		Assert.assertTrue(((QueryCombinedStructure)q).getScope() instanceof QueryStoredResults);
		
	}		
	
	@Test
	public void testScopeLastResults() throws Exception {

		context.put(DBWorkflowContext.STOREDQUERY,new StoredQuery(99));
		
		runWorkflow(new ScopeQuery(SCOPE.scope_last_results));


		Object o = context.get(DBWorkflowContext.SCOPE);
		Assert.assertTrue(o instanceof ScopeQuery);
		Assert.assertTrue(((ScopeQuery)o).getValue() instanceof QueryStoredResults);

		Object q  = context.get(DBWorkflowContext.QUERY);
		Assert.assertTrue(q instanceof QueryCombinedStructure);
		Assert.assertNotNull(((QueryCombinedStructure)q).getScope());	
		Assert.assertTrue(((QueryCombinedStructure)q).getScope() instanceof QueryStoredResults);
		
		QueryStoredResults results = (QueryStoredResults) ((QueryCombinedStructure)q).getScope();
		Assert.assertEquals(99,results.getFieldname().getId().intValue());
		Assert.assertEquals(2,results.getParameters().size());
		Assert.assertEquals(99,results.getParameters().get(0).getValue());
		
	}		
}
