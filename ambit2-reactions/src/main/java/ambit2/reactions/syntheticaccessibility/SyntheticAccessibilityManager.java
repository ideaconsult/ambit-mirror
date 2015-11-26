package ambit2.reactions.syntheticaccessibility;

import ambit2.reactions.ReactionConst.SynthAccessStrategy;

public class SyntheticAccessibilityManager 
{
	private SynthAccessStrategy strategy = SynthAccessStrategy.DESCRIPTORS;

	public SynthAccessStrategy getStrategy()
	{
		return strategy;
	}

	public void setStrategy(SynthAccessStrategy newStrategy)
	{
		strategy = newStrategy;
	}
}
