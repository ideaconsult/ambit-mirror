package ambit2.reactions.syntheticaccessibility;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import ambit2.reactions.retrosynth.ReactionSequence;


public class SyntheticAccessibilityManager 
{	
	protected  SyntheticAccessibilityStrategy strategy = null;
	
	
	public SyntheticAccessibilityStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SyntheticAccessibilityStrategy strategy) {
		this.strategy = strategy;
	}

	public double calcSyntheticAccessibility(IAtomContainer mol)
	{
		return 0.0;
	}
	
	double getSyntheticAccessibilityScore(ReactionSequence reactSeq)
	{
		//TODO
		return 0.0;
	}
	
	double getSyntheticAccessibilityScore(List<ReactionSequence> reactSeqList)
	{
		//TODO
		return 0.0;
	}
	
}
