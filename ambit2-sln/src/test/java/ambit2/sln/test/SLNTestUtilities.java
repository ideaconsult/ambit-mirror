package ambit2.sln.test;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNParser;
import ambit2.sln.SLNHelper;



public class SLNTestUtilities 
{
	static SLNParser slnpar = new SLNParser();
	
	public static void main(String[] args) throws Exception
	{
		SLNTestUtilities tu = new SLNTestUtilities();
		
		tu.testSLN("CH2=CH[5:ccor=z;!fcharge=-5.3](OCH(CH3)CH3)CH3[7]");
		
	}
	
	public void testSLN(String sln)
	{
		
		SLNContainer container = slnpar.parse(sln);
		if (!slnpar.getErrorMessages().equals(""))
		{
			System.out.println("Original sln: " + sln); 
			System.out.println("SLN Parser errors:\n" + slnpar.getErrorMessages());			
			return;
		}
		 
		System.out.println("Input  sln: " + sln); 
		System.out.println("Atom attributes:");		
		System.out.println(SLNHelper.getAtomsAttributes(container));
		System.out.println("Bond attributes:");
		System.out.println(SLNHelper.getBondsAttributes(container));
		
	}
	
}
