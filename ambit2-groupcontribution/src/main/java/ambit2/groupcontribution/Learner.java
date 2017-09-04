package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.utils.math.MathUtilities;
import ambit2.groupcontribution.utils.math.MatrixDouble;

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
	private double epsilon = 1.0e-15;
	
	//Work matrices
	MatrixDouble A, A0, b, b0, modeled_b, C, invC, x;
	
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
	
	public int train()
	{
		Fragmentation.makeFragmentation(trainDataSet, model);
		if (!errors.isEmpty())
			return 1;
		System.out.println("Groups:");
		System.out.println(model.getGroupsAsString());
		
		makeInitialMatrixes();
		if (!errors.isEmpty())
			return 2;
		
		System.out.println("Matrix A0");
		System.out.println(A0.toString(3,0));
		System.out.println("Matrix b0");
		System.out.println(b0.toString(3,0));
		
		fragmentColumnStatistics();
		if (!errors.isEmpty())
			return 3;
		
		makeFinalMatricies();
		if (!errors.isEmpty())
			return 4;
		
		makeModel();
		if (!errors.isEmpty())
			return 5;
		
		System.out.println("Matrix x");
		System.out.println(x.toString(9,4));
		
		return 0;
	}
	
	public void validate()
	{
		//TODO
	}
	
	void makeInitialMatrixes()
	{
		A0 = Fragmentation.generateFragmentationMatrix(trainDataSet, model);
		try
		{
			b0 = Fragmentation.generatePropertyMatrix(trainDataSet, model.getTargetEndpoint());
		}
		catch(Exception e)
		{
			errors.add("Error while generating target endpoint column-matrix" + e.getMessage());
		}
	}
	
	void fragmentColumnStatistics()
	{
		//TODO
	}
	
	void makeFinalMatricies()
	{
		A=A0;
		b=b0;
		//TODO
	}
	
	int  makeModel()
	{
		//Calculating of matrix C = A'A
		C=MathUtilities.Multiply(A.transposed(), A);

		//Calculating of invC and output matrix x
		invC = C.inverse(epsilon);

		if (invC != null)
		{
			MatrixDouble invC_A_transposed =  MathUtilities.Multiply(invC, A.transposed());
			x = MathUtilities.Multiply(invC_A_transposed , b);
		}
		else
		{
			errors.add("There is linear dependency in the data !!! "+
					"The model can not be built!");
			return(-1);
		}		
		return(0);
	}
	
}
