package ambit2.ui.actions.dbadmin;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.ui.query.QDbConnectionDialog;
import ambit2.database.data.ISharedDbData;
import ambit2.exceptions.AmbitException;
import ambit2.io.batch.IJobStatus;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * Opens database connection. 
 * See the example at {@link ambit2.ui.actions.dbadmin.DbBatchImportAction} , {@link ambit2.ui.actions.dbadmin.DbConnectionStatusAction}
 * or {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbOpenAction extends AmbitAction {

	public DbOpenAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Connect");
	}

	public DbOpenAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/database.png"));
	}

	public DbOpenAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Connects to user selected database.");
	}

	public void run(ActionEvent arg0) {
		
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    boolean connected = (dbaData.getDbConnection() != null) 
		    			&& (!dbaData.getDbConnection().isClosed());
		    if (connected)
			    if (JOptionPane.showOptionDialog(mainFrame,
						"<html>You are already connected to <br><font color='#0000FF'>"+
						dbaData.toString() +
						"</font><br><b>Are you sure to disconnect and connect manually?</b></html>",
						"Connect to AMBIT database",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,null,null,null
						) == JOptionPane.NO_OPTION) return;
		    
			QDbConnectionDialog d = new QDbConnectionDialog((JFrame)getMainFrame(),true);
			d.setDbData(dbaData);
			try {
			d.centerParent();
			} catch (Exception x) {
			    
			}
			d.setVisible(true);
//			TODO  modified flag ... not this stupid waiy
			d.getHost();d.getPort();d.getDatabase();d.getUser();d.getPassword();
			if (d.getResult() == JOptionPane.OK_OPTION) {
				super.run(arg0);
				//if (d.isModified()) {
				if (true) {
					try {
						dbaData.close();
						dbaData.open(d.getHost(),d.getPort(),d.getDatabase(),d.getUser(),d.getPassword());
						done();
						JOptionPane.showMessageDialog(mainFrame, "Connected to database");
						
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

	public void done() {
		if (userData instanceof ISharedDbData) {
			//((ISharedDbData) userData).done();		    
			((ISharedDbData) userData).getJobStatus().setStatus(IJobStatus.STATUS_DONE);
			((ISharedDbData) userData).getJobStatus().setMessage("");
		}
		
		
	}
	public void actionPerformed(ActionEvent arg0) {
		run(arg0);
		done();
	}

}
