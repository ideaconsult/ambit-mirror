package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.List;


public class HNMRPredefinedKnowledgeBase 
{
	public static final String keyWordPrefix = "$$";
	public static final String keyWordSeparator = "=";
	
	public static final String ALKANES_CH3[] = 
	{
		"$$NAME= ALKANES/CH3",
		"$$SMARTS= [CH3]",
		"$$INFO= ",
		"$$BASIC_SHIFT= 0.83",
		"$$SUBSTITUENT_DESIGNATIONS= Za Zb",
		"$$SUBSTITUENT_POS_ATOM_INDICES= 1 1",
		"$$POSITION_DISTANCES = 1 2"
	};
	
	
	public static HNMRKnowledgeBase getHNMRKnowledgeBase()
	{
		HNMRKnowledgeBase knowledgeBase = new HNMRKnowledgeBase();
		
		HAtomEnvironment haEnv = parseHAtomEnvironment (ALKANES_CH3, "Alkanes/CH3", knowledgeBase.errors);
		knowledgeBase.hAtomEnvironments.add(haEnv);
		
		return knowledgeBase;
	}
	
	
	public static HAtomEnvironment parseHAtomEnvironment(String lines[], String errorPrefix, List<String> errors)
	{
		HAtomEnvironment haEnv = new HAtomEnvironment();
		
		for (int i = 0; i < lines.length; i++)
		{
			parseLine(lines[i], haEnv, errorPrefix + " line " + (i+1), errors);
		}
		
		postProcess (haEnv, errorPrefix, errors);
		return haEnv;
	}
	
	
	public static int parseLine(String line, HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		int res = line.indexOf(keyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = line.indexOf(keyWordPrefix, curPos + keyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = line.substring(curPos);
			else
			{	
				keyword = line.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword, haEnv, errorPrefix, errors);
		}	
		return 0;
	}
	
		
	public static void parseKeyWord(String keyword, HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		int sepPos = keyword.indexOf(keyWordSeparator);		
		if (sepPos == -1)
		{	
			errors.add(errorPrefix + " Incorrect key word syntax: " + keyword);
			return;
		}
		
		String key = keyword.substring(keyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyword.substring(sepPos+1).trim();
		
		
		if (key.equals("NAME"))	
		{	
			haEnv.name = keyValue;
			return;
		}
		
		if (key.equals("SMARTS"))	
		{	
			haEnv.smarts = keyValue;
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			haEnv.info = keyValue;
			return;
		}
		
		if (key.equals("BASIC_SHIFT"))	
		{	
			try {
				haEnv.chemShift0 = Double.parseDouble(keyValue);
			}
			catch(Exception e) {
				errors.add(errorPrefix + " incorrect BASIC_SHIFT: " + e.getMessage());
			}
			return;
		}
		
		if (key.equals("SUBSTITUENT_DESIGNATIONS"))	
		{	
			String tokens[] = keyValue.split(" ");
			haEnv.substituentDesignations = tokens;
			return;
		}
		
				
		if (key.equals("SUBSTITUENT_POS_ATOM_INDICES"))	
		{	
			String tokens[] = keyValue.split(" ");
			haEnv.substituentPosAtomIndices = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++)
			{
				try {
					int ival = Integer.parseInt(tokens[i]);
					haEnv.substituentPosAtomIndices[i] = ival;
				}
				catch (Exception e) {
					errors.add(errorPrefix + " incorrect SUBSTITUENT_POS_ATOM_INDICES[" + (i+1) + "] : " + e.getMessage());
				}
			}
			return;
		}
		
		if (key.equals("POSITION_DISTANCES"))	
		{	
			String tokens[] = keyValue.split(" ");
			haEnv.positionDistances = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++)
			{
				try {
					int ival = Integer.parseInt(tokens[i]);
					haEnv.positionDistances[i] = ival;
				}
				catch (Exception e) {
					errors.add(errorPrefix + " incorrect POSITION_DISTANCES[" + (i+1) + "] : " + e.getMessage());
				}
			}
			return;
		}
		
		
		errors.add(errorPrefix + " Unknow key word: " + key);
	}
	
	public static void postProcess(HAtomEnvironment haEnv, String errorPrefix, List<String> errors)
	{
		if (haEnv.name == null)
			errors.add(errorPrefix + " NAME is missing");
		if (haEnv.smarts == null)
			errors.add(errorPrefix + " SMARTS is missing");
		if (haEnv.chemShift0 == null)
			errors.add(errorPrefix + " BASIC_SHIFT is missing");
	}
	
	
}	