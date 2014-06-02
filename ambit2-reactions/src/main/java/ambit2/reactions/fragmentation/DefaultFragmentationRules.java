package ambit2.reactions.fragmentation;

import java.util.ArrayList;

public class DefaultFragmentationRules 
{
	private static ArrayList<FragmentationRule> rules = null;
	
	private static final String bondDisconections[] = new String[]{
		"[*;R]-[*;!R]"
	};
	
	private static boolean FlagGeneratedRules = false;
	
	
	public static  ArrayList<FragmentationRule> getRules()
	{	
		if (!FlagGeneratedRules)
		{
			FlagGeneratedRules = true;
			generateRules();
		}
		
		return rules;
	}
	
	private static void generateRules()
	{
		//Generate bond disconnection rules
		for (int i = 0; i < bondDisconections.length; i++)
		{	
		
			//TODO
		}	
	}
	
}
