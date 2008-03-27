/* NearestNeighborsSearch.java
 * Author: Nina Jeliazkova
 * Date: Feb 4, 2007 
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

package ambit2.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import ambit2.log.AmbitLogger;

/**
 * Brute force implementation of nearest neighbors. (uses {@link TreeSet})
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 4, 2007
 */
public class NearestNeighborsSearch<T> extends ArrayList<DistanceComparable<T>> implements INearestNeighborsSearch<T> {
    protected IDistanceFunction<T> distanceFunction;
    protected boolean lowestDistanceIsClosest = true;
    protected static AmbitLogger logger = new AmbitLogger(NearestNeighborsSearch.class);
    public NearestNeighborsSearch() {
        this(null);
    }
    public NearestNeighborsSearch(IDistanceFunction<T> distanceFunction) {
        super();
        setDistanceFunction(distanceFunction);
    }
    @Override
    public boolean add(DistanceComparable<T> o) {
        if (o != null)
            return super.add(o);
        else return false;
    }
    /**
     * null property
     * @param o Object
     * @return
     */
    public boolean addObject(T o) {
        if (o != null)
            return add(new DistanceComparable<T>(o,Double.NaN,lowestDistanceIsClosest));
        else return false;
    }
    /**
     * 
     * @param o Object
     * @param property property to be predicted
     * @return
     */
    public boolean addObject(T o, Object property) {
        if (o != null)
            return add(new DistanceComparable<T>(o,Float.NaN,property,lowestDistanceIsClosest));
        else return false;
    }        
    /**
     * Returns k nearest neighbors, encapsulated in {@link DistanceComparable}. 
     * The closest neighbors come first. 
     */
    public List<DistanceComparable<T>> getNearestNeighbors(T query,int k) throws Exception {
        if (distanceFunction == null) throw new Exception("Distance function not initialized!");
        List<DistanceComparable<T>> kNN = new ArrayList<DistanceComparable<T>>(); 
        for (int i=0; i < size(); i++) {
            DistanceComparable<T> o = get(i);
            try {
                
                float distance = distanceFunction.getDistance(o.getObject(),query);
                o.setDistance(distance);
                boolean add = true;

                if (add) {    
                    //DistanceComparable<T> dc = new DistanceComparable<T>(o,distance,lowestDistanceIsClosest);
                    kNN.add(o);
                }

            } catch (Exception x) {
                //x.printStackTrace();
                logger.error(x);
            }
        }
        Collections.sort(kNN);
        for (int i=kNN.size()-1;i>=k;i--)
            kNN.remove(i);            
        return kNN;
    }

    public IDistanceFunction<T> getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(IDistanceFunction<T> distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public synchronized boolean isLowestDistanceIsClosest() {
        return lowestDistanceIsClosest;
    }

    public synchronized void setLowestDistanceIsClosest(
            boolean lowestDistanceIsClosest) {
        this.lowestDistanceIsClosest = lowestDistanceIsClosest;
        for (int i=0; i < size(); i++)
            get(i).setLowestDistanceIsClosest(lowestDistanceIsClosest);
    }

}

