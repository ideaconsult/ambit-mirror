package ambit2.ui.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ambit2.base.data.Profile;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.base.data.TypedListModel;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;

public class StructureRecordsTableModel extends AbstractTableModel {
	protected List<IStructureRecord> records = null;
	protected Hashtable<Integer,Boolean> selected = null;
	protected TypedListModel<Property> fields;
	protected Hashtable<String,Profile<Property>> properties ;
	protected PropertyChangeListener propertyListener;
	protected final int offset = 3;
	public StructureRecordsTableModel() {
		properties = new Hashtable<String,Profile<Property>>();		
		propertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fields = new ProfileListModel(properties,true);				
				fireTableStructureChanged();
			}
		};
	}
	public List<IStructureRecord> getRecords() {
		return records;
	}

	public void setRecords(List<IStructureRecord> records) {
		this.records = records;
		if (selected==null) selected = new Hashtable<Integer,Boolean>(); else selected.clear();
		fireTableStructureChanged();
	}
    public void setProfile(String key,Profile<Property> profile) {
    	Profile<Property> oldProfile = properties.get(key);
    	if (oldProfile != null) {
    		oldProfile.removePropertyChangeListener(Profile.profile_property_change,propertyListener);
    	}
    	if (profile != null) {
	    	properties.put(key, profile);
	    	profile.addPropertyChangeListener(Profile.profile_property_change,propertyListener);
    	}
    	fields = new ProfileListModel(properties,true);
    	fireTableStructureChanged();
    }
	protected MoleculeReader  transform = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = -635680060021212861L;

	public int getColumnCount() {
		if (records == null) return 0;
		return ((fields==null)?offset:(offset+fields.getSize()));
	}

	public int getRowCount() {
		if (records == null) return 0;
		else return records.size();
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex > 0) return;
		if (selected == null) selected = new Hashtable<Integer,Boolean>();
		selected.put(rowIndex,(Boolean) value);
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (records == null) return null;
		switch (columnIndex) {
		case 0: {
			if (selected!=null) {
				Boolean b = selected.get(rowIndex);
				if (b==null) return false; else return b;
			}
			else return false;
		}
		case 1: try {
			return transform.process(records.get(rowIndex));
		} catch (Exception x) {
			return null;
		}
		case 2: return "";
		default: {

			IStructureRecord record = records.get(rowIndex);
			Property p = (Property)fields.getElementAt(columnIndex-offset);
			Object o = record.getProperty(p);
			return ((fields==null)||(o==null))?"":o;
		}
		}

	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex==0;
	}

    @Override
    public String getColumnName(int column) {
		switch (column) {
		case 0: return "Select";
		case 1: return "Structure";
		case 2: return "";
		default: {
			return (fields==null)?Integer.toString(column):
				fields.getElementAt(column-offset).getName();
		}
		}
    }
}
