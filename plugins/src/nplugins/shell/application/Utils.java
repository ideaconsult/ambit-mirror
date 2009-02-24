package nplugins.shell.application;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import nplugins.core.Introspection;

public class Utils {
	protected static Logger logger = Logger.getLogger("nplugins.shell.application.Utils");
    public static ImageIcon createImageIcon(String path) {
    	try {
	        java.net.URL imgURL = Introspection.getLoader().getResource(path);
	
	        if (imgURL != null) {
	            return new ImageIcon(imgURL);
	        } else {
	            logger.warning("Couldn't find file: " + path);
	            return null;
	        }
    	} catch (Exception x) {
    		return null;
    	}
    }	
    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
           if (osName.startsWith("Mac OS")) {
              Class fileMgr = Class.forName("com.apple.eio.FileManager");
              Method openURL = fileMgr.getDeclaredMethod("openURL",
                 new Class[] {String.class});
              openURL.invoke(null, new Object[] {url});
              }
           else if (osName.startsWith("Windows"))
              Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
           else { //assume Unix or Linux
              String[] browsers = {
                 "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
              String browser = null;
              for (int count = 0; count < browsers.length && browser == null; count++)
                 if (Runtime.getRuntime().exec(
                       new String[] {"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
              if (browser == null)
                 throw new Exception(browsers.toString());
              else
                 Runtime.getRuntime().exec(new String[] {browser, url});
              }
           }
        catch (Exception e) {
           JOptionPane.showMessageDialog(null, e.getMessage() + ":\n" + e.getLocalizedMessage());
           }
 
     }    
}
