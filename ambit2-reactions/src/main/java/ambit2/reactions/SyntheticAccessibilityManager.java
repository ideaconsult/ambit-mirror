package ambit2.reactions;


public class SyntheticAccessibilityManager 
{
	private int strategy = ReactionConst.SA_STRATEGY_DESCRIPTORS;

	public int getStrategy()
	{
		return strategy;
	}

	public void setStrategy(int newStrategy)
	{
		strategy = newStrategy;
	}
}
