/* DbConnectionStatusAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-17 
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.DbConnection;
import ambit2.ui.query.DbStatusPanel;
import ambit2.database.data.ISharedDbData;
import ambit2.ui.AmbitColors;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * Database connection status. Example: Creates three buttons, 
 * one opens database connection, 
 * the other changes caption to "CONNECTED" when connection is established
 * and the third displays database statistics.
 <pre>
 	
 	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
 	DbOpenAction openAction = new DbOpenAction(dbadinData,null);
 	
 	JButton buttonOpen = new JButton(importAction);
 	
 	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbAdminData,null,"Status");
 	JButton buttonStatus = new JButton(statusAction);

 	DbStatisticsAction statisticsAction = new DbConnectionStatistics(dbAdminData,null,"Status");
 	JButton buttonStatistics = new JButton(statisticsAction); 	
 </pre> 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-17
 */
public class DbConnectionStatusAction extends AmbitAction implements Observer {
    protected static ImageIcon connectedIcon = UITools.createImageIcon("ambit2/ui/images/database_connected.png");
    protected static ImageIcon disconnectedIcon = UITools.createImageIcon("ambit2/ui/images/database_notconnected.png");
    /**
     * @param userData
     * @param mainFrame
     */
    public DbConnectionStatusAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Status");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public DbConnectionStatusAction(Object userData, JFrame mainFrame,
            String arg0) {
        this(userData, mainFrame, arg0,connectedIcon);
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public DbConnectionStatusAction(Object userData, JFrame mainFrame,
            String arg0, Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        if (userData instanceof Observable) {
        	((Observable) userData).addObserver(this);
        	update((Observable) userData,null);
        }
        	
    }
    /* (non-Javadoc)
     * @see ambit2.ui.actions.AmbitAction#run(java.awt.event.ActionEvent)
     */
    public void run(ActionEvent arg0) {
        if (userData instanceof Observable) {
	        DbStatusPanel panel = new DbStatusPanel("AMBIT Database Tools",Color.white,AmbitColors.DarkClr);
	        ((Observable) userData).addObserver(panel);
	        panel.update((Observable) userData,arg0);
	        JOptionPane.showMessageDialog(mainFrame,panel,"",JOptionPane.PLAIN_MESSAGE);
	        ((Observable) userData).deleteObserver(panel);
	        panel = null;
        }
    }
    public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof ISharedDbData ) {
    		DbConnection dbc = ((ISharedDbData) arg0).getDbConnection();
    		if ((dbc==null) || dbc.isClosed()) {
    		    putValue(AbstractAction.SMALL_ICON,disconnectedIcon);
    			putValue(AbstractAction.NAME,"Status: NOT CONNECTED");
    			putValue(AbstractAction.SHORT_DESCRIPTION,"Attempt to connect to "+ arg0.toString() + " UNSUCCESSFULL");
    		}
    		else {
    		    putValue(AbstractAction.SMALL_ICON,connectedIcon);
    			putValue(AbstractAction.NAME,"Status: CONNECTED");
    			putValue(AbstractAction.SHORT_DESCRIPTION,"Connected to "+ arg0.toString());
    		}
    	}
    	
    }
}
