/* BorderCellRenderer.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-10 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.ui.query;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import ambit2.ui.AmbitColors;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-10
 */
public class BorderCellRenderer extends DefaultTableCellRenderer {
    Border border = BorderFactory.createEtchedBorder(); 
    Border selectedborder = BorderFactory.createLineBorder(AmbitColors.DarkClr);

    /**
     * 
     */
    public BorderCellRenderer() {
        super();
    }
    public Component getTableCellRendererComponent(JTable arg0, Object arg1,
            boolean arg2, boolean isSelected, int arg4, int arg5) {
        Component c = super.getTableCellRendererComponent(arg0, arg1, arg2, isSelected,
                arg4, arg5);
        if (!isSelected) setBorder(border); else setBorder(selectedborder);
        return c;
    }

}
