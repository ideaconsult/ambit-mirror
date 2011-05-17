package ambit2.tautomers.test;

import java.util.Vector;
import ambit2.tautomers.*;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class TestTautomers 
{
	TautomerManager tman = new TautomerManager();
	
	public static void main(String[] args)
	{		
		TestTautomers tt = new TestTautomers();
		
		
		//tt.visualTest("OC=CCC(CC=CO)CCCC=O");
		//tt.test("O=CC(C)([H])C");  --> problem with the explicit H atoms 
		//tt.test("OC=NCC(CC=CO)CCCC");
		//tt.test("OC=CCCNC=O");
		//tt.testCase("OC=CCCNC=O", new String[]{"OC=CCCNC=O", "OC=CCCN=CO", "O=CCCCNC=O", "O=CCCCN=CO"} , true);
		//tt.testCloning("CC(C)C");
		//tt.test("C=C(O)N");
		//tt.test("NN=CO");
		//tt.visualTest("N=C(N)NC=O");
		//tt.performTestCases();
		//tt.visualTest("NC(C)=N");
		tt.visualTest("C1=CN=C(N)NC1=O");
		//tt.visualTest("C1=CN=C(N)NC1(=O)");
		//tt.visualTest("N=C(O)C=CN");  //two problems (1) alene atoms are obtained, (2) missing tautomers
		//tt.visualTest("C=CN");
		
	}
	
	public void performTestCases()
	{
		int nErrors = 0;
		
		nErrors += testCase("OC=CCCNC=O", 
				new String[]{"OC=CCCNC=O", "OC=CCCN=CO", "O=CCCCNC=O", "O=CCCCN=CO"}, false);
		
		
		
		System.out.println("Errors: " + nErrors);
		
	}
	
	public void test0(String smi)
	{	
		System.out.println("Testing0(combinatorial aproach)0: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		Vector<IAtomContainer> resultTautomers = tman.generateTautomers();			
	}
	
	
	public void test(String smi)
	{	
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		for (int i = 0; i < resultTautomers.size(); i++)		
			System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
			
	}
	
	public void visualTest(String smi)
	{
		System.out.println("Visual Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		
		
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		for (int i = 0; i < resultTautomers.size(); i++)		
			System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
		
		TestStrVisualizer tsv = new TestStrVisualizer(resultTautomers);
		
		
		System.out.println(mol.getAtom(0).getAtomicNumber().intValue());
	} 
	
	
	
	public int testCase(String smi, String expectedTautomers[], boolean FlagPrintTautomers)
	{			
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		if (FlagPrintTautomers)
			for (int i = 0; i < resultTautomers.size(); i++)		
				System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
		
		int res = checkResultTautomerSet(resultTautomers, expectedTautomers);
		if (res == 0)
		{	
			System.out.println("Tautomers OK");
			return (0);
		}	
		else
		{	
			System.out.println("Tautomers test error = " + res);
			return (1);
		}
		
	}
	
	
	//helper utilities for the tests
	public int checkResultTautomerSet(Vector<IAtomContainer> resultStr, String expectedStr[])
	{
		SmartsParser sp = new SmartsParser();
		IsomorphismTester isoTester = new IsomorphismTester();
		int nNotFound = 0;
		
		int checked[] = new int[resultStr.size()];
		for (int i = 0; i < checked.length; i++)
			checked[i] = 0;
		
		for (int i = 0; i < expectedStr.length; i++)
		{	
			QueryAtomContainer query  = sp.parse(expectedStr[i]);
			sp.setNeededDataFlags();
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{
				System.out.println("Smarts Parser errors:\n" + errorMsg);
				continue;
			}						
			
			isoTester.setQuery(query);
			
			boolean FlagFound = false;
			for (int k = 0; k < resultStr.size(); k++)
			{	
				sp.setSMARTSData(resultStr.get(k));
				boolean res = isoTester.hasIsomorphism(resultStr.get(k));
				if (res)
				{
					FlagFound = true;
					checked[k]++;
					break;
				}
			}
			
			if (!FlagFound)
				nNotFound++;
		}
		
		return(nNotFound);
	}
	
	
	
	
	
	
	
	
	
	
}
