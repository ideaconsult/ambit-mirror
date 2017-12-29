package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.IRetroSynthRuleInstance;
import ambit2.reactions.rules.scores.ReactionScore;
import ambit2.rules.weight.DescriptorWeight;

public class SyntheticStrategy 
{
	public ReactionScore reactionScore = new ReactionScore();
	public List<DescriptorWeight> productComplexityDescirptors = new ArrayList<DescriptorWeight>();
	public List<DescriptorWeight> productSimilarityDescirptors = new ArrayList<DescriptorWeight>();
	public Map<String, Double> reactionClassScores = new HashMap<String, Double>();
	public Map<String, Double> trbasformTypeScores = new HashMap<String, Double>();
	
	
	
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Currently the strategy does nothing
		//TODO
		return ruleInstances;
	}
	
}
