package ambit2.smarts;


import ambit2.smarts.DoubleBondStereoInfo.DBStereo;

public class StereoDBTransformation 
{
	//Reactant 
	public int reactDBAt1 = -1;
	public int reactDBAt2 = -1;
	public int reactLigand1 = -1;  //atom index is for mapped or unmapped atom
	public int reactLigand2 = -1;  //atom index is for mapped or unmapped atom
	public DBStereo reactDBStereo = DBStereo.UNDEFINED;
	
	//Product
	public int prodDBAt1 = -1;
	public int prodDBAt2 = -1;
	public int prodLigand1 = -1; //atom index is for mapped or unmapped atom
	public int prodLigand2 = -1; //atom index is for mapped or unmapped atom
	public int prodLigand1ReactMap = -1; //if mapped the reactant partner is not obligatory reactLigand1,2	
	public int prodLigand2ReactMap = -1; //if mapped the reactant partner is not obligatory reactLigand1,2
	public DBStereo prodDBStereo = DBStereo.UNDEFINED;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("  reactDBAt1 = " + reactDBAt1);
		sb.append("  reactDBAt2 = " + reactDBAt2);
		sb.append("  reactLigand1 = " + reactLigand1);
		sb.append("  reactLigand2 = " + reactLigand2);
		sb.append("  reactDBStereo = " + reactDBStereo);
		sb.append("\n");
		sb.append("  prodDBAt1 = " + prodDBAt1);
		sb.append("  prodDBAt2 = " + prodDBAt2);
		sb.append("  prodLigand1 = " + prodLigand1);
		sb.append("  prodLigand2 = " + prodLigand2);
		sb.append("  prodLigand1ReactMap = " + prodLigand1ReactMap);
		sb.append("  prodLigand2ReactMap = " + prodLigand2ReactMap);
		sb.append("  prodDBStereo = " + prodDBStereo);
		
		return sb.toString();
	}
}
