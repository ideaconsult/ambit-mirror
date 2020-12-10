package ambit2.sln.io;

public class SLN2ChemObjectConfig 
{
	public boolean FlagConvertInequality = false;
	
	//Limit values for atom/bond attributes 
	//used for converting in equality expressions
	public int maxCharge = 4;
	public int minCharge = -4;
	public int maxHCount = 4;
	public int minHCount = 0;
}
