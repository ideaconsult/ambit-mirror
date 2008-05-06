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

import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;

public interface ISimilarityProcessor extends IAmbitProcessor {
	public static String p_classification_property = "classificationProperty";
	public static String p_predicted_property = "predictedProperty";
    public void setParameter(Object parameter, Object value);
    public Object getParameter(Object parameter);
	public String getSimilarityProperty();
	public String getPairwiseSimilarityProperty();
	public void setSimilarityProperty(String name);
	public void setPairwiseSimilarityProperty(String name);
	public String getTrainingPrefix();
	/**
	 * Two modes - build and predict
	 * @return true if the model had been already built and can be used for predicting 
	 */
	public abstract boolean isPredicting();
	/**
	 * Two modes - build and predict
	 * Set predicting=true if the model had been already built and can be used for predicting 
	 */
	public abstract void setPredicting(boolean predicting);
	
	/**
	 * Allows to submit molecules to the model one by one. The models may keep internally some structures
	 * (e.g. matrix, fingerprints)
	 * @param object
	 * @throws AmbitException
	 */
	public void incrementalBuild(Object object) throws AmbitException ;
	/**
	 * Calculates similarity between object and a summary object obtained from previous build (e.g. a consensus fingerprint representing the set or a center point of the descriptor space of the set) 
	 * @param object
	 * @throws AmbitException
	 */
	public double predict(Object object) throws AmbitException ;
	/**
	 * Calculates pairwise similarity
	 * @param object1
	 * @param object2
	 * @throws AmbitException
	 */
	public double predict(Object object1,Object object2) throws AmbitException ;
	/**
	 * executes if {@link ISimilarityProcessor#isPredicting()} is false (the model is being built)
	 * and {@link ISimilarityProcessor#isInitialized()} is false (the model had not been initialized).
	 * @throws AmbitException
	 */
	public void buildInitialize() throws AmbitException ;
	/**
	 * executes if {@link ISimilarityProcessor#isPredicting()} is false (the model is being built)
	 * and when no more instances are expected. 
	 * @throws AmbitException
	 */
	public void buildCompleted() throws AmbitException ;
	/**
	 * 
	 * @return true if the model had been initialized
	 */
	public boolean isInitialized();
	/**
	 * 
	 * @param initialized
	 */
	public void setInitialized(boolean initialized);
	
	public String[] getProperties();
	
    public String getStatus(boolean verbose);
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) ;
	
}


