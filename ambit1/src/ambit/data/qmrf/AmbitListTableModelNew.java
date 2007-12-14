/*
Copyright (C) 2005-2006  

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

package ambit.data.qmrf;

import javax.swing.table.AbstractTableModel;

import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitListListener;

public class AmbitListTableModelNew extends AbstractTableModel implements IAmbitListListener {
	protected AmbitList list;
	protected boolean selection;
	public AmbitListTableModelNew(AmbitList list, boolean selection) {
		super();
		list.addAmbitObjectListener(this);
		this.list = list;
		this.selection = selection;
	}
	
	public int getColumnCount() {
		if (selection)	return 3; else return 2;
	}

	public int getRowCount() {
		if (list == null) return 0;
		else return list.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case (0): return new Integer(rowIndex+1);
		case (1): if (selection) return new Boolean(list.getItem(rowIndex).isSelected());
		else return list.getItem(rowIndex);
		case (2): return list.getItem(rowIndex);
		default: return "";
		}
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (list == null) return;
		switch (columnIndex) {
		case 1:
			try {
				if (selection)
					list.getItem(rowIndex).setSelected((Boolean) aValue);
			} catch (Exception x) {
				x.printStackTrace();
			}

		default:
			break;
		}
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return selection && (columnIndex == 1);
	}
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case (0): return "#";
		case (1): if (selection) return ""; else return "Name";
		case (2): return "Name";
		default: return "";
		}
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case (0): return Integer.class;
		case (1): if (selection) return Boolean.class; else return super.getColumnClass(columnIndex);
		default: return super.getColumnClass(columnIndex);
		}		
	}
	public void ambitListChanged(AmbitListChanged event) {
		fireTableStructureChanged();
		
	}
	public void ambitObjectChanged(AmbitObjectChanged event) {
		fireTableDataChanged();
		
	}
	
	public AmbitObject getItem(int row) {
		if (list == null) return null;
		return list.getItem(row);
	}
	public AmbitList getList() {
		return list;
	}
	public void setList(AmbitList list) {
		if (this.list != null) this.list.removeAmbitObjectListener(this);
		this.list = list;
		if (this.list != null)
			this.list.addAmbitObjectListener(this);
		fireTableStructureChanged();
	}
}


