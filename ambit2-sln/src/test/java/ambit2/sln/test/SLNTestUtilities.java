package ambit2.sln.test;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNParser;
import ambit2.sln.SLNHelper;



public class SLNTestUtilities 
{
	static SLNParser slnParser = new SLNParser();
	static SLNHelper slnHelper = new SLNHelper();
	
	public static void main(String[] args) throws Exception
	{
		SLNTestUtilities tu = new SLNTestUtilities();
		
		//tu.testSLN("C[1:c=y]H2=[s=I;ftt=m]CH[5:ccor=z;!fcharge=-3.3](OCH(CH3)CH3)CH3[7]");
		//tu.testSLN("CH2=C[1]HCH3[12]CH3=@1CCC@1CCCC@1");
		//tu.testSLN("CH3[1:I=13;is=2]CH(CH(CH3)CH3)CH2CH3");
		//tu.testSLN("CC<name=ethane;regid=234&a=b;a1=b1;name=wertwert>");
		
		
		tu.testSLN2SLN("C(C)CCH3[a=b;a1=b1]");
		
	}
	
	public void testSLN(String sln)
	{
		
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		 
		System.out.println("Input  sln: " + sln); 
		System.out.println("Atom attributes:");		
		System.out.println(SLNHelper.getAtomsAttributes(container));
		System.out.println("Bond attributes:");
		System.out.println(SLNHelper.getBondsAttributes(container));
		if (container.getAttributes().getNumOfAttributes() > 0)
		{
			System.out.println("Molecule attributes:");
			System.out.println(SLNHelper.getMolAttributes(container));
		}
		
	}
	
	public void testSLN2SLN(String sln)
	{
		
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		 
		System.out.println("Input  sln: " + sln); 
		System.out.println("Ouput  sln: " + slnHelper.toSLN(container));
	}
	
}
