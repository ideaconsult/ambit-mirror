/**
 * Created on 2005-3-30
 *
 */
package ambit2.ui.data;

import javax.swing.table.AbstractTableModel;

import ambit2.data.molecule.AmbitPoint;


/**
 * A table model for the {@link ambit2.data.molecule.AmbitPoint} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitPointTableModel extends AbstractTableModel {
	AmbitPoint object;
	
	/**
	 * 
	 * @param p
	 */
	public AmbitPointTableModel(AmbitPoint p) {
		super();
		object = p;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		int i = 2;
		if ((object != null) && (object.getXvalues() != null))
			i += object.getXvalues().length;
		return i ;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (object == null) return "";
		switch (rowIndex) {
		case 0: {
			if (columnIndex == 0) return "Y predicted";
			else return object.getYPredictedString();
		}
		case 1: {
			if (columnIndex == 0) return "Y observed";
			else return object.getYObservedString();
		}		
		default: {
			if (columnIndex == 0) return object.getDescriptorName(rowIndex-2);
			else return object.getXValue(rowIndex-2);
		}
		}
	}
	/**
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (object == null) return;		
		switch (rowIndex) {
		case 0: {
			object.setYPredicted(aValue.toString()); return;
		}
		case 1: {
			object.setYObserved(aValue.toString()); return;
		}
		default : {
			object.setXValue(rowIndex-2,aValue.toString());
			return;
		}
		}
	}
	public String getColumnName(int col) {
        switch (col) {
        case 0: return "Name";
        case 1: return "Value";
        default : return "";
        }
    }
	
	/**
	 * 
	 * @return {@link AmbitPoint}
	 */
	public AmbitPoint getObject() {
		return object;
	}
	public void setObject(AmbitPoint object) {
		this.object = object;
	}
	/**
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (object.isEditable() && (columnIndex == 1));
	}
}
