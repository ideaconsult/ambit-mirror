package ambit2.groupcontribution.nmr;

import ambit2.smarts.groups.GroupMatch;

public class Substituent 
{
	public Substituent()
	{	
	}
	
	public String info = null;
	public String name = null;
	public String smarts = null;
	public double chemShifts[] = null;
	
	public GroupMatch groupMatch = null;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("SUBST = " + name );
		sb.append("  " + smarts);
		if (chemShifts != null)
		{
			for (int i = 0; i < chemShifts.length; i++)
				sb.append("  " + chemShifts[i]);				
		}
		
		return sb.toString();
	}	
}
