package ambit2.markush;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.PseudoAtom;

import ambit2.core.groups.SuppleAtomContainer;

public class MarkushHelpers 
{
	static String endLine = System.getProperty("line.separator");
	
	public static String toStringExhaustive(SuppleAtomContainer sac)
	{	
		StringBuffer sb = new StringBuffer();
		
		sb.append("Atom List" + endLine);
		sb.append("--------------" + endLine);
		for (int i = 0; i < sac.getAtomCount(); i++)
		{	
			IAtom at = sac.getAtom(i);
			sb.append("  " + i + "  ");
			
			if (at instanceof PseudoAtom)
			{	
				sb.append("PseudoAtom: " + at.getClass().getName() + endLine);
				//PseudoAtom pat = (PseudoAtom) at; 
				continue;
			}	
			
			
			sb.append(at.getSymbol() + endLine);
		}
		
		return(sb.toString());
	}
}
