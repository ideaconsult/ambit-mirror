package ambit2.smarts;


import ambit2.smarts.DoubleBondStereoInfo.DBStereo;

public class StereoDBTransformation 
{
	//Reactant 
	public int reactDBAt1 = -1;
	public int reactDBAt2 = -1;
	public int reactLigand1 = -1;
	public int reactLigand2 = -1;
	public DBStereo reactDBStereo = DBStereo.UNDEFINED;
	
	//Product
	public int prodDBAt1 = -1;
	public int prodDBAt2 = -1;
	public int prodLigand1 = -1;
	public int prodLigand2 = -1;
	public DBStereo prodDBStereo = DBStereo.UNDEFINED;
}
