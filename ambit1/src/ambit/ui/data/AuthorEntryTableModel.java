package ambit.ui.data;

import ambit.data.literature.AuthorEntry;

public class AuthorEntryTableModel extends AbstractPropertyTableModel {

	   public AuthorEntryTableModel(AuthorEntry author) {
	        super(author);
	    }
	    /* (non-Javadoc)
	     * @see javax.swing.table.TableModel#getRowCount()
	     */
	    public int getRowCount() {
	        return 1;
	    }
	    /* (non-Javadoc)
	     * @see javax.swing.table.TableModel#getValueAt(int, int)
	     */
	    public Object getValueAt(int rowIndex, int columnIndex) {
	        if (columnIndex == 0) return "Name";
	        else return ((AuthorEntry) object).getName();
	    }
	    /* (non-Javadoc)
	     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	     */
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	        if (columnIndex ==1)
	        ((AuthorEntry ) object).setName(aValue.toString());
	    }
	    /* (non-Javadoc)
	     * @see ambit.ui.data.AbstractPropertyTableModel#isCellEditable(int, int)
	     */
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
	        return columnIndex ==1;
	    }
}
