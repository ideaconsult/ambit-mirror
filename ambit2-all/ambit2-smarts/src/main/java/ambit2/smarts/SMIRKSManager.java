package ambit2.smarts;

import java.util.Vector;

public class SMIRKSManager 
{
	SmartsParser parser = new SmartsParser();
	
	Vector<String> errors = new Vector<String>();
	
	public SMIRKSManager()
	{
		parser.setComponentLevelGrouping(true);
	}
	
	public boolean hasErrors()
	{
		if (errors.isEmpty())
			return false;
		else
			return true;
	}
	
	public String getErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return(sb.toString());
	}
	
	public SMIRKSReaction parse(String smirks)
	{
		errors.clear();
		SMIRKSReaction reaction = new SMIRKSReaction();
				
		//Separate the components of the SMIRKS string
		int sep1Pos = smirks.indexOf(">");
		if (sep1Pos == -1)
		{
			errors.add("Invalid SMIRKS: missing separators '>'");
			return reaction;
		}
		
		
		int sep2Pos = smirks.indexOf(">", sep1Pos+1);
		if (sep2Pos == -1)
		{
			errors.add("Invalid SMIRKS: missing second separator '>'");
			return reaction;
		}
		
		
		//Parse the components
		//TODO
		
		//Check the components
		//TODO
		
		
		return null;
	}
}
