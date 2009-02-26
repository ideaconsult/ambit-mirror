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
import java.awt.Color;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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
		labels = new JLabel[2][2];
		FormLayout layout = new FormLayout("100dlu,100dlu","12dlu,12dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(1,1,1,1);
        cc.hAlign = CellConstraints.DEFAULT;
        
        for (int i=0; i < labels.length;i++)
        	for (int j=0; j < labels[i].length;j++) {
        		labels[i][j]= new JLabel("");
        		labels[i][j].setForeground(Color.blue);
        		builder.add(labels[i][j],cc.xywh(j+1,i+1,1,1));
        	}
        labels[0][0].setText("Logged in");
        labels[1][0].setText("Database");
		
		JPanel panel = builder.getPanel();
		panel.setBorder(BorderFactory.createLineBorder(ClassHolderEditor.BorderColor));
		add(panel,BorderLayout.CENTER);
	}

	@Override
	protected void animate(PropertyChangeEvent e) {
		System.out.println(e.getPropertyName());
		if (DBWorkflowContext.LOGININFO.equals(e.getPropertyName())) {
			labels[0][1].setText( ((LoginInfo)e.getNewValue()).getUser());
			labels[1][1].setText( ((LoginInfo)e.getNewValue()).getDatabase());
		}

	}

	@Override
	public String toString() {
		return "Status";
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}



}



