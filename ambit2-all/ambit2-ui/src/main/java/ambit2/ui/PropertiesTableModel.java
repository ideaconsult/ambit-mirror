package ambit2.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ambit2.core.data.Profile;
import ambit2.core.io.Property;

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
	public PropertiesTableModel(Profile fields,boolean visibility, int columns) {
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
	public void setFields(Hashtable<String,Property> fields) {
		visibleFields.clear();
		if (fields != null) {
			Iterator<Property> i = fields.values().iterator();
			while (i.hasNext()) {
				Property p = i.next();
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
