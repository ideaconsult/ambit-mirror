/*
 * Created on 2005-12-13
 *
 */
package ambit.ui.actions.dbadmin;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.data.AmbitUser;
import ambit.database.DbAdmin;
import ambit.database.DbConnection;
import ambit.database.data.ISharedDbData;
import ambit.ui.UITools;


/**
 * Action to create a database.
 * @author Nina Jeliazkova nina@acad.bg <b>Modified</b> 2005-12-13
 */
public class DbCreateDatabaseAction extends DbAdminAction {

	/**
	 * @param userData
	 * @param mainFrame
	 */
	public DbCreateDatabaseAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Create database");
	}

	/**
	 * @param userData
	 * @param mainFrame
	 * @param arg0
	 */
	public DbCreateDatabaseAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/database_new.png"));
	}

	/**
	 * @param userData
	 * @param mainFrame
	 * @param arg0
	 * @param arg1
	 */
	public DbCreateDatabaseAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ambit.ui.app.dbadmin.actions.AmbitAction#run(java.awt.event.ActionEvent)
	 */
	public void run(ActionEvent arg0) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			//dbaData.setDbConnection(null);
			DbConnection newConnection = open("mysql","root");
			if (newConnection == null) {
			    JOptionPane.showMessageDialog(mainFrame,"Can't connect to MySQL, no new database created");
			    return;
			}
			try {
				DbAdmin dba = new DbAdmin(newConnection);
				String dbName = "ambit";
				dbName = JOptionPane.showInputDialog(mainFrame,"Database to create",dbName);
				if (!dbName.equals("")) {
					super.run(arg0);
					dba.createDatabase(dbName);
					dba.createTables(dbName);
					AmbitUser user = new AmbitUser("guest");
					dba.createUsers(dbName, user);
				}
				
				dba = null;
				//dba.close();
				newConnection.close();
				newConnection = null;
			} catch (Exception x) {
				JOptionPane.showMessageDialog(getMainFrame(), x);
				logger.error(x);
				
			}
			dbaData.setDbConnection(null);
		}

	}


}
