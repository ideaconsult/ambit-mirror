package ambit2.groupcontribution.utils.math;

public class CrossValidation 
{
	public static enum ObjectSelection {
		NONE, RANDOM, RANDOM_VALUE_DISTRIBUTION
	}
	
	public boolean isLeaveOneOut = true;
	public int numFolds = 5;
	public ObjectSelection selection = ObjectSelection.RANDOM;
	public int numCycles = 1;
	
	public CrossValidation()
	{	
	}
	
	public CrossValidation(int numFolds)
	{	
		this.numFolds = numFolds;
		isLeaveOneOut = false;
	}
	
	public CrossValidation(int numFolds, int numCycles, ObjectSelection selection)
	{	
		this.numFolds = numFolds;
		this.numCycles = numCycles;
		this.selection = selection;
		isLeaveOneOut = false;
	}
	
}
