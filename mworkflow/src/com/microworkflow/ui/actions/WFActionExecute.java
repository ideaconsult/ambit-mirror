/* WFActionExecute.java
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

package com.microworkflow.ui.actions;

import java.awt.event.ActionEvent;

import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.IWorkflowFactory;

public class WFActionExecute extends WFAbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 2471733631679721760L;
    protected IWorkflowContextFactory wfcFactory;
    

    public WFActionExecute(IWorkflowFactory wf, IWorkflowContextFactory wfc) {
        super(wf);
        putValue(NAME, "Run");
        putValue(SHORT_DESCRIPTION, "Run workflow");
        setWfcFactory(wfc);
    }
    @Override
    public void runAction(ActionEvent arg0)  throws Exception {
        getWfFactory().getWorkflow().executeWith(
                getWfcFactory().getWorkflowContext());

    }
    public synchronized IWorkflowContextFactory getWfcFactory() {
        return wfcFactory;
    }
    public synchronized void setWfcFactory(IWorkflowContextFactory wfcFactory) {
        this.wfcFactory = wfcFactory;
    }


}
