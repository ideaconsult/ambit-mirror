package ambit2.notation.generators;

import ambit2.notation.NotationData;

public class DefaultNotationGenerator 
{
	public enum DefaultNotationType {
		PLAIN_TEXT, JSON
	}
		
	public DefaultNotationType notationType = DefaultNotationType.PLAIN_TEXT;
	public String sectionDelimeter = ";";
	public String elementDelimeter = ",";
	public boolean FlagSectionName = true;
	public boolean FlagElement = true;
	public String sectionNameSuffix = ":";
	public String elementNameSuffix = "=";
	
	public String generateNotation (NotationData notationData)
	{
		//TODO
		return null;
	}
	
	
}	