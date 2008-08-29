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

package ambit2.workflow.test;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.Test;

import ambit2.db.LoginInfo;
import ambit2.dbui.LoginPanel;
import ambit2.ui.WizardPanel;


public class LoginInfoPanelTest {

	@Test public void testPanel() throws Exception {
		LoginInfo info = new LoginInfo();
		LoginPanel panel = new LoginPanel();
		panel.setObject(info);
		WizardPanel p = new WizardPanel("x",panel,"help");
		JOptionPane.showConfirmDialog(null,p);
		System.out.println(info.getPassword());
	}
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //"com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }       
        WizardPanel wz = new WizardPanel("Log in",new LoginPanel(),null);
        wz.display(null,"Wizard",true);
    }	
}
