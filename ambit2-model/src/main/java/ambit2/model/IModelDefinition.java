package ambit2.model;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.featureselection.IAttributeSelection;


/**
 * Defines a model, derived from objects of type FeatureType
 * @author nina
 *
 * 
 */
public interface IModelDefinition<ID,Features,ResultType extends Comparable<?>> {
	Object getParameter(Object name);
	void setParameter(Object name,Object value);	
	boolean isBuilt();
	
	void learn(IDataset<ID,Features,ResultType> dataset) throws AmbitException, UnsupportedOperationException;
	ResultType learn(ID id,Features features,ResultType observed,ResultType predicted) throws AmbitException, UnsupportedOperationException;
	
	void predict(IDataset<ID,Features,ResultType> dataset,IModelStatistics<ResultType> stats) throws AmbitException;	
	ResultType predict(ID id,Features features,ResultType observed,ResultType predicted, IModelStatistics<ResultType> stats) throws AmbitException;
	
	public IAttributeSelection<Features> getAttributeSelection();
	public void setAttributeSelection(IAttributeSelection<Features> attributeSelection);
		
}
