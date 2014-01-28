package ambit2.sln;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;



public class SLNContainerAttributes
{
	public HashMap<String,String> userDefiendAttr = new HashMap<String,String>();

	public String name = null;
	public String regid = null;
	public String type = null;
	public ArrayList<double[]> coord2d = null;
	public ArrayList<double[]> coord3d = null;

	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		if (name != null)
			sb.append("name="+name);
		
		if (sb.length() > 1)
			sb.append(";");
		if (regid != null)
			sb.append("regid="+regid);
		
		if (sb.length() > 1)
			sb.append(";");
		if (type != null)
			sb.append("type="+type);
		
		
		
		
		Set<Map.Entry<String,String>> set = userDefiendAttr.entrySet();		
		Iterator<Map.Entry<String,String>> iterator = set.iterator();
		
		while (iterator.hasNext())
		{		
			if (sb.length() > 1)
				sb.append(";");
			Map.Entry<String,String> entry = iterator.next();
			sb.append(entry.getKey() + "=" + entry.getValue());
			
		}	
		sb.append(">");
		return sb.toString();
	}
	
	
}
