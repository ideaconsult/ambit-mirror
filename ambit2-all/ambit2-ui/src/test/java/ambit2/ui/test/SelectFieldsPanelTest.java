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

package ambit2.ui.test;

import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.BeforeClass;
import org.junit.Test;

import ambit2.core.io.Property;
import ambit2.ui.SelectFieldsPanel;



public class SelectFieldsPanelTest {
    @BeforeClass public static void setUp() {

    }

    @Test public void test() throws Exception {
    	Hashtable<String,Property> props = new Hashtable<String,Property>();
    	props.put("CAS",new Property("CAS"));
    	props.put("CAS",new Property("CAS","CASNO"));
    	props.put("Name",new Property("Name"));
    	Property p = new Property("Inchi");
    	p.setEnabled(true);
    	props.put("Inchi",p);
    	props.put("XLogP",new Property("XLogP"));
    	props.put("pKA",new Property("pKA"));
    	props.put("eHOMO",new Property("eHOMO"));
    	props.put("eLUMO",new Property("eLUMO"));
    	props.put("A",new Property("A"));
    	props.put("B",new Property("B"));
    	props.put("V",new Property("V"));
    	props.put("D",new Property("D"));
    	props.put("D",new Property("X"));
    	props.put("Y",new Property("Y"));
    	props.put("Z",new Property("Z"));
    	String help = "All available fields that can be exported are listed in the left pane. Select those fields you want to be exported and click \">\" button to move them to right pane."+
    		"You can move exported columns up and down to change the order they will be saved in .csv file. You can also specify Target Column Name if you wish to set some other caption than default.";
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());     
    	SelectFieldsPanel panel = new SelectFieldsPanel(props,help);
    	panel.setBorder(BorderFactory.createEtchedBorder());
    	
    	JOptionPane.showMessageDialog(null,panel,panel.getClass().getName(),JOptionPane.PLAIN_MESSAGE);
    }
}
