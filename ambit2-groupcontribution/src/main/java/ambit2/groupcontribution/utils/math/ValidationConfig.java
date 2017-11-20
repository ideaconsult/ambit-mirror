package ambit2.groupcontribution.utils.math;

public class ValidationConfig 
{
	public boolean verboseReport = true;   
	public boolean selfTest = true;
	public boolean leaveOneOutValidation = true;
	public CrossValidation crossValidation = new CrossValidation(5);
	public int yScramblingIterations = 1000; //if zero no Y-scrambling
	public boolean externalSetTest = false;
	public boolean bootStrap = false;
}
