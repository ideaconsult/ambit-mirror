package ambit2.groupcontribution.utils.math;

import java.util.List;

public class CrossValidation 
{
	public static enum ObjectSelection {
		NONE, RANDOM, RANDOM_VALUE_DISTRIBUTION
	}
	
	//public boolean isLeaveOneOut = true;
	public int numFolds = 5;
	public ObjectSelection selection = ObjectSelection.RANDOM;
	public int numCycles = 1;
	
	public CrossValidation()
	{	
	}
	
	public CrossValidation(int numFolds)
	{	
		this.numFolds = numFolds;
		//isLeaveOneOut = false;
	}
	
	public CrossValidation(int numFolds, int numCycles, ObjectSelection selection)
	{	
		this.numFolds = numFolds;
		this.numCycles = numCycles;
		this.selection = selection;
		//isLeaveOneOut = false;
	}
	
	public static MatrixDouble[] makeValidationModelMatrices(int[] testObjIndices, 
				MatrixDouble A, MatrixDouble b, MatrixDouble D)
	{
		//The indices of the objects from the validation set
		//These objects are excluded from the training original set
		int size = testObjIndices.length;
		if (size == 0)
			return null;
		
		int m = A.nRows;
		int n = A.nColumns;
			
		MatrixDouble mA = new MatrixDouble(m-size, n);
		MatrixDouble mb = new MatrixDouble(m-size, 1);
		MatrixDouble mD = null;
		if (D != null)
			mD = new MatrixDouble(m-size, D.nColumns);
		
		int curIndex = 0;
		for (int i = 0; i < m; ++i)
		{
			if (ArrayUtils.firstIndexOf(i, testObjIndices) == -1)
			{
				mA.copyRowFrom(curIndex,A,i);
				if (mD != null)
					mD.copyRowFrom(curIndex,D,i);
				mb.el[curIndex][0] = b.el[i][0];
				curIndex++;
			}
		}
		
		int resSize = 2;
		if (mD != null)
			resSize++;
		
		MatrixDouble matrices[] = new MatrixDouble[resSize];
		matrices[0] = mA;
		matrices[1] = mb;
		if (mD != null)
			matrices[2] = mD;
		
		return matrices;
	}
	
	public static MatrixDouble[] makeValidationTestMatrices(int[] testObjIndices, 
			MatrixDouble A, MatrixDouble b, MatrixDouble D)
	{
		int m = testObjIndices.length;
		int n = A.nColumns;
		if (m == 0)
			return null;

		MatrixDouble tA = new MatrixDouble(m, n);
		MatrixDouble tb = new MatrixDouble(m, 1);
		MatrixDouble tD = null;
		if (D != null)
			tD = new MatrixDouble(m, D.nColumns);
		
		for (int i = 0; i < m; i++)
		{
			tA.copyRowFrom(i,A,testObjIndices[i]);
			tb.el[i][0] = b.el[testObjIndices[i]][0];
			if (tD != null)
				tD.copyRowFrom(i,D,testObjIndices[i]);
		}

		int resSize = 2;
		if (tD != null)
			resSize++;
		
		MatrixDouble matrices[] = new MatrixDouble[resSize];
		matrices[0] = tA;
		matrices[1] = tb;
		if (tD != null)
			matrices[2] = tD;
		
		return matrices;
	}
	
}
