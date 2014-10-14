package ambit2.sln.dictionary;

import java.util.HashMap;



public class SLNDictionary 
{
	private HashMap<String,ISLNDictionaryObject> objects = new HashMap<String,ISLNDictionaryObject>();
	
	
	public ISLNDictionaryObject getDictionaryObject(String name)
	{	
		return objects.get(name);
	}
	
	public boolean containsObject(String name)
	{
		return objects.containsKey(name);
	}
	
	public boolean containsObject(String name, ISLNDictionaryObject.Type type)
	{
		return objects.containsKey(name);
	}
	
}
