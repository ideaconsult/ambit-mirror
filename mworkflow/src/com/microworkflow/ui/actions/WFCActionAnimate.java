/* WFCActionAnimate.java
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

public class WFCActionAnimate extends WFCAbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 8871257277903477542L;
    protected boolean animate = false;
    protected static String title = "Animate results";
    protected static String title_stop = "Stop results animation";
    protected static String title_descr = "Animate workflow results";
    public WFCActionAnimate(IWorkflowContextFactory wfcf) {
        super(wfcf);
        putValue(NAME,title);
        putValue(SHORT_DESCRIPTION,title_descr);
    }    
    @Override
    public void runAction(ActionEvent arg0) throws Exception {
        getWfcFactory().getWorkflowContext().fireAnimateEvent(animate);

    }

    public synchronized boolean isAnimate() {
        return animate;
    }

    public synchronized void setAnimate(boolean animate) {
        this.animate = animate;
        if (animate) putValue(NAME, title_stop);
        else putValue(NAME, title);
    }
}
