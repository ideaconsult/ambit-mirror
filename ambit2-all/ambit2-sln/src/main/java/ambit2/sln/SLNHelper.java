package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;


import ambit2.smarts.CMLUtilities;

public class SLNHelper 
{
	
	static public String getAtomsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			SLNAtom at = (SLNAtom)container.getAtom(i); 
			sb.append("  #" + i + "  ");
			sb.append(at.atomType);
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	public String toSLN(SLNContainer container)
	{
		return "";
	}
	
	
}
