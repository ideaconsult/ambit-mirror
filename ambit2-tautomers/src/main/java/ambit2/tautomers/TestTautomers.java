package ambit2.tautomers;

import java.util.Vector;

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
		
		
		//tt.test("OC=CCC(CC=CO)CCCC=O");
		//tt.test("O=CC(C)([H])C");  --> problem with the explicit H atoms 
		//tt.test("OC=NCC(CC=CO)CCCC");
		//tt.test("OC=CCCNC=O");
		tt.testCase("OC=CCCNC=O", new String[]{"OC=CCCNC=O", "OC=CCCN=CO", "O=CCCCNC=O", "O=CCCCN=CO"});
		//tt.testCloning("CC(C)C");
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
	
	public void testCase(String smi, String expectedTautomers[] )
	{			
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		for (int i = 0; i < resultTautomers.size(); i++)		
			System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
		
		int res = checkResultTautomerSet(resultTautomers, expectedTautomers);
		if (res == 0)
			System.out.println("Tautomers OK");
		else
			System.out.println("Tautomers test error = " + res);
		
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
	
	
	public void testCloning(String smi)
	{	
		System.out.println("Atom Clone Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom a = mol.getAtom(i);
			IAtom a1 = cloneAtom(a);
			System.out.println(a.getSymbol() + "  " 
					+ a.getImplicitHydrogenCount() + "  " + a1.getImplicitHydrogenCount());
		}
		
			
	}
	
	IAtom cloneAtom(IAtom a)
	{
		try
		{
			IAtom a1 = (IAtom)a.clone();
			return (a1);
		}	
		catch(Exception e)
		{
			tman.errors.add("Error cloning atom " + a.getSymbol());
		}
		
		return(null);
	}
	
	
	
	
	
}
