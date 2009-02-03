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

package ambit2.similarity.stats;

public class Bin<T extends Comparable> implements Comparable<Bin<T>> {
	protected T low;
	protected T high;

	public Bin(T low, T high) {
		super();
		setLow(low);
		setHigh(high);
	}
	public T getHigh() {
		return high;
	}

	public void setHigh(T high) {
		this.high = high;
	}

	public T getLow() {
		return low;
	}

	public void setLow(T low) {
		this.low = low;
	}
	
	public boolean accept(T t) {
		return ((t.compareTo(low)>0) && (t.compareTo(high)<0));
	}
	@Override
	public boolean equals(Object obj) {
		return compareTo((Bin)obj) == 0;
	}
	public int compareTo(Bin<T> o) {
		int r = low.compareTo(o.getLow());
		if (r==0) return high.compareTo(o.getHigh());
		else return r;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[");
		b.append(low.toString());
		b.append(",");
		b.append(high.toString());
		b.append("]");
		return b.toString();
	}
}

