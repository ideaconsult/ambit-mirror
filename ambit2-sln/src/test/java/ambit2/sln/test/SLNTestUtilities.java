package ambit2.sln.test;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.io.SLN2Substance;
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
		//tu.testSLN2SLN("CCH4[aaa<=123;tttt=23;bbb;aaaa;s=R;spin=t;c=y;hac=2;tbo=1](O)CCH(O)N");
		
		
		//tu.testSLN2SLN("C-[type=#]C<aa=3;p=456>CCC<>");
		
		//tu.testSLNIsomorphism("C[r]N","C1CCCC1CCCCN");
		//tu.testSLNIsomorphism("N[fcharge<0]","CC(C)CC[N-]");
		//tu.testSLNIsomorphism("C[c=o1]","N[CH2]N");
		//tu.testSLNIsomorphism("C[hc=0]","CC(C)CCC");
		//tu.testSLNIsomorphism("C[tbo=4]","NNS");
		//tu.testSLNIsomorphism("C[src=3]","C1CCC1CC2CC2");		
		//tu.testSLNIsomorphism("C[tac=2]","C#C");
		//tu.testSLNIsomorphism("Any=C","C=N");
		//tu.testSLNIsomorphism("N[charge=+1](=O)(O[charge=-1])","[N+](=O)[O-]");
		
		//tu.testSLNIsomorphism("C[1]CC@1C", "C1CC1");  //EmptyStackException !!! 
		
		
		//tu.testSLNIsomorphism("C[rbc=1]","C1CCCC1"); //not working !!!
		
		//tu.testSLNIsomorphism("C~[type=2]CCCCC~[type=3]C","C=CCCCC#C"); 
		
		//tu.testSLN2SLN("Br[type=Cl|type=I]C(C)CCH3<coord2d=(1,2),(3,4)>");
		//tu.testSLN2SLN("C[1:tt=456]CCCCC@1");
		
		//tu.testSLN2SLN("CCXx[n=3;fcharge=2]");
		
		//tu.testSLN("C[hac=3]");
		//tu.testSLN("CC[s=R]H(O)C[rbc=3]C[s=S]H(O)N");
		
		tu.testSLN2CompositionRelation("CCCC<compositionUUID=id-0001;name=test>");
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
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		SmartsHelper.preProcessStructure(mol, true, false);
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
	
	public void testSLN2CompositionRelation(String sln)
	{
		System.out.println("SLN:  " + sln); 
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		
		SLN2Substance sln2sub = new SLN2Substance();
		CompositionRelation compRel = sln2sub.slnContainerToCompositionRelation(container);
		System.out.println("Composition Relation:");
		System.out.println(compositionRelationToString(compRel));
	}
	
	public String compositionRelationToString(CompositionRelation rel) {
		StringBuffer sb = new StringBuffer();
		sb.append("  Content : \"" + rel.getContent() + "\"\n");
		sb.append("  Format : \"" + rel.getFormat() + "\"\n");
		sb.append("  Smiles : \"" + rel.getSmiles() + "\"\n");
		sb.append("  Formula : \"" + rel.getFormula() + "\"\n");
		sb.append("  Inchi : \"" + rel.getInchi() + "\"\n");
		sb.append("  InchiKey : \"" + rel.getInchiKey() + "\"\n");
		sb.append("  CompositionUUID : \"" + rel.getCompositionUUID() + "\"\n");
		sb.append("  Name : \"" + rel.getName() + "\"\n");
		sb.append("  RelationType : \"" + (rel.getRelationType()==null?"null":rel.getRelationType().name()) + "\"\n");
		sb.append("  Proportion : \"" + (rel.getRelation()==null?"null":rel.getRelation().toJSON()) + "\"\n");
		sb.append("  Properties : \n");
		Iterable<Property> properties = rel.getRecordProperties();
		if (properties != null)
			for (Property p : properties)
			{	
				sb.append("     " + p.toString() + "\n");
			}	
		
		return sb.toString();
	}
	
	public CompositionRelation getCompositionRelationFromString(String relString, String separator) 
	{
		IStructureRecord structure = new StructureRecord();
		//TODO
		CompositionRelation rel = new CompositionRelation(null, structure, null, null);
		return rel;
		
	}
	
	
	
}
