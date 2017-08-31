package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.fragmentation.Fragmentation;

/**
 * 
 * @author nick1
 * This class is responsible for:
 *  1) the creation of statistical model
 *  based on the information in GroupContributionModel class.
 *  2) Statistical validation of the model 
 */

public class Learner 
{
	private GroupContributionModel model = null;
	private DataSet trainDataSet = null;
	private DataSet externalDataSet = null;
	
	private List<String> errors = new ArrayList<String>();
	
	public void reset()
	{
		errors.clear();
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public String getAllErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}

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
		Fragmentation.makeFragmentation(trainDataSet, model);
		
		makeInitialMatrixes();
		
		fragmentColumnStatistics();
		
		makeFinalMatricies();
		
		makeModel();
		
		//TODO
	}
	
	public void validate()
	{
		//TODO
	}
	
	void makeInitialMatrixes()
	{
		//TODO
	}
	
	void fragmentColumnStatistics()
	{
		//TODO
	}
	
	void makeFinalMatricies()
	{
		//TODO
	}
	
	void makeModel()
	{
		//TODO
	}
	
	
	
	
}
