package org.opentox.fastox.ambit;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nplugins.shell.NanoPluginsManager;
import nplugins.shell.application.NPluginsApplication;
import nplugins.shell.application.Utils;

/**
 * Fastox
 * @author nina
 *
 */
public class FastoxApplication extends NPluginsApplication {

	public FastoxApplication(String title, int width, int height, String[] args) {
		super(title, width, height, args);
		
		JTextField searchField = new JTextField("Search for structure");
		searchField.setPreferredSize(new Dimension(200,18));
		JToolBar bar = new JToolBar();
		bar.add(searchField);
		bar.add(new JButton("Search"));
		leftPanel.setToolBar(bar);
		
		JTextField searchModelsField = new JTextField("Search for models");
		JToolBar bar1 = new JToolBar();
		bar1.add(searchModelsField);
		mainPanel.setToolBar(bar1);	
		
	}

	protected void addPlugins(NanoPluginsManager manager) {
	    if (manager.size()==0)
			try {
				manager.addPackage("org.opentox.fastox.ambit.plugins.FastoxPlugin","Fastox",
				        createImageIcon("images/search_16.png"));
			} catch (Exception x) {
	            x.printStackTrace();
				logger.severe(x.getMessage());
			}		
		
	}

    public ImageIcon createImageIcon(String path) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getSystemResource(path);
    
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
    
	public static void main(final String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                new FastoxApplication("Fastox",800,800,args);
            }
        });
   }
	@Override
	public String getTitle() {
	    return "Fastox";
	}
	
    public ImageIcon createImageIcon() {
    	return Utils.createImageIcon("images/ambit-32x32.png");
    }		
        
}
