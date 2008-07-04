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

import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import nplugins.core.Introspection;
import nplugins.core.NPluginsException;
import nplugins.core.PluginPackageEntry;
import nplugins.core.PluginsPackageEntries;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.Task;
import nplugins.shell.application.Utils;

import com.jgoodies.binding.beans.Model;

public class NanoPluginsManager extends Model implements INanoPlugin {
	/**
     * 
     */
    private static final long serialVersionUID = -8727245160085445951L;
    private static Logger logger = Logger.getLogger(NanoPluginsManager.class.getName());
	protected String[] cmd_args;
	protected PluginsPackageEntries packageEntries = null;
    protected INanoPlugin thePlugin = null;
    public static final String property_plugin = "plugin";
    
	public NanoPluginsManager() {
		try {
			init();
		} catch (NPluginsException x) {
         
			logger.severe(x.getMessage());
		}
	}
	public void init() throws NPluginsException {
		packageEntries = Introspection.getAvailableTypes(getClass().getClassLoader(), "nplugins.shell.INanoPlugin");
	}
	public void clear() {
		if (packageEntries != null)
			packageEntries.clear();
	}

	public JComponent[] createDetailsComponent() {
		return null;
	}

	public PluginMainPanel createMainComponent() {
		return new PluginsManagerPanel(this);
	}

	public JComponent[] createOptionsComponent() {

		return new JComponent[] {new JLabel("test1"),new JLabel("test2")};
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
        if (packageEntries != null)
            for (int i=0; i < packageEntries.size();i++) {
                b.append(packageEntries.get(i).toString());
                b.append('\n');
            }
        return b.toString();
	}
	public int size() {
		if (packageEntries == null) return 0;
		return packageEntries.size();
	}
	public PluginPackageEntry getPackage(int index) throws Exception {
		return packageEntries.get(index);
	}
    public PluginPackageEntry addPackage(String className) throws Exception {
        return addPackage(className,new String[] {});
    }
	public PluginPackageEntry addPackage(String className,String[] args) throws Exception {
		PluginPackageEntry p =  new PluginPackageEntry(className,"",null);
		p.setParameters(args);
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
    public synchronized INanoPlugin getThePlugin() {
        return thePlugin;
    }
    public synchronized void setThePlugin(INanoPlugin thePlugin) {
        if (this.thePlugin == thePlugin) return;
        firePropertyChange(property_plugin,this.thePlugin,thePlugin);
        
        if (this.thePlugin != null)
            this.thePlugin.clear();
        this.thePlugin = thePlugin;        
        logger.info("Set plugin " + thePlugin);
    }
    public synchronized String[] getCmd_args() {
        return cmd_args;
    }
    public synchronized void setCmd_args(String[] cmd_args) {
        this.cmd_args = cmd_args;
    }
	
}
