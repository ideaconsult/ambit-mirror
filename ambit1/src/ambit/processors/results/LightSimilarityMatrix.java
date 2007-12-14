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

package ambit.processors.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ambit.data.molecule.IAtomContainersList;
import ambit.exceptions.AmbitException;
import ambit.similarity.ISimilarityProcessor;

public class LightSimilarityMatrix extends AbstractSimilarityMatrix {
	protected IAtomContainersList rows;
	protected IAtomContainersList columns;
	protected ISimilarityProcessor processor;
	protected BufferCells buffer;
	protected boolean enabled = true;
	
	public LightSimilarityMatrix(ISimilarityProcessor processor) {
		this(null,null,processor);
	}

	public LightSimilarityMatrix(IAtomContainersList rows, IAtomContainersList columns, ISimilarityProcessor processor) {
		super();
		buffer = new BufferCells(512);
		setColumns(columns);
		setRows(rows);
		setProcessor(processor);
		
	}

	public void update(Object object) throws AmbitException {
		/*
		if (processor.isPredicting()) 
			columns.add(object);
		else { 
			rows.add(object);
			columns.add(object);
		}
		*/
	}

	public void clear() {
		setColumns(null);
		setRows(null);
		fireTableStructureChanged();
	}

	
	public int getRowCount() {
		if (!enabled) return 0;
		if (rows == null) return 0;
		return rows.getAtomContainerCount();
	}

	public int getColumnCount() {
		if (!enabled) return 0;
		if (columns == null) return 0;
		return columns.getAtomContainerCount();
	}

	public Object getValueAt(int row, int column) {
		if (!enabled) return Float.NaN;
		try {
			//upper diagonal triangular matrix
			if (processor == null) return Float.NaN;
			if ((rows != columns) || ((rows == columns) && (row <= column))) {
				SCell cell = buffer.get(row, column);
				if (cell==null) {
					float d = calcValueAt(row, column);
					buffer.add(new SCell(row,column,d));
					return new Float(d);
				} else return cell.value;
			}
			else return Float.NaN;
		} catch (Exception x) {
			return Float.NaN;
		}
	}
	public float calcValueAt(int row, int column) throws Exception {
		return (float)processor.predict(rows.getAtomContainer(row),columns.getAtomContainer(column));		
	}
	public IAtomContainersList getMoleculesColumns() {
		return columns;
	}

	public void setColumns(IAtomContainersList columns) {
		this.columns = columns;
		buffer.clear();
		fireTableStructureChanged();
	}

	public IAtomContainersList getMoleculesRows() {
		return rows;
	}

	public void setRows(IAtomContainersList rows) {
		this.rows = rows;
		buffer.clear();
		fireTableStructureChanged();
	}

	public ISimilarityProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(ISimilarityProcessor processor) {
		this.processor = processor;
		buffer.clear();
		fireTableStructureChanged();
	}
	@Override
	public String toString() {
		if (processor ==null) return "";
		else
		return processor.toString();
	}
	@Override
	public String getColumnName(int column) {
		//Object o = columns.getProperty(column, CDKConstants.CASRN);
		//if ((o == null) || o.equals("")) return Integer.toString(column);
		//else return o.toString();
		return Integer.toString(column);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		fireTableStructureChanged();
	}
}

class SCell implements Comparable<SCell>{
	int row;
	int col;
	Float value;
	long timestamp;

	public SCell(int row,  int col, Float value) {
		this.row = row;
		this.col = col;
		this.value = new Float(value);
		this.timestamp = System.currentTimeMillis();
	}

	public SCell(int row,  int col, float value) {
		this(row,col,new Float(value));
	}
	@Override
	public boolean equals(Object arg0) {
		SCell c = (SCell) arg0;
		return (c.row==row) && (c.col==col);
	}
	public int compareTo(SCell arg0) {
		SCell c = (SCell) arg0;
		int a = c.row-row;
		if (a==0) return c.col-col; else return a;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
}

class TimeoutComparator implements Comparator<SCell> {
	public int compare(SCell arg0, SCell arg1) {
		return (int)(arg0.timestamp-arg1.timestamp);
	}
}
class BufferCells extends ArrayList<SCell>{
	protected int max = 256;
	protected static Comparator comparator = new TimeoutComparator();
	protected SCell query;
	public BufferCells() {
		this(256);
	}
	public BufferCells(int max) {		
		super();
		this.max = max;
		query = new SCell(-1,-1,0);
	}
	@Override
	public boolean add(SCell cell) {
		if (size()>max) {
			Collections.sort(this,comparator);
			remove(0);
		}
		return super.add(cell);
	}
	public synchronized SCell get(int row,int col) {
		query.col = col;
		query.row = row;
		int i = indexOf(query);
		if (i <0) return null; else return get(i);
	}
}


