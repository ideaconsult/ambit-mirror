package ambit2.groupcontribution.utils.math;

public class ValidationConfig 
{
	public boolean verboseReport = true;   
	public boolean selfTest = true;
	public boolean leaveOneOutValidation = false;
	public CrossValidation crossValidation = new CrossValidation(5);
	public int yScramblingIterations = 500; //if zero no Y-scrambling is performed
	public boolean externalSetTest = false;
	public int bootStrapIterations = 0;
	
	public void reset()
	{
		verboseReport = false;
		selfTest = false;
		leaveOneOutValidation = false;
		crossValidation = null;
		yScramblingIterations = 0;
		externalSetTest = false;
		bootStrapIterations = 0;
	}
}
