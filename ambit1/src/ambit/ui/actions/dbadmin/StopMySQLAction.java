/* StopMySQLAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.ui.actions.dbadmin;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.database.MySQLShell;
import ambit.database.data.ISharedDbData;
import ambit.exceptions.AmbitException;
import ambit.ui.actions.AmbitAction;

/**
 * Stops the local copy of mysql that is distributed with {@link ambit.applications.dbadmin.AmbitDatabase}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class StopMySQLAction extends AmbitAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public StopMySQLAction(Object userData, JFrame mainFrame) {
        super(userData, mainFrame,"Stop local MySQL server");
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public StopMySQLAction(Object userData, JFrame mainFrame, String arg0) {
        super(userData, mainFrame, arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public StopMySQLAction(Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        try {
            if (userData instanceof ISharedDbData) {
    		    ISharedDbData dbaData = ((ISharedDbData) userData);
    		    boolean connected = (dbaData.getDbConnection() != null) 
    		    			&& (!dbaData.getDbConnection().isClosed());
    		    if (connected)
    		        dbaData.close();
            }    
            MySQLShell ms = new MySQLShell();
            ms.stopMySQL();
            ms = null;
            JOptionPane.showMessageDialog(mainFrame,"local MySQL stopped");
        } catch (AmbitException x) {
            JOptionPane.showMessageDialog(mainFrame,x);
            logger.error(x);
        }    
        }
}
