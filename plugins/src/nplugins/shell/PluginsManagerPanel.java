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

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PluginsManagerPanel extends PluginMainPanel<NanoPluginsManager> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4578254053120352350L;

	public PluginsManagerPanel(NanoPluginsManager model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addWidgets() {
		add(new JScrollPane(new JTextArea(getPlugin().toString())));
		//JButton actionButton = new JButton(actionMap().get("blockAction"));

	}

    /* Progress is interdeterminate for the first 150ms, then

    private class LoadPluginTask extends Task<INanoPlugin, PluginsPackageEntries> {
    	LoadPluginTask() {
            super(Application.getInstance());
            setUserCanCancel(true);
        }
	@Override protected INanoPlugin doInBackground() throws InterruptedException {
	    for(int i = 0; i < 50; i++) {
		setMessage("Working... [" + i + "]");
		Thread.sleep(150L);
		setProgress(i, 0, 49);
	    }
	    Thread.sleep(150L);
            return null;
	}
	
	@Override protected void succeeded(INanoPlugin ignored) {
	    setMessage("Done");
	}
	@Override protected void cancelled() {
	    setMessage("Canceled");
	}

    
    }
     */	
}
