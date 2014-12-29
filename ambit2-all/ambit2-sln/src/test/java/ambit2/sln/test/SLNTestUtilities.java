package ambit2.sln.test;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.search.SLNSearchManager;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;



public class SLNTestUtilities 
{
	static SLNParser slnParser = new SLNParser();
	static SLNHelper slnHelper = new SLNHelper();
	static SLNSearchManager man = new SLNSearchManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	
	public static void main(String[] args) throws Exception
	{
		SLNTestUtilities tu = new SLNTestUtilities();
		
		//tu.testSLN("C[1:c=y]H2=[s=I;ftt=m]CH[5:ccor=z;!fcharge=-3.3](OCH(CH3)CH3)CH3[7]");
		//tu.testSLN("CH2=C[1]HCH3[12]CH3=@1CCC@1CCCC@1");
		//tu.testSLN("CH3[1:I=13;is=2]CH(CH(CH3)CH3)CH2CH3");
		//tu.testSLN("CC<name=ethane;regid=234&a=b;a1=b1;name=wertwert>");		
		//tu.testSLN("C[1]CCC[2]CC@1@2");
		//tu.testSLN("C[1]CCC-[a=b]@1");
		
		//tu.testSLN2SLN("C(C)C~CH3[S=S*;a=b;a1=b1]<coord2d=(0,1),(1,1)>");
		//tu.testSLN2SLN("C(C)CCH3<coord2d=(1,2),(3,4);a1=b1>");
		//tu.testSLN2SLN("C(C)CCH3<coord3d=(1,2,4),(1,2,3);a1=b1>");
		
		//slnHelper.FlagPreserveOriginalAtomID = false;
		//tu.testSLN2SLN("C[2]CCCC(C)@2CC");
		//tu.testSLN2SLN("C[21]CC[3]CC@21@3");
		//tu.testSLN2SLN("CH2=CHCH2C[1]:CH:C[2]OCH2OC(@2):C(OCH3):CH:(@1)");
		//tu.testSLN2SLN("CC[s=R]H(O)CC[s=S]H(O)N");
		
		//tu.testSLNIsomorphism("C[hac=3]","CC(C)CCC");
		//tu.testSLNIsomorphism("C[hc=0]","CC(C)CCC");
		//tu.testSLNIsomorphism("C[tbo=4]","NNS");
		//tu.testSLNIsomorphism("C[src=3]","C1CCC1CC2CC2");
		//tu.testSLNIsomorphism("C[tac=2]","C#C");
		
		//tu.testSLNIsomorphism("C[rbc=1]","C1CCCC1"); //not working !!!
		
		tu.testSLNIsomorphism("C~[type=2]CCCCC~[type=3]C","C=CCCCC#C"); 
		
		
		//tu.testSLN("C[hac=3]");
		//tu.testSLN("CC[s=R]H(O)C[rbc=3]C[s=S]H(O)N");
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
	
	public void testSLNIsomorphism(String sln, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		SLNContainer query = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		
		isoTester.setQuery(query);
		SmartsParser.prepareTargetForSMARTSSearch(true, true, true, true, true, true, mol); //flags are set temporary
		System.out.println("SLN Isomorphism: " + sln  + "  in  " + smiles + 
				"   " + isoTester.hasIsomorphism(mol));
	}
	
}
