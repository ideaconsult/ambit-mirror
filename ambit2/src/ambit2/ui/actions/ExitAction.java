package ambit2.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.data.ISharedDbData;
import ambit2.exceptions.AmbitException;

public class ExitAction extends AmbitAction {

	public ExitAction(Object userData, JFrame mainFrame) {
		super(userData, mainFrame,"Exit");
	}

	public ExitAction(Object userData, JFrame mainFrame, String arg0) {
		super(userData, mainFrame, arg0);
	}

	public ExitAction(Object userData, JFrame mainFrame, String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
	}

	
	public void run(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void done() {
		// TODO Auto-generated method stub

	}
	public void actionPerformed(ActionEvent arg0) {
         if (JOptionPane.showConfirmDialog(null,"Are you sure to exit ?","Please confirm",JOptionPane.YES_NO_OPTION)
        		==JOptionPane.YES_OPTION) {		
			 if (getUserData() instanceof ISharedDbData) 
				 try {
					 ((ISharedDbData) getUserData()).close();
				 } catch (AmbitException x) {
					 logger.error(x);
				 }
			 Runtime.getRuntime().runFinalization();						 
			 Runtime.getRuntime().exit(0);
         }	 
	}

}
