/* StartMySQLAction.java
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

package ambit2.ui.actions.dbadmin;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.DbAdmin;
import ambit2.database.MySQLShell;
import ambit2.exceptions.AmbitException;
import ambit2.ui.actions.AmbitAction;

/**
 * Starts local copy of mysql that is distributed with {@link ambit2.applications.dbadmin.AmbitDatabase}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class StartMySQLAction extends AmbitAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public StartMySQLAction(Object userData, JFrame mainFrame) {
        super(userData, mainFrame,"Start local MySQL server");
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public StartMySQLAction(Object userData, JFrame mainFrame, String arg0) {
        super(userData, mainFrame, arg0);
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public StartMySQLAction(Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see ambit2.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        
        try {
            //First try it 
            DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            if (dbc.open(false)) {
                logger.info("successfully connected");
                
            }
            try {
                dbc.close();
            } catch (SQLException x) {
                
            }
            JOptionPane.showMessageDialog(mainFrame,"MySQL already running locally on port 33060");
            return;
        } catch (AmbitException x) {
            logger.error(x);
        }
        try {
            MySQLShell ms = new MySQLShell();
            ms.startMySQL();
            ms = null;
            JOptionPane.showMessageDialog(mainFrame,"MySQL started locally on port 33060");
        } catch (AmbitException x) {
            JOptionPane.showMessageDialog(mainFrame,x);
            logger.error(x);
        }
    }
}
