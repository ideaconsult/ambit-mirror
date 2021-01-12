package ambit2.sln.test;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.dictionary.Expander;
import ambit2.sln.dictionary.MarkushHelper;
import ambit2.sln.io.SLN2ChemObjectConfig.ComparisonConversion;
import ambit2.sln.io.SLN2SMARTS;
import ambit2.sln.io.SLN2Substance;
import ambit2.sln.io.SLN2SubstanceConfig;
import ambit2.sln.search.SLNSearchManager;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;



public class SLNTestUtilities 
{
	static SLNParser slnParser = new SLNParser();
	static SLNHelper slnHelper = new SLNHelper();
	static SLN2SMARTS sln2Smarts = new SLN2SMARTS();
	static SLNSearchManager man = new SLNSearchManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SLN2Substance sln2sub = new SLN2Substance();
	static Expander expander = new Expander();
	static MarkushHelper markushHelper = new MarkushHelper();
	
	
	public static void main(String[] args) throws Exception
	{
		SLNTestUtilities tu = new SLNTestUtilities();
		
		//This flag is set TRUE in order to generate SLNs which are identical to the input SLNs
		slnHelper.FlagPreserveOriginalAtomID = true;
		slnParser.setPredefinedGlobalDictionary();
		
		//tu.testSLN("C[1:c=y]H2=[s=I;ftt=m]CH[5:ccor=z;!fcharge=-3.3](OCH(CH3)CH3)CH3[7]");
		//tu.testSLN("CH2=C[1]HCH3[12]CH3=@1CCC@1CCCC@1");
		//tu.testSLN("CH3[1:I=13;is=2]CH(CH(CH3)CH3)CH2CH3");
		//tu.testSLN("CC<name=ethane;regid=234&a=b;a1=b1;name=wertwert>");		
		//tu.testSLN("C[1]CCC[2]CC@1@2");
		//tu.testSLN("C[1]CCC-[a=b]@1");
		
		//tu.testSLN2SLN("C(C)C~CH3[S=S*;a=b;a1=b1]<coord2d=(0,1),(1,1)>");
		//tu.testSLN2SLN("C(C)CCH3<coord2d=(1,2),(3,4);a1=b1>");
		//tu.testSLN2SLN("C(C)CCH3<coord3d=(1,2,4),(1,2,3);a1=b1>");
		
		
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
		
		//tu.testSLNIsomorphism("Het[charge>0]=C","[N+]=C");
		//slnParser.setFlagUseTypeAsStandardAtomAttribute(false);
		//tu.testSLNIsomorphism("Any[type>7]","CNO");
		//tu.testSLNIsomorphism("HalC","[F]CCO");
		//tu.testSLNIsomorphism("C:C","c1ccccc1");
		//tu.testSLNIsomorphism("C:C","C1=CC=CC=C1");
		//tu.testSLNIsomorphism("C:C","C1CCCCC1");
		
		
		//tu.testSLNIsomorphism("C[1]C=C@1C", "C1C=C1CCC"); 
		//tu.testSLNIsomorphism("C[hc=2&charge<=0]", "[CH3-]CC"); 
		
		
		//tu.testSLNIsomorphism("C[rbc=2]","C1CCCC1"); //partially fixed; not working for spiro atoms!!!
		
		//tu.testSLNIsomorphism("C~[type=2]CCCCC~[type=3]C","C=CCCCC#C"); 
		
		//tu.testSLN2SLN("Br[type=Cl|type=I]C(C)CCH3<coord2d=(1,2),(3,4)>");
		//tu.testSLN2SLN("C[1:tt=456]CCCCC@1");
		
		//tu.testSLN2SLN("CCXx[n=3;fcharge=2]");
		//tu.testSLN2SLN("CCC<a<=3;b>=234;c=ddd;t1!=aaa;t2<234;t3>123>");
		
		
		//tu.testSLNMatch("CC<aa<5>", "CCC", new Object[] {"aa",4});
		
		//slnHelper.FlagPreserveOriginalAtomID = false;	
		//tu.testSLN2SLN("C[7]CC@7Aa[v=3,4]HgH3CCCHet{Aa:CH3CH2CH3<v=1,8>}");
		//tu.testSLN2SLN("CCCCAa{Aa:Cl|F|OH|CH3|CCC<v=1,3>}");
		
		
		//tu.testSLNExpander("CC[charge>-1]CCH3");
		//tu.testSLNExpander("AxCCCC{Aa:N[tt=3;charge<3]SO<v=1,3>}{Ax:Any[xx=45]}");
		//tu.testSLNExpander("CCAaCCAaAa{Aa:NSO<v=1,2>|CCC}");
		//tu.expander.test();
		
		tu.testMarkushCombinatorialList("AaCCCAaAx{Aa:O|S|CH3}{Ax:F|Cl|Br|C(=O)OH}");
		
		//tu.testMarkushHelper("Aa:CCC<v=1,3>|CC|CO<v=1,2>");
		
		
		//tu.testSLN("C[hac=3]");
		//tu.testSLN("CC[s=R]H(O)C[rbc=3]C[s=S]H(O)N");
		
		//tu.testSLN2Smiles("CC=CCCS[charge=+3]");
		//tu.testSLN2Smiles("N[1]CH=CHCH2CH=@1");
		
		//sln2Smarts.getConversionConfig().FlagComparisonConversion = ComparisonConversion.convert_as_equal_if_not_nonequality;
		//sln2Smarts.getConversionConfig().FlagComparisonConversion = ComparisonConversion.convert_as_equal;
		sln2Smarts.getConversionConfig().FlagComparisonConversion = ComparisonConversion.convert_as_equal_if_eqaul_is_present;
		//sln2Smarts.getConversionConfig().FlagComparisonConversion = ComparisonConversion.omit;
		
		//tu.testSLN2Smarts("CC=CCCS[charge>+3]");
		//tu.testSLN2Smarts("N[hc=1|hc=2]C[!r]C[src>=1|charge=+1]");
		//tu.testSLNIsomorphism("N[hc=1|hc=2]C[!r]C[src>=1|charge=+1]", "NCc1cnc(O)c(O)c1");
		
		//tu.testSLN2Smarts("C[1:charge=-1]C=CC[src=3]CCC[tbo=4]C[r;!hac=3;tac=4]CH3Any@1");
		
		//tu.testSLN2Smarts("N[hc=1|hc=2]C[!r]C[r]");
		//tu.testSLNIsomorphism("N[hc=1|hc=2]C[!r]C[r]", "NCc1cnc(O)c(O)c1"); 
		

				
		//tu.sln2sub.config.FlagAddImplicitHAtomsOnSLNAtomConversion = true;
		//tu.testSLN2CompositionRelation("CC<compositionUUID=id-0001;name=test>");
		
		//tu.testSLN2CompositionRelation("O=Si=O<compositionUUID=NWKI-144a9226-4b93-36a9-ba2d-6b6c4903357b;cas=7631-86-9"
		//		+ "name=Lesniak2013_NM4;type=NPO_1373;formula=SiO2;role=core>");
		
		//tu.testCompositionRelation2SLN("Smiles : NC(C)CO, Name : Test, CompositionUUID : 123456");
		//tu.testCompositionRelation2SLN("CompositionUUID : 123456");
		
		//tu.testSLN2SubstanceConfig();
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
	
	public void testSLNExpander(String sln)
	{	
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		
		SLNContainer container2 = expander.generateExpandedSLNContainer(container);
		
		System.out.println("Input  sln: " + sln); 
		System.out.println("Ouput  sln: " + slnHelper.toSLN(container2));
	}
	
	public void testMarkushCombinatorialList(String sln)
	{	
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return;
		}
		
		System.out.println("Input  sln: " + sln);
		System.out.println("Generating combinatorial library ...");
		List<SLNContainer> list = expander.generateMarkushCombinatorialList(container);
		for (int i = 0; i < list.size(); i++)
			System.out.println("  " + slnHelper.toSLN(list.get(i)));
	}
	
	public void testSLN2Smiles(String sln) throws Exception
	{	
		System.out.println("Input  sln: " + sln);
		String smi = sln2Smarts.slnToSmiles(sln);
		if (smi == null)
			System.out.println("Conversion errors: " + sln2Smarts.getAllErrors());
		else
			System.out.println("Ouput  smiles: " + smi);
		
		if (!sln2Smarts.getConversionWarnings().isEmpty())
		{
			System.out.println("Conversion warnings: ");
			for (String w: sln2Smarts.getConversionWarnings())
				System.out.println(w);
		}
	}
	
	public void testSLN2Smarts(String sln) throws Exception
	{	
		System.out.println("Input sln: " + sln);
		String smarts = sln2Smarts.slnToSmarts(sln);
		if (smarts == null)
			System.out.println("Conversion errors: " + sln2Smarts.getAllErrors());
		else
			System.out.println("Ouput smarts: " + smarts);
		
		if (!sln2Smarts.getConversionWarnings().isEmpty())
		{
			System.out.println("Conversion warnings: ");
			for (String w: sln2Smarts.getConversionWarnings())
				System.out.println(w);
		}
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
	
	public void testSLNMatch(String sln, String smiles, Object propertyValuePairs[]) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		SmartsHelper.preProcessStructure(mol, true, false);
		SmartsParser.prepareTargetForSMARTSSearch(true, true, true, true, true, true, mol); //flags are set temporary
		
		//Setting molecule properties
		if (propertyValuePairs != null) {
			int n = propertyValuePairs.length/2;
			for (int i = 0; i < n; i++)
				mol.setProperty(propertyValuePairs[2*i], propertyValuePairs[2*i+1]);
		}
		
		man.setQuery(sln);
		if (!man.getErrors().isEmpty())
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLNSearhManager errors:\n" + man.getAllErrorsAsString());			
			return;
		}
		
		boolean res = man.matches(mol);
		
		
		System.out.println("Match: " + sln  + "  in  " + smiles + 
				"   " + res);
	}
	
	public void testMarkushHelper(String sln)
	{
		System.out.println("SLN:  " + sln);
		markushHelper.setSLNString(sln);
		System.out.println("isMarkushAtomSLNString() = " + markushHelper.isMarkushAtomSLNString());
		markushHelper.analyzeMarkushString();
		
		if (!markushHelper.getErrors().isEmpty())
		{
			System.out.println(markushHelper.getErrorMessages());
			return;
		}
		
		System.out.println("Name = " + markushHelper.getMarkushAtomName());
		List<String> components = markushHelper.getComponentList();
		System.out.println("Markush components = ");
		for (int i = 0; i < components.size(); i++)
			System.out.println("  " + components.get(i));
		
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
		
		
		CompositionRelation compRel = sln2sub.slnContainerToCompositionRelation(container);
		System.out.println("Errors:\n" + sln2sub.getAllErrors());
		System.out.println("Result Composition Relation:");
		System.out.println(compositionRelationToString(compRel));
	}
	
	public void testCompositionRelation2SLN(String compRelString)
	{
		System.out.println("Input CompRel:  " + compRelString); 
		CompositionRelation compRel = getCompositionRelationFromString(compRelString, ",");
		System.out.println("Composition Relation:");
		System.out.println(compositionRelationToString(compRel));
		
		SLNContainer slnCont = sln2sub.compositionRelationToSLNContainer(compRel);
		System.out.println("Errors:\n" + sln2sub.getAllErrors());
		System.out.println("Result SLN: " + slnHelper.toSLN(slnCont));
		
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
		CompositionRelation rel = new CompositionRelation(null, structure, null, null);
		String tokens[] = relString.split(separator);
		for (String s : tokens)
		{
			String s_toks[] = s.split(":");
			if (s_toks.length == 2)
			{
				String sectionName = s_toks[0].trim();
				String value = s_toks[1].trim();
				if (sectionName.equals("Content"))
					rel.setContent(value);
				else if (sectionName.equals("Format"))
					rel.setFormat(value);
				else if (sectionName.equals("Formula"))
					rel.setFormula(value);
				else if (sectionName.equals("Inchi"))
					rel.setInchi(value);
				else if (sectionName.equals("InchiKey"))
					rel.setInchiKey(value);
				else if (sectionName.equals("CompositionUUID"))
					rel.setCompositionUUID(value);
				else if (sectionName.equals("Name"))
					rel.setName(value);
				else if (sectionName.equals("Smiles"))
					rel.setSmiles(value);
				else if (sectionName.equals("Format"))
					rel.setFormat(value);
				else if (sectionName.equals("Format"))
					rel.setFormat(value);
			}
		}
		
		return rel;		
	}
	
	public void testSLN2SubstanceConfig()
	{
		SLN2SubstanceConfig cfg = new SLN2SubstanceConfig();
		cfg.jsonFlags.proportion = true;
		
		System.out.println(cfg.toJSONKeyWord(""));
	}
	
}
