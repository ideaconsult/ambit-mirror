/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */
package ambit2.plugin.dbtools.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.processors.batch.BatchProcessor;
import ambit2.core.io.FileInputState;
import ambit2.db.LoginInfo;
import ambit2.plugin.dbtools.ImportWorkflow;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.InputFileSelection;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;

public class ImportWorkflowTest extends WorkflowTest<ImportWorkflow> {


	protected int count = 0;
	@Override
	protected ImportWorkflow getWorkflow() {
		return new ImportWorkflow();
	}
	@Test
	public void testExecuteWith() throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/dbtools/test/empty-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable structures = c.createQueryTable("EXPECTED_STRUCTURES",
		"SELECT * FROM structure");
		Assert.assertEquals(0, structures.getRowCount());			
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(0, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(0, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template");
		Assert.assertEquals(2, templates.getRowCount());
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(0, dictionary.getRowCount());

		LoginInfo li = new LoginInfo();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		ImportWorkflow wf = getWorkflow();
		context.put(DBWorkflowContext.LOGININFO, li);
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
							count++;
						}

					}
				});
		wf.executeWith(context);
		while (!completed) {}
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT * FROM structure");
		Assert.assertEquals(7, structures.getRowCount());	
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT idstructure FROM structure join struc_dataset using(idstructure) join src_dataset using(id_srcdataset) where name='test.sdf'");
		Assert.assertEquals(7, structures.getRowCount());	
		templates = c.createQueryTable("EXPECTED_TEMPLATES",	"SELECT * FROM template join template_def using(idtemplate) where name='test.sdf'");
		Assert.assertEquals(6, templates.getRowCount());			
	}

	
	@Test
	public void testMultiFile() throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/dbtools/test/empty-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable structures = c.createQueryTable("EXPECTED_STRUCTURES",
		"SELECT * FROM structure");
		Assert.assertEquals(0, structures.getRowCount());			
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(0, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(0, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template");
		Assert.assertEquals(2, templates.getRowCount());
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(0, dictionary.getRowCount());

		LoginInfo li = new LoginInfo();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		ImportWorkflow wf = getWorkflow();
		context.put(DBWorkflowContext.LOGININFO, li);
		context.put(InputFileSelection.INPUTFILE,
					 new FileInputState("src/test/resources/ambit2/plugin/dbtools/test/sdf"));

		

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
							count++;
						}

					}
				});
		wf.executeWith(context);
		while (!completed) {}
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT * FROM structure");
		Assert.assertEquals(8, structures.getRowCount());	
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT idstructure FROM structure join struc_dataset using(idstructure) join src_dataset using(id_srcdataset) where name='sdf'");
		Assert.assertEquals(8, structures.getRowCount());	
		templates = c.createQueryTable("EXPECTED_TEMPLATES",	"SELECT * FROM template join template_def using(idtemplate) where name='sdf'");
		Assert.assertEquals(38, templates.getRowCount());
		templates = c.createQueryTable("EXPECTED_TEMPLATES",	"SELECT name,value FROM values_string where name='CasRN' and value='110-51-0'");
		Assert.assertEquals(1, templates.getRowCount());			
		
	}	
}
