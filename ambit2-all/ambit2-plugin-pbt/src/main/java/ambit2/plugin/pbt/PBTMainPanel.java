/* PBTMainPanel.java
 * Author: Nina Jeliazkova
 * Date: Oct 5, 2008 
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

package ambit2.plugin.pbt;

import java.awt.Component;
import java.beans.PropertyChangeEvent;

import javax.swing.JTabbedPane;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import com.microworkflow.ui.WorkflowContextListenerPanel;

public class PBTMainPanel extends WorkflowContextListenerPanel implements INPluginUI<INanoPlugin>  {
    protected JTabbedPane tabbedPane;
    
    public PBTMainPanel() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
        tabbedPane.add(new PBTPage("ambit2/plugin/pbt/p_page.xml"));
        tabbedPane.add(new PBTPage("ambit2/plugin/pbt/b_page.xml"));
        tabbedPane.add(new PBTPage("ambit2/plugin/pbt/t_page.xml"));
    }
    @Override
    protected void animate(PropertyChangeEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    public Component getComponent() {
        return this;
    }

    public INanoPlugin getPlugin() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPlugin(INanoPlugin plugin) {
        // TODO Auto-generated method stub
        
    }

}
