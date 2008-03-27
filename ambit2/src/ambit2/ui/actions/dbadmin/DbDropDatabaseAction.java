package ambit2.ui.actions.dbadmin;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.DbAdmin;
import ambit2.database.data.ISharedDbData;

/**
 * Deletes database. Use with care.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbDropDatabaseAction extends DbAdminAction {

	public DbDropDatabaseAction(Object userData, JFrame mainFrame) {
		super(userData, mainFrame,"Delete database");
		// TODO Auto-generated constructor stub
	}

	public DbDropDatabaseAction(Object userData, JFrame mainFrame, String arg0) {
		super(userData, mainFrame, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbDropDatabaseAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	

	public void run(ActionEvent arg0) {

		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			
			boolean root = false;
			//if ((conn ==null) || conn.isClosed()) {
			dbaData.setDbConnection(null);
			open("mysql"); root = true;  
			try {
				if (dbaData.getDbConnection() != null) {
					DbAdmin dba = new DbAdmin(dbaData.getDbConnection());
					String dbName = "ambittest";
					dbName = JOptionPane.showInputDialog(mainFrame,"Database to delete",dbName);
					if (!dbName.equals("")) { 
						if (dbName.equals(dbaData.getDatabase()))
							JOptionPane.showMessageDialog(mainFrame,"Database in use! Can't delete !"+dbName);
						else	
						if (JOptionPane.showConfirmDialog(mainFrame,"Are you sure to delete database "+dbName+" ? You will lose all data stored in this database!")
								== JOptionPane.OK_OPTION) {
							super.run(arg0);
							dba.dropDatabase(dbName);
						}
					}
					dba.close();
					dba = null;
				}

			} catch (Exception x) {
				JOptionPane.showMessageDialog(getMainFrame(), x.getMessage());
				logger.error(x);
				
			}
			dbaData.setDbConnection(null);
		}

	}

}
