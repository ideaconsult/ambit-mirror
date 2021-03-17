package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.nmr.Substituent;
import ambit2.smarts.groups.GroupMatch;

public class HAtomEnvironment 
{
	public static enum ShiftAssociation {
		SUBSTITUENT_POSITION, H_ATOM_POSITION
	}
	
	public boolean flagUse = true;
	public String flagUseString = null;
	public String name = null;
	public String smarts = null;
	public String info = "";
	public Double chemShift0 = null;
	public Integer implicitHAtomsNumber = null;
	public String shiftDesignations[] = null;
	public int substituentPosAtomIndices[] = null; //it is not needed for SMARTS with one atom
	public int positionDistances[] = null; //it is not needed when only alpha positions are considered	
	public int hPosAtomIndices[] = null;
	public String higherPriorityEnvironments[] = null;
	
	public ShiftAssociation shiftsAssociation = null;
	public List<Substituent> substituents = new ArrayList<Substituent>(); 
	
	public GroupMatch groupMatch = null;
	
	
	public boolean isHigherPriority(HAtomEnvironment hae)
	{
		if (higherPriorityEnvironments != null)
		{	
			for (String s : higherPriorityEnvironments)
				if (hae.name.equals(s))
					return true;
		}
		return false;
	}
	
	public int getSubstituentPosAtomIndicex(int shiftIndex)
	{
		int pos = 0; //default value
		if (substituentPosAtomIndices != null)
			pos = substituentPosAtomIndices[shiftIndex]-1; //1-base --> 0-base	
		return pos;
	}
	
	public int getPositionDistance(int shiftIndex)
	{
		int distance = 1; //default value
		if (positionDistances != null)
			distance = positionDistances[shiftIndex];
		return distance;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("HAtomEnvironment: " + name + "\n");
		if (flagUseString == null)
			sb.append("FlagUse: " + flagUse + "\n");
		else
			sb.append("FlagUse: " + flagUseString + "\n");
		sb.append("Smarts: " + smarts + "\n");
				
		if (!info.contentEquals(""))
			sb.append("info:" + info + "\n");		
		sb.append("ChemShift0: " + chemShift0 + "\n");
		sb.append("implicitHAtomsNumber: " + implicitHAtomsNumber + "\n");
				
		
		if (shiftDesignations != null)
		{
			sb.append("Shift designations:");
			for (int i = 0; i < shiftDesignations.length; i++)
				sb.append(" " + shiftDesignations[i]);
			sb.append("\n");
		}
		
		if (substituentPosAtomIndices != null)
		{
			sb.append("Substituent posistion atom indices:");
			for (int i = 0; i < substituentPosAtomIndices.length; i++)
				sb.append(" " + substituentPosAtomIndices[i]);
			sb.append("\n");
		}
		
		if (hPosAtomIndices != null)
		{
			sb.append("H posistion atom indices:");
			for (int i = 0; i < hPosAtomIndices.length; i++)
				sb.append(" " + hPosAtomIndices[i]);
			sb.append("\n");
		}
		
		if (positionDistances != null)
		{
			sb.append("Substituent position distances:");
			for (int i = 0; i < positionDistances.length; i++)
				sb.append(" " + positionDistances[i]);
			sb.append("\n");
		}
		
		if (higherPriorityEnvironments != null)
		{
			sb.append("Higher Priority Environments:");
			for (int i = 0; i < higherPriorityEnvironments.length; i++)
				sb.append(" " + higherPriorityEnvironments[i]);
			sb.append("\n");
		}
		
		if (shiftsAssociation != null)
		{
			sb.append("Shifts association: ");
			sb.append(shiftsAssociation);
			sb.append("\n");
		}
		
		sb.append("Substituents:\n");
		for (int i = 0; i < substituents.size(); i++)
			sb.append(substituents.get(i).toString() + "\n");
		
		return sb.toString();
	}
}
