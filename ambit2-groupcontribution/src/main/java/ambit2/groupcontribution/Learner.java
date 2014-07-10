package ambit2.groupcontribution;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.dataset.DataSet;

public class Learner 
{
	private GroupContributionModel model = null;
	private DataSet trainDataSet = null;
	private DataSet externalDataSet = null;

	public GroupContributionModel getModel() {
		return model;
	}

	public void setModel(GroupContributionModel model) {
		this.model = model;
	}

	public DataSet getTrainDataSet() {
		return trainDataSet;
	}

	public void setTrainDataSet(DataSet trainDataSet) {
		this.trainDataSet = trainDataSet;
	}

	public DataSet getExternalDataSet() {
		return externalDataSet;
	}

	public void setExternalDataSet(DataSet externalDataSet) {
		this.externalDataSet = externalDataSet;
	}
	
	public void train()
	{
		//TODO
	}
	
	public void validate()
	{
		//TODO
	}
	
}
