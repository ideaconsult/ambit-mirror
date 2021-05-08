package ambit2.notation;

public class DefaultNotationGenerator 
{
	public enum NotationType {
		PLAIN_TEXT, JSON
	}
		
	public NotationType notationType = NotationType.PLAIN_TEXT;
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