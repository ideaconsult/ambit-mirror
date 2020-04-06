package ambit2.groupcontribution;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.correctionfactors.DescriptorInfo;
import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.ArrayUtils;
import ambit2.groupcontribution.utils.math.CrossValidation;
import ambit2.groupcontribution.utils.math.MathUtilities;
import ambit2.groupcontribution.utils.math.MatrixDouble;
import ambit2.groupcontribution.utils.math.Statistics;
import ambit2.groupcontribution.utils.math.ValidationConfig;
import ambit2.smarts.SmartsHelper;

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
	
	private int matrixDig = 5;
	private int matrixDecDig = 3;
	
	//Work matrices: 
	//A0 - initial fragment frequencies, D0/D - initial/final descriptors 
	//Cf0/Cf - initial/final correction factors
	//A - final matrix for modeling combining all info
	//b0/b - target property matrix
	MatrixDouble A = null;
	MatrixDouble A0 = null;
	MatrixDouble D0 = null;
	MatrixDouble D = null;
	MatrixDouble Cf0 = null;
	MatrixDouble Cf = null;
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
	List<Integer> usedCorrectionFactors = new ArrayList<Integer>();
	
	
	String initialColumnGroups[] = null;
	//Map<Integer,String> indexGroupMap = new HashMap<Integer,String>();
	//Map<String,Integer> groupIndexMap = new HashMap<String,Integer>();
	
	ArrayUtils arrayUtils = new ArrayUtils();
	
	DecimalFormat df2 = getDecimalFormat(2);
	
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
			
	public int getMatrixDig() {
		return matrixDig;
	}

	public void setMatrixDig(int matrixDig) {
		this.matrixDig = matrixDig;
	}

	public int getMatrixDecDig() {
		return matrixDecDig;
	}

	public void setMatrixDecDig(int matrixDecDig) {
		this.matrixDecDig = matrixDecDig;
	}

	public int train()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		
		initVariables();
		
		Fragmentation.makeFragmentation(trainDataSet, model);
		if (trainDataSet.nErrors > 0)
		{	
			reportDataSetErrors(trainDataSet);
			return 1;
		}	
		
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
		if (trainDataSet.nErrors > 0)
		{	
			reportDataSetErrors(trainDataSet);
			return 1;
		}	
		
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
		
		if (!model.getCorrectionFactors().isEmpty())
			Cf0 = Fragmentation.generateCorrectionFactorMatrix(trainDataSet, model);
				
		//Setting initialColumnGroups
		Map<String,IGroup> groups = model.getGroups();
		int n = groups.keySet().size();
		initialColumnGroups = groups.keySet().toArray(new String[n]);
				
		try
		{
			b0 = Fragmentation.generatePropertyMatrix(trainDataSet, model.getTargetProperty());
		}
		catch(Exception e)
		{
			errors.add("Error while generating target endpoint column-matrix: " + e.getMessage());
		}
		
		if (model.getDescriptors() != null)
		{	
			//List<String> descriptors = new ArrayList<String>();
			List<DescriptorInfo> diList = model.getDescriptors();
			//for (int i = 0; i < model.getDescriptors().size(); i++)
			//	descriptors.add(diList.get(i).getName());
			try
			{
				//D0 = Fragmentation.generateMultiplePropertyMatrix(trainDataSet, descriptors);
				D0 = Fragmentation.generateMultiplePropertyMatrix2(trainDataSet, diList);
			}
			catch(Exception e)
			{
				errors.add("Error while generating descriptor matrix: " + e.getMessage());
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
		
		if (Cf0 != null)
		{
			Cf = Cf0;
			//TODO filter Cf
			for (int i = 0; i < Cf.nColumns; i++)
				usedCorrectionFactors.add(i);
			n = n + Cf.nColumns;
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
			for (int j = 0; j < D.nColumns; j++)  //TODO use usedDescriptors
				A.copyColumnFrom(nGroupColumns + j, D, j);
		}
		
		if (Cf != null)
		{
			int ind0 = usedGroupColumns.size() + usedDescriptors.size();
			for (int j = 0; j < Cf.nColumns; j++) //TODO use usedCorrectionFactors
				A.copyColumnFrom(ind0 + j, Cf, j);
		}
	}
	
	void calculate_x_sd()
	{
		//modeled_b values must be determined
		//An estimations of the Variation-covariation matrix of the model parameters x 
		//is given by V(x) = (Se^2)/(m-n).inv(A'.A) = (Se^2)/(m-n).invC
		//where Se^2 = e1^2 + e2^2 + ... + em^2
		//Confidence interval of parameter is [x - t.V(x), x + t.V(x)]
		
		int m = b.nRows;
		int n = x.nRows;
		MatrixDouble modeled_b = new MatrixDouble(m,1);
		
		for (int i = 0; i < m; i++)
			modeled_b.el[i][0] = modelValue(i, A, x);
		
		double Se2 = 0;
		for (int i = 0; i < m; i++)
			Se2 += (b.el[i][0]-modeled_b.el[i][0])*(b.el[i][0]-modeled_b.el[i][0]);

		x_sd = new MatrixDouble(n,1);
		for (int i = 0; i < n; i++)
			x_sd.el[i][0] = Math.sqrt( (Se2/(m-n))*invC.el[i][i] );
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
			
			calculate_x_sd();
			
			//Set group contributions
			Map<String,IGroup> groups = model.getGroups();
			for (int i = 0; i < usedGroupColumns.size(); i++)
			{
				int col = usedGroupColumns.get(i);
				String key = initialColumnGroups[col];
				IGroup g = groups.get(key);
				g.setContribution(x.el[i][0]);
				g.setSD(x_sd.el[i][0]);
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
				di.setSD(x_sd.el[offset+i][0]);
			}
			
			//Set correction factors
			offset = usedGroupColumns.size() + usedDescriptors.size();
			List<ICorrectionFactor> cfList = model.getCorrectionFactors();
			for (int i = 0; i < usedCorrectionFactors.size(); i++)
			{
				int cfIndex = usedCorrectionFactors.get(i);
				ICorrectionFactor cf = cfList.get(cfIndex);
				cf.setContribution(x.el[offset+i][0]);
				cf.setSD(x_sd.el[offset+i][0]);
			}
		}
		else
		{
			errors.add("There is linear dependency in the data! "+
					"The model can not be built!\n"
					+ "Linear dependency might be caused:\n"
					+ "  -there are too few chemical objects\n"
					+ "  -the number of fragments is too big due to the combinatorial explosion of high order scheme and too many local descriptors\n"
					+ "  -there are some very rare fragments, functional groups or 'exotic' molecules\n"
					+ "  -the order of the additive scheme is higher than what is needed\n"
					+ "  -corresction factors or external descriptors are colinear with fragment columns\n"
					+ "Rare fragment columns can be cleared by setting appropriate (higher) filtration threshold values (-r option)" );
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
			
			out_s = "r^2  = " + df.format(r2) + "  (PPMCC)" + endline +
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
				+ "R^2  = "+df.format(corCoeff*corCoeff)  + "   (PPMCC)" + endline
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
	
	public void performExternalValidation()
	{	
		if (externalDataSet == null)
			return;
		
		GCMReportConfig repCfg = model.getReportConfig();
		model.setAllowGroupRegistration(false);
		
		String out_s = "External Validation" + endline 
						+ "-----------------------------------" + endline;
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
		
		DecimalFormat df = getDecimalFormat(repCfg.fractionDigits);
		List<Double> modVals = new ArrayList<Double>();
		List<Double> expVals = new ArrayList<Double>();
		MatrixDouble exp_matrix = null;
				
		double exp_value;
		double model_value;
		double diff;
		
		try
		{
			exp_matrix = Fragmentation.generatePropertyMatrix(externalDataSet, model.getTargetProperty());
		}
		catch(Exception e)
		{
			errors.add("Error while generating target endpoint column-matrix: " + e.getMessage());
			return;
		}
				
		for (int i = 0; i < externalDataSet.dataObjects.size(); i++)
		{			
			DataSetObject dso = externalDataSet.dataObjects.get(i);
			//System.out.println("" + i + "  " + dso.molecule.getAtomCount());
			model_value = model.calcModelValue(dso, true);
			exp_value = exp_matrix.el[i][0];
			diff = model_value - exp_value;
			
			modVals.add(model_value);
			expVals.add(exp_value);
			
			if (model.getValidationConfig().verboseReport)
			{	
				out_s = "#" + (i+1) + "  " + df.format(exp_value) + "  "+ df.format(model_value)
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
		
		MatrixDouble model_matrix = new MatrixDouble(modVals);		
		double Q2_external = Statistics.getR2(exp_matrix, model_matrix);
		
		
		out_s =	"RMSE = "+df.format(rmsError) + endline
				+ "MAE = "+df.format(mae) + endline
				+ "R^2  = "+df.format(corCoeff*corCoeff)  + "   (PPMCC)" + endline
				+ "Rc^2 = "+df.format(concordCorCoeff*concordCorCoeff)  + "   (concordance cor. coef.)" + endline
				+ "Q^2  = "+df.format(Q2_external) + endline + endline;
		
		if (repCfg.FlagConsoleOutput)
			System.out.print(out_s);
		if (repCfg.FlagBufferOutput)
			model.addToReport(out_s);
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
		String cfs = null;
		if (!model.getCorrectionFactors().isEmpty())
			cfs = model.getCorrectionFactorsAsString();
		
		if (repCfg.FlagConsoleOutput)
		{	
			System.out.println("Groups:");
			System.out.println(grps);
			if (!model.getCorrectionFactors().isEmpty())
			{	
				System.out.println("Correction factors:");				
				System.out.println(cfs);
			}
		}
		if (repCfg.FlagBufferOutput)
		{	
			model.addToReport("Groups:\n");
			model.addToReport(grps);
			if (!model.getCorrectionFactors().isEmpty())
			{				
				model.addToReport("Correction factors:\n");				
				model.addToReport(cfs);
			}
			model.addToReport("\n");
		}
	}
	
	void reportMatrices()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		
		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Matrix A0  (group counts)");
			System.out.println(A0.toString(3,0));
			if (D0 != null)
			{	
				System.out.println("Matrix D0  (descriptors)");				
				System.out.println(D0.toString(matrixDig,matrixDecDig));	
			}
			if (Cf0 != null)
			{	
				System.out.println("Matrix Cf0  (correction factors)");				
				System.out.println(Cf0.toString(matrixDig,matrixDecDig));	
			}			
			System.out.println("Matrix A  (A0,D0,Cf0 - filtered and united)");
			System.out.println(A.toString(matrixDig,matrixDecDig));
			System.out.println("Matrix b0  (target property)");
			System.out.println(b0.toString(matrixDig,matrixDecDig));
		}
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Matrix A0  (group counts)\n");
			model.addToReport(A0.toString(3,0) + "\n");	
			if (D0 != null)
			{	
				model.addToReport("Matrix D0  (descriptors)\n");
				model.addToReport(D0.toString(matrixDig,matrixDecDig) + "\n");
			}
			if (Cf0 != null)
			{	
				model.addToReport("Matrix Cf0  (correction factors)\n");
				model.addToReport(Cf0.toString(matrixDig,matrixDecDig) + "\n");
			}
			model.addToReport("Matrix A  (A0,D0,Cf0 - filtered and united)\n");
			model.addToReport(A.toString(matrixDig,matrixDecDig) + "\n");			
			model.addToReport("Matrix b0\n");
			model.addToReport(b0.toString(matrixDig,matrixDecDig) + "\n");
		}
	}
	
	void reportFragmentationMatrixAndDescriptors()
	{
		GCMReportConfig repCfg = model.getReportConfig();

		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Matrix A0  (group counts)");
			System.out.println(A0.toString(3,0));
			if (D0 != null)
			{	
				System.out.println("Matrix D0  (descriptors)");				
				System.out.println(D0.toString(matrixDig,matrixDecDig));	
			}
			if (Cf0 != null)
			{	
				System.out.println("Matrix Cf0  (correction factors)");				
				System.out.println(Cf0.toString(matrixDig,matrixDecDig));	
			}
		}
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Matrix A0  (group counts)\n");
			model.addToReport(A0.toString(3,0) + "\n");	
			if (D0 != null)
			{	
				model.addToReport("Matrix D0  (descriptors)\n");
				model.addToReport(D0.toString(matrixDig,matrixDecDig) + "\n");
			}
			if (Cf0 != null)
			{	
				model.addToReport("Matrix Cf0  (correction factors)\n");
				model.addToReport(Cf0.toString(matrixDig,matrixDecDig) + "\n");
			}
		}		
	}
	
	
	void reportContributions()
	{
		GCMReportConfig repCfg = model.getReportConfig();
		Map<String,IGroup> groups = model.getGroups();
		
		double t = 2.0;
		
		if (repCfg.FlagConsoleOutput)
		{
			System.out.println("Group contributions:");
			Set<String> keys = groups.keySet();
			for (String key : keys)
			{
				IGroup g = groups.get(key);
				System.out.println("\t" + key + "\t" 
						//+ g.getContribution() 
						+ (g.isMissing()?(g.getContribution() + "  missing" )
								: getContributionStatReport(g.getContribution(), g.getSD(), t)));
			}
			
			List<DescriptorInfo> diList = model.getDescriptors();
			if (diList != null)
			{	
				System.out.println("Descriptor contributions:");
				for (int i = 0; i < diList.size(); i++)
				{	
					System.out.println("\t" + diList.get(i).fullString + "\t" 
							//+ diList.get(i).getContribution());
							+ getContributionStatReport(diList.get(i).getContribution(), diList.get(i).getSD(), t));
				}
			}
			
			List<ICorrectionFactor> cfList = model.getCorrectionFactors();
			if (!cfList.isEmpty())
			{	
				System.out.println("Correction factor contributions:");
				for (int i = 0; i < cfList.size(); i++)
				{	
					System.out.println("\t" + cfList.get(i).getDesignation() + "\t" 
							//+ cfList.get(i).getContribution());
							+ getContributionStatReport(cfList.get(i).getContribution(), cfList.get(i).getSD(), t));
				}
			}
		}
		
		if (repCfg.FlagBufferOutput)
		{
			model.addToReport("Group contributions:\n");
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
				model.addToReport("Descriptor contributions:\n");
				for (int i = 0; i < diList.size(); i++)
				{	
					model.addToReport("\t" + diList.get(i).fullString + "\t" 
							+ diList.get(i).getContribution() + "\n");
				}
			}
		}
	}
	
	void reportDataSetErrors(DataSet dataSet)
	{
		GCMReportConfig repCfg = model.getReportConfig();		
		if (repCfg.FlagConsoleOutput)
		{	
			System.out.println("Fragmentation errors:");
			System.out.println(dataSet.reportErrorsAsString());
			
		}
		if (repCfg.FlagBufferOutput)
		{	
			model.addToReport("Fragmentation errors:\n");
			model.addToReport(dataSet.reportErrorsAsString());
			model.addToReport("\n");
		}
	}
	
	String getContributionStatReport(double contr, double sd, double t)
	{
		double d1 = contr - t*sd;
		double d2 = contr + t*sd;
		boolean statSignificance = ((d1 >0) || (d2 <0));
		
		double rsd = 0.0;
		if (contr != 0)
			rsd = 100*sd/Math.abs(contr);
		
		String s = "" + contr + (statSignificance?"  Yes  ":"  No  ")
				+  " rsd = " + df2.format(rsd) + "%"
				+ "   [" + d1 + ", " + d2 + "]";;
		
		return s;
	}
	
	public String getMatricesAsString(String separator, boolean mergeMatrices, 
				boolean groupCountMatrix, boolean correctionFactorsMatrix, 
				boolean externalDescriptorMatrix, boolean targetPropertyMatrix, boolean addSmiles) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		if (mergeMatrices)
		{
			//Make header line
			if (addSmiles)
				sb.append("SMILES" + separator);
			
			if (groupCountMatrix)
				sb.append(model.getGroupsAsString(separator));
			
			if (correctionFactorsMatrix)
			{
				if (groupCountMatrix)
					sb.append(separator);
				sb.append(model.getCorrectionFactorsAsString(separator));
			}
			
			if (externalDescriptorMatrix)
			{
				if (groupCountMatrix || correctionFactorsMatrix)
					sb.append(separator);
				sb.append(model.getDescriptorsAsString(separator));
			}
			
			if (targetPropertyMatrix)
			{
				if (groupCountMatrix || correctionFactorsMatrix || externalDescriptorMatrix)
					sb.append(separator);
				sb.append(model.getTargetProperty());
			}
			sb.append("\n");
			
			int m = A0.nRows;
			for (int i = 0; i < m; i++)
			{
				if (addSmiles)
				{
					IAtomContainer mol = trainDataSet.dataObjects.get(i).molecule;
					String smi = SmartsHelper.moleculeToSMILES(mol, true);
					sb.append(smi);
					sb.append(separator);
				}
				
				if (groupCountMatrix)
				{
					for (int j = 0; j < A0.nColumns; j++)
					{	
						sb.append(A0.el[i][j]);
						if (j< (A0.nColumns-1))
							sb.append(separator);
					}	
				}
				
				if (correctionFactorsMatrix)
				{
					if (groupCountMatrix)
						sb.append(separator);
					
					for (int j = 0; j < Cf0.nColumns; j++)
					{	
						sb.append(Cf0.el[i][j]);
						if (j< (Cf0.nColumns-1))
							sb.append(separator);
					}
				}
				
				if (externalDescriptorMatrix)
				{
					if (groupCountMatrix || correctionFactorsMatrix)
						sb.append(separator);
					
					for (int j = 0; j < D0.nColumns; j++)
					{	
						sb.append(D0.el[i][j]);
						if (j< (D0.nColumns-1))
							sb.append(separator);
					}
				}
				
				if (targetPropertyMatrix)
				{
					if (groupCountMatrix || correctionFactorsMatrix || externalDescriptorMatrix)
						sb.append(separator);
					sb.append(b0.el[i][0]);
				}
				
				sb.append("\n");
			}
			
		}
		else
		{
			//TODO
		}
				
		return sb.toString();
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
