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
	int fragColumnStatistics[];
	List<Integer> excludedColumns = new ArrayList<Integer>();
	List<Integer> usedColumns = new ArrayList<Integer>();
	
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
		initVariables();
		
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
	
	void validate()
	{
		//TODO
	}
	
	public void initVariables()
	{	
		excludedColumns.clear();
		usedColumns.clear();
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
		//Calculating Column statistics
		int n0 = A0.nColumns;
		int m = A0.nRows;
		fragColumnStatistics = new int[n0];
		for(int j = 0; j < n0; j++)
			fragColumnStatistics[j] = 0;

		for(int i = 0; i < m; i++)
			for(int j = 0; j < n0; j++)
			{
				if (A0.el[i][j]>0)
					fragColumnStatistics[j]++;
			}
	}
	
	void makeFinalMatricies()
	{
		Double threshold = model.getColStatPercentageThreshold();
		
		if (threshold == null)
		{	
			A=A0;
			b=b0;
			return;
		}
		
		int n0 = A0.nColumns;
		int m = A0.nRows;
		
		for(int j = 0; j < n0; j++)
		{	
			if (((double)fragColumnStatistics[j]/m) < threshold)
            		excludedColumns.add(j);
            else
            		usedColumns.add(j);
		}
		
		//Determining the dimensions of matrixes:  A, C, x
		int n = usedColumns.size();		
		
		//if (useExternalDescriptors)
		//	n = n + model.descriptorNames.size();

		if (n==0)
		{
			errors.add("No fragments left after filtration!");
			return;
		}
		
		b=b0;
		A = new MatrixDouble(m,n);
		//C = new MatrixDouble(n,n);
		//invC = new MatrixDouble(n,n);
		//x = new MatrixDouble(n,1);

		int col;

		//Filling matrix A and modFragCodeIndex 
		for(int j = 0; j < usedColumns.size(); j++)
		{
			col = usedColumns.get(j);
			A.copyColumnFrom(j,A0,col);
			//modFragCodeIndex.put(indexFragCode.get(usedColumns.get(j)),new Integer(j));
		}

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
