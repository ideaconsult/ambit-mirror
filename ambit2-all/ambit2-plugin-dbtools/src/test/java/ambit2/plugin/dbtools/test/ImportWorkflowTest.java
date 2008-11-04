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
import java.io.File;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.core.io.FileInputState;
import ambit2.core.processors.batch.BatchProcessor;
import ambit2.db.LoginInfo;
import ambit2.db.SourceDataset;
import ambit2.plugin.dbtools.ImportWorkflow;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.InputFileSelection;
import ambit2.workflow.ui.SilentWorkflowListener;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;

public class ImportWorkflowTest {
	protected WorkflowOptionsLauncher contextListener;
	protected int count = 0;
	protected DBWorkflowContext context;
	protected boolean completed = false;
	@Before
	public void setUp() throws Exception {
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

	@Test
	public void testExecuteWith() throws Exception {
		LoginInfo li = new LoginInfo();
		li.setDatabase("ambit100");
		li.setPort("33060");
		li.setUser("guest");
		li.setPassword("guest");
		context.put(DBWorkflowContext.LOGININFO, li);
		context.put(InputFileSelection.INPUTFILE,
				new FileInputState(new File("D:/src/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/M__STY/sgroup.sdf")
				));
		context.put(DBWorkflowContext.DATASET, new SourceDataset("TEST-INPUTWORKFLOW"));
		ImportWorkflow wf = new ImportWorkflow();

		wf.addPropertyChangeListener(new WorkflowListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(WorkflowEvent.WF_COMPLETE))
					completed = true;
				
				
			}
		});
		completed = false;

		context.addPropertyChangeListener(BatchProcessor.PROPERTY_BATCHSTATS,new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				
				if (evt.getNewValue()!=null) {
					System.out.println(evt.getPropertyName()+"\t"+evt.getNewValue());
					count++;
				}
				
			}
		});
		wf.executeWith(context);		
		while (!completed) {

		}
		System.out.print(completed);
		System.out.println(count);		
		Assert.assertEquals(24,count);
	}

}
