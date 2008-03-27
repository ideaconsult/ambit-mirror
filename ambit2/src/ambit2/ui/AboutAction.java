/*
 * Created on 2006-2-24
 *
 */
package ambit2.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ambit2.ui.actions.AmbitAction;

/**
 * Displays About dialog.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-24
 */
public class AboutAction extends AmbitAction {
	String app = "Ambit Discovery";
	String iconFile = "ambit2/ui/images/ambit_logo.jpg";
    String developers = "<br><font color='#0000FF'><b>Developed (2005-2007) by Joanna Jaworska and Nina Jeliazkova</b><br>for CEFIC-LRI</font><br>contact: Nina Jeliazkova nina@acad.bg</html>";
    String version = "1.20";
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
		this.app = userData.toString();
	}
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
    	Package jcpPackage = Package.getPackage("ambit2.applications");
		//version will be only available if started from jar file
		//version is specified in package manifest 
		// See MANIFESTAPP.MFT file
		version = jcpPackage.getImplementationVersion();
        } catch (Exception x) {
            
        }
		
		ImageIcon icon  = null;
		try {
			icon = UITools.createImageIcon(iconFile);
		} catch (Exception x) {
			icon =null;
		}
		String m = "<html>" + app +" v" + version + developers  
		;
		//"<br><font color='#0000FF'><b>Developed (2006) by Ideaconsult Ltd.</b><br>4 Angel Kantchev St., 1000 Sofia, Bulgaria</font><br>contact: Nina Jeliazkova nina@acad.bg</html>";
		JLabel logo = new JLabel(m,null,JLabel.CENTER);
		logo.setToolTipText("Click here to go to AMBIT site");
		logo.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				UITools.openURL("http://ambit2.acad.bg");
			}
		});        		
		JOptionPane.showMessageDialog(mainFrame,logo,"About",JOptionPane.INFORMATION_MESSAGE,icon);
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

    public synchronized String getDevelopers() {
        return developers;
    }

    public synchronized void setDevelopers(String developers) {
        this.developers = developers;
    }

    public synchronized String getVersion() {
        return version;
    }

    public synchronized void setVersion(String version) {
        this.version = version;
    }
}
