package ambit2.export.isa.base;

public class ExternalDataFileLocation 
{
	public static final String splitter = ","; 
	
	public String fileName = null;
	public int recordIndex = 0;  
	public int elementIndex = 0;
	
	
	public String getLocationAsIdentifier()
	{
		return "" + fileName + splitter + recordIndex + splitter + elementIndex;
	}
}
