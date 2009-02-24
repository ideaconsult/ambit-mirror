/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package nplugins.shell;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sun.security.action.GetBooleanAction;

import nplugins.core.Introspection;
import nplugins.core.NPluginsException;
import nplugins.core.PluginPackageEntry;
import nplugins.core.PluginsPackageEntries;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.Task;
import nplugins.shell.application.Utils;

import com.jgoodies.binding.beans.Model;

public class NanoPluginsManager extends Model implements INanoPlugin {
	INPApplicationContext applicationContext;
	/**
     * 
     */
    private static final long serialVersionUID = -8727245160085445951L;
    private static Logger logger = Logger.getLogger(NanoPluginsManager.class.getName());
	protected String[] cmd_args;
	protected PluginsPackageEntries packageEntries = null;
    protected INanoPlugin thePlugin = null;
    public static final String property_plugin = "plugin";
    protected  IPluginsStorage storage;
    
    public IPluginsStorage getStorage() {
		return storage;
	}
	public void setStorage(IPluginsStorage storage) {
		this.storage = storage;
	}
	public NanoPluginsManager() {
       this(true,null);
    }    
    /**
     * 
     * @param load if true, then examines and loads all available plugins
     * @param pref_key  sets {@link Introspection#setPref_key(pref_key)}. 
     */
	public NanoPluginsManager(boolean load,String pref_key) {
		try {
			init(load,pref_key);
		} catch (NPluginsException x) {
         
			logger.severe(x.getMessage());
		}
		storage = new MemStorage();
	}
	public void init(boolean load,String pref_key) throws NPluginsException {
	    if (pref_key != null)
	        Introspection.setPref_key(pref_key);
	    if (load)
		packageEntries = Introspection.getAvailableTypes(getClass().getClassLoader(), "nplugins.shell.INanoPlugin");
	}
	public void clear() {
		if (packageEntries != null)
			packageEntries.clear();
	}

	public JComponent[] createDetailsComponent() {
		PackageEntryPanel pkgPanel =  new PackageEntryPanel();
		addPropertyChangeListener("PluginPackageEntry",pkgPanel);
		return new JComponent[] {pkgPanel};
	}

	public PluginMainPanel createMainComponent() {
		return new PluginsManagerPanel(this);
	}

	public JComponent[] createOptionsComponent() {
		JPanel welcome = new JPanel(new BorderLayout()) {
			@Override
			public String toString() {
				return "About";
			}
		};
		JTextArea text = new JTextArea(getHelp());
		text.setBorder(null);
		text.setBackground(welcome.getBackground());
		text.setEditable(false);
		
		welcome.add(new JLabel(getLogo()),BorderLayout.NORTH);
		welcome.add(new JScrollPane(text),BorderLayout.CENTER);
		return new JComponent[] {welcome};
	
	}
	protected PluginPackageEntry selectPackageEntry(PluginPackageEntry entry) {
		firePropertyChange("PluginPackageEntry",null,entry);
		return entry;
	}
	public ActionMap getActions() {
        String group = "toolbar";
        ActionMap map = new ActionMap();
        for (int i=0; i < size(); i++) 
            try {
                Task<INanoPlugin,Void> task = new SetPluginTask(this,getPackage(i));
                NPluginsAction<INanoPlugin,Void> action =  new NPluginsAction<INanoPlugin,Void>(task,
                        getPackage(i).getTitle(),
                        getPackage(i).getIcon(),
                        group);
                action.setActions(map);
                map.put(Integer.toString(i),action);

            } catch (Exception x) {
                logger.severe(x.getMessage());
            }
            
        Task<INanoPlugin,Void> task = new SetPluginTask(this,null);
        NPluginsAction<INanoPlugin,Void> action =  new NPluginsAction<INanoPlugin,Void>(task,"Manager",getIcon(),group);
        action.setActions(map);
        map.put("Manager",action);
        
        return map;    
	}

	public int getOrder() {
		return 0;
	}

	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParameters(String[] args) {
		this.cmd_args = args;

	}

	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public int compareTo(INanoPlugin arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("Available plugins " + size());
        /*
        if (packageEntries != null)
            for (int i=0; i < packageEntries.size();i++) {
                b.append(packageEntries.get(i).toString());
                b.append('\n');
            }
            */
        return b.toString();
	}
	public int size() {
		if (packageEntries == null) return 0;
		return packageEntries.size();
	}
	public PluginPackageEntry getPackage(int index) throws Exception {
		return packageEntries.get(index);
	}
    public PluginPackageEntry addPackage(String className,String defaultTitle, ImageIcon defaultIcon) throws Exception {
        return addPackage(className,new String[] {},defaultTitle,defaultIcon);
    }	
    public PluginPackageEntry addPackage(String className) throws Exception {
        return addPackage(className,new String[] {},null,null);
    }
	public PluginPackageEntry addPackage(String className,String[] args,String defaultTitle, ImageIcon defaultIcon) throws Exception {
		PluginPackageEntry p =  new PluginPackageEntry(className,"",null);
		p.setParameters(args);
		p.setDefaultIcon(defaultIcon);
		p.setDefaultTitle(defaultTitle);
		if (packageEntries == null) packageEntries = new PluginsPackageEntries();
		packageEntries.add(p);
		return p;
	}	
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	public ImageIcon getIcon() {
		return Utils.createImageIcon("nplugins/shell/resources/plugin_edit.png");
	}
	public ImageIcon getLogo() {
		return getIcon();
	}	
	public String getHelp() {
		return "Welcome";
	}
    public synchronized INanoPlugin getThePlugin() {
        return thePlugin;
    }
    public synchronized void setThePlugin(INanoPlugin thePlugin) {
        if (this.thePlugin == thePlugin) return;
        firePropertyChange(property_plugin,this.thePlugin,thePlugin);
        
        if (this.thePlugin != null)
        	storage.savePlugin(this.thePlugin);
        this.thePlugin = thePlugin;
        
        logger.info("Set plugin " + thePlugin);
    }
   
    public synchronized String[] getCmd_args() {
        return cmd_args;
    }
    public synchronized void setCmd_args(String[] cmd_args) {
        this.cmd_args = cmd_args;
    }
	public INPApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(INPApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
}
