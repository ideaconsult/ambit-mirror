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
	public String substituentDesignations[] = null;
	public int substituentPosAtomIndex[] = null; //it is not needed for SMARTS with one atom
	public int positionDistances[] = null; //it is not needed when only alpha positions are considered
		
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
		
		if (substituentDesignations != null)
		{
			sb.append("Substituent designations:");
			for (int i = 0; i < substituentDesignations.length; i++)
				sb.append(" " + substituentDesignations[i]);
			sb.append("\n");
		}
		
		if (substituentPosAtomIndex != null)
		{
			sb.append("Substituent pos  atom indices:");
			for (int i = 0; i < substituentPosAtomIndex.length; i++)
				sb.append(" " + substituentPosAtomIndex[i]);
			sb.append("\n");
		}
		
		if (positionDistances != null)
		{
			sb.append("Substituent position atom indices:");
			for (int i = 0; i < positionDistances.length; i++)
				sb.append(" " + positionDistances[i]);
			sb.append("\n");
		}
		
		sb.append("Substituents:\n");
		for (int i = 0; i < substituents.size(); i++)
			sb.append(substituents.get(i).toString() + "\n");
		
		return sb.toString();
	}
}
