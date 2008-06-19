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

package com.microworkflow.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.microworkflow.ui.MapTableModel.Mode;

public class WorkflowContextPanel extends WorkflowContextListenerPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6970563738241836748L;
	protected MapTableModel mtm;

	public WorkflowContextPanel(IWorkflowContextFactory wfcfactory) {
		super(wfcfactory);
        mtm = new MapTableModel();
        mtm.setMode(Mode.labels_in_rows);
        JTable table = new JTable(mtm);
        //table.setPreferredSize(new Dimension(200,200));
        add(new JScrollPane(table), BorderLayout.CENTER);
	}
	@Override
	protected void animate(PropertyChangeEvent arg0) {
        mtm.setMap(getWorkflowContext());
        mtm.keyChange(arg0.getPropertyName(),arg0.getNewValue());
	}
	@Override
	public void clear() {
        if (mtm != null)
            mtm.setMap(null);
	}
    public synchronized void setAnimate(boolean animate) {
        super.setAnimate(animate);
        if (animate)
            mtm.setMap(getWfcfactory().getWorkflowContext());            
    }	
}


