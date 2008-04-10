/*
Copyright (C) 2005-2008  

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

package ambit2.workflow;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ambit2.repository.StructureRecord;

public class StructureRecordsTableModel extends AbstractTableModel {
	protected List<StructureRecord> records;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5428865165072188779L;

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (records != null)
			return records.size();
		else 
			return 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0: return records.get(rowIndex).getIdchemical();
		case 1: return records.get(rowIndex).getIdstructure();
		case 2: return records.get(rowIndex).getContent();
		default: return "";
		}
	}

	public List<StructureRecord> getRecords() {
		return records;
	}

	public void setRecords(List<StructureRecord> records) {
		this.records = records;
		fireTableStructureChanged();
	}

}


