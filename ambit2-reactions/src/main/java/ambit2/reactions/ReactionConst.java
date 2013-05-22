package ambit2.reactions;

public class ReactionConst 
{
	public static final String KeyWordPrefix = "$$";
	public static final String KeyWordSeparator = "=";
	public static final String KeyWordElementSeparator = ",";
	
	//Synthetic Accessibility Evaluation Strategies	
	public static final int SA_STRATEGY_DESCRIPTORS = 0;
	public static final int SA_STRATEGY_START_MATERIALS = 1;
	public static final int SA_STRATEGY_RETRO_SYNTHESIS = 2;
	
	//Retro Synthesis Rule Types 
	public static final int RS_TYPE_TRANSFORMATION = 0;
		
	
	//some const-to-string conversion utilities
	
	public static String RetroSynthRuleTypeToString(int type)
	{
		//TODO
		return("");
	}
	
	public static int getRetroSynthRuleTypeFromString(String stringType)
	{
		//TODO
		return -1;
	}
	
	
		
}
