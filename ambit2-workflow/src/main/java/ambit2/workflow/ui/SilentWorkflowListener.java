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

import java.beans.PropertyChangeEvent;

import javax.swing.JOptionPane;

import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.ValueLatchPair;
import com.microworkflow.ui.IWorkflowContextFactory;

/**
 * Same as parent class, but does not show dialogs. Useful for testing.
 * @author nina
 *
 */
public class SilentWorkflowListener extends WorkflowOptionsLauncher {
	protected Object error = null;
    public SilentWorkflowListener(IWorkflowContextFactory wfcfactory) {
        super(wfcfactory);
	}	
	public void propertyChange(PropertyChangeEvent evt) {
		error = null;
		
		if (evt.getPropertyName().equals(DBWorkflowContext.ERROR)) {
			error = evt.getNewValue();
		} else if (evt.getPropertyName().equals(DBWorkflowContext.USERINTERACTION)) {
			ValueLatchPair latch = null; 			
			try {
				if (evt.getNewValue() instanceof ValueLatchPair)
					latch = (ValueLatchPair) evt.getNewValue();
				else return;
				latch.getLatch().setValue(latch.getValue());
			} catch (Exception x) {
				latch.getLatch().setValue(null);
				x.printStackTrace();
				JOptionPane.showMessageDialog(getFrame(),x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);	
			}
		}

	}
}
