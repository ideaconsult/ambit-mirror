package ambit2.reactions.retrosynth;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.IRetroSynthRuleInstance;

public class SyntheticStrategy 
{
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Currently the strategy does nothing
		//TODO
		return ruleInstances;
	}
}
