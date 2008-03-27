/*
 * Created on 2005-12-19
 *
 */
package ambit2.ui.actions.dbadmin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.DbAdmin;
import ambit2.database.DbConnection;
import ambit2.ui.editors.AmbitObjectPanel;
import ambit2.data.AmbitUser;
import ambit2.database.data.ISharedDbData;
import ambit2.ui.AmbitColors;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * Creates user.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-19
 */
public class DbCreateUserAction extends AmbitAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public DbCreateUserAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Add User");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public DbCreateUserAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/users.png"));
        
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public DbCreateUserAction(Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        // TODO Auto-generated constructor stub
    }
	public void run(ActionEvent arg0) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			if ((conn ==null) || conn.isClosed()) JOptionPane.showMessageDialog(mainFrame, "Use Database/Open first");
			else {
			    AmbitUser user = new AmbitUser();
			    AmbitObjectPanel p = new AmbitObjectPanel("New user",user);
			    p.setBorder(BorderFactory.createLineBorder(AmbitColors.DarkClr));
			    p.setPreferredSize(new Dimension(300,400));
				if (JOptionPane.showConfirmDialog(mainFrame,p,"Add user to AMBIT database",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
						try {
							super.run(arg0);
							DbAdmin dba = new DbAdmin(conn);
							dba.createUsers(dbaData.getDatabase(),user);
							dba = null;
							JOptionPane.showMessageDialog(mainFrame,"User "+user.getName()+ " added.");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(mainFrame,ex.toString(),"Error when adding user to database",JOptionPane.OK_OPTION); 
							ex.printStackTrace();
						}
				}
				p = null;
				user = null;
			}
		}		
	}

}
