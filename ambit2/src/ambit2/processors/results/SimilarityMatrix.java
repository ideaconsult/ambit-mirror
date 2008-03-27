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

package ambit2.processors.results;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;

/**
 * Similarity matrix
 * TODO uder development
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class SimilarityMatrix extends AbstractSimilarityMatrix {
	protected int columns = 0;
	protected ArrayList<Float[]> matrix; //to be able to add rows incrementally
	
	public SimilarityMatrix() {
		super();
		matrix = new ArrayList<Float[]>();
	}

	/**
	 * expected Float[] as a property
	 */
	public void update(Object object) throws AmbitException {
		Object aRow = ((IAtomContainer) object).getProperty(AmbitCONSTANTS.PairwiseSimilarity);
		
		if ((aRow != null) && (aRow instanceof Double[]) )
			addRow(((Float[]) aRow));

	}
	public void addRow(Float[] aRow) {
		columns = aRow.length;
		matrix.add(aRow);
	}
	public void clear() {
		matrix.clear();
		columns = 0;
		fireTableStructureChanged();

	}

	public int getColumnCount() {
		return columns;
	}
	public int getRowCount() {
		return matrix.size();
	}
	public Object getValueAt(int row, int col) {
		Object aRow = matrix.get(row);
		if ((aRow != null) && (aRow instanceof Double[]) ) {
			return ((Double[]) aRow)[col];
		} else return null;
	}

}


