package ambit2.reactions;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

public class SyntheticStrategy 
{
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Currently the strategy does nothing
		//TODO
		return ruleInstances;
	}
}
