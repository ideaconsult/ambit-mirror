package ambit2.base.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;


/**
 * Table model for {@link Profile}
 * @author nina
 *
 */
public class PropertiesTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4031712164231916058L;
	protected List<Property> visibleFields;
	protected boolean visibility=true;
	protected int columns = 1;
	public PropertiesTableModel(TypedListModel<Property> fields,boolean visibility, int columns) {
		visibleFields = new ArrayList<Property>();
		this.visibility = visibility;
		this.columns = columns;
		setFields(fields);
	}
	public int getColumnCount() {
		return columns;
	}
	public int getRowCount() {
		return visibleFields.size();
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0: return visibleFields.get(rowIndex);
		case 1: return visibleFields.get(rowIndex).getLabel();
		default : return "";
		}		
		
	}
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: return "Field name";
		case 1: return "Field label";
		default : return "";
		}
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 1: {
			visibleFields.get(rowIndex).setLabel(aValue.toString());
			break;
		}
		default :
		}
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex>0;
	}
	public void setFields(TypedListModel<Property> fields) {
		visibleFields.clear();
		if (fields != null) {
			for (int i=0; i < fields.getSize();i++) {
				Property p = fields.getElementAt(i);
				if (!(visibility ^ p.isEnabled())) {
					visibleFields.add(p);
				}
			}	
			Collections.sort(visibleFields,new Comparator<Property>() {
				public int compare(Property o1, Property o2) {
					return o1.getOrder() - o2.getOrder();
				}
			});
		}
		fireTableStructureChanged();
	}
}
