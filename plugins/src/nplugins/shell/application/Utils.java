package nplugins.shell.application;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import nplugins.core.Introspection;

public class Utils {
	protected static Logger logger = Logger.getLogger("nplugins.shell.application.Utils");
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Introspection.getLoader().getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            logger.warning("Couldn't find file: " + path);
            return null;
        }
    }	
}
