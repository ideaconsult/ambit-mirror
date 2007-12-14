/**
 * Created on Feb 6, 2005
 *
 */
package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.TreeMap;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import ambit.data.AmbitUser;
import ambit.database.data.ISharedDbData;
import ambit.exceptions.AmbitException;
import ambit.ui.CoreDialog;
import ambit.ui.CorePanel;


/**
 * A database connection dialog
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QDbConnectionDialog extends CoreDialog {
	protected DbConnectionPanel dbPanel;
	/**
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog() throws HeadlessException {
		super();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Dialog owner) throws HeadlessException {
		super(owner);

	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Frame owner) throws HeadlessException {
		super(owner);

	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Frame owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Dialog owner, String title)
			throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Frame owner, String title)
			throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public QDbConnectionDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public QDbConnectionDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}

	protected void addWidgets() {
		super.addWidgets();
		setTitle("Connect to database");
		dbPanel = new DbConnectionPanel("Login");
		Container contentPane = getContentPane();
		contentPane.add(dbPanel, BorderLayout.CENTER);
	}
	
	public void setUser(String user) {
		dbPanel.setUser(user);
	}
	public void setPassword(String pass) {
		dbPanel.setPassword(pass);		
	}
	public void setDatabase(String database) {
		dbPanel.setDatabase(database);		
	}
	
	public void setHost(String host) {
		dbPanel.setHost(host);		
	}
	public void setPort(String port) {
		dbPanel.setPort(port);		
	}
	public String getUser() {
		return dbPanel.getUser();		
	}
	public String getPassword() {
		return dbPanel.getPassword();		
	}
	public String getDatabase() {
		return dbPanel.getDatabase();		
	}
	
	public String getHost() {
		return dbPanel.getHost();		
	}
	public String getPort() {
		return dbPanel.getPort();		
	}	
	public boolean isModified() {
		return dbPanel.isModified();
	}

	public void setDbData(ISharedDbData dbaData) {
		setHost(dbaData.getHost());
		setPort(dbaData.getPort());		
		setDatabase(dbaData.getDatabase());
		AmbitUser user = dbaData.getUser();
		if (user == null) setUser("");
		else setUser(user.getName());
		setPassword("");
	}
	public static void execute(JFrame parent,ISharedDbData dbaData){
		QDbConnectionDialog d = new QDbConnectionDialog(parent,true);
		d.setDbData(dbaData);
		d.setVisible(true);
		try {
		d.centerParent();
		} catch (Exception x) {
		    
		}
		
//		TODO  modified flag ... not this stupid waiy
		d.getHost();d.getPort();d.getDatabase();d.getUser();d.getPassword();
		if (d.getResult() == JOptionPane.OK_OPTION) {
			if (d.isModified()) {
				try {
					dbaData.close();
					dbaData.open(d.getHost(),d.getPort(),d.getDatabase(),d.getUser(),d.getPassword());
					JOptionPane.showMessageDialog(parent, "Connected to database");
					
				} catch (AmbitException x) {
					JOptionPane.showMessageDialog(parent,x.getMessage());
					dbaData.setDbConnection(null);
				}
			}
		}
		d.dispose();
		d = null;
	}
}

class DbConnectionPanel extends CorePanel {
	protected TreeMap labels, edits;
	protected boolean modified = false;
	String[] dbParams = {"guest","guest","localhost","3306","ambit"};
	public static String[] dbKeys = {"1USER","2PASSWORD","3HOST","4PORT","5DATABASE"};
	protected static int index_user = 0;
	protected static int index_password = 1;
	protected static int index_host = 2;
	protected static int index_port = 3;
	protected static int index_database = 4;
	
	
	public DbConnectionPanel(String title) {
		super(title);
		modified = false;
	}
	
	public DbConnectionPanel(String title,  Color  bClr, Color fClr) {
		super(title,bClr,fClr);
		modified = false;
	}	
	public void setUser(String user) {
		dbParams[index_user] = user;
		((JFormattedTextField) edits.get(dbKeys[index_user])).setText(user);
	}
	public void setPassword(String pass) {
		dbParams[index_password] = pass;
		((JPasswordField) edits.get(dbKeys[index_password])).setText(pass);
	}
	
	public void setHost(String host) {
		dbParams[index_host] = host;
		((JFormattedTextField) edits.get(dbKeys[index_host])).setText(host);
	}
	public void setPort(String port) {
		dbParams[index_port] = port;
		((JFormattedTextField) edits.get(dbKeys[index_port])).setText(port);
	}
	
		
	public void setDatabase(String database) {
		dbParams[index_database] = database;
		((JFormattedTextField) edits.get(dbKeys[index_database])).setText(database);
	}
	
	public String getHost() {
		String host = ((JFormattedTextField) edits.get(dbKeys[index_host])).getText();
		modified = modified || !(host.equals(dbParams[index_host]));
		return host;
	}
	
	public String getDatabase() {
		String database = ((JFormattedTextField) edits.get(dbKeys[index_database])).getText();
		modified = modified || !(database.equals(dbParams[index_database]));
		return database;
	}
	public String getUser() {
		String user = ((JFormattedTextField) edits.get(dbKeys[index_user])).getText();
		modified = modified || !(user.equals(dbParams[index_user]));
		return user;
	}
	public String getPort() {
		String user = ((JFormattedTextField) edits.get(dbKeys[index_port])).getText();
		modified = modified || !(user.equals(dbParams[index_port]));
		return user;
	}	
	public String getPassword() {
		String pass = ((JPasswordField) edits.get(dbKeys[index_password])).getText();
		modified = modified || !(pass.equals(dbParams[index_password]));
		return pass;
	}

	protected void addWidgets() {
		labels = new TreeMap();
		edits = new TreeMap();
		createWidget(labels,edits,dbKeys[index_user],"User","guest","Enter user name",true);
		createPasswordWidget(labels,edits,dbKeys[index_password],"Password","guest","Enter password here",true);				
		createWidget(labels,edits,dbKeys[index_host],"Host","localhost","The computer where database is running, 'localhost' means this computer",true);
		createWidget(labels,edits,dbKeys[index_port],"Port","3306","Port",true);		
		createWidget(labels,edits,dbKeys[index_database],"Database","ambit","Database name (can not be modified)",true);
		GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
       	c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(1,1,1,1);		
		
		placeWidgets(labels,edits,c);
	}
	public boolean isModified() {
		return modified;
	}

};