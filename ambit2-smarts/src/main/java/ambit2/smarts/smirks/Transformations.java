package ambit2.smarts.smirks;

import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class Transformations 
{
	public static void fixSHValence7Atoms(IAtomContainer mol)
	{
		for (IAtom at: mol.atoms())
		{
			if (!at.getSymbol().equals("S"))
				continue;
			if (at.getImplicitHydrogenCount() == null)
				continue;
			if (at.getImplicitHydrogenCount() != 1)
				continue;
			
			if (at.getValency() != null)
			{	
				if (at.getValency() == 7)
					at.setImplicitHydrogenCount(null);
			}
			else
			{
				//If valence is not set bond order sum is calculated
				List<IBond> boList = mol.getConnectedBondsList(at);
				int boSum = 0;
				for (IBond bo: boList)
					boSum += bo.getOrder().numeric();
				if (boSum == 6)
					at.setImplicitHydrogenCount(null);
			}
		}
	}
}
