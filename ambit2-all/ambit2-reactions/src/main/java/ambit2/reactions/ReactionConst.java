package ambit2.reactions;

public class ReactionConst 
{
	public static final String KeyWordPrefix = "$$";
	public static final String KeyWordSeparator = "=";
	public static final String KeyWordElementSeparator = ",";
	
	//Synthetic Accessibility Evaluation Strategies		
	public enum SynthAccessStrategy {
		DESCRIPTORS, 
		START_MATERIALS,
		RETRO_SYNTHESIS,
		UNKNOWN,
	}
	
	
	//Retro Synthesis Rule Types 
	public enum RetroSynthRuleType {
		TRANSFORMATION, 
		UNKNOWN;
		
		public static RetroSynthRuleType getFromString(String stringType)
		{
			if (stringType.equals("TRANSFORMATION"))
				return RetroSynthRuleType.TRANSFORMATION;
			
			return RetroSynthRuleType.UNKNOWN;
		}
	}
	
	
	//some const-to-enum conversion utilities ------------------------------
	
	
	
	
	
	
	
	
		
}
