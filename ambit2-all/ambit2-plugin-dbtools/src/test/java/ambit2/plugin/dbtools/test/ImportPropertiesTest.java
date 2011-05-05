package ambit2.plugin.dbtools.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IBatchStatistics.RECORDS_STATS;
import ambit2.base.processors.batch.BatchProcessor;
import ambit2.core.io.FileInputState;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.dbui.LoginInfoBean;
import ambit2.plugin.dbtools.ImportPropertiesWorkflow;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.InputFileSelection;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;

public class ImportPropertiesTest extends WorkflowTest<ImportPropertiesWorkflow>{
	protected int count = 0;
	@Override
	protected ImportPropertiesWorkflow getWorkflow() {
		return new ImportPropertiesWorkflow();
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
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(0, dictionary.getRowCount());

		LoginInfoBean li = new LoginInfoBean();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		ImportPropertiesWorkflow wf = getWorkflow();
		context.put(DBWorkflowContext.LOGININFO, li);
		context.put("SELECTION", new CASKey());		
		context.put(InputFileSelection.INPUTFILE,
					 new FileInputState("src/test/resources/ambit2/plugin/dbtools/test/sdf/test.sdf"));

		

		wf.addPropertyChangeListener(new WorkflowListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(WorkflowEvent.WF_COMPLETE))
					completed = true;

			}
		});
;
		completed = false;

		context.addPropertyChangeListener(BatchProcessor.PROPERTY_BATCHSTATS,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {

						if (evt.getNewValue() != null) {
							//System.out.println(evt.getNewValue());
							count++;
						}

					}
				});
		wf.executeWith(context);
		
		while (!completed) {}

		Assert.assertTrue(context.get(BatchProcessor.PROPERTY_BATCHSTATS) instanceof IBatchStatistics);
		IBatchStatistics stats = (IBatchStatistics)context.get(BatchProcessor.PROPERTY_BATCHSTATS);
		Assert.assertEquals(7,stats.getRecords(RECORDS_STATS.RECORDS_READ));
		Assert.assertEquals(7,stats.getRecords(RECORDS_STATS.RECORDS_ERROR));
		Assert.assertEquals(0,stats.getRecords(RECORDS_STATS.RECORDS_PROCESSED));
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT * FROM structure");
		Assert.assertEquals(4, structures.getRowCount());	
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT idstructure FROM structure join struc_dataset using(idstructure) join src_dataset using(id_srcdataset) where name='test.sdf'");
		Assert.assertEquals(0, structures.getRowCount());	
		templates = c.createQueryTable("EXPECTED_TEMPLATES",	"SELECT * FROM template join template_def using(idtemplate) where name='test.sdf'");
		Assert.assertEquals(0, templates.getRowCount());			
	}
}
