package ambit2.reactions.test;

import ambit2.reactions.RetroSynthesis;

public class TestRetroSynthesis 
{
	RetroSynthesis retroSyn;
	
	
	public static void main(String[] args) throws Exception 
	{
		
		TestRetroSynthesis trs = new TestRetroSynthesis();
		trs.retroSyn = new RetroSynthesis();
		System.out.println("Retro Synthesis Knowledge base:");
		System.out.println(trs.retroSyn.getReactionKnowledgeBase().toString());
		
		trs.test0();
		
	}
	
	
	public void test0() throws Exception
	{	
	}
	
}
