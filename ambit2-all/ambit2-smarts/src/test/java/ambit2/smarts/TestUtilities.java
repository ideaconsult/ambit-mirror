package ambit2.test.smarts;

import java.util.Stack;
import java.util.Vector;
import java.util.BitSet;
import java.io.RandomAccessFile;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

import ambit2.smarts.*;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
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
	static SmartsFingerprinter sfp = new SmartsFingerprinter();
	
	
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
		System.out.println("IsomorphismTester: " + smarts  + "  in  " + smiles + 
				"   " + isoTester.hasIsomorphism(mol));
		//boolean res = checkSequence(query,isoTester.getSequence());
		//isoTester.printDebugInfo();
		//System.out.println("sequnce check  -- > " + res);		
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
		
	
	public boolean checkSequence(QueryAtomContainer query, Vector<SequenceElement> sequence)
	{
		IAtomContainer skelleton = SequenceElement.getCarbonSkelleton(sequence);		
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
	
	
	
	int testExtractAtomContainer(String smarts)
	{	
		QueryAtomContainer query  = sp.parse(smarts);		
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))		
			return(-1);
		
		sfp.forceAromaticBonds = true;
		IMolecule mol =  sfp.extractAtomContainer(query);
		
		System.out.println(smarts);
		printAromaticity(mol);
		System.out.println(SmartsHelper.moleculeToSMILES(mol));
		
		IMolecule mol2 =  SmartsHelper.getMoleculeFromSmiles(smarts);
		printAromaticity(mol2);
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
		int res = tu.testExtractAtomContainer("C1=CC=CC=C1");
		System.out.println("res = " + res);
		
	}
	
	public void printAromaticity(IMolecule mol)
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
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		Vector<IAtom> atoms = man.getAtomMappings(mol);
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
}
