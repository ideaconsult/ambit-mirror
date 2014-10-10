package ambit2.sln;

import java.util.HashMap;

public class SLNDictionary 
{
	private HashMap<String,SLNDictionaryObject> objects = new HashMap<String,SLNDictionaryObject>();
	
		
	public SLNDictionaryObject getDictionaryObject(String name)
	{	
		return objects.get(name);
	}
	
	public boolean containsObject(String name)
	{
		return objects.containsKey(name);
	}
	
	public boolean containsObject(String name, SLNDictionaryObject.Type type)
	{
		return objects.containsKey(name);
	}
	
	
	
	
}
