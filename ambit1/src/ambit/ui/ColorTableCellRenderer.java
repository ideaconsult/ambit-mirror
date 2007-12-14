/*
Copyright (C) 2005-2006  

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

package ambit.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer {
	protected Color evenColor = new Color(240, 240, 240);
	protected Color oddColor = AmbitColors.BrightClr;//new Color(250, 250, 250);

	public ColorTableCellRenderer() {
		super();
	}
	public ColorTableCellRenderer(Color evenColor, Color oddColor) {
		super();
		this.evenColor = evenColor;
		this.oddColor = oddColor;
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		if (isSelected)
			c.setBackground(table.getSelectionBackground());
		else if ((row % 2) == 0) {
			c.setBackground(evenColor);
		} else
			c.setBackground(oddColor);
		
		
        String t = value.toString();

        setToolTipText(t);
		return c;
	}
}

