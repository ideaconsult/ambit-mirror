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

package ambit.ui.data.descriptors;

import javax.swing.table.AbstractTableModel;

import ambit.data.descriptors.AtomEnvironment;
import ambit.data.descriptors.AtomEnvironmentList;

public class AtomEnvironmentListTableModel extends AbstractTableModel {
	protected AtomEnvironmentList aeList;
	public AtomEnvironmentListTableModel(AtomEnvironmentList aeList) {
		super();
		this.aeList = aeList;
	}

	public int getColumnCount() {
		return 4; //4+30
	}

	public int getRowCount() {
		if (aeList == null) return 0;
		else return aeList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (aeList == null) return "";
		AtomEnvironment ae = aeList.get(rowIndex);
		switch (columnIndex) {
		case 0: return new Integer(ae.getAtomno()+1); //new Integer(rowIndex+1);
		case 1: return ae.getCentral_atom();
		case 3: return new Integer(ae.getFrequency());
		case 2: return new Integer(ae.getSublevel());
		default: return ae.getAtom_environment()[columnIndex-4 + 30];
		}
	}
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: return "#";
		case 1: return "Atom";
		case 3: return "Frequency";
		case 2: return "Level";
		case 4: return "Environment";
		default: return "";
		}
	}
	public AtomEnvironmentList getAeList() {
		return aeList;
	}

	public void setAeList(AtomEnvironmentList aeList) {
		this.aeList = aeList;
		fireTableStructureChanged();
	}

}


