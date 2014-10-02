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
	
	public int getNumOfAttributes()
	{
		int n = userDefiendAttr.size();
		if (name != null)
			n++;
		if (regid != null)
			n++;
		if (type != null)
			n++;
		if (coord2d != null)
			n++;
		if (coord3d != null)
			n++;
		return n;
	}
	
	public static String coord2dToString(ArrayList<double[]>coord2d)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coord2d.size(); i++)
			sb.append(coord2d.get(i));
		return sb.toString();
	}

	public static String coord3dToString(ArrayList<double[]>coord3d)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coord3d.size(); i++)
			sb.append(coord3d.get(i));
		return sb.toString();
	}

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
		
		if (sb.length() > 1)
			sb.append(";");
		if (coord2d != null)
			sb.append("coord2d="+coord2d);
		
		if (sb.length() > 1)
			sb.append(";");
		if (coord3d != null)
			sb.append("coord3d="+coord3d);
		
		
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
