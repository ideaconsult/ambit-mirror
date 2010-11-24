package ambit2.tautomers;

import ambit2.smarts.SmartsHelper;
import org.openscience.cdk.interfaces.IMolecule;

public class TestTautomers 
{
	TautomerManager tman = new TautomerManager();
	
	public static void main(String[] args)
	{		
		TestTautomers tt = new TestTautomers();
		
		
		tt.test("OC=CCC(CC=CO)CCCC=O");
		tt.test("O=CC(C)([H])C");
		tt.test("O=CC(C)(C)C");
	}
	
	
	public void test(String smi)
	{	
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		tman.generateTautomers();
	}
	
	
	
	
}
