package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.List;

public class HNMRPredefinedKnowledgeBase 
{
	public static final String KeyWordPrefix = "$$";
	
	public static final String ALKANES[] = 
	{
		"$$NAME= ALKANES $$SMARTS= [C;^3]",
		"$$INFO= ",
		"$$BASIC_SHIFT = 0.83",
		""
	};
	
	
	public static HNMRKnowledgeBase getHNMRKnowledgeBase()
	{
		HNMRKnowledgeBase knowledgeBase = new HNMRKnowledgeBase();
		
		parseHAtomEnvironment (ALKANES, "Alkanes", knowledgeBase.errors);
		
		return knowledgeBase;
	}
	
	
	public static HAtomEnvironment parseHAtomEnvironment(String lines[], String errorPrefix, List<String> errors)
	{
		HAtomEnvironment haEnv = new HAtomEnvironment();
		
		for (int i = 0; i < lines.length; i++)
		{
			parseLine(haEnv, errorPrefix + " line " + (i+1), errors);
		}
		
		return haEnv;
	}
	
	public static int parseLine(HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		//TODO
		return 0;
	}
	
		
}	