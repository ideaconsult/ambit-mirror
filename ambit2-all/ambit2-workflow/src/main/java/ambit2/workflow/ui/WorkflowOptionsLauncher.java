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
package ambit2.workflow.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ambit2.db.IDBProcessor;
import ambit2.ui.EditorPreferences;
import ambit2.ui.Utils;
import ambit2.ui.WizardPanel;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.events.WorkflowContextListener;
import com.microworkflow.process.ValueLatchPair;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;

public class WorkflowOptionsLauncher implements WorkflowContextListener {
	protected Component frame;
    protected IWorkflowContextFactory wfcfactory;
    protected WorkflowContext context;
    protected boolean animate = false;
    protected Vector<String> properties;
    
    public WorkflowOptionsLauncher(IWorkflowContextFactory wfcfactory) {
        properties = new Vector<String>();
        properties.add(UserInteractionEvent.PROPERTYNAME);
        properties.add(DBWorkflowContext.ERROR);        
		setWfcfactory(wfcfactory);
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DBWorkflowContext.ERROR)) {
			JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(getFrame()), evt.getNewValue());
		} else if (evt.getPropertyName().equals(DBWorkflowContext.USERINTERACTION)) {
			//if (evt instanceof UserInteractionEvent) {
			ValueLatchPair latch = null; 	
			IAmbitEditor editor = null;
			Connection connection = null;
			try {
				if (evt.getNewValue() instanceof ValueLatchPair)
					latch = (ValueLatchPair) evt.getNewValue();
				else return;
				//ValueLatchPair latch = ((UserInteractionEvent) evt).getLatch();
				editor = EditorPreferences.getEditor(latch.getValue());
				JComponent p = null;
				if (editor !=null) {
					p = editor.getJComponent();
					if (editor instanceof IDBProcessor) {
						connection =((DBWorkflowContext)context).getDataSource().getConnection();
						((IDBProcessor)editor).setConnection(connection);	
						((IDBProcessor)editor).open();
					}
				} else {
					p = new JLabel(latch.getValue().toString());
				}

				String help = Utils.getHelp(latch.getValue().getClass());
				String title = Utils.getTitle(latch.getValue().getClass());
				WizardPanel wizard = new WizardPanel(title,p,help);
				if (wizard.display(JOptionPane.getFrameForComponent(wizard),context.getName() + " Wizard",true)
						== WizardPanel.ANSWER_MODE.next) {
					if (editor!=null)
						editor.confirm();
					latch.getLatch().setValue(latch.getValue());
				} else {
					latch.getLatch().setValue(null);
				}
			} catch (Exception x) {
				latch.getLatch().setValue(null);
				x.printStackTrace();
				JOptionPane.showMessageDialog(getFrame(),x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	
			} finally {
				try {
					if (editor instanceof IDBProcessor) 
						((IDBProcessor)editor).getConnection().close();
					if (connection != null) connection.close();
				} catch (Exception x) {
					
				}
				
			}
		}

	}
	
	   public synchronized Vector<String> getProperties() {
	        return properties;
	    }
	    public synchronized void setProperties(Vector<String> properties) {
	        if (this.getWorkflowContext() != null) {
	            for (String p : this.properties)
	                this.getWorkflowContext().removePropertyChangeListener(p,this);
	            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
	        }    	
	        this.properties = properties;
	        if (this.getWorkflowContext() != null) {
	            for (String p : properties)
	                this.getWorkflowContext().addPropertyChangeListener(p,this);
	            this.getWorkflowContext().addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
	        }
	    }
	
	public Component getFrame() {
		return frame;
	}
	public void setFrame(Component frame) {
		this.frame = frame;
	}
	   public synchronized WorkflowContext getWorkflowContext() {
	    	if (wfcfactory!= null)
	    		return getWfcfactory().getWorkflowContext();
	    	else return context;
	    }

	    public synchronized void setWorkflowContext(WorkflowContext wfc) {
	        clear();
	        if (this.getWorkflowContext() != null) {
	            for (String p : properties)
	                this.getWorkflowContext().removePropertyChangeListener(p,this);
	            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
	        }
	        
	        this.context = wfc;
	        if (wfc != null) {
	            for (String p : properties)
	                wfc.addPropertyChangeListener(p,this);
	            wfc.addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
	        }
	     }
	    public synchronized IWorkflowContextFactory getWfcfactory() {
	        return wfcfactory;
	    }
	    public synchronized void setWfcfactory(IWorkflowContextFactory wfcfactory) {
	        this.wfcfactory = wfcfactory;
	        if (wfcfactory != null)
	        	setWorkflowContext(wfcfactory.getWorkflowContext());
	    }	    
	    public  void clear() {
	    	
	    }
}
