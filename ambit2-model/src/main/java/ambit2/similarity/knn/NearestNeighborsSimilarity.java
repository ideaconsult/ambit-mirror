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

package ambit2.similarity.knn;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.apache.poi.ss.formula.functions.T;

import ambit2.featureselection.IAttributeSelection;
import ambit2.similarity.AbstractSimilarityModel;
import ambit2.similarity.measure.DistanceComparable;
import ambit2.similarity.measure.IDistanceFunction;
import ambit2.similarity.stats.Histogram;

/**
 * Calculates average distance to kNN nearest neighbors, as defined by {@link IDistanceFunction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 4, 2007
 */
public abstract class NearestNeighborsSimilarity<ID, Features> extends 
				AbstractSimilarityModel<ID, Features,Double>  {

    protected NearestNeighborsSearch<Features> nearestNeighborsSearch;
    protected IAttributeSelection<Features> attributeSelection = null;
    
	protected int kNN = 5;
	public NearestNeighborsSimilarity(IDistanceFunction<Features> distanceFunction, int knn) {
		super();
        nearestNeighborsSearch = new NearestNeighborsSearch<Features>(distanceFunction);
        setKNN(knn);
	}

	public void buildInitialize() throws AmbitException {
		super.buildInitialize();
		setResult(null);
        nearestNeighborsSearch.clear();
	}
	public Double learn(ID id, Features features, Double observed, Double predicted) throws AmbitException ,UnsupportedOperationException {
		try {
            nearestNeighborsSearch.addObject(
            		transformAttributes(features));
            return null;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
    
	protected abstract Features getSelectedAttributes(Features attributes);
	
    protected Features transformAttributes(Features attributes) {
    	if ((attributeSelection != null) && attributeSelection.isEvaluated()) 
    			return getSelectedAttributes(attributes);
    	else return attributes;
    }
    

    protected Object classifyQuery(Features query, List<DistanceComparable<Features>> neighbors) throws Exception  {

        Iterator<DistanceComparable<Features>> i = neighbors.iterator();
        double property = 0;
        Histogram h = new Histogram();
        while (i.hasNext()) {
            Object p = i.next().getProperty();
            if (p != null) h.addObject(p.toString());
            
        }
        return h.getMaxFrequency();
    }    
    /**
     * Weighted average Sum((1/distance)*property).
     * @param query
     * @param neighbors
     * @return the property
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
            	logger.log(Level.SEVERE,p.toString(),x);
            }
        }
        if (normalize != 0) property = property/normalize;
        return property;
    }
    protected double calculateAverageDistance(Features query, List<DistanceComparable<Features>> neighbors) throws Exception  {

        Iterator<DistanceComparable<Features>> i = neighbors.iterator();
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
    public Double predict(ID id, Features features, Double observed, Double predicted, ambit2.model.IModelStatistics<Double> stats) throws AmbitException {
		try {
			Features query = transformAttributes(features);
            if (query != null) { 
                List<DistanceComparable<Features>> neighbors = nearestNeighborsSearch.getNearestNeighbors(query, kNN);
                return calculateAverageDistance(query,neighbors);
            }  else 
            	return Double.NaN;
		
		} catch (Exception x) {
			throw new AmbitException(x);
		}    	
    };
    public double getPairwise(Features features1, Features features2) throws AmbitException {
		try {
            return nearestNeighborsSearch.getDistanceFunction().getDistance(
            		transformAttributes(features1), 
            		transformAttributes(features2));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public void close() {
		//super.close();

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
            logger.log(Level.SEVERE,x.getMessage(),x);
        }

    }

    public Object getParameter(Object parameter) {
        if ("kNN".equals(parameter)) return new Integer(kNN);
        else return null;
    }
    public synchronized NearestNeighborsSearch<Features> getNearestNeighborsSearch() {
        return nearestNeighborsSearch;
    }

    public synchronized void setNearestNeighborsSearch(NearestNeighborsSearch<Features> nnSearch) {
        this.nearestNeighborsSearch = nnSearch;
    }

    public synchronized int getKNN() {
        return kNN;
    }

    public synchronized void setKNN(int knn) {
        kNN = knn;
    }




}


