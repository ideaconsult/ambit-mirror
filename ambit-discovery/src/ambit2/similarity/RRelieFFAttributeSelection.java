/*
Copyright Ideaconsult Ltd. (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/
package ambit2.similarity;

import java.util.List;

import org.openscience.cdk.qsar.model.QSARModelException;


public abstract class RRelieFFAttributeSelection<T> extends AbstractAttributeSelection<T>  {
	
	protected double maxProperty;
	protected double minProperty;
	protected double threshold = 0;
	protected double[] weights;
	
	public void evaluateAttributes(NearestNeighborsSearch<T> nn, int numberOfAttributes) throws QSARModelException {
		setEvaluated(false);
		weights = null;
		if (numberOfAttributes == 0) return; 
			calculateMaxMinProperty(nn);
	    	int M = nn.size();
	    	threshold = 1/Math.sqrt(2*M);
	    	System.out.println(threshold);
	    	int K = 10;
	    	
	    	double Ndc = 0;
	    	double[] Nda = new double[numberOfAttributes];
	    	double[] Ndcda = new double[numberOfAttributes];
	    	double[] w = new double[numberOfAttributes];    	
	    	for (int a=0; a < numberOfAttributes;a++) {
	    		Nda[a] = 0;
	    		Ndcda[a] = 0;
	    		w[a] = 0;
	    	}
	    	
	    	for (int m=0; m < M; m++) {
	    		int index = (int)Math.round(Math.random()*nn.size());
	    		if (index >= nn.size()) index = nn.size()-1;
	    		DistanceComparable<T> R = nn.get(index);
	    		if (R.getProperty() == null) continue;
	    		try {
	    			List<DistanceComparable<T>> knn = nn.getNearestNeighbors(R.getObject(), K);
	    			for (int j=0; j < knn.size(); j++) {
	    				double dRank = diff_rank(j+1,0.5);
	    				double dProperty = diff(R.getProperty(),knn.get(j).getProperty(),minProperty,maxProperty);
	    				Ndc += dProperty*dRank;
	    				
	    				for (int a=0; a < numberOfAttributes; a++) {
	    					Object o1 = getAttribute(R.getObject(),a);
	    					Object o2 = getAttribute(knn.get(j).getObject(),a);
	    					double dAttribute = diff(
	    							o1,
	    							o2,
	    							getMaxAttribute(a),
	    							getMinAttribute(a)
	    							);
	    					
	    					/*
	    					if (dAttribute > 0)
	    						System.out.println(a + " " + dAttribute + " " + o1 + " " + o2);
	    						*/
	    					Nda[a] += dAttribute*dRank;
	    					Ndcda[a] += dProperty*dAttribute*dRank;
	    				}
	    				
	    			}
	    		} catch (Exception x) {
	    			throw new QSARModelException(x.getMessage());
	    		}
	    	}	
	    	for (int a=0; a < numberOfAttributes; a++) 
	    		w[a] = Ndcda[a]/Ndc - (Nda[a]-Ndcda[a])/(M-Ndc);    	
	    	weights = w;
	    	setEvaluated(true);
	    }
	    protected double diff_rank(int rank,double sigma) {
	    	return Math.exp(-((double)rank)/sigma);
	    }
	    protected double diff(Object property1, Object property2, double maxA, double minA) {
	    	if ((property1 ==null) && (property2 == null)) return 0; 
	    	if ((property1 instanceof Number) && (property2 instanceof Number)) //numeric
	    		return Math.abs(((Number)property1).doubleValue() - ((Number)property2).doubleValue())/(maxA-minA);
	    	else //nominal 
	    		if (property1.equals(property2)) return 0; else return 1;
	    }
	    
	    protected void calculateMaxMinProperty(NearestNeighborsSearch<T> nn) {
			minProperty = 0;
			maxProperty =1;
	    	for (int i=0; i < nn.size(); i++) {
	    		Object p = nn.get(i).getProperty();
	    		if (p instanceof Number) {
	    			Number n = (Number) p;
	    			if (i==0) {
	    				minProperty = n.doubleValue();
	    				maxProperty = n.doubleValue();
	    			} else {
	    				if (minProperty > n.doubleValue()) minProperty = n.doubleValue();
	    				if (maxProperty < n.doubleValue()) maxProperty = n.doubleValue();
	    			}
	    		}
	    	}
	    }
	    protected abstract Object getAttribute(T attributes, int attribute_index);
	    protected abstract double getMaxAttribute(int attribute_index);
	    protected abstract double getMinAttribute(int attribute_index);
		public double getThreshold() {
			return threshold;
		}
		public void setThreshold(double threshold) {
			this.threshold = threshold;
		}

}


