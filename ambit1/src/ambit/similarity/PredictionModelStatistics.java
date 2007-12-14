/* PredictionModelStatistics.java
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

package ambit.similarity;

import java.util.Enumeration;

import ambit.stats.datastructures.Bin;


public class PredictionModelStatistics<U extends Bin> extends AbstractModelStatistics<U> {
    protected static final int MAE = 0;
    protected static final int RMSE = 1;
    
    protected static final int ALL = 2;
    protected static final int IGNORED = 3;
    protected static final int LAST = 4;
    protected String[] headings = {"Mean absolute error","Root mean squared error","Total Number of Instances","Ignored instances"};

    
    public PredictionModelStatistics(String title) {
        super(title);
    }

    protected double calculateError(Object observable, Object predicted) throws Exception {
        return Double.parseDouble(observable.toString()) - Double.parseDouble(predicted.toString());
    }
    public void addPoint(Comparable dataset, Object observable, Object predicted) {
        double[] t = getStats(dataset);
    
        t[ALL]++;
        try {
            double error = calculateError(observable, predicted);
            t[MAE] += Math.abs(error);
            t[RMSE] += Math.pow(error,2);
        } catch (Exception x) {
            t[IGNORED] ++;
        }
    }
    @Override
    protected double[] createStats() {
        return new double[LAST];
    }
    public String toStatus(boolean verbose) {
        StringBuffer b = new StringBuffer();
        Enumeration<Comparable> e = stats.keys();
        
        while (e.hasMoreElements()) {
            Comparable c = e.nextElement();
            double[] t = stats.get(c);
            double denom = (t[ALL]-t[IGNORED]);
            b.append(c.toString());
            b.append('\n');
            for (int i=0; i < t.length;i++) {
                b.append(headings[i]);
                b.append('\t');
                if (i==RMSE) {
                    if (denom != 0)
                        b.append(double_nf.format(Math.sqrt(t[i]/denom)));
                } else if (i==MAE)  {
                    if (denom != 0)
                        b.append(double_nf.format(t[i]/denom));
                } else    
                    b.append(integer_nf.format(t[i]));
                b.append('\n');
            }
        }
        b.append(super.toStatus(verbose));
        return b.toString();
    }

}    
 
