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

package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ambit.data.molecule.SmartsFragmentsList;
import ambit.ui.AmbitColors;
import ambit.ui.data.AmbitListEditor;

public class DbSmartsQueryPanel extends JPanel {
	
	public DbSmartsQueryPanel(SmartsFragmentsList fragments,ActionMap actions) {
		super(new BorderLayout());
		
		add(new AmbitListEditor(fragments,true),BorderLayout.CENTER);
		
		
		JPanel toolbar = new JPanel();
		toolbar.setMinimumSize(new Dimension(Integer.MAX_VALUE,64));
		setBackground(AmbitColors.BrightClr);
		setForeground(AmbitColors.DarkClr);
        add(new JLabel("Search by functional groups"),BorderLayout.NORTH);

        if (actions != null) {
        	toolbar.setLayout(new GridLayout(1,actions.size()));
            Object[] keys = actions.allKeys();
            if (keys != null) {
    	        for (int i=0; i < keys.length;i++) {
    	        	JButton b = new JButton(actions.get(keys[i]));
    	        	b.setMinimumSize(new Dimension(32,24));
    	        	b.setPreferredSize(new Dimension(48,32));
    	        	toolbar.add(b);
    	        }	
            }
            }

		add(toolbar,BorderLayout.SOUTH);
	}
}
