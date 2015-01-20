package ambit2.smarts.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.validate.BasicValidator;
import org.openscience.cdk.validate.CDKValidator;
import org.openscience.cdk.validate.ValidationReport;
import org.openscience.cdk.validate.ValidatorEngine;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.ChemObjectFactory;
import ambit2.smarts.ChemObjectToSmiles;
import ambit2.smarts.EquivalenceTester;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.Screening;
import ambit2.smarts.ScreeningData;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsScreeningKeys;
import ambit2.smarts.SmartsToChemObject;
import ambit2.smarts.StructInfo;
import ambit2.smarts.StructureSetAnalyzer;




public class TestUtilities 
{	
	enum ReactionOperation {
		APPLY, CombinedOverlappedPos, SingleCopyForEachPos
	}
	
	static SmartsHelper smartsHelper = new SmartsHelper(SilentChemObjectBuilder.getInstance()); 
	static SmartsParser sp = new SmartsParser();
	//static SmilesParser smilesparser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SmartsToChemObject smToChemObj = new SmartsToChemObject(SilentChemObjectBuilder.getInstance());
	static ChemObjectToSmiles cots = new ChemObjectToSmiles(); 
	
	boolean FlagClearAromaticityBeforePreProcess = true;
	boolean FlagTargetPreprocessing = false;
	boolean FlagExplicitHAtoms = false;
	boolean FlagPrintAtomAttributes = false;
	boolean FlagPrintTransformationData = false;
	
	
	boolean FlagProductPreprocessing = true;
	boolean FlagClearImlicitHAtomsBeforeProductPreProcess = true;
	boolean FlagClearHybridizationOnProductPreProcess = true;
	boolean FlagAddImlicitHAtomsOnProductPreProcess = false;
	boolean FlagImplicitHToExplicitOnProductPreProcess = false;
	boolean FlagExplicitHToImplicitOnProductPreProcess = false;
	
	
	boolean FlagDoubleBondAromaticityNotSpecified  = false;
	
	int FlagSSMode = SmartsConst.SSM_NON_OVERLAPPING;
	int FlagSSModeForSingleCopyForEachPos = SmartsConst.SSM_NON_IDENTICAL;
	
	ReactionOperation FlagReactionOperation = ReactionOperation.APPLY;
	
	public TestUtilities()
	{	
	}
	@Test
	public void fakeTest() {
		//there should be at least one test
	}
	
	public void preProcess(IAtomContainer mol) throws Exception 
	{	
		if (FlagClearAromaticityBeforePreProcess)
		{
			for (IAtom atom:mol.atoms()) if (atom.getFlag(CDKConstants.ISAROMATIC)) 
				atom.setFlag(CDKConstants.ISAROMATIC,false);
			for (IBond bond: mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC))
				bond.setFlag(CDKConstants.ISAROMATIC,false);
		}
		
				
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		
		
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		if (FlagExplicitHAtoms)			
			AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
		
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
	}
	
	
	public void preProcessProduct(IAtomContainer mol) throws Exception 
	{
		if (FlagClearAromaticityBeforePreProcess)
		{
			for (IAtom atom:mol.atoms()) if (atom.getFlag(CDKConstants.ISAROMATIC)) 
				atom.setFlag(CDKConstants.ISAROMATIC,false);
			for (IBond bond: mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC))
				bond.setFlag(CDKConstants.ISAROMATIC,false);
		}
		
		
		//AtomContainerManipulator.clearAtomConfigurations(mol);
		//AtomContainerManipulator.removeHydrogens(mol);
				
				
		for (IAtom atom:mol.atoms())
		{	
			atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
			if (FlagClearImlicitHAtomsBeforeProductPreProcess)
				atom.setImplicitHydrogenCount(null);
		}


		/*
					atom.setAtomTypeName((String) CDKConstants.UNSET);
		            atom.setMaxBondOrder((IBond.Order) CDKConstants.UNSET);
		            atom.setBondOrderSum((Double) CDKConstants.UNSET);
		            atom.setCovalentRadius((Double) CDKConstants.UNSET);
		            atom.setValency((Integer) CDKConstants.UNSET);
		            atom.setFormalCharge((Integer) CDKConstants.UNSET);
		            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
		            atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
		            atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, false);
		            atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, false);
		            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, CDKConstants.UNSET);
		            atom.setFlag(CDKConstants.ISAROMATIC, false);
		            atom.setProperty("org.openscience.cdk.renderer.color", CDKConstants.UNSET);
		            atom.setAtomicNumber((Integer) CDKConstants.UNSET);
		            atom.setExactMass((Double) CDKConstants.UNSET); 
		 */
		
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		
		if (FlagAddImlicitHAtomsOnProductPreProcess)
		{	
			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			adder.addImplicitHydrogens(mol);
			if (FlagImplicitHToExplicitOnProductPreProcess)			
				AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
		}
		
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
	}
	
	
	public static void printSmartsTokens(String smarts)
	{
		System.out.println("Smarts " + smarts); 
		IQueryAtomContainer qac = sp.parse(smarts);
		if (!sp.getErrorMessages().equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + sp.getErrorMessages());			
			return;
		}
		System.out.println("Atoms: "+SmartsHelper.getAtomsString(qac));
		
		/*
		for (int i = 0; i < qac.getAtomCount(); i++)
			if (qac.getAtom(i) instanceof SmartsAtomExpression)
				System.out.println(
						SmartsHelper.getAtomExpressionTokens((SmartsAtomExpression)qac.getAtom(i)));
		*/		
		
		System.out.println("Bonds:\n" + SmartsHelper.getBondsString(qac));
	}
	
	public void testSmartsToQueryToSmarts(String smarts)
	{
		 
		IQueryAtomContainer qac = sp.parse(smarts);
		if (!sp.getErrorMessages().equals(""))
		{
			System.out.println("Original smarts: " + smarts); 
			System.out.println("Smarts Parser errors:\n" + sp.getErrorMessages());			
			return;
		}
		
		String smarts2 =  smartsHelper.toSmarts(qac);
		System.out.println("Original  smarts: " + smarts /*+ "  -->  " + smarts2*/); 
		System.out.println("Generated smarts: " + smarts2); 
		
	}
	
	public static int boolSearch(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return -1;
		}		
		boolean res = man.searchIn(mol);
		if (res)
			return(1);
		else
			return(0);
	}
	
	public void testSmartsManagerBoolSearch(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}		
		boolean res = man.searchIn(mol);
		System.out.println("Man_search " + smarts + " in " + smiles + "   --> " + res);
	}
	
	
	public void testSmartsManagerBoolSearchMDL(String smarts, String mdlFile)
	{		
		try
		{
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);

			if (!reader.hasNext()) 
			{
				System.out.println("Could find a molecule in the file: " + mdlFile);
				return;	
			}	
				
				
			Object o = reader.next();
			if (o instanceof IAtomContainer) 
			{
				
				IAtomContainer mol = (IAtomContainer)o;
				if (mol.getAtomCount() == 0) 
				{
					System.out.println("Empty molecule (0 atoms)!");
					return;
				}
				
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				
				man.setQuery(smarts);
				if (!man.getErrors().equals(""))
				{
					System.out.println(man.getErrors());
					return;
				}		
				boolean res = man.searchIn(mol);
				System.out.println("Man_search " + smarts + " in MDL_FILE   -->  " + res);
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	public void structureStatisticsMDL(int nStr, String mdlFile, String outFile)
	{		
		String endLine = "\r\n";
		
		try
		{
			//Input
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);
					
			//Output
			File file = new File(outFile);
			RandomAccessFile outf = new RandomAccessFile(file,"rw");
			outf.setLength(0);
			
			int record = 0;
			while (reader.hasNext()) 
			{	
				record++;
				if (record > nStr)
					break;
				
				if (record % 100 == 0)
					System.out.println("rec " + record);
				
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{	
					IAtomContainer mol = (IAtomContainer)o;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					
					//Calc. ring statistics
					SSSRFinder sssrf = new SSSRFinder(mol);
					IRingSet ringSet = sssrf.findSSSR();
					int n = ringSet.getAtomContainerCount();
					double ringSizeSum = 0;
					double ringAverSize = 0;
					for(int i = 0; i < n; i++)					
						ringSizeSum += ringSet.getAtomContainer(i).getAtomCount();
					
					if (n > 0)
						ringAverSize = ringSizeSum / n;
					
					String outData = "" + record + "  " + mol.getAtomCount() +  "   "
						+ n + "    " + ringAverSize + endLine;
					
					outf.write(outData.getBytes());
				}
			}
			
			outf.close();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	public void showFullAtomMappingsCDKIsomorphism(String smarts, String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		
		System.out.println("Show full atom/bond  mapping for Man_search of  " + smarts + "  against  " + smiles);
		List bondMapList = man.getBondMappings(mol);
		System.out.println("bondMapList.size() = " + bondMapList.size());
		
		int n = 0;		
		for (Object aBondMapping: bondMapList)
		{
			n++;
			System.out.println("# " + n);
			System.out.print("  bond mapping: " + bondMappingToString((List)aBondMapping,mol));
						
			List<IAtom> atomMapping = man.generateFullAtomMapping((List)aBondMapping, mol, man.getQueryContaner());
			System.out.print("  atom mapping: ");
			for (int i = 0; i < atomMapping.size(); i++)
			{
				IAtom a = atomMapping.get(i);
				if (a == null)
					System.out.print(" null");
				else
					System.out.print(" "+a.getSymbol()+"<"+mol.getAtomNumber(a)+">");
			}
			System.out.println();
		}
	}
	
	
	String bondMappingToString(List bondMapping, IAtomContainer target)
	{
		StringBuffer sb = new StringBuffer();
		RMap rmap;
		
		HashMap <Integer,Integer> hmap = new HashMap <Integer,Integer>();
		
		//Converting the CDK bondMapping to a normal HashMap
		for (Object aMap : bondMapping) 
		{
			rmap = (RMap) aMap;
			Integer key  = new Integer (rmap.getId2()); 
			Integer value  = new Integer (rmap.getId1());
			
			//Id2 is the index of the query bond (which is the key)
			//id1 is the index of the target bond
			hmap.put(key, value);
		}
		
		for (int i = 0; i < bondMapping.size(); i++)
		{
			int boNum = hmap.get(new Integer(i)).intValue();
			IBond b = target.getBond(boNum);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			int at0Index = target.getAtomNumber(at0);
			int at1Index = target.getAtomNumber(at1);
			
			sb.append(i + "->("+at0.getSymbol()+at0Index+","+at1.getSymbol()+at1Index+")  ");
		}
		
		return(sb.toString());
	}
	
	
	public void showFullIsomorphismMappings(String smarts, String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		System.out.println("Man_search " + smarts + " in " + smiles);
		IAtomContainerSet mappingSet = man.getAllIsomorphismMappings(mol);
		for (int k = 0; k < mappingSet.getAtomContainerCount(); k++)
		{	
			IAtomContainer ac = mappingSet.getAtomContainer(k);
			System.out.print("Mapping: ");
			for (int i = 0; i < ac.getAtomCount(); i++)
			{
				IAtom a = ac.getAtom(i);
				if (a == null)
					System.out.print(" null");
				else
					System.out.print(" "+a.getSymbol()+"<"+mol.getAtomNumber(a)+">");
			}
			
			System.out.print("      " + SmartsHelper.moleculeToSMILES(ac,true));
			//System.out.println();
		}
		System.out.println();
	}
	
	
	public void testIsomorphismTester(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		sp.setSMARTSData(mol);
		System.out.println("IsomorphismTester: " + smarts  + "  in  " + smiles + 
				"   " + isoTester.hasIsomorphism(mol));
		//boolean res = checkSequence(query,isoTester.getSequence());
		//isoTester.printDebugInfo();
		//System.out.println("sequnce check  -- > " + res);		
	}
	
	
	public void testIsomorphismPositions(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		sp.setSMARTSData(mol);
		List<Integer> pos = isoTester.getIsomorphismPositions(mol);
		System.out.println("Isomorphism Positions: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < pos.size(); i++)
			System.out.print("  " + pos.get(i).intValue());
		System.out.println();
	}
	
	//helper function
	public String getBondMapping(IMolecule mol, List<IAtom> amap) 
	{
		List<IBond> v = isoTester.generateBondMapping(mol,amap);
		StringBuffer sb = new StringBuffer();		
		for (int i = 0; i < v.size(); i++)		
			sb.append(" "+mol.getBondNumber(v.get(i)));
		return(sb.toString());
	}
	
	public void testIsomorphismMapping(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		List<IAtom> map = isoTester.getIsomorphismMapping(mol);
		System.out.println("Isomorphism Mapping: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < map.size(); i++)
		{	
			IAtom a = map.get(i);
			int n = mol.getAtomNumber(a);
			System.out.print("  " + n);
		}
		System.out.println();
		System.out.println("Bond mapping: " + getBondMapping(mol,map));		
	}
	
	public void testIsomorphismAllMappings(String smarts, String smiles) throws Exception
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		List<List<IAtom>> allmaps = isoTester.getAllIsomorphismMappings(mol);
		System.out.println("Isomorphism Mapping: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < allmaps.size(); i++)
		{	
			List<IAtom> map = allmaps.get(i);
			for (int j = 0; j < map.size(); j++)
			{	
				IAtom a = map.get(j);
				int n = mol.getAtomNumber(a);
				System.out.print("  " + n);
			}
			System.out.println("    bond mapping: " + getBondMapping(mol,map));
		}
	}
	
	
	public void testAtomSequencingFromFile(String fname)
	{
		int nError = 0;
		int nTests = 0;
		int nParserFails = 0;
		try
		{
			RandomAccessFile f = new RandomAccessFile(fname, "r");
			
			long length = f.length();
			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();								
				String frags[] = SmartsHelper.getCarbonSkelletonsFromString(line);
				for (int k = 0; k < frags.length; k++)
				{
					//System.out.println("frag="+frags[k]);
					
					IQueryAtomContainer query  = sp.parse(frags[k].trim());
					sp.setNeededDataFlags();
					String errorMsg = sp.getErrorMessages();
					if (!errorMsg.equals(""))
					{
						System.out.println("line="+line);						
						System.out.println("Smarts Parser errors: " + errorMsg);
						
						nParserFails++;
						continue;
					}
					isoTester.setQuery(query);
					boolean res = checkSequence(query,isoTester.getSequence());			
					System.out.println(frags[k].trim() + " -- > " + (res?"OK":"FAILED"));
					if (!res)
						nError++;	
					nTests++;
				}
			}
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
		System.out.println("\nNumber of test = " + nTests);
		System.out.println("Number of errors = " + nError);
		System.out.println("Number of parser fails = " + nParserFails);
	}
	
	
	public void testAtomSequencing(String smarts[])
	{					
		int nError = 0;		
		for (int i = 0; i < smarts.length; i++)
		{
			IQueryAtomContainer query  = sp.parse(smarts[i]);
			sp.setNeededDataFlags();
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{
				System.out.println("Smarts Parser errors:\n" + errorMsg);			
				continue;
			}
			isoTester.setQuery(query);
			boolean res = checkSequence(query,isoTester.getSequence());			
			System.out.println(smarts[i] + " -- > " + (res?"OK":"FAILED"));
			if (!res)
				nError++;			
		}
		
		System.out.println("\nNumber of errors = " + nError);		
	}
		
	
	public boolean checkSequence(IQueryAtomContainer query, List<QuerySequenceElement> sequence)
	{
		ChemObjectFactory factory = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		IAtomContainer skelleton = factory.getCarbonSkelleton(sequence);		
		//System.out.println("skelleton = " + SmartsHelper.moleculeToSMILES(skelleton));
		try
		{
			boolean res = UniversalIsomorphismTester.isSubgraph(skelleton, query);			
			return(res);
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
		}
		return(false);
	}
	
	int testSMARTStoChemObj(String smarts) throws Exception
	{	
		IQueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))	
		{	
			System.out.println("Smarts Error: " + errorMsg);
			return(-1);
		}	
		
		//smToChemObj.forceAromaticBondsAlways = true;
		IAtomContainer mol =  smToChemObj.extractAtomContainer(query);
		System.out.println(smarts + "  --> " + SmartsHelper.moleculeToSMILES(mol,true));
		return(0);
	}
	
	int testSMARTSBondToIBond(String smarts)
	{	
		IQueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))	
		{	
			System.out.println("Smarts Error: " + errorMsg);
			return(-1);
		}	
		
		System.out.println(smarts + "  --> bond list converted to simple bonds when possible: ");
		for (int i = 0; i < query.getBondCount(); i++)
		{
			IBond bo = smToChemObj.toBond(query.getBond(i));
			if (bo == null)
				System.out.println("#" + i + "  null");
			else
			{
				String aromatic = "";
				if (bo.getFlag(CDKConstants.ISAROMATIC))
					aromatic = " aromatic";
				System.out.println("#" + i + "  " + SmartsHelper.bondOrderToIntValue(bo) + "  " +aromatic + 
						"           class = " + query.getBond(i).getClass().getName());
			}
		}
		
		
		return(0);
	}
	
	int testExtractAtomContainer(String smarts) throws Exception
	{	
		IQueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))		
			return(-1);
		
		smToChemObj.forceAromaticBondsAlways = true;
		IAtomContainer mol =  smToChemObj.extractAtomContainer(query);
		
		System.out.println("Creating a Chem object with extractAtomContainer ");
		System.out.println(smarts);		
		printAromaticity(mol);
		System.out.println("Object to SMILES");
		System.out.println(SmartsHelper.moleculeToSMILES(mol,true));
		
		System.out.println("Creating a Chem object with teh SMILES parser ");
		System.out.println(smarts);	
		IMolecule mol2 =  SmartsHelper.getMoleculeFromSmiles(smarts);
		printAromaticity(mol2);
		System.out.println("Object to SMILES");
		System.out.println(SmartsHelper.moleculeToSMILES(mol2,true));
		
		
		if (mol.getAtomCount() != query.getAtomCount())
			return(1);
		if (mol.getBondCount() != query.getBondCount())
			return(2);
		
		try
		{
			boolean res = UniversalIsomorphismTester.isSubgraph(mol, query);			
			if (!res)
				return(3);			
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
			return(100);
		}		
		
		return(0);
	}
	
	public void testWithFile(String fname, String testType)
	{
		List<String> failSmiles = new ArrayList<String>(); 
		if ((!testType.equals("ExtractAtomContainer")))
		{	
			System.out.println("Incorrect test type: " + testType);
			System.out.println("Following ones are allowed: ExtractAtomContainer");
			return;
		}
		int nError = 0;
		int nTests = 0;
		int nParserFails = 0;
		try
		{
			RandomAccessFile f = new RandomAccessFile(fname, "r");
			
			long length = f.length();
			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();	
				int res = 0;
				if (testType.equals("ExtractAtomContainer"))
					res = testExtractAtomContainer(line);
				
				//Statistics
				if (res < 0)
					nParserFails++;
				if (res > 0)
				{	
					//System.out.println(line);
					//System.out.println(res);
					failSmiles.add(line);
					nError++;
				}	
				nTests++;
				//System.out.println("Test # " + nTests);
			}
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
		System.out.println("\nNumber of test = " + nTests);		
		System.out.println("Number of parser fails = " + nParserFails);
		System.out.println("Number of errors = " + nError);
		for (int i = 0; i < failSmiles.size(); i++)
			System.out.println(failSmiles.get(i));
	}
	
		
	
	public void printAromaticity(IAtomContainer mol)
	{	
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			System.out.println("Atom " + i + "  aromatic = " +atom.getFlag(CDKConstants.ISAROMATIC));
		}
		
		for (int i = 0; i < mol.getBondCount(); i++)
		{
			IBond bond = mol.getBond(i);
			System.out.println("Bond " + i + "  aromatic = " +bond.getFlag(CDKConstants.ISAROMATIC));
		}
	}
	
	public void printAromaticity(String smiles)throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		printAromaticity(mol);
	}
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		List<IAtom> atoms = man.getFirstPosAtomMappings(mol);
		System.out.println(smarts + " mapped against " + smiles + 
						" gave " + atoms.size()+" atoms:");
		for (int i = 0; i < atoms.size(); i++)
		{
			System.out.print(" "+mol.getAtomNumber(atoms.get(i)));
		}
		System.out.println();
	}
	
	void modify(int a[])
	{
		a[1] = 10;
		a[2] = 20;
	}
	
	public void testIntArray()
	{
		int a[] = new int [4];
		a[0] = 1;
		a[1] = 2;
		a[2] = 3;
		a[3] = 4;
		int c[] = a.clone();
		SmartsHelper.printIntArray(a);
		SmartsHelper.printIntArray(c);
		modify(a);
		SmartsHelper.printIntArray(a);
		SmartsHelper.printIntArray(c);
	}
	
	public void testIntParsing(String s)
	{	
		Integer intObj = Integer.parseInt(s);
		int n = intObj.intValue();
		System.out.println(n);		
		System.out.println(s.substring(7,2));
	}
	
	public void testAtomIndexesForMapping(int nGA, int nTA)
	{
		if (nGA == 2)
		{
			for(int i = 0; i < nTA; i++)			
				//if (el.atoms[0].matches(targetAt.get(i)))					
					for(int j = 0; j < nTA; j++)						
						if (i != j)
							//if (el.atoms[1].matches(targetAt.get(j)))
							{
								System.out.println(i+" "+j);
							}
					
			return;
		}
		
		if (nGA == 3)
		{
			for(int i = 0; i < nTA; i++)			
				//if (el.atoms[0].matches(targetAt.get(i)))					
					for(int j = 0; j < nTA; j++)						
						if (i != j)
							//if (el.atoms[1].matches(targetAt.get(j)))
								for(int k = 0; k < nTA; k++)
									if ((k != i) && (k != j))
										//if (el.atoms[2].matches(targetAt.get(k)))
										{
										System.out.println(i+" "+j+ " "+k);
										}					
			return;
		}
		
		//This case should be very rare (el.atoms.length >= 4)
				
		//a stack which is used for obtaining all
		//posible mappings between el.atoms and targetAt
		//The stack element is an array t[], where t[k] means that 
		//el.atoms[k] is mapped against atom targetAt(t[k])
		//t[t.lenght-1] is used as a work variable which describes how mamy 
		//element of the t array are mapped
		Stack<int[]> st = new Stack<int[]>();
		
		int num = 0;
		
		//Stack initialization
		for(int i = 0; i < nTA; i++)
		{
			//if (el.atoms[0].matches(targetAt.get(i)))
			{
				int t[] = new int[nGA+1];
				t[t.length-1] = 1;
				t[0] = i;				
				st.push(t);
				
				/*
				System.out.print("push in ");
				for(int k = 0; k < t.length-1; k++)
					System.out.print(t[k]+" ");
				System.out.println();
				*/
			}
		}
		
		while (!st.isEmpty())
		{
			int t[] = st.pop();
			int n = t[t.length-1];
			
			if (n == t.length-1)
			{
				/*
				for(int k = 0; k < t.length-1; k++)
					System.out.print(t[k]+" ");
				System.out.println();
				*/
				num++;
				continue;
			}
			
			for(int i = 0; i < nTA; i++)
			{	
				//Check whether i is among first elements of t
				boolean Flag = true;
				for (int k = 0; k < n; k++)
					if ( i == t[k]) 
					{
						Flag = false;
						break;
					}
				
				if (Flag)
				//if (el.atoms[n].matches(targetAt.get(i)))
				{	
					//new stack element
					int tnew[] = new int[nGA+1];
					for(int k = 0; k < n; k++)
						tnew[k] = t[k];
					tnew[n] = i;
					tnew[t.length-1] = n+1;
					st.push(tnew);
					
					/*
					System.out.print("push in ");
					for(int k = 0; k < t.length-1; k++)
						System.out.print(tnew[k]+" ");
					System.out.println();
					*/
				}
			}
		}
		System.out.println("num = "+num);
	}
	
	public void testCML(String smiles) throws Exception
	{
		System.out.println("Writing " + smiles + " to CML file");
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);		
		CMLUtilities cmlut = new CMLUtilities();
		cmlut.setCMLSMARTSProperties(mol);
		
		//sp.prepareTargetForSMARTSSearch(true,true,true,true,true,false, mol);
		//int t[] = new int[3];
		//mol.getAtom(0).setProperty("x", new Integer(505));
		//mol.getAtom(0).setProperty("y", "TT");		
		//System.out.println(SmartsHelper.atomPropertiesToString(mol.getAtom(0)));		
		//System.out.println("MyProperty = " + mol.getAtom(0).getProperty("xy").toString());		
		//System.out.println("XXMyProperty2 = " + mol.getAtom(0).getProperty("XXMyProperty2").toString());
		
		//String s = "MyProperty";
		//System.out.println(s+ "  hashcode = " +s.hashCode());
		//String s1 = "MyProperty";
		//System.out.println(s1+ "  hashcode = " +s1.hashCode());
		
		//if (true) return;
		
		try
		{	
			StringWriter output = new StringWriter();			
			CMLWriter cmlwriter = new CMLWriter(output);
			cmlwriter.write(mol);
			cmlwriter.close();
			String cmlcode = output.toString();
			System.out.println(cmlcode);
			IChemFile chemFile = parseCMLString(cmlcode);			
			IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
			
			System.out.println("-------------------------");
			String smiles2 = SmartsHelper.moleculeToSMILES(mol2,true);
			
			cmlut.extractSMARTSProperties(mol2);
			System.out.println(smiles2);
			for (int i = 0; i < mol2.getAtomCount(); i++)
			{	
				System.out.println("AtType" + i+ " = " + mol2.getAtom(i).getSymbol());
				System.out.println(SmartsHelper.atomPropertiesToString(mol2.getAtom(i)));
			}	
			
			
			//System.out.println(SmartsHelper.atomPropertiesToString(mol2.getAtom(0)));
			//System.out.println("get - x = " + mol2.getAtom(0).getProperty("x").toString());
			//System.out.println("get - y = " + mol2.getAtom(0).getProperty("y").toString());
			
			//System.out.println("Properties Map Size = " + mol2.getAtom(0).getProperties().size());
			//System.out.println("MyProperty Type = " + mol2.getAtom(0).
			//								getProperty("MyProperty").getClass().getName());
			//
			
			//System.out.println("RingData2 = " + mol2.getAtom(0).getProperty("RingData2").toString());
			
			
			
			//CMLReader reader = new CMLReader("\\test.cml");
			//CMLReader reader = new CMLReader();
            //IChemFile chemFile = SilentChemObjectBuilder.getInstance().newChemFile();            
            //reader.read(chemFile);
			//Molecule m = (Molecule)reader.read(new Molecule());
			
			/*
			FileWriter output = new FileWriter("\\molecule.cml");
			CMLWriter cmlwriter = new CMLWriter(output);
			cmlwriter.write(mol);
			cmlwriter.close();
			*/
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	private IChemFile parseCMLString(String cmlString) throws Exception {
        IChemFile chemFile = null;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        return chemFile;
    }
	
	
	String getFingerprint(String smiles) throws Exception
	{
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		Fingerprinter fp = new Fingerprinter();
		try
		{
			BitSet bs = fp.getFingerprint(mol);			
			return(bs.toString());			
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return("");
	}
	
	void testFingerprint() throws Exception
	{
		String smiles[] = {"c1ccccc1", "C1CCCCC1","C1CCCCC1" };
		for (int i = 0; i < smiles.length; i++)
		{	
			System.out.println(smiles[i]);
			System.out.println(getFingerprint(smiles[i]));
		}	
	}
	
	String toBitString(BitSet bs)
	{
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < bs.size(); i++)
		{	
			if (bs.get(i))
				strBuff.append("1");
			else
				strBuff.append("0");
		}
		return(strBuff.toString());
		
	}
	
	void testHydrogenCount() throws Exception
	{
		IAtomContainer mol = new AtomContainer();
		mol.addAtom(new Atom("C"));
		
		/**
		https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=20024&atid=120024
		Integer hc = mol.getAtom(0).getHydrogenCount();
		 */
		Integer hc = mol.getAtom(0).getImplicitHydrogenCount();
		
		Integer fc = mol.getAtom(0).getFormalCharge();
		Integer nc = mol.getAtom(0).getFormalNeighbourCount();
		Integer v = mol.getAtom(0).getValency();
		System.out.println("getHydrogenCount() = " + hc);
		System.out.println("getFormalCharge() = " + fc);
		System.out.println("getFormalNeighbourCount() = " + nc);
		System.out.println("getValency() = " + v);		
	}
		
	void testFragmentation(String smiles) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		cof.setAtomSequence(mol, mol.getAtom(0));
		for (int i = 0; i < cof.sequence.size(); i++)
		{
			IAtomContainer frag = cof.getFragmentFromSequence(i);
			System.out.println(cots.getSMILES(frag)); 
		}
	}
	
	void testChemObjectToSmiles(String smiles) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		System.out.println(smiles+ "  --->  "+cots.getSMILES(mol)); 
	}
	
	void testProduceStructuresExhaustively(String smiles, int maxNumSteps) throws Exception
	{
		System.out.println("Producing structs form " + smiles+ "   maxNumSteps = " + maxNumSteps);
		System.out.println("-------------------------------");
		List<StructInfo> vStr = new ArrayList<StructInfo>();
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		
		cof.produceStructuresExhaustively(mol, vStr, maxNumSteps, 100);
		
		for (int i = 0; i < vStr.size(); i++)
			System.out.println(vStr.get(i).smiles);
	}
	
	void printSequence(String smiles) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		cof.setAtomSequence(mol, mol.getAtom(0));
		System.out.println(smiles);
		for (int i = 0; i < cof.sequence.size(); i++)
			System.out.println("SeqEl " + i + " : "+cof.sequence.get(i).toString());
	}
	
	void produceStructures() throws Exception
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		List<StructInfo> vStr = new ArrayList<StructInfo>();
		cof.produceStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					5, 50000, 8, vStr, "/java_frags.txt");
		
	}
	
	void produceRandomStructures() throws Exception
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		List<StructInfo> vStr = new ArrayList<StructInfo>();
		cof.produceRandomStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					30, 5, 50000, vStr, "/java_random_frags.txt");
	}
	
	void makeStructureStatistics() throws Exception
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		cof.performStructureStatistics("/exhaustive-str-set-ambit-first-900str.txt", 
							"../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
							100, 5000, "/frags_stat_ambit_5000.txt");
	}
	
	void filterStructsBySize(String inputSmilesFile, String outSmilesFile, int maxNumAtoms)	
	{
		List<String> outSmiles = new ArrayList<String>();
		try
		{	
			File file = new File(inputSmilesFile);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
						
			int n = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				if (n % 1000 == 0)
					System.out.print(" " + n + "  " + outSmiles.size());
				
				String line = f.readLine();
				IQueryAtomContainer q = sp.parse(line);
				if (q.getAtomCount() <= maxNumAtoms)
					outSmiles.add(line);
			}
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		
		try
		{	
			File file = new File(outSmilesFile);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i<outSmiles.size(); i++)
				f.write((outSmiles.get(i) + "\r\n").getBytes());
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	void testSmartsSreeningKeys() throws Exception
	{
		SmartsScreeningKeys smKeys = new SmartsScreeningKeys();
		List<String> keys = smKeys.getKeys();
		for (int i = 0; i < keys.size(); i++)
			System.out.println(" " + (i+1) + "  " + keys.get(i));
	}
	
	int compareIsoTester(String smarts, String mdlFile)
	{
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return -1;
		}
		
		int numRecords = 5000;
		boolean CDKRes, isoRes;
		int numDiff = 0;
		int numOfIso = 0;
		
		try
		{
			String fileName;
			if (mdlFile == null)			 
				fileName = "../src/test/resources/einecs/einecs_structures_V13Apr07.sdf";
			else
				fileName = mdlFile;
				
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(fileName),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				record++;
				if (record % 1000 == 0)
					System.out.print("  -->" + record);
				if (record > numRecords)
					break;
				
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					
					CDKRes = UniversalIsomorphismTester.isSubgraph(mol, query);						
					isoTester.setQuery(query);						
					isoRes = isoTester.hasIsomorphism(mol);	
					if (isoRes)
						numOfIso++;
					
					if (CDKRes != isoRes)
						numDiff++;
				}
			}
			System.out.println();	
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		System.out.println("  numDiff = " + numDiff + "  numIso = " + numOfIso + "   " + smarts);
		return (numDiff);
	}
	
	
	List<IAtomContainer> getContainersFromMDL(String mdlFile)
	{	
		List<IAtomContainer> structures = new ArrayList<IAtomContainer>();
		
		try
		{
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				record++;				
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					structures.add(mol);
				}
			}	
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (structures);
	}
	
	void testStructureAnalysis(String mdlFile)
	{	
		StructureSetAnalyzer analyzer = new StructureSetAnalyzer(SilentChemObjectBuilder.getInstance());
		analyzer.structures = getContainersFromMDL(mdlFile);
		System.out.println("Loaded " + analyzer.structures.size() + " structures");
		
		analyzer.maxHitStructSize = 20;
		analyzer.stochasticAnalysis();
	}
	
	
	
	void compareIsoTesterMulti() throws Exception
	{
		SmartsScreeningKeys smKeys = new SmartsScreeningKeys();
		List<String> keys = smKeys.getKeys();
		for (int i = 0; i < keys.size(); i++)
			compareIsoTester (keys.get(i), null);
	}
	
	
	void compareIsoTesterMulti(String inputSmilesFile)
	{
		try
		{	
			File file = new File(inputSmilesFile);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
						
			int n = 0;
			int nFailedStr = 0;
			while (f.getFilePointer() < length)
			{	
				String line = f.readLine().trim();
				if (line.length() < 10)  //Only larger fragments are tested
					continue;
				n++;
				System.out.println("  " + line);
				int res = compareIsoTester (line, null);
				if (res > 0)
					nFailedStr++;
				System.out.println("nFailedStr = " + nFailedStr + "    nTest = " + n);
				System.out.println();
			}
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	void testScreeningKeys(String target, String queryString) throws Exception
	{		
		List<String> myKeys = new ArrayList<String>(); 
		myKeys.add("CCC");
		myKeys.add("CCN");
		myKeys.add("CCO");
		//Screening screen = new Screening(myKeys);
		Screening screen = new Screening(SilentChemObjectBuilder.getInstance());
		
		System.out.println("Query " + queryString);
		IQueryAtomContainer query = sp.parse(queryString);
		screen.setQuery(query);
		System.out.println(screen.queryKeysToString());
		System.out.println();
				
		System.out.println("Target " + target);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(target);
		ScreeningData sd = screen.getScreeningDataForTarget(mol);
		System.out.println(screen.strKeysToString(sd.structureKeys));
		
		
	}
	
	public void printSSSR(String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		SSSRFinder sssrf = new SSSRFinder(mol);
		IRingSet ringSet = sssrf.findSSSR();
		
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++)
		{
			System.out.print("  Ring:");
			IAtomContainer ring =  ringSet.getAtomContainer(i);
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
				IAtom a = ring.getAtom(k);
				System.out.print(" "+ mol.getAtomNumber(a));
			}
			System.out.println();
		}	
		System.out.println();
	}	
	
	
	public void testRingInfo(String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagTargetPreprocessing)
			preProcess(mol);
		
		SSSRFinder sssrf = new SSSRFinder(mol);
		IRingSet ringSet = sssrf.findSSSR();
		
		IRingSet rsConnected = ringSet.getConnectedRings((IRing)ringSet.getAtomContainer(0));
		
		System.out.println(smiles);
		System.out.print("Connected Rings to ring 0:");
		for (int i = 0; i < rsConnected.getAtomContainerCount(); i++)
		{
			System.out.print("  Ring:");
			IAtomContainer ring =  ringSet.getAtomContainer(i);
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
				IAtom a = ring.getAtom(k);
				System.out.print(" "+ mol.getAtomNumber(a));
			}
			System.out.println();
		}	
		System.out.println();
	}
	
	
	public void testConvertKekuleSmartsToAromatic(String smarts)	 throws Exception
	{
		System.out.println("testing Kekule to Aromatic: " + smarts);
		IQueryAtomContainer qac = sp.parse(smarts);
		smToChemObj.convertKekuleSmartsToAromatic(qac);
	}
	
	public void testSMIRKS(String smirks, String targetSmiles, int SSMode) throws Exception
	{
		FlagSSMode = SSMode;
		testSMIRKS(smirks, targetSmiles);
	}
	
	public void testSMIRKS(String smirks, String targetSmiles, ReactionOperation operation) throws Exception
	{
		FlagReactionOperation = operation;
		testSMIRKS(smirks, targetSmiles);
	}
	
	public void testSMIRKS(String smirks, String targetSmiles) throws Exception
	{
		System.out.println("Testing SMIRKS: " + smirks);		
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		smrkMan.FlagSSMode = FlagSSMode;
		
		//Product processing flags
		smrkMan.FlagProcessResultStructures = FlagProductPreprocessing;
		smrkMan.FlagClearHybridizationBeforeResultProcess = FlagClearHybridizationOnProductPreProcess;
		smrkMan.FlagClearImlicitHAtomsBeforeResultProcess = this.FlagClearImlicitHAtomsBeforeProductPreProcess;
		smrkMan.FlagClearAromaticityBeforeResultProcess = this.FlagClearAromaticityBeforePreProcess;
		smrkMan.FlagAddImlicitHAtomsOnResultProcess = this.FlagAddImlicitHAtomsOnProductPreProcess;
		smrkMan.FlagConvertAddedImlicitHToExplicitOnResultProcess = this.FlagImplicitHToExplicitOnProductPreProcess;
		smrkMan.FlagConvertExplicitHToImplicitOnResultProcess = this.FlagExplicitHToImplicitOnProductPreProcess;
		
		smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = FlagDoubleBondAromaticityNotSpecified;
		
		SMIRKSReaction reaction = smrkMan.parse(smirks);		
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			//System.out.println("\n" + reaction.transformationDataToString());
			return;
		}
		
		if (FlagPrintTransformationData)
			System.out.println(reaction.transformationDataToString());
		
		
		if (targetSmiles.equals(""))
			return;
		
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		
		if (FlagTargetPreprocessing)
			this.preProcess(target);
		
		
		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Target (reactant):");
			System.out.println("Reactant atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Reactant bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}
		
		switch (FlagReactionOperation)
		{
		case APPLY:
			boolean res = smrkMan.applyTransformation(target, reaction);
			
			if (FlagPrintAtomAttributes)
			{	
				System.out.println("Product:");
				System.out.println("Product atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
				System.out.println("Product bond attributes:\n" + SmartsHelper.getBondAttributes(target));
			}	
			
			System.out.println("    " +  SmartsHelper.moleculeToSMILES(target,true));
			
			
			String transformedSmiles = SmartsHelper.moleculeToSMILES(target,true);

			if (res)
				System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
			else
				System.out.println("Reaction not appicable!");
			break;
			
		case CombinedOverlappedPos:
			IAtomContainerSet resSet = smrkMan.applyTransformationWithCombinedOverlappedPos(target, null, reaction);
			if (resSet == null)
				System.out.println("Reaction not appicable!");
			else
			{
				System.out.println("Reaction application With Combined Overlapped Positions: ");
				for (int i = 0; i < resSet.getAtomContainerCount(); i++)
					System.out.println(SmartsHelper.moleculeToSMILES(resSet.getAtomContainer(i),true));
			}	
			break;
		
		case SingleCopyForEachPos:
			IAtomContainerSet resSet2 = smrkMan.applyTransformationWithSingleCopyForEachPos(target, null, reaction, FlagSSModeForSingleCopyForEachPos);
			if (resSet2 == null)
				System.out.println("Reaction not appicable!");
			else
			{
				System.out.println("Reaction application With Single Copy For Each Position: ");
				for (int i = 0; i < resSet2.getAtomContainerCount(); i++)
					System.out.println(SmartsHelper.moleculeToSMILES(resSet2.getAtomContainer(i),true));
			}	
			break;
		}
		
		System.out.println();
	}
	
	
	//This is the old variant
	public void testSMIRKS_(String smirks, String targetSmiles) throws Exception
	{
		System.out.println("Testing SMIRKS: " + smirks);		
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		smrkMan.FlagSSMode = FlagSSMode;
		smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = FlagDoubleBondAromaticityNotSpecified;
		SMIRKSReaction reaction = smrkMan.parse(smirks);		
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			//System.out.println("\n" + reaction.transformationDataToString());
			
			//System.out.println("Reactant Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.reactant));
			//System.out.println("Product Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.product));
			return;
		}
		
		//System.out.println("reactant SMARTS: " + smartsHelper.toSmarts(reaction.reactant) );
		//System.out.println("product SMARTS: " + smartsHelper.toSmarts(reaction.product) );
		
		if (FlagPrintTransformationData)
			System.out.println(reaction.transformationDataToString());
		
		//System.out.println("Reactant Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.reactant));
		//System.out.println("Product Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.product));
		//System.out.println();
		
		if (targetSmiles.equals(""))
			return;
		
		
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		
		
		if (FlagTargetPreprocessing)
			this.preProcess(target);
		
		
		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Reactant atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Reactant bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}
		
		boolean res = smrkMan.applyTransformation(target, reaction);


		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Product before pre-processiong:");
			System.out.println("Product atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Product bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}	

		System.out.println("    " +  SmartsHelper.moleculeToSMILES(target,true));


		if (FlagProductPreprocessing)
			this.preProcessProduct(target);

		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Product after pre-processiong:");
			System.out.println("Product atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Product bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}	

		String transformedSmiles = SmartsHelper.moleculeToSMILES(target,true);

		if (res)
			System.out.println("Reaction application: " + targetSmiles + "  -->  " + transformedSmiles);
		else
			System.out.println("Reaction not appicable!");
			
	}	
	
	public void testSmiles2Smiles(String smiles) throws Exception
	{
		System.out.println("Testing smiles: " + smiles);
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(smiles);
		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}	
		
		String smiles2 = SmartsHelper.moleculeToSMILES(target,true); 
		SmilesGenerator smiGen = new SmilesGenerator();
		smiGen.setUseAromaticityFlag(true);
		String smiles3 = smiGen.createSMILES(target);
		//String smiles4 = smiGen.createChiralSMILES(target, new boolean[0]);   //This function requires 2D coordinates
		System.out.println(smiles + "  --> " + smiles2 + "    " + smiles3);
	}
	
	public void testSmiles2MOLFile(String smiles, String molFileName) throws Exception
	{
		System.out.println("Testing smiles: " + smiles);
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(smiles);
		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}	
		
		
		System.out.println("Writing molecule to file: " + molFileName);
		//This is specialized Ambit version of the MDL Writer (special hack to code aromatic bonds as bonds of type 4)
		//MDLWriter writer = new MDLWriter(new FileWriter(new File(molFileName)));
		//writer.write((IMolecule)target);
		//writer.close();
		
		MDLV2000Writer writer = new MDLV2000Writer(new FileWriter(new File(molFileName)));
		writer.writeMolecule(target);
		writer.close();
		
	}
	
	public void testMOL2MOLFile(String inMolFileName, String outMolFileName) throws Exception
	{
		System.out.println("Testing molecule from file: " + inMolFileName);
		
		IteratingMDLReader reader = new IteratingMDLReader(new FileReader(new File(inMolFileName)), DefaultChemObjectBuilder.getInstance());
		IAtomContainer target = null;
		if (reader.hasNext())
			target = reader.next();
		reader.close();
		
		if (target == null)
		{
			System.out.println("Read null object");
			return;
		}
				
		String smiles = SmartsHelper.moleculeToSMILES(target,true);
		System.out.println("    " + smiles);
		
		if (FlagPrintAtomAttributes)
		{	
			System.out.println("Atom attributes:\n" + SmartsHelper.getAtomsAttributes(target));
			System.out.println("Bond attributes:\n" + SmartsHelper.getBondAttributes(target));
		}	
		
		
		System.out.println("Writing molecule to file: " + outMolFileName);
		MDLV2000Writer writer = new MDLV2000Writer(new FileWriter(new File(outMolFileName)));
		writer.writeMolecule(target);
		writer.close();
	}
	
	
	
	
	public void testEquivalenceTestes(String targetSmiles) throws Exception
	{
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		
		if (FlagTargetPreprocessing)
			preProcess(target);
		
		EquivalenceTester eqTest = new  EquivalenceTester();
		
		eqTest.setTarget(target);
		eqTest.quickFindEquivalentTerminalHAtoms();
		System.out.println("Testting equivalnece tester: " +targetSmiles);
		for (int i = 0; i < eqTest.atomClasses.length; i++)
			System.out.print(" " + eqTest.atomClasses[i]);
		
		System.out.println();
	}
	
	
	public void testCombinations()
	{
		List<List<Integer>> clusterIndexes = new ArrayList<List<Integer>>(); 
		List<Integer> vInt;
		
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		vInt.add(new Integer(1));
		vInt.add(new Integer(2));
		clusterIndexes.add(vInt);
		
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		vInt.add(new Integer(1));
		vInt.add(new Integer(2));
		vInt.add(new Integer(3));
		clusterIndexes.add(vInt);
		
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		vInt.add(new Integer(1));
		clusterIndexes.add(vInt);
		
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		vInt.add(new Integer(1));
		clusterIndexes.add(vInt);
		
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		vInt.add(new Integer(1));
		clusterIndexes.add(vInt);
		
		
		
		int comb[] = new int[clusterIndexes.size()];
		for (int i = 0; i < comb.length; i++)
			comb[i] = 0;
		
		//Generation of next combination
		int digit = 0;
		do 
		{
			//printing the combination 
			for (int i = comb.length-1; i >= 0; i--)
				System.out.print(comb[i]);
			System.out.println();
			
			digit = 0;
			while (digit < comb.length)
			{
				comb[digit]++;
				if(comb[digit] == clusterIndexes.get(digit).size())
				{
					comb[digit] = 0;
					digit++;
				}
				else
					break;
			}	
		}
		while (digit < comb.length);
	}
	
	public void testAtomAttributes(String targetSmiles) throws Exception
	{
		System.out.println("Pre atom configuration");
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(targetSmiles);
		
		if (FlagTargetPreprocessing)
			preProcess(target);
		
		String atr = SmartsHelper.getAtomsAttributes(target);
		System.out.println(atr);
		
		System.out.println("Post atom configuration");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
		String atr1 = SmartsHelper.getAtomsAttributes(target);
		System.out.println(atr1);
		
	}
	
	public void testBondIndexChange(String smarts) throws Exception
	{
		IQueryAtomContainer q = sp.parse(smarts);
		if (!sp.getErrorMessages().equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + sp.getErrorMessages());			
			return;
		}
		
		System.out.println(smarts);		
		IAtomContainer mol0 =  smToChemObj.extractAtomContainer(q);
		System.out.println(SmartsHelper.moleculeToSMILES(mol0,true));
		for (int i = 0; i < q.getBondCount(); i++)
		{
			IBond b = q.getBond(i);
			int ind0 = q.getAtomNumber(b.getAtom(0));
			int ind1 = q.getAtomNumber(b.getAtom(1));
			
			System.out.println("bond #" + i + "   --> " +ind0 + "  " + ind1);
		}
		
		/*
		boolean res = fixAtomIndexes(q);
		System.out.println("Fixing res = " + res);
		IAtomContainer mol1 =  smToChemObj.extractAtomContainer(q);
		System.out.println(SmartsHelper.moleculeToSMILES(mol1));
		for (int i = 0; i < q.getBondCount(); i++)
		{
			IBond b = q.getBond(i);
			int ind0 = q.getAtomNumber(b.getAtom(0));
			int ind1 = q.getAtomNumber(b.getAtom(1));
			
			System.out.println("bond #" + i + "   --> " +ind0 + "  " + ind1);
		}
		*/
	}
	
	public void testPreprocessing(String smiles, boolean FlagConnectAtom) throws Exception
	{
		System.out.println("Testing the preprorocessing for " + smiles);
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		if (FlagConnectAtom)
		{
			//Create an atoms and connect it to the first atom
			IAtom a = new Atom();
			a.setSymbol("C");		
			mol.addAtom(a);

			IBond b = MoleculeTools.newBond(mol.getBuilder());
			IAtom a01[] = new IAtom[2];
			a01[0] = a;
			a01[1] = mol.getFirstAtom();
			b.setAtoms(a01);
			b.setOrder(IBond.Order.SINGLE);
			mol.addBond(b);
		}
		//SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		System.out.println("-------------------");
		SmartsHelper.preProcessStructure(mol, false, false);
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
	}
	
	public void test_IBond_Order()
	{
		IBond.Order a[] = IBond.Order.values();
		for (int i = 0; i < a.length; i ++)
			System.out.println(a[i]);
		
	}
	
	@Test
	public void testCDKStrangeIsomorphism() throws Exception
	{
		//Testing CDK isomorphism of C**C against SCCS --> it gives wrong result 
				
		//Setting search query  'C**C'
		QueryAtomContainer q = new QueryAtomContainer();		
		//setting atoms
		IQueryAtom a0 = new AliphaticSymbolAtom("C"); 
		q.addAtom(a0);		
		IQueryAtom a1 = new AnyAtom();
		q.addAtom(a1);		
		IQueryAtom a2 = new AnyAtom();
		q.addAtom(a2);		
		IQueryAtom a3 = new AliphaticSymbolAtom("C"); 
		q.addAtom(a3);		
		//setting bonds		
		OrderQueryBond b0 = new OrderQueryBond(IBond.Order.SINGLE);
		b0.setAtoms(new IAtom[] {a0,a1});
		q.addBond(b0);		
		OrderQueryBond b1 = new OrderQueryBond(IBond.Order.SINGLE);
		b1.setAtoms(new IAtom[] {a1,a2});
		q.addBond(b1);		
		OrderQueryBond b2 = new OrderQueryBond(IBond.Order.SINGLE);
		b2.setAtoms(new IAtom[] {a2,a3});
		q.addBond(b2);
		
		
		//Creating 'SCCS' target molecule
		AtomContainer target = new AtomContainer();
		//atoms
		IAtom ta0 = new Atom("S");
		target.addAtom(ta0);
		IAtom ta1 = new Atom("C");
		target.addAtom(ta1);
		IAtom ta2 = new Atom("C");
		target.addAtom(ta2);
		IAtom ta3 = new Atom("S");
		target.addAtom(ta3);
		//bonds
		IBond tb0 = new Bond();
		tb0.setAtoms(new IAtom[] {ta0,ta1});
		tb0.setOrder(IBond.Order.SINGLE);
		target.addBond(tb0);
		
		IBond tb1 = new Bond();
		tb1.setAtoms(new IAtom[] {ta1,ta2});
		tb1.setOrder(IBond.Order.SINGLE);
		target.addBond(tb1);
		
		IBond tb2 = new Bond();
		tb2.setAtoms(new IAtom[] {ta2,ta3});
		tb2.setOrder(IBond.Order.SINGLE);
		target.addBond(tb2);
				
				
		//Isomorphism check
		boolean res = UniversalIsomorphismTester.isSubgraph(target, q);
		System.out.println("Mapping C**C against SCCS = " + res);
	}
	
	public void testSMIRKS_JoergTestCase() throws Exception 
	{
		ValidatorEngine validatorEngine = new ValidatorEngine();
		validatorEngine.addValidator(new CDKValidator());
		validatorEngine.addValidator(new BasicValidator());
			
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule m = sp.parseSmiles("c1ccccc1");		
		
		SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		SMIRKSReaction smr = smrkMan.parse("[c:1]1[c:2][c:3][c:4][c:5][c:6]1>>[c:1]1[c:2]([O])[c:3]([O])[c:4][c:5][c:6]1");
		//SMIRKSReaction smr = smrkMan.parse("[c:1][c:2]>>[c:2][c:1]Cl");
		
		smrkMan.FlagProcessResultStructures = true;
		
		/*
		smrkMan.applyTransformation(m, smr);
		myValidation(m);
		ValidationReport r = null;
		r = validatorEngine.validateAtomContainer(m);		
		String transformedSmiles = SmartsHelper.moleculeToSMILES(m);
		System.out.println(transformedSmiles);
		
		if (r == null)
			System.out.println("ValidationReport is null");
		else
		{	
			System.out.println(r.getErrorCount());
		}
		*/
		
		
		
		
		IAtomContainerSet rproducts = smrkMan
				.applyTransformationWithSingleCopyForEachPos(m, null, smr);
		
		
		int containerCount = rproducts.getAtomContainerCount();
		ValidationReport r = null;
		for(int i = 0; i < containerCount; i++)
		{	
			System.out.println("Product " + i);
			IAtomContainer act = rproducts.getAtomContainer(i);
			if (act == null)
				System.out.println("null pointer product");
			else
				System.out.println("atoms " + act.getAtomCount());
			
			r = validatorEngine.validateAtomContainer(act);
		}
		System.out.println(r.getErrorCount());
		
		
	}
	
	public void myValidation(IAtomContainer mol)
	{
		System.out.println("Checking the molecule");
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom a = mol.getAtom(i);
			if (a == null)
				System.out.println("atom #" + i + "is null");
			else
				System.out.println("atom #" + i + "  " + a.getSymbol() + "  " + a.getImplicitHydrogenCount());
		}
	}
	
	public void testCOF_FileOperations(String inFile, String outFile) throws Exception
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		ArrayList<ArrayList<String>> smiSets = cof.loadSmilesTuplesFromFile(inFile);
		
		for (int i = 0; i < smiSets.size(); i++)
		{	
			ArrayList<String> ss = smiSets.get(i);
			for (int k = 0; k < ss.size(); k++ )
				System.out.print(ss.get(k) + "  ");
			System.out.println();
		}
		
		cof.saveSmilesTuplesToFile(smiSets, outFile);
	}
	
	
	public void testBug78() throws Exception
	{
		SMIRKSManager smrkMan = new SMIRKSManager(DefaultChemObjectBuilder.getInstance());
		SMIRKSReaction reaction = smrkMan.parse("[H:3][C:1]#[C]>>[H:3][#6:1]=O");
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			return;
		}
		//IAtomContainer substrate = FormatConverter.fromSmiles("C#C");
		IAtomContainer substrate = SmartsHelper.getMoleculeFromSmiles("C#C",true);
		
		System.out.println(smrkMan.applyTransformationWithSingleCopyForEachPos(substrate, null, reaction));
		
	}
	
	public void testAtomAttribsOnChangingBond() throws Exception
	{
		
		IAtomContainer mol = new AtomContainer();
		IAtom a1 = new Atom("C");
		mol.addAtom(a1);
		IAtom a2 = new Atom("C");
		mol.addAtom(a2);
		IAtom a3 = new Atom("N");
		mol.addAtom(a3);
		
		IAtom h1 = new Atom("H"); mol.addAtom(h1); IBond bh1 = new Bond(a1,h1, IBond.Order.SINGLE);
		//IAtom h2 = new Atom("H"); mol.addAtom(h2); IBond bh2 = new Bond(a2,h2, IBond.Order.SINGLE);
		
		IBond b = new Bond(a1,a2);
		IBond b2 = new Bond(a2,a3);
		
		
		mol.addBond(b);
		b.setOrder(IBond.Order.DOUBLE);
		
		mol.addBond(b2);
		b2.setOrder(IBond.Order.SINGLE);
		
		mol.addBond(bh1);
		//mol.addBond(bh2);
		
		
		
		//IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles("CCCCC");
		//IBond b = mol.getBond(0);
		
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		
		//FlagExplicitHAtoms = true;
		//preProcess(mol);
		
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		//CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		//adder.addImplicitHydrogens(mol);
		
		
		System.out.println("percieveAtomTypesAndConfigureAtoms");
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		
		
		b.setOrder(IBond.Order.TRIPLE);
		
		
		for (IAtom atom:mol.atoms())
		{	
			atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
			//atom.setImplicitHydrogenCount(null);
			//atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
			
			/*
			atom.setAtomTypeName((String) CDKConstants.UNSET);
            
			atom.setMaxBondOrder((IBond.Order) CDKConstants.UNSET);
            atom.setBondOrderSum((Double) CDKConstants.UNSET);
            atom.setCovalentRadius((Double) CDKConstants.UNSET);
            atom.setValency((Integer) CDKConstants.UNSET);
            atom.setFormalCharge((Integer) CDKConstants.UNSET);
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
            atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, false);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, false);
            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, CDKConstants.UNSET);
            atom.setFlag(CDKConstants.ISAROMATIC, false);
            atom.setProperty("org.openscience.cdk.renderer.color", CDKConstants.UNSET);
            atom.setAtomicNumber((Integer) CDKConstants.UNSET);
            atom.setExactMass((Double) CDKConstants.UNSET); 
            */
		 
		}
		
		
		//AtomContainerManipulator.clearAtomConfigurations(mol);
		
		//a1.setHybridization((IAtomType.Hybridization)CDKConstants.UNSET);
		//a2.setHybridization((IAtomType.Hybridization)CDKConstants.UNSET);
		
		
		/*
		AtomContainerManipulator.clearAtomConfigurations(mol);
		mol.removeBond(b);
		IBond b1 = new Bond(a1,a2);
		b1.setOrder(IBond.Order.SINGLE);
		mol.addBond(b1);
		*/
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		
		
		System.out.println("after molecule change");
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		
	}
	
	public void testExpliticHToImplicit(String smiles) throws Exception
	{
		System.out.println("Testing    " + smiles);
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles, true);
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		System.out.println("    " +  SmartsHelper.moleculeToSMILES(mol,true) + "\n");
		
		SmartsHelper.convertExcplicitHAtomsToImplicit(mol);
		System.out.println("excplicit H --> implicit H");
		System.out.println(SmartsHelper.getAtomsAttributes(mol));
		System.out.println(SmartsHelper.getBondAttributes(mol));
		
		System.out.println("    " +  SmartsHelper.moleculeToSMILES(mol,true));
	}
	
//-------------------------------------------------------------------------------
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		TestUtilities tu = new TestUtilities();
		//tu.testSmartsManagerBoolSearch("[!$([OH1,SH1])]C(=O)[Br,Cl,F,I]","CN(C)C(=O)Cl");
		//tu.testSmartsManagerBoolSearch("[x1;C]", "CCCC");
		//tu.testSmartsManagerAtomMapping("N", "CCNCCNCC");
		//tu.testSmartsManagerAtomMapping("[x2]", "C1CCC12CC2");
		//tu.testSmartsManagerBoolSearch("c1ccccc1[N+]", "c1ccccc1[N+]");		
		//tu.testSmartsManagerBoolSearch("(CCC.C1CC12CC2).C=CC#C.CCN.(ClC)", "CCCCC");
		//tu.testSmartsManagerBoolSearch("(CCCC.CC.CCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(Cl.CCCC.CC.CCCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(CCCC.CC).(CCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(CCBr.CCN).(OCC)", "BrCCCCC.CCCN.OCCC");
		//tu.testSmartsManagerBoolSearch("(CCBr).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC");
		
		
		
		sp.mUseMOEvPrimitive = true;
		//sp.mSupportMOEExtension = true;
		//printSmartsTokens("[#6,i,#G3,#X,#N,q3,v2]");
		//tu.testSmartsManagerBoolSearch("[#6,i]", "c1ccccc1");
		//tu.testSmartsManagerBoolSearch("[#G6]", "CCC");
		//tu.testSmartsManagerBoolSearch("[#G6]", "CCS");
		//tu.testSmartsManagerBoolSearch("[#G4]", "CCS");
		//tu.testSmartsManagerBoolSearch("[#G4]", "O-O");
		//tu.testSmartsManagerBoolSearch("[#X]", "CCC");
		//tu.testSmartsManagerBoolSearch("[#X]", "CCS");
		//tu.testSmartsManagerBoolSearch("[q2]", "C1CCC1");
		//tu.testSmartsManagerBoolSearch("[q3]", "C1CCC1");
		//tu.testSmartsManagerBoolSearch("[q0]", "CCCC");  //!!!!!
		//tu.testSmartsManagerBoolSearch("[#N]", "CCC");
		//tu.testSmartsManagerBoolSearch("[#N]", "CCF");
		//tu.testSmartsManagerBoolSearch("[v2]", "CCC");
		//tu.testSmartsManagerBoolSearch("[v4]", "CCC");
		//man.useMOEvPrimitive(true);
		//tu.testSmartsManagerBoolSearch("[v2]", "CCC");
		//tu.testSmartsManagerBoolSearch("[#G7]", "CCC");
		//tu.testSmartsManagerBoolSearch("[#G7]", "CCCF");
		//tu.testSmartsManagerBoolSearch("[#G7]", "ClCCC");
		//tu.testSmartsManagerBoolSearch("[C;^3]", "C#CC=C");  //sp3
		//tu.testSmartsManagerBoolSearch("[C;^2]", "CCC=C");  //sp2
		//tu.testSmartsManagerBoolSearch("[C;^1]", "C#CCC");  //sp
		
		//Cheking [OH]AA!-*
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "COCC=N");
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "OCC=N");
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "OCC-N");
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "[H][O]CC=N");
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "[O+]CC=N");
		//tu.testSmartsManagerBoolSearch("[OH]AA!-*", "OSC=N");
		
		//tu.testSmartsManagerBoolSearch("C~/[i]=[i]~/C", "CC=CC");
		
		
		//String smarts[] = {"CCC", "CCCCC", "C1CCC(C2CCC2C)CCCCC1"};
		//tu.testAtomSequencing(smarts);		
		//tu.testAtomSequencingFromFile("\\NCI001000.txt");
		
		/*
		tu.testIsomorphismTester("C1CCC1","CCCCC");
		tu.testIsomorphismTester("CC","CCCCC");
		tu.testIsomorphismTester("CC1CCC1","C1CCC1C");
		tu.testIsomorphismTester("CC1CCC1","C1CCC1C");
		tu.testIsomorphismTester("CC[C,O]C","CCCC");
		tu.testIsomorphismTester("CC[C,O]C","COCC");
		tu.testIsomorphismTester("CC[C,O]C","COC=C");
		tu.testIsomorphismTester("C(C)(C)(C)C","CC(C)(C)CC");
		tu.testIsomorphismTester("C1CCC1C2CCCC2","CC1CCC1C2CCCC2");		
		*/
		
		
		
		//tu.getCarbonSkelletonsFromString();		
		//tu.testAtomIndexesForMapping(4, 5);
		//tu.testCML("[H]C1CCC12CCNCC2");
		//tu.testIntParsing("1234");
		
		//tu.testFingerprint();
		//tu.testWithFile("\\NCI001000.txt","ExtractAtomContainer");
		
		//int res = tu.testExtractAtomContainer("C1=CC=CC=C1");
		//int res = tu.testExtractAtomContainer("CC=CC#CCN");
		//int res = tu.testExtractAtomContainer("c1ccccc1");
		//System.out.println("res = " + res);
		
		//tu.testSMARTStoChemObj("*CCCC*CC~CN");
		//tu.testSMARTStoChemObj("C[#3]CCC[n;++H2][O+,o,O-][$([O,O-,O++]CBr)]CC");
		
		man.useMOEvPrimitive(true);
		//tu.testSmartsManagerBoolSearch("[#G6;H][i]~[i]~[i]~[i]~[i]-&!:*","Brc1cc(C=O)c(O)c([N+](=O)[O-])c1");
		//tu.testSmartsManagerBoolSearch("[#G6;H][i]~[i]~[i]~[i]~[i]-*","Brc1cc(C=O)c(O)c([N+](=O)[O-])c1");
		//tu.showFullAtomMappingsCDKIsomorphism("CCN", "CCCNCCC");
		//tu.showFullAtomMappingsCDKIsomorphism("C1CC=C1", "C1CC=C1CCC");
		//tu.showFullAtomMappingsCDKIsomorphism("[#G6;H][i]~[i]~[i]~[i]~[i]-*","Brc1cc(C=O)c(O)c([N+](=O)[O-])c1");
		//tu.testSmartsManagerBoolSearch("[X4]", "[H]C([H])([H])[H]");
		
		//tu.testHydrogenCount();
		//tu.printAromaticity("cccccc");
		
		//tu.testChemObjectToSmiles("CCC(CC)CC#CNCC1CC(CCNCl)CC1");
		//tu.testChemObjectToSmiles("C1CCCCC=1");		
		//tu.testChemObjectToSmiles("c1cc(Br)ccc1CC(CCC[Na])CCNNCC2CCCCCCCC2");		
		//tu.testChemObjectToSmiles("C1=CC=CC=C1");
		
		
		//tu.printSequence("P1CS=N1");
		//tu.testFragmentation("P1CS=N1");
		//tu.testProduceStructuresExhaustively("CCc1cc(CC)ccc1", 12);
		//tu.testProduceStructuresExhaustively("c1cc(Br)ccc1CC(CCC)CCNNCC2CCCCCCCC2", 12);
		
		//tu.produceRandomStructures();
		//tu.makeStructureStatistics();
		//tu.filterStructsBySize("/java_frags0.txt","/java_frags.txt",8);
		//tu.testSmartsSreeningKeys();
		
		//tu.compareIsoTesterMulti();
		//tu.compareIsoTester("C",null);
		//tu.testIsomorphismTester("C", "CCCCC");
		
		//!!!  essential IsomorphismTest test
		//tu.compareIsoTesterMulti("/Projects/Nina/java_random_frags.txt");
		
		
		//Testing SSSR algorithm wether it gives exactly the same numbering of the rings
		//tu.printSSSR("C[C@@H]1C[C@H]2[C@@H]3CCC4=CC(=O)C=C[C@]4(C)[C@@]3(F)[C@@H](O)C[C@]2(C)[C@@]1(O)C(=O)CO");
		//tu.printSSSR("C[C@@H]1C[C@H]2[C@@H]3CCC4=CC(=O)C=C[C@]4(C)[C@@]3(F)[C@@H](O)C[C@]2(C)[C@@]1(O)C(=O)CO");
		//tu.printSSSR("C[C@@H]1C[C@H]2[C@@H]3CCC4=CC(=O)C=C[C@]4(C)[C@@]3(F)[C@@H](O)C[C@]2(C)[C@@]1(O)C(=O)CO");
		//tu.printSSSR("C[C@@H]1C[C@H]2[C@@H]3CCC4=CC(=O)C=C[C@]4(C)[C@@]3(F)[C@@H](O)C[C@]2(C)[C@@]1(O)C(=O)CO");
		
		//These were problematic cases, but now are OK
		//tu.compareIsoTester("S(OC)(=O)(=O)O",null);  //OK
		//tu.compareIsoTester("C1(CC)(C(O)(CCC1C(C)C)C)C",null);  //OK
		//tu.compareIsoTester("C1(c(c)c)(C(CN(CC1)C)C)C(C(C)O)O",null);  //OK
		//tu.compareIsoTester("S(c(c)c)(=O)(=O)N(C)C",null);  //OK
		//tu.compareIsoTester("C1(C=C)(C(C(CC2C1C(O)CC3(C(C(CO)=O)(O)CCC23)C)F)=CC=O)C",null);  //OK
		//tu.compareIsoTester("C(C)(O)(CC)C(CC)(C)C",null);  //OK
		//tu.compareIsoTester("S(N)(=O)(=O)c(cc)c",null);  //OK
		//tu.compareIsoTester("S(N)(=O)(=O)c(cc)c",null);  //OK
		//tu.compareIsoTester("N(C)(C)(C)CC",null);  //OK
				
		//tu.testIsomorphismPositions("C1(N)CCCC1", "C1CC(N)CC1NCCO");
		//tu.testIsomorphismMapping("C1CCC1", "CCCCNC1CCC1");
		//tu.testIsomorphismMapping("CCN", "CCCCN");
		
		//tu.testIsomorphismAllMappings("CCN", "CCCCNCC");
		//tu.testIsomorphismAllMappings("C1CCC1", "C1CCC1N");
		//tu.testIsomorphismAllMappings("C1CCC1N", "C1CCC1N");
		
		//tu.testSmartsManagerBoolSearch("C1CC1", "CC(C)CCC");
		//tu.testSmartsManagerBoolSearch("CC(C)C", "C1CC1");
		
		//tu.testSmartsManagerBoolSearch("[CH]#CC=O", "COC(=O)C#C");
		//tu.testSmartsManagerBoolSearch("C#CC=O", "COC(=O)C#C");
		//tu.testIsomorphismTester("[CH]#CC=O", "COC(=O)C#C");
		//tu.testIsomorphismTester("C#CC=O", "COC(=O)C#C");
		
		//tu.testSmartsManagerBoolSearch("a:1:a:a:a:a:a1", "c1ccccc1");
		//tu.testSmartsManagerBoolSearch("a1:a:a:a:a:a:1", "c1ccccc1");
		//tu.testSmartsManagerBoolSearch("a:1:a:a:a:a:a:1", "c1ccccc1");
		//tu.testSmartsManagerBoolSearch("a1:a:a:a:a:a:1", "c1ccccc1");
		
		
		//tu.testSmartsManagerBoolSearch("[C;]", "CCC");   //----------> gives an Exception  at ambit2.smarts.SmartsLogicalExpression.doAND(SmartsLogicalExpression.java:71)
		
		
		//tu.testStructureAnalysis("I:/Projects/Nina/TOXCST_v3a_320_12Feb2009.sdf");
		//tu.testScreeningKeys("c1ccccc1NCC","[N;++]CCCCC");
		
		//tu.testIsomorphismTester("a", "c1ccccc1");
		//tu.testIsomorphismTester("cF", "C1=CC=CC=C1F");
		
		//tu.testIsomorphismTester("CC1CC1", "CC1CC1");
		//tu.testIsomorphismTester("CC1C=C1", "CC1C=C1");
		//tu.testIsomorphismTester("CC1CC=1", "CC=1CC=1");
		//tu.testIsomorphismTester("CC=1CC=1", "CC=1CC=1");
		
		
		//tu.testSmartsManagerBoolSearch("[#G6;H][i]", "[H]O[N+](=O)[O-]");
		//tu.testIsomorphismTester("[#G6;H][i]", "[H]O[N+](=O)[O-]");
		//tu.testSmartsManagerBoolSearch("[i][#G6v2]", "O[N+](=O)[O-]");
		//tu.testIsomorphismTester("[i][#G6v2]", "O[N+](=O)[O-]");
		
		//tu.testIsomorphismTester("*#*", "C1#CCN=NN1");
		
		
		//tu.sp.mSupportOpenBabelExtension = false;
		//tu.printSmartsTokens("[C^3]N");
		//tu.testSmartsManagerBoolSearch("[C^1]","CCCC");
		//tu.testSmartsManagerBoolSearch("[C^2]","CCCC");
		//tu.testSmartsManagerBoolSearch("[C^3]","CCCC");
		//tu.testSmartsManagerBoolSearch("[C^2]","C1=CC=CC=1");
		//tu.testSmartsManagerBoolSearch("[C^1]","CC#CC");
		//tu.testSmartsManagerBoolSearch("[C^1]","CC=C=C");
		//tu.testSmartsManagerBoolSearch("[C^1; +]C","CCCC");  //the space within atom expression does not cause parser error !!!
		
		//tu.showFullIsomorphismMappings("N", "CCCNCCCNCC");
		//tu.showFullIsomorphismMappings("CN", "CCCNCCCNCC");
		//tu.showFullIsomorphismMappings("CNC", "CCCNCCCNCC");
		//tu.showFullIsomorphismMappings("CN(N)C", "CCCN(N)CCCNCC");
		//tu.showFullIsomorphismMappings("C1NCC1", "CCC1NCC1C");
				
		//tu.testRingInfo("C1CCC2CCCC3CCCC1C23");
		//tu.testRingInfo("C1CCC1C2CCCC2");
		//tu.testRingInfo("C1CCC12CCCC2");
		
		
		//tu.testConvertKekuleSmartsToAromatic("C1CNC1CCCC2COCC23CCCC3");
		//tu.testConvertKekuleSmartsToAromatic("C2CNC1CCCC1CC2CCCC3C=C[C;++]=CC=C3");
		
		//tu.makeStructureStatistics();
		
		//System.out.println(man.isFlagUseCDKIsomorphismTester());
		
		man.setUseCDKIsomorphismTester(false);
		
		//tu.testSmartsManagerBoolSearch("cccc","C1=CC=CC=C1");
		//tu.testSmartsManagerBoolSearch("cccc","c1ccccc1");
		//tu.testSmartsManagerBoolSearch("c1ccccc1c2ccccc2","C1=CC=CC=C1-C2=CC=CC=C2");
		//tu.testSmartsManagerBoolSearch("c1ccccc1!@c2ccccc2","c1ccccc1c2ccccc2");
		//tu.testSmartsManagerBoolSearch("CC=C","C1=CC=CC=C1"); //The is false because "CC=C" is interpreted as aliphatic fragment
		//tu.testSmartsManagerBoolSearch("CC=C","C1=CC=CC=C1-C2=CC=CC=C2");   //The is false because "CC=C" is interpreted as aliphatic fragment
		//tu.testSmartsManagerBoolSearch("cc=,:c","C1=CC=CC=C1-C2=CC=CC=C2"); //The result is true 
				
		//tu.testSmartsManagerBoolSearch("cc-c","c1ccccc1ccc");
		//tu.testSmartsManagerBoolSearch("CC=C","C1=CC=CC=C1C2=CC=CC=C2");		
		//tu.testSmartsManagerBoolSearch("cc=c","C1=CC=CC=C1C2=CC=CC=C2");  //the result is true if man.supportDoubleBondAromaticityNotSpecified(true);
		//tu.testSmartsManagerBoolSearch("cc=c","c1ccccc1c2ccccc2");
		//tu.testSmartsManagerBoolSearch("[C;$(C=O)]", "O=CCC");
		//tu.testSmartsManagerBoolSearch("[C;D3](O)(O)", "OCC1OC(O)C(O)C1(O)");
		
		//This example is not working since it has recursion inside recursion
		//tu.testSmartsManagerBoolSearch("[$([S;H0;D2])!$([S;H0;D2]([$([CX4]),$(C(=O)C)])[$([CX4]),$(C(=O)C)])]", "CCSC=NC");
		//tu.testSmartsManagerBoolSearch("[$([S;H0;D2])!$([S;H0;D2]([CX4])[CX4])!$([S;H0;D2]([CX4])C(=O)C)!$([S;H0;D2](C(=O)C)C(=O)C)]", "CCSC=NC");
		
		//tu.FlagTargetPreprocessing = true;
		//tu.FlagExplicitHAtoms = false;
		//tu.testSmartsManagerBoolSearch("[O,o,OH,N,n,$(P=O),$(C=S),$(S=O),$(C=O)]~[A,a]~[A,a]~[O,o,OH,N,n,$(P=O),$(C=S),$(S=O),$(C=O)]","[S-]C(=S)N");
		//tu.testSmartsManagerBoolSearch("[O,o,OH,$(P=O),$(C=S),$(S=O),$(C=O)]","[S-]C(=S)N");
		//tu.testSmartsManagerBoolSearch("C[H]","CC");
		
		//Stereo info testing
		//tu.testSmartsManagerBoolSearch("C[C@](O)(N)CCC","CC(O)(N)CCC");
		//tu.testSmartsManagerBoolSearch("C/C=C/C","C\\C=C/C");
		
		//These examples with a great probability proof that there is a bug in the CDK isomorphism algorithm
		//tu.testSmartsManagerBoolSearch("[N,$(C=S)]~[A,a]~[A,a]~[N,$(C=S)]","[S-]C(=S)N");
		//tu.testSmartsManagerBoolSearch("[N,$(C=S)]~[A,a]~[A,a]~[N,$(C=S)]","C(=S)N");	
		//tu.testSmartsManagerBoolSearch("[N,$(C=S)]~[*]~[*]~[N,$(C=S)]","C(=S)N");   //[*] matches [H]
		//tu.testSmartsManagerBoolSearch("[N,$(C=S)]~*~*~[N,$(C=S)]","C(=S)N");       //false because * does not match [H]
		//tu.showFullAtomMappings("[N,$(C=S)]~[A,a]~[A,a]~[N,$(C=S)]","[S-]C(=S)N");
		//tu.testSmartsManagerBoolSearch("[N,C]~[A,a]~[A,a]~[N,C]","[S-]C(=S)N");
		
		//tu.testSmartsManagerBoolSearch("[#6]-c1cccc2-[#7]3C-,:4(=[#7]-[#6]-5-[#7][C]3(=O)[#6]-3-[#6]-[#6]C(=O)[#7]-3-#6-c~3cc-,:44ccccc4~[#7]~3[#6]-5~[#8]-c12)c1ccccc1","CCCC");
		//tu.testSmartsManagerBoolSearch("[#6]-c1cccc2-[#7]3C-,:4(=[#7]-[#6]-5-[#7][C]3(=O)[#6]-3-[#6]-[#6]C(=O)[#7]-3-[#6]-c~3cc-,:44ccccc4~[#7]~3[#6]-5~[#8]-c12)c1ccccc1","CCCC");
		
		//tu.testSmartsManagerBoolSearch("C=,-1CCCCC1","C1CCCCC1");
		//tu.testSmartsManagerBoolSearch("C%001CCCCC%0001","C1CCCCC1");
		//tu.testSmartsManagerBoolSearch("C-,=3CCC=3","C1CCCCC1");
		
		//tu.testSmartsManagerBoolSearch("[N,C]~*~*~[N,C]","ClCNCl");
		//tu.showFullAtomMappingsCDKIsomorphism("[N,C]~*~*~[N,C]","ClCNCl");
		
		//tu.testSmartsManagerBoolSearch("C**C","SCCS");
		
		//tu.showFullAtomMappings("CCN","CCNCCCC");
		//tu.testSmartsManagerBoolSearch("[A,a]~[A,a]~[A,a]~[A,a]","[S-]C(=S)N");
		
		//tu.testCDKStrangeIsomorphism();		
		
		
		
		//tu.testSmartsManagerBoolSearchMDL("CC=C","D:/projects/nina/biphenyl.mol");
		//tu.testSmartsManagerBoolSearchMDL("cc=c","D:/projects/nina/biphenyl.mol");
		
		tu.FlagProductPreprocessing = true;
		//tu.FlagPrintAtomAttributes = true;
		//tu.FlagExplicitHAtoms = true;
		//tu.testSMIRKS("[H][#6:1]:1:[#6:5]:[#6:6]:[#6:7]:[#8:4]:1>>[OH1]-[#6:1]1:[#6:5]:[#6:6]:[#6:7]:[#8:4]:1", "o1cccc1");
		//tu.testSMIRKS("[H][#6:1]:1:[#6:5]:[#6:6]:[#6:7]:[#8:4]:1>>[OH1]-[#6:1]1:[#6:5]:[#6:6]:[#6:7]:[#8:4]:1", "O1C=CC=C1");
		
		//Causes exception
		//tu.testSMIRKS("[H][#6:1]-,:1=,:[#6:5]-,:[#6:6]=,:[#6:7]-,:[#8:4]-,:1>>[OH1]-[#6:1]-,:1=,:[#6:5]-,:[#6:6]=[#6:7]-,:[#8:4]-,:1", "O1C=CC=C1");
		
		
		//tu.testSMIRKS("[N:1][C:2][C:3][C:4]>>[C:4]=[C:3].[C:2]=[N----:1]Cl", "SNCCCN");
		//tu.testSMIRKS("[N:1][C:2]([C:3])>>[N:1][C].[C:2]=[O]", "NCC"); //---> Parser error is produced (Exception is fixed now) !!!!!
		//tu.testSMIRKS("[N:1][C:2]([C:3])>>[N:1][C].[C:2]=[O:4]", "NCC"); //---> Parser error is produced (Exception is fixed now) !!!!!
		//tu.testSMIRKS("[N:1][C:2]([C])>>[N:1][C].[C:2]=[O:4]", "NCC"); //---> Parser error is produced !!!!!
		//tu.testSMIRKS("[N:1][C:1]([C])>>[N:1][C].[C:2]=[O]", "NCC"); //---> Should produce duplciation index errors (it is fixed) !!!!!
		//tu.testSMIRKS("[N:1][C:2]([H])>>[N:1][H].[Cl-][C:2]=[O]", "[H]N(C)C[H]"); 
		
		//Ambit-SMARTS/SMIRKS parser does not require atom ampping info (":n") to be at the end of the atom expression 
		//tu.testSMIRKS("[N;+:1][C:2]([H])>>[N:1;+][H].[Cl-][C:2]=[O]", "[H][N+1](C)C[H]");    
		
		//tu.testSMIRKS("[N;!$(N=O):1][C:2]>>[N:1]Cl.[C:2]", "NCC");
		//tu.testSMIRKS("[N;!$(N=O):1][C:2]>>[N;!$(N=O):1]Cl.[C:2]", "NCCCCCN=O");  //--> No more exception is thrown due to the recursive SMARTS
		//tu.testSMIRKS("[N;$(N-O-Br):1][C:2]>>[N:1]Cl.[C:2]", "ONCC");  //--> No more error  is obtained due to the recursive SMARTS
		//tu.testSMIRKS("[N:1][C;!$([C;r5]);!$(CC=C):2]>>[N:1]Cl.[C:2]", "NC1CCCCC1CCCC2CCC=CC2N");
		
		
		//tu.testSMIRKS("[O:1]([H:10])[c:2]1[cH:3][cH:4][c:5]([O:6][H:11])[cH:7][c:8]1[$(C),$(Cl),$(OC),$(CC):9]>>[O:1]=[C:2]1[CH:3]=[CH:4][C:5](=[O:6])[CH:7]=[C:8]1[$(C),$(Cl),$(OC),$(CC):9].[H:10][H:11]", "[H]Oc1([H])c([H])(C)c([H])c([H])(O[H])c([H])c1([H])");
		//tu.testSMIRKS("[O:1]([H:10])[c:2]1[cH:3][cH:4][c:5]([O:6][H:11])[cH:7][c:8]1[C:9]>>[O:1]=[C:2]1[CH:3]=[CH:4][C:5](=[O:6])[CH:7]=[C:8]1[C:9].[H:10][H:11]", "[H]Oc1c(C)cc(O[H])cc1");
		//tu.testSMIRKS("[O:1]([H:10])[c:2]1[cH:3][cH:4][c:5]([O:6][H:11])[cH:7][c:8]1[C]>>[O:1]=[C:2]1[CH:3]=[CH:4][C:5](=[O:6])[CH:7]=[C:8]1[C].[H:10][H:11]",
		//		"[H]Oc1c(C)cc(O[H])cc1");
		
		//tu.testSMIRKS("[c:1]1[c:2][c:3][c:4][c:5][c:6]1>>[c:1]1[c:2]([O])[c:3]([O])[c:4][c:5][c:6]1", "c1ccccc1");
		//tu.testSMIRKS("[C:1][O][Cl:2]>>[C:1][N][Cl:2]", "CCOCl");
		//tu.testSMIRKS_JoergTestCase();
		
		//Test cases from  Yannick Djoumbou (y.djoumbou@gmail.com)
		//tu.testSMIRKS("[*;#6:1][C:2]#[C:3][*;#6:4]>>[*:1]/[C:2](/[H])=[*:3](/[H])[C:4]", "COCCCC#CCCC");
		//tu.testSMIRKS("[*:1][C:2]#[C:3][*;#6:4]>>[*:1]/[C:2](/[H])=[C:3](/[H])[C:4]", "COCCCC#CCCC");		
		//tu.testSMIRKS("[*;#6:1][C:2]=[O:3]>>[*;#6:1][C:2]-[O:3][*;#6]","CCCC=O");
		//tu.testSMIRKS("[C:1]=O>>[C:1]1OCCO1","CCCCCCC=O");
		//tu.testSMIRKS("[*:1]C(=O)O>>[*:1]C(N)=O","CCCCCCC(=O)O");
		
		//tu.testSMIRKS("[Cl:3].[N:4][O:1][C:2]>>[N:4].[Cl:3][O:1][C:2]","Cl.NOC"); //Multi component reactions do not work ???!!!!
		//tu.testSMIRKS("[N:4][O:1][C:2]>>[N:4].[Cl][O:1][C:2]","NOCF.NOC"); //Multi component reaction application ??? to be clarified
				
		//tu.FlagProductPreprocessing = true;
		//tu.testSMIRKS("[H:99][O;X2:1]c1ccccc1>>C1CCC([O;X2:1]c2ccccc2)OC1.[H:99]","[H]Oc1ccccc1");
		//tu.testSMIRKS("[H:99][O;X2:1]c1ccccc1>>C1CCC([O;X2:1]c2ccccc2)OC1.[H:99]","[H]OC1=CC=CC=C1");		
		//tu.testSMIRKS("[H:99][O;X2:1]C1=CC=CC=C1>>C1CCC([O;X2:1]c2ccccc2)OC1.[H:99]","[H]OC1=CC=CC=C1");
		
		//tu.testSMIRKS("[H:99][O;X2:1][a:2]>>C1CCC([O;X2:1][a:2])OC1.[H:99]","[H]Oc1ccccc1");		
		//tu.testSMIRKS("[H:99][O;X2:1]c1ccccc1>>C1CCC([O;X2:1]c2ccccc2)OC1.[H:99]","[H]Oc1ccccc1NN"); //benzene atom are not mapped and NN is disconnected 
		//tu.testSMIRKS("[H:99][O;X2:1][a:2]>>C1CCC([O;X2:1][a:2])OC1.[H:99]","[H]Oc1ccccc1NN");
		
		
		//tu.testSMIRKS("[H:99][O;X2:1][a:2]>>[H:99].[O;X2:1][a:2]","[H]OC1=CC=CC=C1");		
		//tu.testSMIRKS("[H:99][O;X2:1]c1ccccc1>>C1CCC([O;X2:1]C2=CC=CC=C2)OC1.[H:99]","[H]Oc1ccccc1");		                                                    
		//tu.testSMIRKS("[H:99][O;X2:1]C1=CC=CC=C1>>C1CCC([O;X2:1]c2ccccc2)OC1.[H:99]","[H]Oc1ccccc1");                                     
		//tu.testSMIRKS("[H:99][O;X2:1][c:2]1[c:3][c:4][c:5][c:6][c:7]1>>C1CCC([O;X2:1][c:2]2[c:3][c:4][c:5][c:6][c:7]2)OC1.[H:99]","[H]Oc1ccccc1");
		//tu.testSMIRKS("[H:99][O;X2:1][c:2]1[c:3][c:4][c:5][c:6][c:7]1>>C1CCC([O;X2:1][c:2]2[c:3][c:4][c:5][c:6][c:7]2)OC1.[H:99]","[H]Oc1ccccc1NN");
		
		
		//tu.testSMIRKS("[#8:2]-[#6:3]>>[#8:2].[#6:3]=S","NCCCOCCCC", ReactionOperation.SingleCopyForEachPos);	
		//tu.testSMIRKS("[#8:2]-[#6:3]>>[#8:2].[#6:3]=S","NCCCOCCCCOCCCCOCCC", ReactionOperation.CombinedOverlappedPos);	
		//tu.testSMIRKS("[#8:2]-[#6:3]>>[#8:2].[#6:3]=S","NCCCOCCCCOCCCCOCCC", ReactionOperation.SingleCopyForEachPos);	
		//tu.testSMIRKS("[#6:1]-[#8:2]-[#6:3]>>[#6:1]-[#8:2].[#6:3]=O","NC1OC(C(=O)C1", ReactionOperation.SingleCopyForEachPos);	
		
		//tu.testSMIRKS("[#6:1]-[#8:2]-[#6:3]>>[#6:1]-[#8:2].[#6:3]=O","NC1OC(C(=O)C1", SmartsConst.SSM_NON_IDENTICAL);	
		//tu.testSMIRKS("[#8;$([#8]([#6])[#6]):2]-[#6:3]>>[#8;$([#8]([#6])[#6]):2].[#6:3]=O","NC1OC(C(=O)C1", ReactionOperation.SingleCopyForEachPos);	
		
		//tu.FlagSSModeForSingleCopyForEachPos = SmartsConst.SSM_ALL;
		//tu.testSMIRKS("[#6:1]-[#8:2]-[#6:3]>>[#6:1]-[#8:2].[#6:3]=S","NCCCOCC", ReactionOperation.SingleCopyForEachPos);	
		
		//tu.FlagExplicitHAtoms = true;
		tu.FlagTargetPreprocessing = true;
		tu.FlagProductPreprocessing = true;
		//tu.FlagPrintAtomAttributes = true;
		//tu.FlagSSMode =  SmartsConst.SSM_NON_IDENTICAL_FIRST;
		//tu.FlagExplicitHToImplicitOnProductPreProcess = true;
		
		//tu.testSMIRKS("[c:1]1[c:6]([H])[c:5]([H])[c:4][c:3][c:2]1>>[OH1]-[#6:5]([H])-1-[#6:4]=[#6:3]-[#6:2]=[#6:1]-[#6:6]([H])-1-[OH1]", "C1=CC=CC=C1");
		//tu.testSMIRKS("[c:1]1[c:6][c:5][c:4][c:3][c:2]1>>[OH1]-[#6:5]-1-[#6:4]=[#6:3]-[#6:2]=[#6:1]-[#6:6]-1-[OH1]", "C1=CC=CC=C1"); //This is a bug !!! 3 double bonds remain
		//tu.testSMIRKS("[c:1]1[c:6][c:5][c:4][c:3][c:2]1>>[OH1]-[#6:5]-1-[#6:4]-[#6:3]-[#6:2]-[#6:1]-[#6:6]-1-[OH1]", "C1=CC=CC=C1"); //This is a bug !!! 3 double bonds remain
		//tu.testSMIRKS("[c:1]1[c:6][c:5][c:4][c:3][c:2]1>>[OH1]-[c:1]1[c:6][c:5][c:4][c:3][c:2]1-[OH1]", "C1=CC=CC=C1"); //This is a bug !!! 3 double bonds remain
		
		
		//tu.testSMIRKS("[C:1]>>[C:1]=[Cl]", "CC");
		//tu.testSMIRKS("[C:1][C:2]>>[C:1]=[C:2]", "CC");
		//tu.testSMIRKS("[C:1]>>[C:1]=[C]", "C");
		//tu.testSMIRKS("[C:1]([H])[C:2]([H])>>[C:1]=[C:2]", "CCC");
		//tu.testSMIRKS("[C:1][C:2]>>[C:1]=[C:2]", "CCC");
		//tu.testSMIRKS("[C:1]~[C:2]>>[C:1]-[C:2]", "C#CCC=C");
		//tu.testSMIRKS("[C:1]-[C:2]>>[C:1]~[C:2]", "C#CCC=C");
		
		//tu.testSMIRKS("[C:1][C:3][C:4][C:2]>>[C:1]1[C:3][C:4][C:2]~1", "CCCC");
		//tu.testSMIRKS("[C:1]C>>[C:1]#&!=,-N", "CC");
		
		
		tu.FlagSSModeForSingleCopyForEachPos = SmartsConst.SSM_ALL;
		tu.testSMIRKS("[c:1]1[cH1:6][cH1:5][c:4][c:3][c:2]1>>[OH1]-[#6@H:5]-1-[#6:4]=[#6:3]-[#6:2]=[#6:1]-[#6@H:6]-1-[OH1]", 
				"C=C(CC)C1=CC=CC=C1Cl",  ReactionOperation.SingleCopyForEachPos);
		
		
		//tu.testAtomAttribsOnChangingBond();
		
		//tu.testSMIRKS("[#6:5]1[#6:4]=[#6:3][#6:2]=[#6:1][#6:6]=1>>[OH1]-[#6:5]-1-[#6:4]=[#6:3]-[#6:2]=[#6:1]-[#6:6]-1-[OH1]", "C1=CC=CC=C1");
		//tu.testSMIRKS("[#6:5]1[#6:4]=[#6:3][#6:2]=[#6:1][#6:6]=1>>[OH1]-[#6:5]-1-[#6:4]-[#6:3]-[#6:2]-[#6:1]-[#6:6]-1-[OH1]", "C1=CC=CC=C1");
		
		
		//tu.testSMIRKS("[H:99][O;X2:1][a:2]>>[H:99].Cl[O;X2:1][a:2]","[H]Oc1ccccc1");
		//tu.testSMIRKS("[H:99][O;X2:1][a:2]>>[H:99].Cl[O;X2:1][a:2]","[H]OC1=CC=CC=C1");
		
		//tu.testSMIRKS("[H:99][O;X2]C2=CC=CC=C2>>C1CCC([O;X2]C2=CC=CC=C2)OC1.[H:99]","[H]OC1=CC=CC=C1");
		
		//tu.testSmiles2Smiles("C1=CC=CC=C1");
		//tu.testSmiles2Smiles("c1ccccc1");
		//tu.testSmiles2Smiles("C[C@](CC)(O)Cl");
		
		//tu.testSmiles2MOLFile("C[C@](CC)(O)Cl","/test-chirality-01.mol");
		//tu.testMOL2MOLFile("D:/test-mol-chiral.mol", "D:/test-mol-chiral-conv.mol");
		//tu.testMOL2MOLFile("D:/test-mol-chiral-02.mol", "D:/test-mol-chiral-02-conv.mol");
		
		//tu.testSMIRKS("[H:99][O;X2:1][C:2]2=[C:3]C=CC=C2>>C1CCC([O;X2:1]C2=CC=CC=C2)OC1.[H:99]","[H]Oc1ccccc1");
		                                                      
		//tu.testPreprocessing("c1ccccc1", true);
		
		//tu.testSMARTSBondToIBond("C=,@C");
		
		
		//tu.testEquivalenceTestes("[H]C([H])CCC([H])[H]");
		
		
		//tu.testCombinations();
		
		//tu.structureStatisticsMDL(5000, "/einecs_structures_V13Apr07.sdf", "/db-5000-str-stat.txt");
		
		
		//tu.testAtomAttributes("Oc1ccc(O)cc1");
		
		//tu.testBondIndexChange("C1CCC1CCN");
		
		//tu.test_IBond_Order();
		
		
		//List<Integer> pos = SmartsHelper.getSmartsPositions("[*;r4,r5,r6,r7,r8](=*)=*", 
		//		SmartsHelper.getMoleculeFromSmiles("N1=C=C=CNN1"));
		//System.out.println(pos.size());
		
		//tu.testCOF_FileOperations("/test.smi", "/test2.smi");
				
		//tu.testSmartsToQueryToSmarts("[C,$(CO),Br][n+++;R5]n[A--]ACCa*Cl");
		//tu.testSmartsToQueryToSmarts("Cl/C=C/Cl");
		//tu.testSmartsToQueryToSmarts("C!-C:C=C#C@C!@C~C");
		//tu.testSmartsToQueryToSmarts("C!:1CCCCC!:1%1CCCC1");
		//tu.testSmartsToQueryToSmarts("[#6]-c1cccc2-[#7]3C-,:4(=[#7]-[#6]-5-[#7][C]3(=O)[#6]-3-[#6]-[#6]C(=O)[#7]-3-[#6]-c~3cc-,:44ccccc4~[#7]~3[#6]-5~[#8]-c12)c1ccccc1");
		
		//tu.testSmartsToQueryToSmarts("C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1C1CC1");
		
		//tu.testSmartsToQueryToSmarts("Cl/C=C/Cl");          //!!!!! CIS/TRANS info is missing
		//tu.testSmartsToQueryToSmarts("C[C@](CO)(N)CC");   //!!!!! Bug in the SMARTS outputting --> C[C&@@](CO)(N)CC   
		
		//tu.testBug78();
		
		//tu.testExpliticHToImplicit("CCC=C");
		
	}
	
}
