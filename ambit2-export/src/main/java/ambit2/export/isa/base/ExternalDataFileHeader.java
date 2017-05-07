package ambit2.export.isa.base;

import java.util.ArrayList;
import java.util.List;

public class ExternalDataFileHeader 
{
	public List<String> fieldNames = new ArrayList<String>();
	
	public static ExternalDataFileHeader getDeafultExternalDataFileHeader()
	{
		ExternalDataFileHeader edfh = new ExternalDataFileHeader();
		edfh.fieldNames.add("Record_Number");		
		edfh.fieldNames.add("Value");
		edfh.fieldNames.add("Error");
		edfh.fieldNames.add("Unit");
		return edfh;
	}
}

