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

package ambit2.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import ambit2.ui.AbstractAmbitAction;
import ambit2.ui.AbstractPanel;
import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BrowserModeNavigator extends AbstractPanel<IBrowserMode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8336772938262715397L;
	protected JToggleButton[] button;
	protected JButton zoomIn;
	protected JButton zoomOut;
	
	public BrowserModeNavigator(IBrowserMode mode) {
		super(mode);
	}
	public JComponent buildPanel(final IBrowserMode browserMode) {
		
		StringBuffer blayout = new StringBuffer();
		BrowserMode[] values = IBrowserMode.BrowserMode.values();
		button = new JToggleButton[values.length];
		
		ButtonGroup group = new ButtonGroup();
		boolean first = true;
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browserMode.setBrowserMode(BrowserMode.valueOf(e.getActionCommand()));
				
				
			}
		};
		for (int i=0; i < values.length;i++) {
			BrowserMode mode = values[i];
			ImageIcon icon = mode.getIcon(false);
			String title = mode.getTitle();
			if (icon != null) title = "";
			button[i] = new JToggleButton(title,icon);
			//button[i].setBorder();
			button[i].addActionListener(listener);
			button[i].setActionCommand(mode.toString());
	        button[i].setToolTipText(mode.getTooltip());
	        button[i].setFocusable(false);
	        button[i].setSelectedIcon(mode.getIcon(true));
	        button[i].setFocusable(false);
	        group.add(button[i]);
	        button[i].setSelected(mode.equals(browserMode.getBrowserMode()));
	        if (!first) 
	        	blayout.append(",1dlu,");
	        
	        blayout.append("pref");
	        first = false;
	        
		}
		blayout.append(",8dlu,pref,1dlu,pref");
		zoomIn = new JButton(new AbstractAmbitAction("Zoom in","images/zoom_in.png","Increase cell size") {
			/**
		     * 
		     */
		    private static final long serialVersionUID = -7230604413152102914L;

			public void actionPerformed(ActionEvent e) {
				//browserMode.zoom(browserMode.getBrowserMode().getCellSize(0,0).getHeight()*1.1);
				browserMode.zoom(1.1,1.1);
			}
		});
		zoomIn.setFocusable(false);
		zoomOut = new JButton(new AbstractAmbitAction("Zoom out","images/zoom_out.png","Decrease cell size") {
			
			/**
		     * 
		     */
		    private static final long serialVersionUID = 332852452089868875L;

			public void actionPerformed(ActionEvent e) {
				//browserMode.zoom(browserMode.getBrowserMode().getCellSize(0,0).getHeight()*0.9);
				browserMode.zoom(0.9,0.9);
			}
		});
		zoomOut.setFocusable(false);
		FormLayout layout = new FormLayout(
	            blayout.toString(),
				"pref");		
        PanelBuilder panel = new PanelBuilder(layout);
        panel.setBorder(null);
        CellConstraints cc = new CellConstraints();        
        for (int i=0; i < values.length;i++) {
        	panel.add(button[i], cc.xy(i*2+1,1));

        }
        //panel.add(new JToolBar.Separator(),cc.xy((values.length)*2+1,1));
        panel.add(zoomIn,cc.xy((values.length)*2+1,1));
        panel.add(zoomOut,cc.xy((values.length)*2+3,1));
        return panel.getPanel();
	}	
}
