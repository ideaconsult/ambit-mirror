package ambit2.export.isa.base;

public class ExternalDataFileLocation 
{
	public static final String splitter = ","; 
	
	public String fileName = null;
	public int recordIndex = -1;  
	public int elementIndex = 0;
	
	
	public String getLocationAsIdentifier()
	{
		return "" + fileName + splitter + recordIndex + splitter + elementIndex;
	}
}
