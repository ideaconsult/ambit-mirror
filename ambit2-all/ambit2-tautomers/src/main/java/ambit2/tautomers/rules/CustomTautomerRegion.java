package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.TopLayer;
import ambit2.tautomers.RuleStateFlags;

public class CustomTautomerRegion 
{
	public String smarts = null;
	public RuleStateFlags flags = null;
	public IQueryAtomContainer query = null;
	
	public static List<IAtom[]> getNitroGroupPositions(IAtomContainer target)
	{
		List<IAtom[]> pos = new ArrayList<IAtom[]>();
		for (IAtom at : target.atoms())
		{
			if (!at.getSymbol().equals("N"))
				continue;
			
			TopLayer tl = (TopLayer)at.getProperty(TopLayer.TLProp);
			if (tl.atoms.size() < 3)
				continue;
			
			int nOAtoms = 0;
			IAtom atoms[] = new IAtom[3];
			atoms[0] = at;
			
			for (int i = 0; i < tl.atoms.size(); i++)
			{
				IAtom a0 = tl.atoms.get(i);
				if (a0.getSymbol().equals("O"))
				{	
					if ((at.getFormalCharge() == 0) && (a0.getFormalCharge() == 0) 
							&& (tl.bonds.get(i).getOrder() ==IBond.Order.DOUBLE))
					{
						//N=O
						nOAtoms++;
						atoms[nOAtoms] = a0;
					}
					else
						if ((at.getFormalCharge() == +1) && (a0.getFormalCharge() == -1) 
								&& (tl.bonds.get(i).getOrder() ==IBond.Order.SINGLE))
						{
							//[N+]-[O-]
							nOAtoms++;
							atoms[nOAtoms] = a0;
						}	
				}
				
				if (nOAtoms == 2)
					break;
			}
			if (nOAtoms == 2)
				pos.add(atoms);
		}
		
		return pos;
	}
	
	public static List<IAtom[]> getNitroxidePositions(IAtomContainer target)
	{
		List<IAtom[]> pos = new ArrayList<IAtom[]>();
		for (IAtom at : target.atoms())
		{
			if (!at.getSymbol().equals("N"))
				continue;
			
			TopLayer tl = (TopLayer)at.getProperty(TopLayer.TLProp);
			if (tl.atoms.size() < 1)
				continue;
			
			for (int i = 0; i < tl.atoms.size(); i++)
			{
				IAtom a0 = tl.atoms.get(i);
				if (a0.getSymbol().equals("O"))
				{
					
					if ((at.getFormalCharge() == 0) && (a0.getFormalCharge() == 0) 
							&& (tl.bonds.get(i).getOrder() ==IBond.Order.DOUBLE))
					{
						//N=O
						IAtom atoms[] = new IAtom[2];
						atoms[0] = at;
						atoms[1] = a0;
						pos.add(atoms);
						break;
					}
					else
						if ((at.getFormalCharge() == +1) && (a0.getFormalCharge() == -1) 
								&& (tl.bonds.get(i).getOrder() ==IBond.Order.SINGLE))
						{
							//[N+]-[O-]
							IAtom atoms[] = new IAtom[2];
							atoms[0] = at;
							atoms[1] = a0;
							pos.add(atoms);
							break;
						}	
				}
			}
		}
		
		return pos;
	}
	
		
}