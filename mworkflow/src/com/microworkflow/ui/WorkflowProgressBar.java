/* WorkflowProgressBar.java
 * Author: Nina Jeliazkova
 * Date: Mar 17, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package com.microworkflow.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import com.microworkflow.events.WorkflowEvent;

public class WorkflowProgressBar extends JProgressBar implements IWorkflowListenerUI {
    /**
     * 
     */
    private static final long serialVersionUID = -4301737383158703262L;

    public WorkflowProgressBar() {
        setIndeterminate(true);
        setVisible(false);
    }
    public JComponent getUIComponent() {
        return this;
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowEvent.WF_EXECUTE.equals(arg0.getPropertyName()) ||
            WorkflowEvent.WF_START.equals(arg0.getPropertyName())
            ) 
            setVisible(true);
        else
        if (WorkflowEvent.WF_COMPLETE.equals(arg0.getPropertyName()))
            setVisible(false);
    }

}
