package ambit2.reactions.syntheticaccessibility;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.retrosynth.ReactionSequence;
import ambit2.reactions.rules.SyntheticStrategyDescriptorSolver;
import ambit2.rules.weight.DescriptorWeight;


public class SyntheticAccessibilityManager 
{	
	protected  SyntheticAccessibilityStrategy strategy = null;
	protected SyntheticStrategyDescriptorSolver solver = new SyntheticStrategyDescriptorSolver();
	
	
	public SyntheticAccessibilityStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SyntheticAccessibilityStrategy strategy) {
		this.strategy = strategy;
	}

	public double calcSyntheticAccessibility(IAtomContainer mol)
	{	
		double sa = 0.0;
		
		for (DescriptorWeight dw : strategy.descirptors)
		{
			Double c = (Double)solver.calculateDescriptor(dw.descriptorName, mol);
			sa += c * dw.weight;
		}
		
		if (strategy.startMaterialSimilarityWeight > 0.0)
			sa += strategy.startMaterialSimilarityWeight * getStartMaterialSimilarityScore(mol);
		
		if (strategy.retroSynthesisWeight > 0.0)
			sa += strategy.retroSynthesisWeight * getRetroSyntheticScore(mol);
		
		return sa;
	}
	
	
	double getStartMaterialSimilarityScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	double getRetroSyntheticScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	/*
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
	*/
	
}
