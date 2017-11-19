package ambit2.groupcontribution.utils.math;

public class ValidationConfig 
{
	public boolean selfTest = true;
	public CrossValidation singleOutValidation = new CrossValidation();
	public CrossValidation crossValidation = null;
	public int yScramblingIterations = 1000;
	
	public boolean externalTestSet = false;
	public boolean bootStrap = false;
	
}
