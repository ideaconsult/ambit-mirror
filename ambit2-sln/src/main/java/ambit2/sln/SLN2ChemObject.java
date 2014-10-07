package ambit2.sln;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.AtomContainer;

public class SLN2ChemObject 
{
	private List<String> conversionErrors = new ArrayList<String>();

	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	public String atomContainerToSLN(AtomContainer container)
	{
		//TODO
		return null;
	}
	
	public AtomContainer  slnToAtomContainer(String sln)
	{
		//TODO
		return null;
	}
}
