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

package ambit.ui.query;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

public abstract class SimilarityTableModel extends AbstractTableModel {
	protected Object data;
	public SimilarityTableModel(Object data) {
		super();
		this.data = data;
	}
	
	public Object getValueAt(int arg0, int arg1) {
		float similarity = getSimilarity(arg0,arg1);
		return new Color(similarity,similarity,similarity);
	}
	protected float getSimilarity(int row, int col) {
		return row;
	}

}


