/* SetPluginTask.java
 * Author: Nina Jeliazkova
 * Date: Jul 3, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package nplugins.shell;

import java.util.logging.Level;
import java.util.logging.Logger;

import nplugins.core.NPluginsException;
import nplugins.core.PluginPackageEntry;
import nplugins.shell.application.Task;
import nplugins.shell.application.TaskMonitor;



public class SetPluginTask implements Task<INanoPlugin,Void> {
    protected NanoPluginsManager manager;
    protected PluginPackageEntry plugin;
    protected static Logger logger = Logger.getLogger("nplugins.shell.SetPluginTask");
    public SetPluginTask(NanoPluginsManager manager, PluginPackageEntry plugin) {
        this.manager = manager;
        this.plugin = plugin;
        logger.setLevel(Level.FINEST);
        
    }
    public void cancel() {
        // TODO Auto-generated method stub
        
    }

    public void done() {
        // TODO Auto-generated method stub
        
    }

    public INanoPlugin execute(TaskMonitor monitor) throws NPluginsException {
        try {
            if (plugin == null) {
                logger.warning("Plugin not assigned, loading "+manager);
                INanoPlugin thePlugin = manager;
                manager.setThePlugin((INanoPlugin)thePlugin);
                return (INanoPlugin)thePlugin;                
            } else {
                logger.fine("Creating object from "+plugin);
                Object thePlugin  = manager.getStorage().restorePlugin(plugin);
                if (thePlugin == null)
                	thePlugin = plugin.createObject();
                if (thePlugin instanceof INanoPlugin) {
                	((INanoPlugin)thePlugin).setApplicationContext(manager.getApplicationContext());
                    ((INanoPlugin)thePlugin).setParameters(plugin.getParameters());
                    manager.setThePlugin((INanoPlugin)thePlugin);
                    return (INanoPlugin)thePlugin;
                } else {
                    logger.warning("Not of the expected type");
                }
                return null;
            }
        } catch (Exception x) {
        	x.printStackTrace();
            logger.severe(x.getMessage());
            throw new NPluginsException(x);
        }
    }

    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return false;
    }

 

}
