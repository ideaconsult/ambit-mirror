package ambit.ui.actions.dbadmin;

import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.data.AmbitUser;
import ambit.database.DbConnection;
import ambit.database.data.ISharedDbData;
import ambit.exceptions.AmbitException;
import ambit.ui.actions.AmbitAction;
import ambit.ui.query.QDbConnectionDialog;

/**
 * Database administration.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbAdminAction extends AmbitAction {

	public DbAdminAction(Object userData, JFrame mainFrame) {
		super(userData, mainFrame);
		// TODO Auto-generated constructor stub
	}

	public DbAdminAction(Object userData, JFrame mainFrame, String arg0) {
		super(userData, mainFrame, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbAdminAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	protected void open(String database) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			String userName = "root";
			AmbitUser user = dbaData.getUser();
			if (user != null) userName = user.getName();
			
			DbConnection newConnection = open(database,userName);
			if (newConnection != null) {
			    if (dbaData.getDbConnection() != null) 
			        try {
			        dbaData.getDbConnection().close();
			        } catch (SQLException x) {
			            logger.error(x);
			        }
			    dbaData.setDbConnection(newConnection);
			}    
		}	
	}	
	/*
	protected void open(String database) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);

			QDbConnectionDialog d = new QDbConnectionDialog(getMainFrame(),true);
			d.setHost("localhost");
			d.setDatabase(database);
			AmbitUser user = dbaData.getUser();
			if (user == null) d.setUser("root");
			else d.setUser(user.getName());
			d.setPassword("");
			
			d.centerParent();
			d.setVisible(true);
			if (d.getResult() == JOptionPane.OK_OPTION) {
				super.run(null);
				
				String host = d.getHost();
				String port = d.getPort();
				String db = d.getDatabase();
				String u = d.getUser();
				String pass = d.getPassword();
				if (d.isModified()) {
					try {
						dbaData.close();
						dbaData.open(host,port,db,u,pass,false);
						
					} catch (AmbitException x) {
						JOptionPane.showMessageDialog(getMainFrame(),x.getMessage());
						logger.error(x);
						dbaData.setDbConnection(null);
					}
				}
			}
			d.dispose();
			d = null;
		}	
	}
	*/
	protected DbConnection open(String database,String user) {
	    	DbConnection newConnection = null;
			QDbConnectionDialog d = new QDbConnectionDialog((JFrame)getMainFrame(),true);
			d.setHost("localhost");
			d.setDatabase(database);
			d.setUser(user);
			d.setPassword("");
			try {
			d.centerParent();
			} catch (Exception x) {
			    
			}
			d.setVisible(true);
			if (d.getResult() == JOptionPane.OK_OPTION) {
				super.run(null);
				
				String host = d.getHost();
				String port = d.getPort();
				String db = d.getDatabase();
				String u = d.getUser();
				String pass = d.getPassword();
				if (d.isModified()) {
					try {
					    newConnection = new DbConnection(host,port,db,u,pass);
					    newConnection.open(false);
						
					} catch (AmbitException x) {
						JOptionPane.showMessageDialog(getMainFrame(),x.getMessage());
						logger.error(x);
						newConnection = null;
					}
				}
			}
			d.dispose();
			d = null;
			return newConnection;
	}
	
}
