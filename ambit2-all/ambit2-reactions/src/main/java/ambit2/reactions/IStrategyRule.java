package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtomContainer;



public interface IStrategyRule 
{		
	public double getRuleScore(IAtomContainer container);
	public double getRuleInstanceScore(IRetroSynthRuleInstance instance, IAtomContainer container);
	
	
	public enum Type
	{
		GROUP_COMPATIBILITY,
		PRIORITY,
		GROUP_INFLUENCE,
		REACTION_CONDITIONS,
		STRATEGIC_BOND,
		UNKNOWN;
		
		public static Type getFromString(String stringType)
		{
			if (stringType.equals("GROUP_COMPATIBILITY"))
				return Type.GROUP_COMPATIBILITY;
			if (stringType.equals("PRIORITY"))
				return Type.PRIORITY;
			if (stringType.equals("GROUP_INFLUENCEY"))
				return Type.GROUP_INFLUENCE;
			if (stringType.equals("REACTION_CONDITIONS"))
				return Type.REACTION_CONDITIONS;
			if (stringType.equals("STRATEGIC_BOND"))
				return Type.STRATEGIC_BOND;

			return Type.UNKNOWN;
		}
	}
}
