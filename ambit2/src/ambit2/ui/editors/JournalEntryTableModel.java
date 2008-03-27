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

package ambit2.ui.editors;

import ambit2.data.AmbitObject;
import ambit2.data.literature.JournalEntry;
import ambit2.ui.data.AbstractPropertyTableModel;

public class JournalEntryTableModel extends AbstractPropertyTableModel {
	protected String[] rownames = {"Abbreviation","City","Name","Publisher"};
	public JournalEntryTableModel(AmbitObject object) {
		super(object);

	}

	public int getRowCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) return rownames[rowIndex];
		if (object == null) return "";
		JournalEntry journal = (JournalEntry) object;
		
		switch (rowIndex) {
		case 0:
			return journal.getAbbreviation();
		case 1:
			return journal.getCity();			
		case 2:
			return journal.getName();
		case 3:
			return journal.getPublisher();			
		default:
			return "";
		}
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 0) return;
			if (object == null) return;
			JournalEntry journal = (JournalEntry) object;
			switch (rowIndex) {
			case 0:
				journal.setAbbreviation(aValue.toString());
				break;
			case 1:
				journal.setCity(aValue.toString());
				break;
			case 2:
				journal.setName(aValue.toString());
				break;
			case 3:
				journal.setPublisher(aValue.toString());
				break;
			default:
				return ;
			}
		}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
}


