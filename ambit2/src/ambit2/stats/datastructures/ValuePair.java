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


public class ValuePair implements Comparable<ValuePair>{
    protected Comparable observed;
    protected Comparable predicted;
    public ValuePair(Comparable observed, Comparable predicted) {
        this.observed = observed;
        this.predicted = predicted;
    }
    @Override
    public boolean equals(Object obj) {
        return ((ValuePair)obj).observed.equals(observed) &&
        ((ValuePair)obj).predicted.equals(predicted);
    }
    @Override
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == observed ? 0 : observed.hashCode());
    	hash = 31 * hash + var_code; 
    	var_code = (null == predicted ? 0 : predicted.hashCode());
    	hash = 31 * hash + var_code; 
	
    	return hash;
    }
	public int compareTo(ValuePair o) {
		// TODO Auto-generated method stub
		int r = observed.compareTo(o.observed);
		if (r==0) return predicted.compareTo(o.predicted);
		else return r;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Observed\t");
		b.append(observed.toString());
		b.append('\t');
		b.append("Predicted\t");
		b.append(predicted.toString());
		b.append('\t');
		return b.toString();
	}
	public Comparable getObserved() {
		return observed;
	}
	public void setObserved(Comparable observed) {
		this.observed = observed;
	}
	public Comparable getPredicted() {
		return predicted;
	}
	public void setPredicted(Comparable predicted) {
		this.predicted = predicted;
	}
}
