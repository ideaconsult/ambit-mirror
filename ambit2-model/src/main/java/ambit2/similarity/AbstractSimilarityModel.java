package ambit2.similarity;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.processors.IAmbitResult;
import ambit2.model.AbstractModel;
import ambit2.model.IDataset;

public abstract class AbstractSimilarityModel<ID, Features, ResultType extends Comparable<?>> extends
		AbstractModel<ID, Features, ResultType> {
	
	protected IAmbitResult result;
	
	public IAmbitResult getResult() {
		return result;
	}

	public void setResult(IAmbitResult result) {
		this.result = result;
	}
	
	public void buildInitialize() throws AmbitException {
		built = false;
		if (result != null)
			result.clear();
		
	}
	public double getPairwise(IDataset<ID,Features,ResultType> dataset1,IDataset<ID,Features,ResultType> dataset2) throws AmbitException {
		return getPairwise(dataset1.getFeatures(),dataset2.getFeatures());
	}
	public abstract double getPairwise(Features features1, Features features2) throws AmbitException;	
	
	public void learn(IDataset<ID, Features, ResultType> dataset)
			throws AmbitException, UnsupportedOperationException {
		dataset.first();
		while (dataset.next()) {
			try {
				ResultType result = learn(dataset.getID(),
						dataset.getFeatures(),
						dataset.getObserved(),
						dataset.getPredicted());
				dataset.setPredicted(result);
			} catch (Exception x) {
				dataset.handleError(x);
			}
		}
		
	}	
}
