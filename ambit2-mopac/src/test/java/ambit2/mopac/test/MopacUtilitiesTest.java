package ambit2.mopac.test;

import ambit2.mopac.MopacUtilities;

public class MopacUtilitiesTest 
{
	static MopacUtilities utils;  
	
	public static void main(String[] args) throws Exception 
	{
		utils = new MopacUtilities();
		
		test("C=C");
		test("CCCC=C");
		
	}
	
	static void test(String smiles) throws Exception
	{
		double energy = utils.getTotalEnergy(smiles);
		System.out.println("MOPAC energy for " + smiles + " is " + energy);
	}

}
