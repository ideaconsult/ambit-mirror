/* SetOfAtomContainersPanel.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit2.ui.domain;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 10, 2006
 */
public class QSARDatasetPanel extends JScrollPane {
    protected JTable table = null;
    
    /**
     * 
     */
    public QSARDatasetPanel() {
        super(addWidgets(new QSARDatasetTableModel()));
        table = (JTable)getViewport().getComponent(0);
    }
    public QSARDatasetPanel(TableModel model) {
        super(addWidgets(model));
        table = (JTable)getViewport().getComponent(0);
    }
    protected static TableColumnModel createColumnModel(TableModel model) {
        TableColumnModel cModel = new DefaultTableColumnModel();
        final int columnSize[] = {32,64,64,48,48};
        for (int i=0; i < model.getColumnCount(); i++) {
	        TableColumn column = new TableColumn(i);
	        column.setHeaderValue(model.getColumnName(i));
	        if (i<5)
	            column.setPreferredWidth(columnSize[i]);
	        cModel.addColumn(column);
        }
        return cModel;
    }
    protected static JTable addWidgets(TableModel model) {

        JTable table = new JTable(model);
        
        //table.setTableHeader(null);
        
        /*
        table.setRowHeight(cellSize.height + 24);
        */
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        
        /*
        table.setDefaultRenderer(Compound.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(AmbitPoint.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(Image.class, new ImageCellRenderer());
        
        table.setDefaultRenderer(SourceDataset.class, new SourceDatasetEditor());
        table.setDefaultRenderer(LiteratureEntry.class, new LiteratureEntryEditor());
        table.setDefaultRenderer(JournalEntry.class, new JournalEntryEditor());
        //table.setDefaultRenderer(AuthorEntries.class, new AuthorEntriesEditor());
        
        table.setDefaultEditor(AmbitObject.class,new AmbitCellEditor());
        
        table.setPreferredScrollableViewportSize(new Dimension(cellSize.width*3, (cellSize.height+30)*2));
        */
        return table;
    }
    public void addListSelectionListener(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }
}
