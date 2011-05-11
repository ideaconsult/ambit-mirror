package ambit2.smarts;

public class SMIRKSManager 
{
	String errorMsg = "";
	
	public SMIRKSManager()
	{	
	}
	
	public String getErrors()
	{
		return(errorMsg);
	}
	
	public SMIRKSReaction parse(String smirks)
	{
		errorMsg = "";
		SMIRKSReaction reaction = new SMIRKSReaction();
				
		//Separate the components of the SMIRKS string
		int sep1Pos = smirks.indexOf(">");
		if (sep1Pos == -1)
		{
			errorMsg += "Invalid SMIRKS: missing separators '>'";
			return reaction;
		}
		
		
		
		int sep2Pos = smirks.indexOf(">", sep1Pos+1);
		if (sep2Pos == -1)
		{
			errorMsg += "Invalid SMIRKS: missing second separator '>'";
			return reaction;
		}
		
		
		
		//TODO
		
		//Parse the components
		//TODO
		
		//Check the components
		//TODO
		
		
		return null;
	}
}
