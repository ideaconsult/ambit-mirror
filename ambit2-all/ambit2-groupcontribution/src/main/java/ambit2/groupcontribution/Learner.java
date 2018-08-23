package ambit2.groupcontribution;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ambit2.groupcontribution.correctionfactors.DescriptorInfo;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.ArrayUtils;
import ambit2.groupcontribution.utils.math.CrossValidation;
import ambit2.groupcontribution.utils.math.MathUtilities;
import ambit2.groupcontribution.utils.math.MatrixDouble;
import ambit2.groupcontribution.utils.math.Statistics;
import ambit2.groupcontribution.utils.math.ValidationConfig;

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
	private String endline = "\n";
	
	//Work matrices: 
	//A0 - initial fragment frequencies, D0/D - initial final descriptors 
	//A - final matrix for modeling combining all info
	//b0/b - target property matrix
	MatrixDouble A = null;
	MatrixDouble A0 = null;
	MatrixDouble D0 = null;
	MatrixDouble D = null;
	MatrixDouble b = null;
	MatrixDouble b0 = null;
	MatrixDouble modeled_b = null;
	MatrixDouble C = null;
	MatrixDouble invC = null;
	MatrixDouble x = null;
	MatrixDouble x_sd = null;
		
	int fragColumnStatistics[];
	
	List<Integer> excludedGroupColumns = new ArrayList<Integer>();
	List<Integer> usedGroupColumns = new ArrayList<Integer>();
	List<Integer> excludedDescriptors = new ArrayList<Integer>();
	List<Integer> usedDescriptors = new ArrayList<Integer>();
	
	String initialColumnGroups[] = null;
	//Map<Integer,String> indexGroupMap = new HashMap<Integer,String>();
	//Map<String,Integer> groupIndexMap = new HashMap<String,Integer>();
	
	ArrayUtils arrayUtils = new ArrayUtils();
	
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
		
		
		return 0;
	}
	
	public int performFragmentationOnly()
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
		
		if (repCfg.reportMatrices)
			reportFragmentationMatrixAndDescriptors();
		
		return 0;
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
		
		if (model.getDescriptors() != null)
		{	
			List<String> descriptors = new ArrayList<String>();
			List<DescriptorInfo> diList = model.getDescriptors();
			for (int i = 0; i < model.getDescriptors().size(); i++)
				descriptors.add(diList.get(i).getName());
			try
			{
				D0 = Fragmentation.generateMultiplePropertyMatrix(trainDataSet, descriptors);
			}
			catch(Exception e)
			{
				errors.add("Error while generating descriptor matrix " + e.getMessage());
			}
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
		
		if (D0 != null)
		{	
			D = D0; 	
			//TODO filter D0 
			for (int i = 0; i < D.nColumns; i++)
				usedDescriptors.add(i);
			n = n + D.nColumns; 
		}	

		if (n==0)
		{
			errors.add("No fragments/descriptors left after filtration!");
			return;
		}
		
		b=b0;
		A = new MatrixDouble(m,n);
		
		
		//Filling matrix A  
		int col;
		for(int j = 0; j < usedGroupColumns.size(); j++)
		{
			col = usedGroupColumns.get(j);
			A.copyColumnFrom(j,A0,col);
		}
		
		if (D != null)		
		{
			int nGroupColumns = usedGroupColumns.size();
			for (int j = 0; j < D.nColumns; j++)
				A.copyColumnFrom(nGroupColumns + j, D, j);
		}		
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
			
			//TODO calculate x_sd
			
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
			
			//Set descriptor contributions
			int offset = usedGroupColumns.size();
			List<DescriptorInfo> diList = model.getDescriptors();
			for (int i = 0; i < usedDescriptors.size(); i++)
			{
				int dIndex = usedDescriptors.get(i);
				DescriptorInfo di = diList.get(dIndex);
				di.setContribution(x.el[offset+i][0]);
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
	
	double modelValue(int objNum, MatrixDouble matrix_A, MatrixDouble matrix_x)
	{
		double res = 0.0;
		for (int i = 0; i < matrix_x.nRows; i++)
			res += matrix_A.el[objNum][i] * matrix_x.el[i][0];
		return(res);
	}
	
	void makePartialMatrices()
	{
		
	}
	
	MatrixDouble  makePartialModel(MatrixDouble matrix_A, MatrixDouble matrix_b)
	{
		//Calculating of matrix C = A'A
		MatrixDouble mC=MathUtilities.Multiply(matrix_A.transposed(), matrix_A);

		//Calculating of invC and output matrix x
		MatrixDouble inv_mC = mC.inverse(epsilon);

		if (inv_mC != null)
		{
			MatrixDouble invC_A_transposed =  
					MathUtilities.Multiply(inv_mC, matrix_A.transposed());
			MatrixDouble matrix_x = MathUtilities.Multiply(invC_A_transposed , matrix_b);
			return matrix_x;
		}
		
		return null;
	}
	
	public void validate()
	{
		ValidationConfig validation = model.getValidationConfig(); 
		
		if (validation.selfTest)
			performSelfTest(validation);
		
		if (validation.yScramblingIterations > 0)
			performYScrambling(validation.yScramblingIterations);
		
		if (validation.leaveOneOutValidation)
			performLOOValidation();
		
		if (validation.crossValidation != null)
			performCrossValidation(validation.crossValidation);
		
		//TODO bootstrap, external test
	}
	
	public void performSelfTest(ValidationConfig validation)
	{
		GCMReportConfig repCfg = model.getReportConfig();
		DecimalFormat df = getDecimalFormat(repCfg.fractionDigits);
		int n = A.nRows;
		
		if (validation.selfTest)
		{
			if (repCfg.FlagConsoleOutput)
				System.out.println("Self test validation:" + endline 
						+ "----------------------------");
			if (repCfg.FlagBufferOutput)
				model.addToReport("Self test validation:" + endline
						+ "----------------------------" + endline);
			
			MatrixDouble modeled_b = new MatrixDouble(n,1);
			String out_s;
			for (int i = 0; i < n; i++)
			{	
				modeled_b.el[i][0] = modelValue(i, A,x);
				
				if (validation.verboseReport)
				{	
					double diff = modeled_b.el[i][0] - b.el[i][0];
					out_s = "#" + (i+1) + "   " 
							+ df.format(b.el[i][0]) + " " + df.format(modeled_b.el[i][0])
							+ "  diff = " + df.format(diff) + endline;

					if (repCfg.FlagConsoleOutput)
						System.out.print(out_s);
					if (repCfg.FlagBufferOutput)
						model.addToReport(out_s);
				}
			}
			
			double r = Statistics.corrleationCoefficient(b, modeled_b);
			double r2 = r*r;
			double R2 = Statistics.getR2(b, modeled_b);
			double rmse = Statistics.rmsError(b, modeled_b);
			double mae = Statistics.meanAbsoluteError(b, modeled_b);
			
			out_s = "r^2  = " + df.format(r2) + "  (PPMC)" + endline +
					"R^2  = " + df.format(R2) + "  (1-RSS/TSS)" + endline +
					"RMSE = " + df.format(rmse) + endline + 
					"MAE  = " + df.format(mae) + endline + endline;
			if (repCfg.FlagConsoleOutput)
				System.out.print(out_s);
			if (repCfg.FlagBufferOutput)
				model.addToReport(out_s);
		}
	}
	
	public void performYScrambling(int numIterations)
	{	
		GCMReportConfig repCfg = model.getReportConfig();
		if (repCfg.FlagConsoleOutput)
			System.out.println("Y-scrambling validation:" + endline 
					+ "----------------------------");
		if (repCfg.FlagBufferOutput)
			model.addToReport("Y-scrambling validation:" + endline
					+ "----------------------------" + endline);
		
		
		double r2Values[] = new double [numIterations];
		int m = A.nRows;
		MatrixDouble b_scrambled = new MatrixDouble(m,1);
		MatrixDouble model_b_scrambled = new MatrixDouble(m,1);
		MatrixDouble invC_A_transposed =  MathUtilities.Multiply(invC, A.transposed());
		
		for (int i = 0; i < numIterations; i++)
		{	
			//Scrambling the experimental values
			int p[] = arrayUtils.getRandomPermutation(m);
			for (int k = 0; k < m; k++)
				b_scrambled.el[k][0] = b.el[p[k]][0]; 
			
			//Making scrambled model			
			MatrixDouble x_scrambled = MathUtilities.Multiply(invC_A_transposed, b_scrambled);
			for (int k = 0; k < m; k++)
				model_b_scrambled.el[k][0] = modelValue(k, A, x_scrambled);
			
			double R2 = Statistics.getR2(b_scrambled, model_b_scrambled); 
			r2Values[i] = R2;
			//System.out.println("iteration #" + i + "  " + R2);
		}
		
		double R2_mean = Statistics.mean(r2Values);
		String out_s = "R^2_YS_average = " + R2_mean 
				+ "    (" + numIterations + " scramblings)" 
				+ endline + endline;
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
	}
	
	public void performLOOValidation()
	{	
		GCMReportConfig repCfg = model.getReportConfig();
		
		String out_s = "LOO Validation  /Leave-One-Out/ " + endline 
						+ "-----------------------------------" + endline;
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
		
		DecimalFormat df = getDecimalFormat(repCfg.fractionDigits);
		List<Double> modVals = new ArrayList<Double>();
		List<Double> expVals = new ArrayList<Double>();
		int m = A.nRows;
		int n = A.nColumns;
		
		double model_value;
		double diff;
		
		int testObjIndices[] = new int[1];
		
		for (int i = 0; i < m; i ++)
		{
			testObjIndices[0] = i;  //the i-th object is excluded
			MatrixDouble matr[] = CrossValidation.makeValidationModelMatrices(testObjIndices, A, b, null);
			MatrixDouble mA = matr[0];
			MatrixDouble mb = matr[1];
			
			MatrixDouble matrix_x = makePartialModel(mA, mb);
			if (matrix_x == null)
			{
				System.out.println("Validation #"+(i+1)+" SINGULARITY");
				continue;
			}
						
			//Calculate model for the excluded object i
			model_value = 0;
			for (int k = 0; k < n; k++)
				model_value += A.el[i][k] * matrix_x.el[k][0];
			
			diff = model_value - b.el[i][0];
			//registerError(diff,i);
			modVals.add(new Double(model_value));
			expVals.add(new Double(b.el[i][0]));
			
			if (model.getValidationConfig().verboseReport)
			{	
				out_s = "LOO #" + (i+1) + "  " + df.format(b.el[i][0]) + "  "+ df.format(model_value)
					+ "      diff = " + df.format(diff) + endline;
				if (repCfg.FlagConsoleOutput)
					System.out.print(out_s);
				if (repCfg.FlagBufferOutput)
					model.addToReport(out_s);
			}
		}	
		
		double rmsError = Statistics.rmsError(expVals, modVals);
		double mae = Statistics.meanAbsoluteError(expVals, modVals);
		double corCoeff = Statistics.corrleationCoefficient(expVals, modVals);
		double concordCorCoeff = Statistics.getConcordanceCorrelationCoefficient(expVals, modVals);
		
		MatrixDouble model_b0 = new MatrixDouble(modVals);
		MatrixDouble exp_b0 = new MatrixDouble(expVals);  //This matrix is recreated instead of using b0 because some rows might be missing because of singularity
		double Q2_LOO = Statistics.getR2(exp_b0, model_b0);
		
		
		out_s =	"RMSE = "+df.format(rmsError) + endline
				+ "MAE = "+df.format(mae) + endline
				+ "R^2  = "+df.format(corCoeff*corCoeff)  + "   (PPMC)" + endline
				+ "Rc^2 = "+df.format(concordCorCoeff*concordCorCoeff)  + "   (concordance cor. coef.)" + endline
				+ "Q^2  = "+df.format(Q2_LOO) + endline + endline;
		
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
	}
	
	public void performCrossValidation(CrossValidation cv)
	{	
		GCMReportConfig repCfg = model.getReportConfig();
		
		String out_s = "Cross validation " + endline 
						+ "-----------------------------------" + endline;
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
		
		for (int cycle = 0; cycle < cv.numCycles; cycle++)
		{	
			double params[] = crossValidation(cv.numFolds);
		}	
	}	
	
	double[] crossValidation(int nFolds)
	{
		int m = A0.nRows;
		int p[] = arrayUtils.getRandomPermutation(m);
		int fsize = m / nFolds;
		
		for (int k = 0; k<nFolds; k++)
		{
			//fold: indices m1,...,m2-1
			int m1 = k * fsize;
			int m2 = (k+1) * fsize;
			if (m2 >= m)
				m2 = m;
			int testObjIndices[] = new int[m2-m1];
			for (int i=m1; i<m2; i++)
				testObjIndices[i-m1] = p[i];
			
			//MatrixDouble mod_matr[] = CrossValidation
			
		}
		
		return null;
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
			if (D0 != null)
			{	
				System.out.println("Matrix D0");				
				System.out.println(D0.toString(3,3));	
			}	
			System.out.println("Matrix A");
			System.out.println(A.toString(3,0));
			System.out.println("Matrix b0");
			System.out.println(b0.toString(3,3));
		}
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Matrix A0\n");
			model.addToReport(A0.toString(3,0) + "\n");	
			if (D0 != null)
			{	
				model.addToReport("Matrix D0\n");
				model.addToReport(D0.toString(3,3) + "\n");
			}	
			model.addToReport("Matrix A\n");
			model.addToReport(A.toString(3,0) + "\n");			
			model.addToReport("Matrix b0\n");
			model.addToReport(b0.toString(3,3) + "\n");
		}
	}
	
	void reportFragmentationMatrixAndDescriptors()
	{
		GCMReportConfig repCfg = model.getReportConfig();

		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Matrix A0");
			System.out.println(A0.toString(3,0));
			if (D0 != null)
			{	
				System.out.println("Matrix D0");				
				System.out.println(D0.toString(3,3));	
			}
		}
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Matrix A0\n");
			model.addToReport(A0.toString(3,0) + "\n");	
			if (D0 != null)
			{	
				model.addToReport("Matrix D0\n");
				model.addToReport(D0.toString(3,3) + "\n");
			}
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
			
			List<DescriptorInfo> diList = model.getDescriptors();
			if (diList != null)
			{	
				System.out.println("Descriptor contibutions:");
				for (int i = 0; i < diList.size(); i++)
				{	
					System.out.println("\t" + diList.get(i).getName() + "\t" 
							+ diList.get(i).getContribution());
				}
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
			
			List<DescriptorInfo> diList = model.getDescriptors();
			if (diList != null)
			{	
				model.addToReport("Descriptor contibutions:\n");
				for (int i = 0; i < diList.size(); i++)
				{	
					model.addToReport("\t" + diList.get(i).getName() + "\t" 
							+ diList.get(i).getContribution() + "\n");
				}
			}
		}
	}
	
	DecimalFormat getDecimalFormat(int digits)
	{
		DecimalFormat df = new DecimalFormat(" ##0.0000;-##0.0000");;
		if (digits >= 0)	
		{	
			//Setting custom format
			df.setMinimumFractionDigits(digits);
			df.setMaximumFractionDigits(digits);			
		}		
		return df;
	}
}
