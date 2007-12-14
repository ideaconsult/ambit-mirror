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

package ambit.similarity;

import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit.exceptions.AmbitException;
import ambit.stats.datastructures.Histogram;

/**
 * Calculates average distance to kNN nearest neighbors, as defined by {@link IDistanceFunction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 4, 2007
 */
public abstract class NearestNeighborsSimilarity<T> extends DefaultSimilarityProcessor  {

    protected NearestNeighborsSearch<T> nearestNeighborsSearch;
    protected IAttributeSelection<T> attributeSelection = null;
    
	protected int kNN = 5;
	public NearestNeighborsSimilarity(IDistanceFunction<T> distanceFunction, int knn) {
		super();
        nearestNeighborsSearch = new NearestNeighborsSearch<T>(distanceFunction);
        setKNN(knn);
		setPredicting(false);
	}

	public void buildInitialize() throws AmbitException {
		super.buildInitialize();
		setResult(null);
        nearestNeighborsSearch.clear();
	}

	/**
     * Adds {@link #calculateComparableProperty} to {@link NearestNeighborsSearch}} 
	 */
	public void incrementalBuild(Object object) throws AmbitException {
		try {
            nearestNeighborsSearch.addObject(
            		transformAttributes(calculateComparableProperty(object)));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
    
	protected abstract T calculateComparableProperty(Object object)  throws AmbitException ;
	protected abstract T getSelectedAttributes(T attributes);
	
    protected T transformAttributes(T attributes) {
    	if ((attributeSelection != null) && attributeSelection.isEvaluated()) 
    			return getSelectedAttributes(attributes);
    	else return attributes;
    }
    
    protected double calculateAverageDistance(T query, List<DistanceComparable<T>> neighbors) throws Exception  {

        Iterator<DistanceComparable<T>> i = neighbors.iterator();
        float distance = 0;
        int n = 0;
        while (i.hasNext()) {
            Float f = i.next().getDistance();
            if ((f==null) || f.isNaN()) continue;
            distance += f;
            n++;
        }
        if (n>1) distance = distance/n;
        return distance;

    }
    protected Object classifyQuery(T query, List<DistanceComparable<T>> neighbors) throws Exception  {

        Iterator<DistanceComparable<T>> i = neighbors.iterator();
        double property = 0;
        Histogram h = new Histogram();
        while (i.hasNext()) {
            Object p = i.next().getProperty();
            if (p != null) h.addObject(p.toString());
            
        }
        //System.out.println(h);
        return h.getMaxFrequency();
    }    
    /**
     * Weighted average Sum((1/distance)*property).
     * @param query
     * @param neighbors
     * @return
     * @throws Exception
     */
    protected Object calculateProperty(T query, List<DistanceComparable<T>> neighbors) throws Exception  {

        Iterator<DistanceComparable<T>> i = neighbors.iterator();
        double property = 0;
        //double n = 0;
        float weight = 0;
        float normalize = 0;
        while (i.hasNext()) {
            DistanceComparable next = i.next();
            Object p = next.getProperty();
            Float f = next.getDistance();
            if ((f==null) || f.isNaN()) continue;
            if ((p==null) || ("".equals(p))) continue;
            if (nearestNeighborsSearch.isLowestDistanceIsClosest() ) {
                if (f == 0) weight = 1;
                else weight = 1/f;
            } else weight = f;
            try {
                if (p instanceof Number) {
                    if (Double.isNaN(((Number)p).doubleValue())) continue;
                    property += weight*((Number)p).doubleValue(); 
                    //n++; 
                    normalize += weight;
                } else {
                    property += weight*Double.parseDouble(p.toString());
                    //n++;
                    normalize += weight;
                    
                }
            } catch (Exception x) {
                
                logger.error(p.toString()+x);
            }
        }
        if (normalize != 0) property = property/normalize;
        return property;
    }
    
	public double predict(Object object) throws AmbitException {
        if (object  == null) return Double.NaN;
		if (isPredicting())
			
			try {
				T query = transformAttributes(calculateComparableProperty(object));
                Double d = null;
                double distance = Double.NaN;
                if (query != null) { 
                    List<DistanceComparable<T>> neighbors = nearestNeighborsSearch.getNearestNeighbors(query, kNN);
                    distance = calculateAverageDistance(query,neighbors);
                    d = new Double(distance);
                }    
				((IAtomContainer) object).setProperty(getTrainingPrefix() + "." + getSimilarityProperty(), d);
				return distance;
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		else return super.predict(object);

	}
	
	public double predict(Object object1, Object object2) throws AmbitException {
		try {
            return nearestNeighborsSearch.getDistanceFunction().getDistance(
            		transformAttributes(calculateComparableProperty(object1)), 
            		transformAttributes(calculateComparableProperty(object2)));

		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public void close() {
		super.close();
		/*
		if (bitsets != null)
		bitsets.clear();
		*/
		/*
			propertyChangeSupport.firePropertyChange("Result",
	                null,
	                similarityMatrix);
			LightSimilarityMatrix newmatrix = new LightSimilarityMatrix(
					((LightSimilarityMatrix)similarityMatrix).getMoleculesRows(),
					new ArrayList(),
					this);
			setSimilarityMatrix(newmatrix);
		*/
	}

	public String toString() {
	    if (nearestNeighborsSearch ==null) return "Undefined similarity";
        else return "Similarity by " + kNN + "NN, " +  nearestNeighborsSearch.getDistanceFunction();
	}
    public void setParameter(Object parameter, Object value) {
        /*
        if (parameter.equals(p_classification_property)) classificationProperty = value.toString();
        else if (parameter.equals(p_predicted_property)) predictedProperty = value.toString();  
        else
        */ 
        if ("kNN".equals(parameter)) 
        try {
            int n = Integer.parseInt(value.toString());
            setKNN(n);
        } catch (Exception x) {
            logger.error(x);
        }

    }

    public Object getParameter(Object parameter) {
        if ("kNN".equals(parameter)) return new Integer(kNN);
        else return null;
    }
    public synchronized NearestNeighborsSearch<T> getNearestNeighborsSearch() {
        return nearestNeighborsSearch;
    }

    public synchronized void setNearestNeighborsSearch(NearestNeighborsSearch<T> nnSearch) {
        this.nearestNeighborsSearch = nnSearch;
    }

    public synchronized int getKNN() {
        return kNN;
    }

    public synchronized void setKNN(int knn) {
        kNN = knn;
    }

	public IAttributeSelection<T> getAttributeSelection() {
		return attributeSelection;
	}

	public void setAttributeSelection(IAttributeSelection<T> attributeSelection) {
		this.attributeSelection = attributeSelection;
	}


}


