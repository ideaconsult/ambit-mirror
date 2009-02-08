package ambit2.plugin.dbtools.test;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.core.processors.batch.BatchProcessor;
import ambit2.db.LoginInfo;
import ambit2.db.search.MissingFingerprintsQuery;
import ambit2.db.search.QueryDataset;
import ambit2.plugin.dbtools.DBUtilityWorkflow;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;

public class DBUtilityWorkflowTest extends WorkflowTest<DBUtilityWorkflow> {

	@Override
	protected DBUtilityWorkflow getWorkflow() {

		return new DBUtilityWorkflow();
	}
	@Test
	public void testExecuteWith() throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/dbtools/test/dataset-properties.xml");
		IDatabaseConnection c = getConnection();
		ITable structures = c.createQueryTable("EXPECTED_STRUCTURES",
		"SELECT * FROM structure");
		Assert.assertEquals(4, structures.getRowCount());			
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(3, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(3, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template");
		Assert.assertEquals(1, templates.getRowCount());
		ITable fp = c.createQueryTable("FP1024",
				"SELECT * FROM fp1024 where status = 'valid'");
		Assert.assertEquals(0, fp.getRowCount());

		LoginInfo li = new LoginInfo();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		context.put(DBWorkflowContext.LOGININFO, li);
		context.put(DBWorkflowContext.QUERY,new MissingFingerprintsQuery());
		context.put("NEWQUERY",new QueryDataset("Default"));
				//new QueryStructureByID(100215));
/*
		context.put(DBWorkflowContext.DATASET, new SourceDataset(
				"TEST-INPUTWORKFLOW"));
				*/
		DBUtilityWorkflow wf = getWorkflow();

		wf.addPropertyChangeListener(new WorkflowListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(WorkflowEvent.WF_COMPLETE))
					completed = true;

			}
		});
		context.addPropertyChangeListener(BatchProcessor.PROPERTY_BATCHSTATS,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {

						if (evt.getNewValue() != null) {
							System.out.println(evt.getNewValue());
							//count++;
						}

					}
				});		
		completed = false;

		wf.executeWith(context);
		while (!completed) {}
		fp = c.createQueryTable("FP1024","SELECT * FROM fp1024 where status = 'valid'");
		Assert.assertEquals(4, fp.getRowCount());

		//Assert.assertEquals(7, structures.getRowCount());	
		//Assert.assertEquals(7, count);
	}

}
