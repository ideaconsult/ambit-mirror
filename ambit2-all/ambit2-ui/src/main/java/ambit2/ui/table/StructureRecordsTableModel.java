package ambit2.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;

public class StructureRecordsTableModel extends AbstractTableModel {
	protected List<IStructureRecord> records = null;
	protected List<Boolean> selected = null;
	public List<IStructureRecord> getRecords() {
		return records;
	}

	public void setRecords(List<IStructureRecord> records) {
		this.records = records;
		if (selected==null) selected = new ArrayList<Boolean>(); 
		fireTableStructureChanged();
	}

	protected MoleculeReader  transform = new MoleculeReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = -635680060021212861L;

	public int getColumnCount() {
		if (records == null) return 0;
		else return 2;
	}

	public int getRowCount() {
		if (records == null) return 0;
		else return records.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (records == null) return null;
		switch (columnIndex) {
		case 0: {
			if ((selected!=null) && (rowIndex<selected.size())) return selected.get(rowIndex);
			else return false;
		}
		case 1: try {
			return transform.process(records.get(rowIndex));
		} catch (Exception x) {
			return null;
		}
		default: return "";
		}
	}
	@Override
	public String getColumnName(int column) {
		return "Structure";
	}

}
