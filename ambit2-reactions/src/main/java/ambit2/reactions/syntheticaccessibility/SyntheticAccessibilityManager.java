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
		switch (strategy.synthAccessMethod)
		{
		case DESCRIPTORS:
			return getDescriptorsSynthAccScore(mol);
		case RETRO_SYNTHESIS:
			return getRetroSyntheticScore(mol);
		case START_MATERIALS:
			return getStartMaterialsSynthAccScore(mol);
		case COMBINED_METHOD: 
			double sa = 0.0;
			if (strategy.combinedDescrWeight > 0.0)
				sa += strategy.combinedDescrWeight * getDescriptorsSynthAccScore(mol);
			if (strategy.combinedRetroSynthWeight > 0.0)
				sa += strategy.combinedRetroSynthWeight * getRetroSyntheticScore(mol);
			if (strategy.combinedStartMatWeight > 0.0)
				sa += strategy.combinedStartMatWeight * getStartMaterialsSynthAccScore(mol);
			return sa;
		}
		return 0.0;
	}
	
	double getDescriptorsSynthAccScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	double getRetroSyntheticScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	double getStartMaterialsSynthAccScore(IAtomContainer mol)
	{
		//TODO
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
