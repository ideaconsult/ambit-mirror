/**
 * Created on 2005-3-24
 *
 */
package ambit.ui.data;

import javax.swing.table.AbstractTableModel;

import ambit.data.AmbitList;

/**
 * A table model for (@link ambit.data.AmbitList) 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitListTableModel extends AbstractTableModel {
	protected AmbitList list = null;
	protected String[] columns = {"#","Name"};
	
	/**
	 * 
	 * @param list
	 */
	public AmbitListTableModel(AmbitList list) {
		super();
		this.list = list;
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columns.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		switch (columnIndex) {
		case (0): {return Integer.toString(rowIndex+1);}
		case (1): {return Integer.toString(list.getRowID(rowIndex));}	
		case (2): {
		    Object o = list.getItem(rowIndex); 
			if (o != null) return o; else return "NA";}
		default: return "";
		}
		
	}

	public String getColumnName(int col) {
        return columns[col].toString();
    }


    public Class getColumnClass(int column) {
        return getValueAt(0,column).getClass();
      }	
}
