package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtomContainer;


public class RetroSynthesis 
{
	ReactionKnowledgeBase knowledgeBase; 
	IAtomContainer molecule;
	RetroSynthesisResult rsResult;
	
	
	public RetroSynthesis() throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase();
	}
	
	public ReactionKnowledgeBase getReactionKnowledgeBase()
	{
		return knowledgeBase;
	}
	
	public void setStructure(IAtomContainer str) 
	{	
		molecule = str;
	}
	
	
	public RetroSynthesisResult run()
	{
		RetroSynthesisResult rsResult = new RetroSynthesisResult();
		searchPaths();
		return rsResult;
	}
	
	//First strategy
	void searchPaths()
	{
		//TODO
	}
	
	
	
	
	
}
