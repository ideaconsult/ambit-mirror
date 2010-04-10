package ambit2.plugin.dbtools.test;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.processors.batch.BatchProcessor;
import ambit2.db.LoginInfo;
import ambit2.db.search.structure.QueryDataset;
import ambit2.plugin.dbtools.DBUtilityWorkflow;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.calculation.CalculationDescriptors;
import ambit2.workflow.calculation.CalculationFingerprints;
import ambit2.workflow.calculation.CalculationSmartsData;
import ambit2.workflow.calculation.CalculationStructuralKeys;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class DBUtilityWorkflowTest extends WorkflowTest<DBUtilityWorkflow> {

	@Override
	protected DBUtilityWorkflow getWorkflow() {

		return new DBUtilityWorkflow();
	}

	@Test
	public void testCalculateSmartsData() throws Exception {

		testExecuteWith(new CalculationSmartsData());
		IDatabaseConnection c = getConnection();
		ITable fp = c.createQueryTable("structure","SELECT atomproperties FROM structure where atomproperties is not null");
		Assert.assertEquals(4, fp.getRowCount());
		c.close();
	}
	@Test
	public void testCalculateStrucKeys() throws Exception {

		testExecuteWith(new CalculationStructuralKeys());
		IDatabaseConnection c = getConnection();
		ITable fp = c.createQueryTable("structure","SELECT count(*) c FROM sk1024");
		Assert.assertEquals(new BigInteger("4"), fp.getValue(0,"c"));
		c.close();
	}	
	@Test
	public void testCalculateFP1024() throws Exception {

		testExecuteWith(new CalculationFingerprints());
		IDatabaseConnection c = getConnection();
		ITable fp = c.createQueryTable("structure","SELECT * FROM fp1024");
		Assert.assertEquals(4, fp.getRowCount());
		c.close();
	}	
	
	@Test
	public void testCalculateDescriptors() throws Exception {

		testExecuteWith(new CalculationDescriptors(1000));
		IDatabaseConnection c = getConnection();
		ITable fp = c.createQueryTable("structure","SELECT * FROM properties");
		Assert.assertEquals(7, fp.getRowCount());
		ITable v = c.createQueryTable("values","SELECT * FROM property_values");
		Assert.assertEquals(19, v.getRowCount());		
		c.close();
	}		
	public void testExecuteWith(Sequence sequence) throws Exception {
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
	//	Assert.assertEquals(0, fp.getRowCount());

		LoginInfo li = new LoginInfo();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		context.put(DBWorkflowContext.LOGININFO, li);
		context.put("NEWQUERY",new QueryDataset("Default"));
				//new QueryStructureByID(100215));
/*
		context.put(DBWorkflowContext.DATASET, new SourceDataset(
				"TEST-INPUTWORKFLOW"));
				*/
		Workflow wf = new Workflow();
		wf.setDefinition(new LoginSequence(sequence));

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


		//Assert.assertEquals(7, structures.getRowCount());	
		//Assert.assertEquals(7, count);
	}

}
