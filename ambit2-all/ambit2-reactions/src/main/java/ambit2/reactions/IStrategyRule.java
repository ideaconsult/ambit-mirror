package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IStrategyRule 
{	
	public double getRuleWeight(IAtomContainer container);
	public double getRuleInstanceWeight(IRetroSynthRuleInstance instance, IAtomContainer container);
}
