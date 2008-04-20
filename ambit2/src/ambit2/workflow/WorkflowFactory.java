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

package ambit2.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.data.molecule.SourceDataset;
import ambit2.database.DatasourceFactory;
import ambit2.repository.LoginInfo;
import ambit2.repository.processors.ProcessorCreateQuery;
import ambit2.repository.processors.ProcessorCreateSession;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.ValueLatchPair;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.IWorkflowFactory;

public class WorkflowFactory implements IWorkflowFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6456499162628345432L;
	protected Workflow workflow;
	protected List<String> tasks;

	public WorkflowFactory() {
		tasks = new ArrayList<String>();
        tasks.add("Find analogs");
        tasks.add("Log in");
        workflow = new Workflow();
        setTheTask("Log in");
	}
	public Workflow getWorkflow() {
		return workflow;
	}
	public Iterator<String> tasks() {
		return tasks.iterator();
	}

	public void setTheTask(String theTask) {
		System.out.println(theTask);
		if ("Log in".equals(theTask)) {
			/*
			Primitive thenP = new Primitive(DBWorkflowContext.DATASOURCE,new Performer() {
				@Override
				public Object execute() {
					System.out.println(getTarget());
					return null;
				}
			}); 			
			thenP.setName("OK");
			*/
			Primitive datasource = new Primitive(DBWorkflowContext.DATASET,DBWorkflowContext.DATASET,new Performer() {
				@Override
				public Object execute() {
					Object o = getTarget();
					if (o == null) {
						ValueLatchPair<SourceDataset> latch = new ValueLatchPair<SourceDataset>(new SourceDataset());
						context.put(DBWorkflowContext.DATASET,latch);
						//This is a blocking operation!
						try {
							SourceDataset li = latch.getLatch().getValue();
							return li;
						} catch (InterruptedException x) {
							context.put(DBWorkflowContext.ERROR, x);
							return null;
						}
						
					} else return o;	
				}
			}); 
			datasource.setName("datasource");			
			workflow.setDefinition(getLoginWorkflow(datasource));
		}
		else
			if ("Find analogs".equals(theTask)) workflow.setDefinition(getFindAnalogsWorkflow());
	}
	
	private Activity getFindAnalogsWorkflow() {
		ActivityPrimitive test = new ActivityPrimitive(DBWorkflowContext.SESSION,DBWorkflowContext.SESSION,new ProcessorCreateSession());
		test.setName("Session");
		ActivityPrimitive p1 = new ActivityPrimitive("Query","Result",new ProcessorCreateQuery());
		p1.setName("Query");	
		return test.addStep(p1);
	}
	private Activity getLoginWorkflow(Activity onSuccess) {

		Primitive login = new Primitive(DBWorkflowContext.DBCONNECTION_URI,DBWorkflowContext.DBCONNECTION_URI,new Performer() {
			@Override
			public Object execute() {
				Object o = getTarget();
				if (o == null) {
					ValueLatchPair<LoginInfo> latch = new ValueLatchPair<LoginInfo>(new LoginInfo());
					context.put(DBWorkflowContext.LOGININFO,latch);
					//This is a blocking operation!
					try {
						LoginInfo li = latch.getLatch().getValue();
						return DatasourceFactory.getConnectionURI(
								li.getScheme(), li.getHostname(), li.getPort(), 
								li.getDatabase(), li.getUser(), li.getPassword());
					} catch (InterruptedException x) {
						context.put(DBWorkflowContext.ERROR, x);
						return null;
					}
					
				} else return o;	
			}
		}); 
		login.setName("Login");

		Conditional connect = new Conditional(
				new TestCondition() {
					public boolean evaluate() {
						try {
							Object o = context.get(DBWorkflowContext.DBCONNECTION_URI);
							if (o != null) {
								context.put(DBWorkflowContext.DATASOURCE,DatasourceFactory.getDataSource(o.toString()));
								return  true;
							} else {
								context.remove(DBWorkflowContext.DBCONNECTION_URI);
								return false;
							}
						} catch (Exception x) {
							context.put(DBWorkflowContext.ERROR, x);
							context.remove(DBWorkflowContext.DBCONNECTION_URI);
							return false;
						}
					}
				}, 
				onSuccess,
				login);
		connect.setName("connect");
		return login.addStep(connect);
	}	
}


