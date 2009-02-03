/* ClassificationModelStatistics.java
 * Author: Nina Jeliazkova
 * Date: Mar 14, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.similarity.stats;

import java.util.Enumeration;
import java.util.Hashtable;


public class ClassificationModelStatistics<U extends Bin> extends AbstractModelStatistics<U> {
	protected static final int ACCURACY = 0;
    protected static final int CORRECT = 1;
    protected static final int ERROR = 2;
    protected static final int ALL = 3;
    protected static final int LAST = 4;
    protected String[] headings = {"Accuracy","Correctly classified","Incorreclty classified","Total Number of Instances"};
    protected Hashtable<Comparable,Histogram<SelfBin<ValuePair>>> histogram;
    protected boolean classUnknown = false;
    
    public ClassificationModelStatistics(String title) {
        super(title);
        histogram = new Hashtable<Comparable,Histogram<SelfBin<ValuePair>>>();
    }

    @Override
    public void clear(Comparable dataset) {
        super.clear(dataset);
        histogram.remove(dataset);
    }
    @Override
    public void clear() {
        super.clear();
        histogram.clear();
    }
    @Override
    protected double[] createStats() {
        return new double[LAST];
    }
    @Override
    public void addPoint(Comparable dataset, Double observed, Double predicted) {
        double[] t = getStats(dataset);
        t[ALL]++;
        if (observed.equals(predicted)) t[CORRECT]++;
        else t[ERROR]++;

        Histogram<SelfBin<ValuePair>> h = histogram.get(dataset);
        if (h==null) {
        	h = new Histogram<SelfBin<ValuePair>>();
        	histogram.put(dataset,h);
        }
        if ((observed!=null) && (predicted !=null))
        	h.addObject(new SelfBin(new ValuePair(observed.toString(),predicted.toString())));
        
    }

   
    public String toStatus(boolean verbose) {
        StringBuffer b = new StringBuffer();
        Enumeration<Comparable> e = stats.keys();
        while (e.hasMoreElements()) {
            Comparable c = e.nextElement();
            double[] t = stats.get(c);
            b.append(c.toString());
            b.append('\n');
            
            int start = 0;
            if (classUnknown) start = ALL;
            for (int i=start; i < t.length;i++) {
                b.append(headings[i]);
                b.append('\t');
                if (i==ACCURACY) {
                	if (t[ALL]>0)
                		b.append(double_nf.format(t[CORRECT]/t[ALL]));
                } else b.append(integer_nf.format(t[i]));
                b.append('\n');
            }
            b.append("\n");
            b.append(new ConfusionMatrix(histogram.get(c)).toString());
            b.append('\n');
            b.append('\n');
        }
        b.append(super.toStatus(verbose));
        return b.toString();
    }

	public boolean isClassUnknown() {
		return classUnknown;
	}

	public void setClassUnknown(boolean classUnknown) {
		this.classUnknown = classUnknown;
	}
	@Override
	public Histogram<U> getHistogram(Comparable dataset, int mode) {
		switch (mode) {
		case 0:	return (Histogram<U>)distance_histogram.get(dataset);
		case 1: return (Histogram<U>)histogram.get(dataset);
		default: return (Histogram<U>)distance_histogram.get(dataset);
		}
	}

}



