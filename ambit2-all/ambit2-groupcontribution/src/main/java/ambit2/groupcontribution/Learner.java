package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.groups.IGroup;
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
	
	List<Integer> excludedGroupColumns = new ArrayList<Integer>();
	List<Integer> usedGroupColumns = new ArrayList<Integer>();
	String initialColumnGroups[] = null;
	//Map<Integer,String> indexGroupMap = new HashMap<Integer,String>();
	//Map<String,Integer> groupIndexMap = new HashMap<String,Integer>();
	
	
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
		GCMReportConfig repCfg = model.getReportConfig();
		
		initVariables();
		
		Fragmentation.makeFragmentation(trainDataSet, model);
		if (!errors.isEmpty())
			return 1;
		
		if (repCfg.reportGroups)
			reportGroups();
		
		makeInitialMatrixes();
		if (!errors.isEmpty())
			return 2;
		
		fragmentColumnStatistics();
		if (!errors.isEmpty())
			return 3;
		
		makeFinalMatricies();
		if (!errors.isEmpty())
			return 4;
		
		if (repCfg.reportMatrices)
			reportMatrices();
		
		makeModel();
		if (!errors.isEmpty())
			return 5;
		
		if (repCfg.reportContributions)
			reportContributions();
		//System.out.println("Matrix x");
		//System.out.println(x.toString(9,4));
		
		return 0;
	}
	
	void validate()
	{
		//TODO
	}
	
	public void initVariables()
	{	
		excludedGroupColumns.clear();
		usedGroupColumns.clear();
	}	
	
	void makeInitialMatrixes()
	{
		A0 = Fragmentation.generateFragmentationMatrix(trainDataSet, model);
		
		//Setting initialColumnGroups
		Map<String,IGroup> groups = model.getGroups();
		int n = groups.keySet().size();
		initialColumnGroups = groups.keySet().toArray(new String[n]);
		
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
            		excludedGroupColumns.add(j);
            else
            		usedGroupColumns.add(j);
		}
		
		//Determining the dimensions of matrixes:  A, C, x
		int n = usedGroupColumns.size();		
		
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

		//Filling matrix A  
		for(int j = 0; j < usedGroupColumns.size(); j++)
		{
			col = usedGroupColumns.get(j);
			A.copyColumnFrom(j,A0,col);
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
			
			//Set group contributions
			Map<String,IGroup> groups = model.getGroups();
			for (int i = 0; i < usedGroupColumns.size(); i++)
			{
				int col = usedGroupColumns.get(i);
				String key = initialColumnGroups[col];
				IGroup g = groups.get(key);
				g.setContribution(x.el[i][0]);
				g.setMissing(false);
			}
		}
		else
		{
			errors.add("There is linear dependency in the data !!! "+
					"The model can not be built!");
			return(-1);
		}		
		return(0);
	}
	
	//----------- report utils-------------
	
	void reportGroups()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		
		String grps = model.getGroupsAsString();
		if (repCfg.FlagConsoleOutput)
		{	
			System.out.println("Groups:");
			System.out.println(grps);
		}
		if (repCfg.FlagBufferOutput)
		{	
			model.addToReport("Groups:\n");
			model.addToReport(grps);
			model.addToReport("\n");
		}
	}
	
	void reportMatrices()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		
		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Matrix A0");
			System.out.println(A0.toString(3,0));
			System.out.println("Matrix A");
			System.out.println(A.toString(3,0));
			System.out.println("Matrix b0");
			System.out.println(b0.toString(3,3));
		}
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Matrix A0\n");
			model.addToReport(A0.toString(3,0) + "\n");			
			model.addToReport("Matrix A\n");
			model.addToReport(A.toString(3,0) + "\n");			
			model.addToReport("Matrix b0\n");
			model.addToReport(b0.toString(3,3) + "\n");
		}
	}
	
	void reportContributions()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		Map<String,IGroup> groups = model.getGroups();
		
		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Group contibutions:");
			Set<String> keys = groups.keySet();
			for (String key : keys)
			{
				IGroup g = groups.get(key);
				System.out.println("\t" + key + "\t" 
						+ g.getContribution() + (g.isMissing()?"  missing":""));
			}
		}
		
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Group contibutions:\n");
			Set<String> keys = groups.keySet();
			for (String key : keys)
			{
				IGroup g = groups.get(key);
				model.addToReport("\t" + key + "\t" 
						+ g.getContribution() + (g.isMissing()?"  missing":"") + "\n");
			}
		}
	}
}
