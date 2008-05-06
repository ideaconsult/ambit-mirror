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

package ambit2.similarity;

import java.io.Writer;

import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;

public class SimilarityHistogram implements IAmbitResult {
	protected double[] bins;
	protected double[] counter;
	protected String similarityProperty;
	protected String title;
	public SimilarityHistogram(String title, String similarityProperty) {
		this(title,similarityProperty,getDefaultBins(0,1));
	}	
	public SimilarityHistogram(String title,String similarityProperty, double[]bins) {
		super();
		this.title = title;
		this.similarityProperty = similarityProperty;
		this.bins = bins;
		counter = new double[bins.length];
	}
	protected static double[] getDefaultBins(double min, double max) {
		if (min == max) max = min + 1;
		double[] bins= new double[10];
		double step = (max-min)/bins.length;
		for (int i=0; i < bins.length;i++) bins[i] = min + (i+1)*step;
		return bins;
	}
	public void update(Object object) throws AmbitException {
		Object d = null;
		if (object instanceof IChemObject)
			d = ((IChemObject) object).getProperty(similarityProperty);
		else d = object;
		if (d instanceof Number) {
			double v = ((Number)d).doubleValue();
			for (int i=0; i < bins.length;i++) 
				if ( v < bins[i]) {
					counter[i]++;
					break;
				}
			
		}
	}

	public void clear() {
		for (int i=0; i < counter.length;i++) counter[i] = 0;

	}

	public void write(Writer writer) throws AmbitException {
		// TODO Auto-generated method stub

	}
	public double[] getBins() {
		return bins;
	}
	public double[] getCounter() {
		return counter;
	}
	@Override
	public String toString() {
		return similarityProperty;
	}
	public String getSimilarityProperty() {
		return similarityProperty;
	}
	public void setSimilarityProperty(String similarityProperty) {
		this.similarityProperty = similarityProperty;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}


