/*
 * Created on 2006-2-24
 *
 */
package ambit.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ambit.ui.actions.AmbitAction;

/**
 * Displays About dialog.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-24
 */
public class AboutAction extends AmbitAction {
	String iconFile;
	/**
     * 
     */
    
    public AboutAction(Object userData, JFrame mainFrame) {
		this(userData,mainFrame,"About",null);
		
	}

	public AboutAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData,mainFrame,arg0,null);
	}

	public AboutAction(Object userData, JFrame mainFrame, String arg0, Icon arg1) {
		super(userData,mainFrame,arg0, arg1);
		setUserData(userData);
		setMainFrame(mainFrame);
		worker = null;
	}
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

		//"<br><font color='#0000FF'><b>Developed (2006) by Ideaconsult Ltd.</b><br>4 Angel Kantchev St., 1000 Sofia, Bulgaria</font><br>contact: Nina Jeliazkova nina@acad.bg</html>";
		JLabel logo = new JLabel(userData.toString(),null,JLabel.CENTER);
		logo.setToolTipText("Click here to go to AMBIT site");
		logo.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				UITools.openURL("http://ambit.acad.bg");
			}
		});        		
		JOptionPane.showMessageDialog(mainFrame,logo,"About",JOptionPane.INFORMATION_MESSAGE,
				UITools.createImageIcon(getIconFile()));
		/*
				"<html>PerfSonar client demo v" + version +  
				"<br>Developed by ISTF <font color='#0000FF'><u>http://www.ist.bg/</u></font><br>e-mail <u>nina@acad.bg</u></html>");
				*/
		
	}

    public synchronized String getIconFile() {
        return iconFile;
    }
    public synchronized void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }


}
