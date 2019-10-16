package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.nmr.Substituent;
import ambit2.smarts.groups.GroupMatch;

public class HAtomEnvironment 
{
	public String name = null;
	public String smarts = null;
	public String info = "";
	public Double chemShift0 = null;
	public String substituentPosDesignations[] = null;
	public int substituentPosAtomIndex[] = null;
		
	public List<Substituent> substituents = new ArrayList<Substituent>(); 
	
	public GroupMatch groupMatch = null;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("HAtomEnvironment: " + name + "\n");
		sb.append("Smarts: " + smarts + "\n");
				
		if (!info.contentEquals(""))
			sb.append("info:" + info + "\n");		
		sb.append("ChemShift0: " + chemShift0 + "\n");
		
		if (substituentPosDesignations != null)
		{
			sb.append("Substituent designations:");
			for (int i = 0; i < substituentPosDesignations.length; i++)
				sb.append(" " + substituentPosDesignations[i]);
			sb.append("\n");
		}
		
		if (substituentPosAtomIndex != null)
		{
			sb.append("Substituent pos  atom indices:");
			for (int i = 0; i < substituentPosAtomIndex.length; i++)
				sb.append(" " + substituentPosAtomIndex[i]);
			sb.append("\n");
		}
		
		sb.append("Substituents:\n");
		for (int i = 0; i < substituents.size(); i++)
			sb.append(substituents.get(i).toString() + "\n");
		
		return sb.toString();
	}
}
