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
		
		tu.testSLN("CH3CH[5:S=R;charge=-5](OCH(CH3)CH3)CH3");
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
		 
		System.out.println("Inpit  sln: " + sln); 
		System.out.println("Atom attributes:");		
		System.out.println(SLNHelper.getAtomsAttributes(container));
		System.out.println("Bond attributes:");
		System.out.println(SLNHelper.getBondsAttributes(container));
		
	}
	
}
