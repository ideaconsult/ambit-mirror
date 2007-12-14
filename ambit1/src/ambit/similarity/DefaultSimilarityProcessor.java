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

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitResult;
import ambit.processors.results.AbstractSimilarityMatrix;
import ambit.processors.results.LightSimilarityMatrix;
import ambit.stats.datastructures.Bin;

public abstract class DefaultSimilarityProcessor<T extends Comparable> extends DefaultAmbitProcessor implements ISimilarityProcessor {
    protected IModelStatistics<T> modelStatistics;
	protected String similarityProperty;
	protected String pairwiseSimilarityProperty;

	protected boolean initialized = false;
	protected boolean predicting = true;
	protected IAmbitResult summary = null;
	//protected SimilarityHistogram histogram;
	protected String trainingPrefix = "";
	
	public DefaultSimilarityProcessor() {
		super();
		/*
		histogram = new SimilarityHistogram("",getClass().getName());
        histogram.setTitle(toString());
        */
		setSimilarityProperty(getClass().getName());
		setPairwiseSimilarityProperty(getSimilarityProperty()+".pairwise");
        modelStatistics = null;
		
	}
	protected AbstractSimilarityMatrix createSimilarityMatrix() {
		return new LightSimilarityMatrix(this);
	}

	public void buildInitialize() throws AmbitException {
		setPredicting(false);
		setInitialized(true);

        
	}
	public void buildCompleted() throws AmbitException {
		setPredicting(true);
		//histogram.clear();
	}
	
	public void incrementalBuild(Object object) throws AmbitException {
		throw new AmbitException("Not implemented!");
	}

	public double predict(Object object) throws AmbitException {
		if (isPredicting())
			throw new AmbitException("Not implemented!");
		else 
			throw new AmbitException("Model not built yet! "+toString());
	}
	public double predict(Object object1, Object object2) throws AmbitException {
		throw new AmbitException("Not implemented!");
	}
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName,oldValue,newValue);
	}
	public void close() {
		try {
			if (isPredicting()) {
				propertyChangeSupport.firePropertyChange("Result",
		                null,
		                modelStatistics);
				//histogram = new SimilarityHistogram(toString(),getSimilarityProperty());
			} else buildCompleted();
			
		} catch (AmbitException x) {
			x.printStackTrace();
		}
		propertyChangeSupport.firePropertyChange("Result",
                null,
                this);

		super.close();
		
	}
	
	public boolean isPredicting() {
		return predicting;
	}
	public void setPredicting(boolean predicting) {
		this.predicting = predicting;
        
	}
	public IAmbitResult getResult() {
		return summary;
	}
	public void setResult(IAmbitResult result) {
		this.summary = result;
	}

	public Object process(Object object) throws AmbitException {
        if (object == null) return object;
		if (isPredicting()) {
			predict(object);
			//similarityMatrix.update(object);
			//histogram.update(object);
			return object;
		} else {
			try {
				if (!isInitialized()) buildInitialize();
				incrementalBuild(object);
				//similarityMatrix.update(object);
				return object;
			} catch (AmbitException x) {
				throw new AmbitException(x);
			}
		}

	}
	public boolean isInitialized() {
		return initialized;
	}
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}


	public String getPairwiseSimilarityProperty() {
		return pairwiseSimilarityProperty;
	}


	public void setPairwiseSimilarityProperty(String pairwiseSimilarityProperty) {
		this.pairwiseSimilarityProperty = pairwiseSimilarityProperty;
	}


	public String getSimilarityProperty() {
		return similarityProperty;
	}


	public void setSimilarityProperty(String similarityProperty) {
		this.similarityProperty = similarityProperty;
		//histogram.setSimilarityProperty(similarityProperty);
	}
	public String[] getProperties() {
		return new String[] {getTrainingPrefix() + "." + getSimilarityProperty()};
	}

	public String getTrainingPrefix() {
		return trainingPrefix;
	}
	public void setTrainingPrefix(String trainingPrefix) {
		this.trainingPrefix = trainingPrefix;
	}
    public String getStatus(boolean verbose) {
        if (modelStatistics == null) return "N/A";
        else return modelStatistics.toStatus(verbose);
    }
    public IAmbitEditor getEditor() {

        return new SimilarityProcessorEditor(this);
    }
	public IModelStatistics<T> getModelStatistics() {
		return modelStatistics;
	}
    
}


