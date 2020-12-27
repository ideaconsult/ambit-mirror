package ambit2.sln;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;



public class SLNContainerAttributes
{	
	//TODO handle brackets within expression
	
	public Map<String,String> userDefiendAttr = new HashMap<String,String>();
	public Map<String,Integer> userDefiendAttrComparisonOperation = new HashMap<String,Integer>();
	
	
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
		{	
			sb.append("(");
			sb.append(coord2d.get(i)[0]);
			sb.append(",");
			sb.append(coord2d.get(i)[1]);
			sb.append(")");
			if(i < coord2d.size()-1)
				sb.append(",");
		}	
		return sb.toString();
	}

	public static String coord3dToString(ArrayList<double[]>coord3d)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coord3d.size(); i++)
		{	
			sb.append("(");
			sb.append(coord3d.get(i)[0]);
			sb.append(",");
			sb.append(coord3d.get(i)[1]);
			sb.append(",");
			sb.append(coord3d.get(i)[2]);
			sb.append(")");
			if(i < coord3d.size()-1)
				sb.append(",");
		}	
		return sb.toString();
	}

	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		if (name != null)			
			sb.append("name="+name);
		
		if (regid != null)
		{	
			if (sb.length() > 1)
				sb.append(";");
			sb.append("regid="+regid);
		}	
		
		
		if (type != null)
		{	
			if (sb.length() > 1)
				sb.append(";");
			sb.append("type="+type);
		}
			
		
		if (coord2d != null)
		{	
			if (sb.length() > 1)
				sb.append(";");
			sb.append("coord2d="+coord2dToString(coord2d));
		}
		
		
		if (coord3d != null)
		{	
			if (sb.length() > 1)
				sb.append(";");
			sb.append("coord3d="+coord3dToString(coord3d));
		}
		
		Set<Map.Entry<String,String>> set = userDefiendAttr.entrySet();		
		Iterator<Map.Entry<String,String>> iterator = set.iterator();
		
		while (iterator.hasNext())
		{		
			if (sb.length() > 1)
				sb.append(";");
			Map.Entry<String,String> entry = iterator.next();
			//handle comparison operation
			Integer compOp = userDefiendAttrComparisonOperation.get(entry.getKey());
			String compOpStr = SLNConst.comparisonOperationToSLNString(compOp);
			sb.append(entry.getKey() + compOpStr + entry.getValue());
			
		}	
		sb.append(">");
		
		String attr = sb.toString();
		if (attr.equals("<>"))
			return "";
		else
			return attr;
	}
	
	
	public boolean matchesUserDefinedAttributes(IAtomContainer mol)
	{
		Set<Map.Entry<String,String>> set = userDefiendAttr.entrySet();		
		Iterator<Map.Entry<String,String>> iterator = set.iterator();
		
		while (iterator.hasNext())
		{	
			Map.Entry<String,String> entry = iterator.next();
			String attrib = entry.getKey();
			String value = entry.getValue();
			
			//Get comparison operation
			Integer compOp = userDefiendAttrComparisonOperation.get(entry.getKey());			
			//Get molecule property
			Object prop = mol.getProperty(attrib);
			
			if (prop == null)
			{
				if (compOp == SLNConst.CO_DIFFERS)
					continue;
				else
				{
					if (value.equalsIgnoreCase("null"))
						continue;
					else
						return false;
				}
			}
			
			String propVal = prop.toString();
			boolean res = SLNConst.compare(propVal, value, compOp);
			
			if (!res)
				return false;
		}	
		
		return true;
	}
	
	
}
