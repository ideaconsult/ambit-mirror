/* AbstractModelStatistics.java
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

import java.text.NumberFormat;
import java.util.Hashtable;



public abstract class AbstractModelStatistics<U extends Bin> extends DoubleValuesHistogram<U> {
    protected Hashtable<Comparable,double[]> stats;
    protected NumberFormat integer_nf = NumberFormat.getInstance();
    protected NumberFormat double_nf = NumberFormat.getInstance();

    public AbstractModelStatistics(String title) {
    	super(title);
        stats = new Hashtable<Comparable, double[]>();
        integer_nf.setMaximumFractionDigits(0);
        double_nf.setMaximumFractionDigits(3);
    }
    public void clear() {
    	super.clear();
        stats.clear();
    }
    public void clear(Comparable dataset) {
    	super.clear(dataset);
        double t[] = stats.get(dataset);
        if (t != null) {
            stats.remove(dataset);
            t = null;
        }
    }    
    protected  double[] getStats(Comparable dataset) {
        double[] t = stats.get(dataset);
        if (t == null) {
            t = createStats();
            stats.put(dataset,t);
        }
        return t;
    }
    protected abstract double[] createStats();

}    
 

