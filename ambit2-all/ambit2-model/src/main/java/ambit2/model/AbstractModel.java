package ambit2.model;

import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.logging.Logger;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.featureselection.IAttributeSelection;

public abstract class AbstractModel<ID,Features,ResultType extends Comparable<?>> implements 
					IModelDefinition<ID, Features, ResultType> {
	
	protected final String p_model = IModelDefinition.class.getName();
	protected final String p_stats = IModelStatistics.class.getName();
	protected Logger logger = Logger.getLogger(getClass().getName());
	protected Hashtable<Object,Object> parameters = new Hashtable<Object, Object>();
	protected PropertyChangeSupport ps = new PropertyChangeSupport(this);
	protected IModelStatistics<ResultType> stats = null;
	protected IAttributeSelection<Features> attributeSelection;
	protected boolean built = false;
	
	public IModelStatistics<ResultType> getStats() {
		return stats;
	}
	
	public void setStats(IModelStatistics<ResultType> stats) {
		IModelStatistics<ResultType> oldstats = this.stats;
		ps.firePropertyChange(p_stats, oldstats,stats);
		this.stats = stats;			
	}

	public Object getParameter(Object name) {
		return parameters.get(name);
	}

	public boolean isBuilt() {
		return built;
	}
	
	public void setParameter(Object name, Object value) {
		if (value == null) 
			parameters.remove(name);
		else
			parameters.put(name,value);
		
	}

	public void predict(IDataset<ID, Features, ResultType> dataset,IModelStatistics<ResultType> stats)
			throws AmbitException {
		if (!isBuilt()) throw new AmbitException("Model not built yet!");
		dataset.first();
		while (dataset.next()) {
			try {
				ResultType result = predict(dataset.getID(),dataset.getFeatures(),dataset.getObserved(),dataset.getPredicted(),stats);
				dataset.setPredicted(result);
			} catch (Exception x) {
				dataset.handleError(x);
			}
		}
		
	}
	public IAttributeSelection<Features> getAttributeSelection() {
		return attributeSelection;
	}

	public void setAttributeSelection(IAttributeSelection<Features> attributeSelection) {
		this.attributeSelection = attributeSelection;
	}	
}
