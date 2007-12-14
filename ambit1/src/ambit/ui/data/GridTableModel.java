/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import javax.swing.table.AbstractTableModel;

import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObjectChanged;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class GridTableModel extends AbstractTableModel implements ICompoundsListTableModel, IAmbitListListener {
    protected int columns = 1;
    protected AmbitList list = null;
    public GridTableModel(AmbitList list, int columns) {
        super();
        this.columns = columns;
        this.list = list;
        if (list != null)
        	this.list.addAmbitObjectListener(this);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
    	if (list == null) return 0;
        return (int)Math.ceil(((double)list.size()) / columns);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
    	if (list == null) return "";
        int index = rowIndex * columns +  columnIndex;
        if (index < list.size())
            return list.getItem(index);
        else  return null;
    }
    public Class getColumnClass(int column) {
        Object o = getValueAt(0,column);
        if (o != null) return o.getClass(); else return String.class;
      }	
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return Integer.toString(column);
    }
    public synchronized AmbitList getList() {
        return list;
    }
    public synchronized void setList(AmbitList list) {
    	if (this.list != null) this.list.removeAmbitObjectListener(this);
        this.list = list;
        if (list != null)
        	this.list.addAmbitObjectListener(this);
        fireTableStructureChanged();
    }
    public void ambitListChanged(AmbitListChanged event) {
    	fireTableStructureChanged();
    	//fireTableDataChanged();
    	
    }
    public void ambitObjectChanged(AmbitObjectChanged event) {
    	fireTableDataChanged();
    	
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return true;
    }
}
