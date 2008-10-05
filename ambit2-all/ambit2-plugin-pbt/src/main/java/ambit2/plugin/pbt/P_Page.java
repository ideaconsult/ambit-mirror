/* P_Page.java
 * Author: Nina Jeliazkova
 * Date: Oct 4, 2008 
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

import javax.swing.JPanel;

import com.jgoodies.forms.layout.FormLayout;

public class P_Page extends JPanel {
    public P_Page() {
        buildPanel();
    }
    void buildPanel() {
        
        FormLayout layout = new FormLayout(
                "50px, 10px, 100px, 5px, 50px, 5px, 150px, 10px, 80px, 120px",
               // "2*(pref, 2px, pref, 9px), pref, 2px, pref");
             //"4*( pref, 3px, pref, 3px, pref, 5px), pref, 2px, pref");
            " 30*(pref), pref, 2px, pref");
        
    }
}
