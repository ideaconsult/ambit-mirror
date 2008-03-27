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

package ambit2.stats.datastructures;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;


public class ConfusionMatrix extends AbstractTableModel {
	protected ArrayList<String> observed;
	protected ArrayList<String> predicted;
	protected Histogram<SelfBin<ValuePair>> histogram;
	private SelfBin<ValuePair> vp;
	public ConfusionMatrix(Histogram<SelfBin<ValuePair>> histogram) {
		super();
		observed = new ArrayList<String>();
		predicted = new ArrayList<String>();
		this.histogram = histogram;
		Iterator<SelfBin<ValuePair>> i = histogram.keySet().iterator();
		while (i.hasNext()) {
			ValuePair v = i.next().getLow();
			if (observed.indexOf(v.observed.toString())== -1) 
				observed.add(v.observed.toString());
			if (predicted.indexOf(v.predicted.toString())== -1) 
				predicted.add(v.predicted.toString());			
		}
		vp = new SelfBin<ValuePair>(new ValuePair("",""));
	}
	public int getColumnCount() {
		return predicted.size();
	}
	public int getRowCount() {
		return observed.size();
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		vp.getSelf().observed = observed.get(rowIndex);
		vp.getSelf().predicted = predicted.get(columnIndex);		
		MutableInt freq = histogram.get(vp);
		if (freq == null) return "";
		else return freq;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Confusion matrix\n");
		b.append("\\");
		for (int c=0; c < getColumnCount(); c++) {
			b.append('(');
			b.append(Integer.toString(c+1));
			b.append(')');
			b.append('\t');
		}
		b.append(" <-- classified as");
		b.append('\n');
		for (int r=0; r < getRowCount(); r++) {
			for (int c=0; c < getColumnCount(); c++) {
				b.append(getValueAt(r,c));
				b.append('\t');
			}
			b.append('|');
			b.append(observed.get(r));
			b.append('\n');
		}	
		
		for (int c=0; c < getColumnCount(); c++) {
			b.append('\n');
			b.append('(');
			b.append(Integer.toString(c+1));
			b.append(')');
			b.append('\t');
			b.append(predicted.get(c));
		}				
		return b.toString();
	}
}