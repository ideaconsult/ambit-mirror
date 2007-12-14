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

package ambit.ui.data.molecule;

import ambit.data.molecule.SourceDataset;
import ambit.ui.data.AbstractPropertyTableModel;

public class SourceDataSetTableModel extends AbstractPropertyTableModel {
	protected String[] rowNames ;
	//protected LiteratureEntryTableModel referenceModel;
	public SourceDataSetTableModel(SourceDataset object) {
		super(object);
		//referenceModel = new LiteratureEntryTableModel(object.getReference());
	}

	public int getRowCount() {
		//return referenceModel.getRowCount()+1;
		return 2;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if (columnIndex ==0) 
			if (rowIndex == 0) return "Dataset"; else return "Reference";
			//else return referenceModel.getValueAt(rowIndex-1, columnIndex);
		else
			if (object == null) return "";
			else
				switch (rowIndex) {
				case 0: return ((SourceDataset) object).getName();
				default: return ((SourceDataset) object).getReference();
				//default: return referenceModel.getValueAt(rowIndex-1, columnIndex);
				}
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex ==0) return;
		else switch (rowIndex) {
		case 0: ((SourceDataset) object).setName(aValue.toString());
		//default:  referenceModel.setValueAt(aValue,rowIndex-1, columnIndex);
		default: ;// ((SourceDataset) object).setReference(aValue);
		}
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
	/*
	@Override
	public synchronized void setObject(AmbitObject object) {
		if (object instanceof SourceDataset) {
			referenceModel.setObject(((SourceDataset)object).getReference());
			super.setObject(object);
		} else {
			referenceModel.setObject(null);
			super.setObject(null);
		}
	}
	*/
	@Override
	public boolean isExpanded(int rowIndex) {
		return rowIndex ==0;
	}
}


