package ambit2.notation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NotationData 
{
		
	//The string key is formatted <section name>.<element name>
	public Map<String, Object> elementData = new HashMap<String, Object>();
	
	public List<String> checkData(NotationConfig config) 
	{
		List<String> errors = new ArrayList<String>();
		
		//Checking element data
		Set<Entry<String, Object>> entries = elementData.entrySet();
		for (Entry<String, Object> entry : entries)
		{
			String tokens[] = entry.getKey().split(".");
			String sectionName = null;
			String elementName = null;
			if (tokens.length == 1)
				elementName = tokens[0];
			else
			{
				elementName = tokens[0];
				elementName = tokens[1];
			}
			
			String err = checkElement(sectionName, elementName, config);
			if (err != null)
				errors.add(err);
		}
		
		return errors;
	}
	
	public String checkElement(String sectionName, String elementName, NotationConfig config)
	{
		//TODO
		return null;
	}
	
}
