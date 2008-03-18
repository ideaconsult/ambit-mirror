/* WFAbstractAction.java
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

import javax.swing.AbstractAction;
import javax.swing.Icon;

import EDU.oswego.cs.dl.util.concurrent.misc.SwingWorker;

public abstract class MWFAbstractAction extends AbstractAction {
    /**
     * Defines an Action object with a default description string and default icon.
     */
    public MWFAbstractAction() { 
        this("N/A");
    }
    /**
     * Defines an Action object with the specified description string and a default icon.
     */
    public MWFAbstractAction(String name) {
        this(name,null);
    }
    /**
     *     Defines an Action object with the specified description string and a the specified icon.
     */ 
    public MWFAbstractAction(String name, Icon icon) {
        super(name,icon);
    }
 

    public void actionPerformed(ActionEvent arg0) {
        executeinThread(arg0);
    }
    public abstract void runAction(ActionEvent arg0) throws Exception ;
    
    public void executeinThread(final ActionEvent arg0)  {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    setEnabled(false);
                    runAction(arg0);
                } catch (Exception x) {
                    x.printStackTrace();
                }                   
                return null; //TODO return smth reasonable
            }
            //Runs on the event-dispatching thread.
            public void finished() {
                setEnabled(true);
            }
        };
        worker.start();         
        
    }    
}
