/* DistanceComparable.java
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

/**
 * Encapsulates object and its calculated distance. See {@link NearestNeighborsSearch}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 4, 2007
 */
public class DistanceComparable<T> implements Comparable<DistanceComparable> {
    protected T object;
    protected Float distance;
    protected Object property = null;
    protected  boolean lowestDistanceIsClosest = true;

    
    public DistanceComparable(T object, double distance, boolean lowestDistanceIsClosest) {
         this(object, new Float(distance), null, lowestDistanceIsClosest);
    }
    public DistanceComparable(T object, Float distance,Object property, boolean lowestDistanceIsClosest) {
        super();
        setObject(object);
        setDistance(distance);        
        setLowestDistanceIsClosest(lowestDistanceIsClosest);
        setProperty(property);
   }    
    public int compareTo(DistanceComparable o) {
        if (lowestDistanceIsClosest)
            return getDistance().compareTo(o.getDistance());
        else
            return -getDistance().compareTo(o.getDistance());
    }
    public synchronized Float getDistance() {
        return distance;
    }
    public synchronized void setDistance(Float distance) {
        this.distance = distance;
    }
    public synchronized T getObject() {
        return object;
    }
    public synchronized void setObject(T object) {
        this.object = object;
    }
    public synchronized boolean isLowestDistanceIsClosest() {
        return lowestDistanceIsClosest;
    }
    public synchronized void setLowestDistanceIsClosest(
            boolean lowestDistanceIsClosest) {
        this.lowestDistanceIsClosest = lowestDistanceIsClosest;
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((DistanceComparable)obj) == 0;
    }
    @Override
    public String toString() {
        
        return object.getClass().getName() + " ("+ distance.toString() + ")";
    }
    public synchronized Object getProperty() {
        return property;
    }
    /**
     * Property to be predicted by nearest neighbors (e.g. endpoint value)
     * @param property
     */
    public synchronized void setProperty(Object property) {
        this.property = property;
    }
}