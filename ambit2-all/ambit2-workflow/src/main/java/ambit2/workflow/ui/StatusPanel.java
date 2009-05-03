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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambit2.db.LoginInfo;
import ambit2.ui.editors.ClassHolderEditor;
import ambit2.workflow.DBWorkflowContext;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowContextListenerPanel;

public class StatusPanel extends WorkflowContextListenerPanel {
	protected JLabel[][] labels;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5795610413694375982L;

    public StatusPanel(WorkflowContext wfcontext) {
		super();
		setWorkflowContext(wfcontext);
		setAnimate(true);
		setLayout(new BorderLayout());
		labels = new JLabel[2][3];
		FormLayout layout = new FormLayout("fill:64dlu,fill:64dlu,fill:64dlu","12dlu,12dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(1,1,1,1);
        cc.hAlign = CellConstraints.DEFAULT;
        
        for (int i=0; i < labels.length;i++)
        	for (int j=0; j < labels[i].length;j++) {
        		labels[i][j]= new JLabel("");
        		labels[i][j].setAlignmentX((j==0)?RIGHT_ALIGNMENT:LEFT_ALIGNMENT);
        		labels[i][j].setForeground(getForeground());
        		labels[i][j].setOpaque(true);
        		labels[i][j].setBackground(getBackground());
        		builder.add(labels[i][j],cc.xywh(j+1,i+1,1,1));
        	}
        labels[0][0].setText("Not logged in"); 
        labels[0][2].setText("");
        labels[0][2].setVisible(false);
        labels[1][0].setText("Database");
		
        labels[0][2].addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		super.mouseClicked(e);
        		logout();
        	}
        });
		JPanel panel = builder.getPanel();
		panel.setBorder(BorderFactory.createLineBorder(ClassHolderEditor.BorderColor));
		add(panel,BorderLayout.CENTER);
	}
    protected void logout() {
    	try {
    		if (JOptionPane.showConfirmDialog(null,"Log out from database. Search results will become unavailable.","Please confirm",JOptionPane.YES_NO_OPTION)
	        		==JOptionPane.YES_OPTION) {
    			if (getWorkflowContext() instanceof DBWorkflowContext) {
    				((DBWorkflowContext)getWorkflowContext()).logout(getWorkflowContext().get(DBWorkflowContext.DBCONNECTION_URI).toString());
    			}
    			/*
	    		DatasourceFactory.logout(getWorkflowContext().get(DBWorkflowContext.DBCONNECTION_URI).toString());
	    		getWorkflowContext().put(DBWorkflowContext.DBCONNECTION_URI,null);
	    		getWorkflowContext().put(DBWorkflowContext.DATASOURCE,null);
	    		getWorkflowContext().put(DBWorkflowContext.SESSION,null);
	    		*/
    		}
    	} catch (Exception x) {
    		getWorkflowContext().put(DBWorkflowContext.ERROR, x);
    	}
    }

	@Override
	protected void animate(PropertyChangeEvent e) {
/*
		if (DBWorkflowContext.LOGININFO.equals(e.getPropertyName())) {
			if (e.getNewValue()!=null) {
				labels[0][1].setText( ((LoginInfo)e.getNewValue()).getUser());
				labels[1][1].setText( ((LoginInfo)e.getNewValue()).getDatabase());
			} else {
					
			}
		} else
		*/
		if (DBWorkflowContext.DBCONNECTION_URI.equals(e.getPropertyName()) || DBWorkflowContext.DATASOURCE.equals(e.getPropertyName())) {
			if (e.getNewValue()!= null) {
				labels[0][0].setText("Logged in"); 
				labels[0][2].setText("<html><u>Log out</u></html>");
				labels[0][2].setVisible(true);
				
				Object li = getWorkflowContext().get(DBWorkflowContext.LOGININFO);
				if ((li == null) || !(li instanceof LoginInfo)) {
					labels[0][1].setText( e.getNewValue().toString());
					labels[1][1].setText( e.getNewValue().toString());			
					labels[0][1].setToolTipText(e.getNewValue().toString());
				} else {
					labels[0][1].setText(((LoginInfo)li).getUser());
					labels[1][1].setText(((LoginInfo)li).getDatabase());	
					labels[0][1].setToolTipText(e.getNewValue().toString());					
				}

			} else {
				labels[0][0].setText("Not logged in"); 
				labels[0][2].setText("<html><u>Log in</u></html>");
				labels[0][2].setVisible(false);			
				labels[0][1].setText("");
				labels[1][1].setText("");			
				labels[0][1].setToolTipText("");				
			}
			
		}		

	}

	@Override
	public String toString() {
		return "Connection status";
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}



}



