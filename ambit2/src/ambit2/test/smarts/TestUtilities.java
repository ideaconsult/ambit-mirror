package ambit2.test.smarts;

import java.util.Vector;

import ambit2.smarts.*;

import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;


public class TestUtilities 
{	
	static SmartsParser sp = new SmartsParser();
	static SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	
	public static  IMolecule getMoleculeFromSmiles(String smi) 
	{	
		IMolecule mol = null;
		try {
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(smi);
		}
		catch (InvalidSmilesException e) {
			System.out.println(e.toString());
	 	}
		return (mol);
	}
	
	public static int boolSearch(String smarts, String smiles)
	{	
		IMolecule mol = TestUtilities.getMoleculeFromSmiles(smiles);	
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
		IMolecule mol = getMoleculeFromSmiles(smiles);	
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
		IMolecule mol = getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}
						
		System.out.println("IsomorphismTester: " + smarts  + "  in  " + smiles);
		isoTester.setQuery(query);
		boolean res = checkSequence(query,isoTester.getSequence());
		//isoTester.printDebugInfo();
		System.out.println("sequnce check  -- > " + res);
		
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
		
		String smarts[] = {"CCC", "CCCCC", "C1CCC(C2CCC2C)CCCCC1"};
		tu.testAtomSequencing(smarts);
		//tu.testIsomorphismTester("C1C(CC)CCC1","CCCCC");
		//tu.testIsomorphismTester("C1CCC1CC2CCC2","CCCCC");
	}
	
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles)
	{	
		IMolecule mol = getMoleculeFromSmiles(smiles);	
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
	
}
