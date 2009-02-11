package ambit2.ambitxt;


import java.util.prefs.BackingStoreException;

import javax.swing.ImageIcon;

import nplugins.core.PluginClassPath;
import nplugins.shell.NanoPluginsManager;
import nplugins.shell.application.NPluginsApplication;
import ambit2.mopac.MopacShell;
import ambit2.namestructure.Name2StructureProcessor;

public class AmbitXT extends NPluginsApplication {
	public AmbitXT(String title, int width, int height, String[] args) {
		super(title,width,height,args);
		//this is a quick dirty hack to make the builder include mopac and name structure packages ...
		try {
		MopacShell mopac = new MopacShell();
		System.out.println(mopac.getExecutable());
		Name2StructureProcessor p = new Name2StructureProcessor();
		p.process("benzene");
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}
	@Override
	protected NanoPluginsManager createManager() {
	    PluginClassPath path;
	    try {
    	    path = new PluginClassPath();
    	    path.setPref_key("/ambitxt/nplugins");
    	    if (path.size()==0)
    	        path.add("ext");
    	    
    	    return new NanoPluginsManager(true,path.getPref_key());
	    } catch (BackingStoreException x) {
	        return new NanoPluginsManager(false,null);
	    }
	    
	    
	}

	@Override
	protected void addPlugins(NanoPluginsManager manager) {
	    if (manager.size()==0)
		try {
			manager.addPackage("ambit2.plugin.search.SearchPlugin","Simple search",
			        createImageIcon("images/search_16.png"));
			manager.addPackage("ambit2.plugin.analogs.AnalogsFinderPlugin","Find analogs",
			        createImageIcon("images/molecule_16.png"));
			manager.addPackage("ambit2.plugin.pbt.PBTCheckerPlugin","PBT Assessment",
			        createImageIcon("images/pill_16.png"));
			manager.addPackage("ambit2.plugin.dbtools.DBUtilityPlugin","Database utilities",
			        createImageIcon("images/database_16.png"));
			manager.addPackage("ambit2.plugin.usermgr.UserManagerPlugin","Users",
			        createImageIcon("images/user_16.png"));

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
                new AmbitXT("Ambit XT",800,800,args);
            }
        });
   }
	@Override
	public String getTitle() {
	    return "Ambit XT";
	}
	
}
