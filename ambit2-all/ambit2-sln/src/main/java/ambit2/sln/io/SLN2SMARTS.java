package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

public class SLN2SMARTS 
{	
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();

	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	public String slnToSmiles(String sln)
	{
		//TODO
		return null;
	}
	
	public String slnToSmarts(String sln)
	{
		//TODO
		return null;
	}
	
	public String slnToSmirks(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmilesToSLN(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmartsToSLN(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmirksToSLN(String sln)
	{
		//TODO
		return null;
	} 
	
}
