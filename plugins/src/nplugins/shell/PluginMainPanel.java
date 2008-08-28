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
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * The component that appears at right in each tab.
 * @author Nina Jeliazkova
 *
 */
public abstract class PluginMainPanel<P extends INanoPlugin> extends JPanel implements INPluginUI<P> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1891938749518929463L;
	protected P plugin = null;
	protected static Logger logger = Logger.getLogger("nplugins.shell.PluginMainPanel");
	

	public PluginMainPanel(P plugin, LayoutManager layout) {
		super(layout);
		this.plugin = plugin;
		addWidgets();
	}


	public PluginMainPanel(P model) {
		this(model,new BorderLayout());
	}
	protected abstract void addWidgets();
		

	public P getPlugin() {
		return plugin;
	}


	public void setPlugin(P plugin) {
		this.plugin = plugin;
	}

	public Component getComponent() {
		return this;
	}

}


