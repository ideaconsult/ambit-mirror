package ambit2.smarts.test;

import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.BitSet;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.File;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.smarts.*;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.graph.ConnectivityChecker;



public class TestUtilities 
{	
	static SmartsParser sp = new SmartsParser();
	//static SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	static SmartsToChemObject smToChemObj = new SmartsToChemObject();
	static ChemObjectToSmiles cots = new ChemObjectToSmiles(); 
	
	
	public static void printSmartsTokens(String smarts)
	{
		
		QueryAtomContainer qac = sp.parse(smarts);
		if (!sp.getErrorMessages().equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + sp.getErrorMessages());			
			return;
		}
		System.out.println(SmartsHelper.getAtomsString(qac));
		/*
		for (int i = 0; i < qac.getAtomCount(); i++)
			if (qac.getAtom(i) instanceof SmartsAtomExpression)
				System.out.println(
						SmartsHelper.getAtomExpressionTokens((SmartsAtomExpression)qac.getAtom(i)));
		*/				
	}
	
	public static int boolSearch(String smarts, String smiles)
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
	
	public void testSmartsManagerBoolSearch(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}		
		boolean res = man.searchIn(mol);
		System.out.println("Man_search " + smarts + " in " + smiles + "   --> " + res);
	}
	
	public void showFullAtomMappings(String smarts, String smiles)
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		System.out.println("Man_search " + smarts + " in " + smiles);
		List bondMapList = man.getBondMappings(mol);
		for (Object aBondMapping: bondMapList)
		{
			Vector<IAtom> atomMapping = man.generateFullAtomMapping((List)aBondMapping, mol, man.getQueryContaner());
			System.out.print("Mapping: ");
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
	
	public void testIsomorphismTester(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
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
	
	
	public void testIsomorphismPositions(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		sp.setSMARTSData(mol);
		Vector<Integer> pos = isoTester.getIsomorphismPositions(mol);
		System.out.println("Isomorphism Positions: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < pos.size(); i++)
			System.out.print("  " + pos.get(i).intValue());
		System.out.println();
	}
	
	public void testIsomorphismMapping(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		Vector<IAtom> map = isoTester.getIsomorphismMapping(mol);
		System.out.println("Isomorphism Mapping: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < map.size(); i++)
		{	
			IAtom a = map.get(i);
			int n = mol.getAtomNumber(a);
			System.out.print("  " + n);
		}
		System.out.println();
	}
	
	public void testIsomorphismAllMappings(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}						
		
		isoTester.setQuery(query);
		Vector<Vector<IAtom>> allmaps = isoTester.getAllIsomorphismMappings(mol);
		System.out.println("Isomorphism Mapping: " + smarts  + "  in  " + smiles);
		for (int i = 0; i < allmaps.size(); i++)
		{	
			Vector<IAtom> map = allmaps.get(i);
			for (int j = 0; j < map.size(); j++)
			{	
				IAtom a = map.get(j);
				int n = mol.getAtomNumber(a);
				System.out.print("  " + n);
			}
			System.out.println();
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
					
					QueryAtomContainer query  = sp.parse(frags[k].trim());
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
			QueryAtomContainer query  = sp.parse(smarts[i]);
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
		
	
	public boolean checkSequence(QueryAtomContainer query, Vector<QuerySequenceElement> sequence)
	{
		IAtomContainer skelleton = ChemObjectFactory.getCarbonSkelleton(sequence);		
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
	
	int testSMARTStoChemObj(String smarts)
	{	
		QueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))	
		{	
			System.out.println("Smarts Error: " + errorMsg);
			return(-1);
		}	
		
		//smToChemObj.forceAromaticBondsAlways = true;
		IAtomContainer mol =  smToChemObj.extractAtomContainer(query);
		System.out.println(smarts + "  --> " + SmartsHelper.moleculeToSMILES(mol));
		return(0);
	}
	
	int testExtractAtomContainer(String smarts)
	{	
		QueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))		
			return(-1);
		
		smToChemObj.forceAromaticBondsAlways = true;
		IAtomContainer mol =  smToChemObj.extractAtomContainer(query);
		
		System.out.println("Creating a Chem object with extractAtomContainer ");
		System.out.println(smarts);		
		printAromaticity(mol);
		System.out.println("Object to SMILES");
		System.out.println(SmartsHelper.moleculeToSMILES(mol));
		
		System.out.println("Creating a Chem object with teh SMILES parser ");
		System.out.println(smarts);	
		IMolecule mol2 =  SmartsHelper.getMoleculeFromSmiles(smarts);
		printAromaticity(mol2);
		System.out.println("Object to SMILES");
		System.out.println(SmartsHelper.moleculeToSMILES(mol2));
		
		
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
		Vector<String> failSmiles = new Vector<String>(); 
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
	
	public void printAromaticity(String smiles)
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		printAromaticity(mol);
	}
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles)
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		Vector<IAtom> atoms = man.getFirstPosAtomMappings(mol);
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
	
	public void testCML(String smiles)
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
			String smiles2 = SmartsHelper.moleculeToSMILES(mol2);
			
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
            //IChemFile chemFile = DefaultChemObjectBuilder.getInstance().newChemFile();            
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
	
	
	String getFingerprint(String smiles)
	{
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
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
	
	void testFingerprint()
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
	
	void testHydrogenCount()
	{
		IAtomContainer mol = new AtomContainer();
		mol.addAtom(new Atom("C"));
		
		Integer hc = mol.getAtom(0).getHydrogenCount();
		Integer fc = mol.getAtom(0).getFormalCharge();
		Integer nc = mol.getAtom(0).getFormalNeighbourCount();
		Integer v = mol.getAtom(0).getValency();
		System.out.println("getHydrogenCount() = " + hc);
		System.out.println("getFormalCharge() = " + fc);
		System.out.println("getFormalNeighbourCount() = " + nc);
		System.out.println("getValency() = " + v);		
	}
		
	void testFragmentation(String smiles)
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		ChemObjectFactory cof = new ChemObjectFactory();
		cof.setAtomSequence(mol, mol.getAtom(0));
		for (int i = 0; i < cof.sequence.size(); i++)
		{
			IAtomContainer frag = cof.getFragmentFromSequence(i);
			System.out.println(cots.getSMILES(frag)); 
		}
	}
	
	void testChemObjectToSmiles(String smiles)
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		System.out.println(smiles+ "  --->  "+cots.getSMILES(mol)); 
	}
	
	void testProduceStructuresExhaustively(String smiles, int maxNumSteps)
	{
		System.out.println("Producing structs form " + smiles+ "   maxNumSteps = " + maxNumSteps);
		System.out.println("-------------------------------");
		Vector<StructInfo> vStr = new Vector<StructInfo>();
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		ChemObjectFactory cof = new ChemObjectFactory();
		
		cof.produceStructuresExhaustively(mol, vStr, maxNumSteps, 100);
		
		for (int i = 0; i < vStr.size(); i++)
			System.out.println(vStr.get(i).smiles);
	}
	
	void printSequence(String smiles)
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		ChemObjectFactory cof = new ChemObjectFactory();
		cof.setAtomSequence(mol, mol.getAtom(0));
		System.out.println(smiles);
		for (int i = 0; i < cof.sequence.size(); i++)
			System.out.println("SeqEl " + i + " : "+cof.sequence.get(i).toString());
	}
	
	void produceStructures() 
	{
		ChemObjectFactory cof = new ChemObjectFactory();
		Vector<StructInfo> vStr = new Vector<StructInfo>();
		cof.produceStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					5, 50000, 8, vStr, "/java_frags.txt");
		
	}
	
	void produceRandomStructures() 
	{
		ChemObjectFactory cof = new ChemObjectFactory();
		Vector<StructInfo> vStr = new Vector<StructInfo>();
		cof.produceRandomStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					30, 50000, vStr, "/java_random_frags.txt");
	}
	
	void makeStructureStatistics()
	{
		ChemObjectFactory cof = new ChemObjectFactory();
		cof.performStructureStatistics("/java_frags.txt", 
							"../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
							100, 20000, "/java_frags_stat20000.txt");
	}
	
	void filterStructsBySize(String inputSmilesFile, String outSmilesFile, int maxNumAtoms)	
	{
		Vector<String> outSmiles = new Vector<String>();
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
				QueryAtomContainer q = sp.parse(line);
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
	
	void testSmartsSreeningKeys()
	{
		SmartsScreeningKeys smKeys = new SmartsScreeningKeys();
		Vector<String> keys = smKeys.getKeys();
		for (int i = 0; i < keys.size(); i++)
			System.out.println(" " + (i+1) + "  " + keys.get(i));
	}
	
	int compareIsoTester(String smarts, String mdlFile)
	{
		QueryAtomContainer query  = sp.parse(smarts);
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
				
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
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
	
	
	Vector<IAtomContainer> getContainersFromMDL(String mdlFile)
	{	
		Vector<IAtomContainer> structures = new Vector<IAtomContainer>();
		
		try
		{
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
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
		StructureSetAnalyzer analyzer = new StructureSetAnalyzer();
		analyzer.structures = getContainersFromMDL(mdlFile);
		System.out.println("Loaded " + analyzer.structures.size() + " structures");
		
		analyzer.maxHitStructSize = 20;
		analyzer.stochasticAnalysis();
	}
	
	
	
	void compareIsoTesterMulti()
	{
		SmartsScreeningKeys smKeys = new SmartsScreeningKeys();
		Vector<String> keys = smKeys.getKeys();
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
	
	void testScreeningKeys(String target, String queryString)
	{		
		Vector<String> myKeys = new Vector<String>(); 
		myKeys.add("CCC");
		myKeys.add("CCN");
		myKeys.add("CCO");
		//Screening screen = new Screening(myKeys);
		Screening screen = new Screening();
		
		System.out.println("Query " + queryString);
		QueryAtomContainer query = sp.parse(queryString);
		screen.setQuery(query);
		System.out.println(screen.queryKeysToString());
		System.out.println();
				
		System.out.println("Target " + target);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(target);
		ScreeningData sd = screen.getScreeningDataForTarget(mol);
		System.out.println(screen.strKeysToString(sd.structureKeys));
		
		
	}
	
	public void printSSSR(String smiles)
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
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
		}	
		System.out.println();
	}	
	
	
//-------------------------------------------------------------------------------
	
	
	public static void main(String[] args)
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
		//tu.showFullAtomMappings("CCN", "CCCNCCC");
		//tu.showFullAtomMappings("C1CC=C1", "C1CC=C1CCC");
		//tu.showFullAtomMappings("[#G6;H][i]~[i]~[i]~[i]~[i]-*","Brc1cc(C=O)c(O)c([N+](=O)[O-])c1");
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
		
		tu.testSmartsManagerBoolSearch("a:1:a:a:a:a:a1", "c1ccccc1");
		tu.testSmartsManagerBoolSearch("a1:a:a:a:a:a:1", "c1ccccc1");
		tu.testSmartsManagerBoolSearch("a:1:a:a:a:a:a:1", "c1ccccc1");
		tu.testSmartsManagerBoolSearch("a1:a:a:a:a:a:1", "c1ccccc1");
		
		
		//tu.testSmartsManagerBoolSearch("[C;]", "CCC");   //----------> gives an Exception  at ambit2.smarts.SmartsLogicalExpression.doAND(SmartsLogicalExpression.java:71)
		
		
		//tu.testStructureAnalysis("I:/Projects/Nina/TOXCST_v3a_320_12Feb2009.sdf");
		//tu.testScreeningKeys("c1ccccc1NCC","[N;++]CCCCC");
	}
	
}
