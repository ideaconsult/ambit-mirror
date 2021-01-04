package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SLNDictionary 
{
	private HashMap<String,ISLNDictionaryObject> objects = new HashMap<String,ISLNDictionaryObject>();
	private List<String> names = new ArrayList<String>();
	
		
	public ISLNDictionaryObject getDictionaryObject(String name)
	{	
		return objects.get(name);
	}
	
	public ISLNDictionaryObject getDictionaryObject(int index)
	{	
		String name = names.get(index);
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
	
	public List<String> getNames() 
	{
		return names;
	}
	
}
