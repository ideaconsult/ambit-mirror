package ambit2.reactions.retrosynth;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.IRetroSynthRuleInstance;
import ambit2.reactions.rules.scores.ReactionScore;

public class SyntheticStrategy 
{
	public ReactionScore reactionScore = new ReactionScore();
	
	
	/*
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Currently the strategy does nothing
		//TODO
		return ruleInstances;
	}
	*/
}
