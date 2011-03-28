package ambit2.tautomers;

import java.util.Vector;

import ambit2.smarts.SmartsHelper;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

public class TestTautomers 
{
	TautomerManager tman = new TautomerManager();
	
	public static void main(String[] args)
	{		
		TestTautomers tt = new TestTautomers();
		
		
		//tt.test("OC=CCC(CC=CO)CCCC=O");
		//tt.test("O=CC(C)([H])C");  --> problem with the explicit H atoms 
		//tt.test("OC=NCC(CC=CO)CCCC");
		tt.test("OC=CCCNC=O");
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
	
	//helper utilities for the tests
	public boolean identicalStructureSets(Vector<IAtomContainer> v, Vector<String> smi)
	{
		//TODO - very important for the theoretical testing
		return(true);
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
