package ambit2.sln;

import java.util.HashMap;

public class SLNDictionary 
{
	HashMap<String,SLNContainer> nameSLN = new HashMap<String,SLNContainer>();
	
		
	public SLNContainer getSLNContainer(String atomName)
	{	
		return nameSLN.get(atomName);
	}
	
	public boolean containsAtomName(String atomName)
	{	
		return false;
	}
	
}
