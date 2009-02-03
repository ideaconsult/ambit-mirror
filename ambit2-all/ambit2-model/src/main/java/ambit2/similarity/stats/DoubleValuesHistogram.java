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



public class DoubleValuesHistogram<U extends Bin> extends ModelValuesHistogram<Double,U> {
	protected static double[] b = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
	public DoubleValuesHistogram(String title) {
		super(title);
	}

	@Override
	protected Bin<Double> createBin(Double value) {
		if (value.doubleValue() > 1.0) {
			Bin<Double> bin = new Bin<Double>(new Double(Math.floor(value.doubleValue())),new Double(Math.ceil(value.doubleValue())));
			if (bin.getHigh().equals(bin.getLow())) bin.setHigh(bin.getLow()+1);
			return bin;
		} else 
			for (int i=0; i < b.length-1; i++) 
				if ((value >= b[i]) && (value <= b[i+1])) 
					return new Bin<Double>(b[i],b[i+1]);
			return new Bin<Double>(Double.MIN_VALUE,0.0);
		
	}

}


