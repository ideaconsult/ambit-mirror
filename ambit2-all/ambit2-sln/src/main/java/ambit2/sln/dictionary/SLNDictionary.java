package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ambit2.sln.SLNHelper;


public class SLNDictionary 
{
	private HashMap<String,ISLNDictionaryObject> objects = new HashMap<String,ISLNDictionaryObject>();
	private List<String> names = new ArrayList<String>();
	
		
	public void addDictionaryObject(ISLNDictionaryObject dictObj)
	{
		names.add(dictObj.getObjectName());
		objects.put(dictObj.getObjectName(), dictObj);
	}
	
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
	
	public String toSLN() {
		StringBuffer sb = new StringBuffer();
		SLNHelper slnHelper = new SLNHelper();
		
		for (int i = 0; i<names.size(); i++)
		{
			sb.append("{");
			String name = names.get(i);
			sb.append(name);
			sb.append(":");
			sb.append(toSLN(objects.get(name), slnHelper));
			sb.append("}");
		}
		return sb.toString();
	}
	
	
	String toSLN(ISLNDictionaryObject dictObj, SLNHelper slnHelper) {
		switch (dictObj.getObjectType())
		{
		case MACRO_ATOM:
			MacroAtomDictionaryObject ma = (MacroAtomDictionaryObject) dictObj;
			return slnHelper.toSLN(ma.container);
		}
		
		return "";
	}
}
