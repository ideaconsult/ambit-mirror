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

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Interface to be implemented by each plugin window that is to be loaded as a plugin. The class implementing this
 * interface must have default constructor without parameters.

 * The plug-in loader looks in a specified folder and for each {@link INanoPlugin}
 * creates {@link PluginMainPanel}. Plugins are sorted according to {@link INanoPlugin#getOrder()}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> July 3, 2008
 */
public interface INanoPlugin extends PropertyChangeListener, Comparable<INanoPlugin>, Serializable {
	
	INPApplicationContext getApplicationContext();
	
	void setApplicationContext(INPApplicationContext context);
	
		/*
	 * Creates the window at the right
	 * @return
	 */    
    INPluginUI<INanoPlugin> createMainComponent();

	/**
	 * If not null will be displayed at the left pane, just below the actions.
	 * @param parentComponent
	 * @return
	 */    
    JComponent[] createDetailsComponent();
    
    /**
     * If not null will be displayed in a tabbed window by {@link SetOptionsAction}, each JComponent in a different tab.
     * The idea is to provide UI for different options.
     * @param parentComponent
     * @return
     */
	JComponent[] createOptionsComponent();
    /**
     * @return
     */
    public ActionMap getActions();
    
    /**
     * Clears everything
     *
     */
    public void clear();
    /**
     * Sets command line parameters.
     * @param args
     */
    public void setParameters(String[] args);
    
    /**
     * Used when sorting plugins. The order reflects the order in which the tabs will appear.
     * Tabs with smaller order come first.
     * @return
     */
    public int getOrder();
    
    public ResourceBundle getResourceBundle();
    
    public ImageIcon getIcon();
    
    boolean isModified();
    void setModified(boolean modified);
    boolean canClose();
    
    void close();
}