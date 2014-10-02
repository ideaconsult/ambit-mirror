package ambit2.sln;

import java.util.Set;

public class SLNHelper 
{
	
	static public String getAtomsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			SLNAtom at = (SLNAtom)container.getAtom(i); 
			sb.append("  #" + i + "  ");
			sb.append(at.atomType + "  " + at.atomName + "  H" + at.numHAtom + "  " + at.toString());
			//TODO print atom attributes 
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public String getBondsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < container.getBondCount(); i++)
		{
			SLNBond bo = (SLNBond)container.getBond(i);  
			SLNAtom at0 = (SLNAtom)bo.getAtom(0);
			SLNAtom at1 = (SLNAtom)bo.getAtom(1);
			int at0_num = container.getAtomNumber(at0);
			int at1_num = container.getAtomNumber(at1);
			sb.append("  #" + i + " atoms (" + at0_num + "," + at1_num + ") order "  + bo.toString());

			sb.append("\n");
		}
		return(sb.toString());
	}
	
	static public String getMolAttributes(SLNContainer container)
	{
		SLNContainerAttributes attr = container.getAttributes();
		if (attr.getNumOfAttributes() == 0)
			return "";
		
		StringBuffer sb = new StringBuffer();
		if (attr.name != null)
			sb.append("   name = " + attr.name + "\n");
		if (attr.regid != null)
			sb.append("   regid = " + attr.regid + "\n");
		if (attr.type != null)
			sb.append("   type = " + attr.type + "\n");
		
		Set<String> keys = attr.userDefiendAttr.keySet();
		for (String key : keys)
		{	
			sb.append("   " + key);
			String value = attr.userDefiendAttr.get(key);
			if (value != null)
				sb.append(" = " + value);
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	
	/*	public String toSLN(SLNContainer container)
	{
		//Temporary code
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < container.getAtomCount(); i++)
			sb.append(container.getAtom(i).toString());
		return sb.toString();	
	}*/
	
	
}
