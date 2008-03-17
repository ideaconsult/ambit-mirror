/* WFActionAnimate.java
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

import com.microworkflow.ui.IWorkflowFactory;

public class WFActionAnimate extends WFAbstractAction {
    protected boolean animate = false;
    /**
     * 
     */
    private static final long serialVersionUID = 5741420991282621755L;

    public WFActionAnimate(IWorkflowFactory wffactory) {
        super(wffactory);
        putValue(NAME,"Animate");
        putValue(SHORT_DESCRIPTION,"Animate workflow");
    }

    @Override
    public void runAction(ActionEvent arg0) throws Exception {
        getWfFactory().getWorkflow().fireAnimateEvent(animate);

    }

    public synchronized boolean isAnimate() {
        return animate;
    }

    public synchronized void setAnimate(boolean animate) {
        this.animate = animate;
        if (animate) putValue(NAME, "Stop animation");
        else putValue(NAME, "Animate");
    }

}
