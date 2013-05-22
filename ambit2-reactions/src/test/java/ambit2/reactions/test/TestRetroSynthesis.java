package ambit2.reactions.test;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.reactions.*;
import ambit2.smarts.SmartsHelper;
public class TestRetroSynthesis 
{
	RetroSynthesis retroSyn;
	
	
	public static void main(String[] args) throws Exception 
	{
		
		TestRetroSynthesis trs = new TestRetroSynthesis();
		trs.retroSyn = new RetroSynthesis();
		System.out.println("Retro Synthesis Knowledge base:");
		System.out.println(trs.retroSyn.getReactionKnowledgeBase().toString());
		
		trs.test("C1CCCCC1");
		
	}
	
	
	public void test(String smi) throws Exception
	{	
		System.out.println("Testing Retro Synthesis for " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		
		retroSyn.setStructure(mol);
		RetroSynthesisResult result = retroSyn.run(); 
		
		System.out.println(result.toString());
	}
	
}
