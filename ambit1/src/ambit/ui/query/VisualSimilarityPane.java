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

package ambit.ui.query;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;



public abstract class VisualSimilarityPane extends JPanel {
	protected int cellSize = 8;
	protected JScrollPane scrollPane;
	protected JTable table;
	public VisualSimilarityPane(Object similarityData) {
		super();
		addWidgets(similarityData);
	}
	public void addWidgets(Object similarityData) {
        table = new JTable(createTableModel(similarityData));
        table.setColumnModel(new SimilarityTableColumnModel(table));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        table.setRowHeight(cellSize);
        table.setShowGrid(true);
        table.setGridColor(Color.white);
        table.setColumnModel(new SimilarityTableColumnModel(table));
        table.setSurrendersFocusOnKeystroke(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        scrollPane = new JScrollPane(table);
        add(scrollPane);
	}
	protected abstract SimilarityTableModel createTableModel(Object data);

}



class SimilarityTableColumnModel extends DefaultTableColumnModel {
    public SimilarityTableColumnModel(JTable table) {
        super();
        TableCellRenderer r = new DefaultTableCellRenderer() {
            Border b = BorderFactory.createMatteBorder(1,1,0,0,Color.white);
            Border sb = BorderFactory.createMatteBorder(2,2,2,2,Color.blue);
            /* (non-Javadoc)
             * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
             */
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                // TODO Auto-generated method stub
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setBackground(new Color((int) (row%255) ,(int) (row*column%255),(int) (column%255)));
                if (isSelected)
                    setBorder(sb);
                else
                    setBorder(b);
                return c;
            }
        };
        for (int i=0; i < table.getColumnCount();i++) {
            TableColumn c = new TableColumn(i);
            c.setMinWidth(4);
            c.setMaxWidth(4);
            c.setHeaderValue(new Integer(i));
            c.setPreferredWidth(4);
            c.setCellRenderer(r);
            addColumn(c);
        }
    }
}