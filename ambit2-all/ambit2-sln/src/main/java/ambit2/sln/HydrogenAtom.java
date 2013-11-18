package ambit2.sln;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class HydrogenAtom extends SLNAtom
{
	/**
	 * Local copy of IAtomContainer. 
	 */
	private IAtomContainer atomContainer;

	/**
	 * Creates a new instance.
	 *
	 */
	public HydrogenAtom()
	{
		super();
	}

	public boolean matches(IAtom atom) 
	{
		if (!atom.getSymbol().equals("H")) 
		{
			return false;
		}

		if (atom.getFormalCharge() == 1) 
		{
			// proton matches
			return true;
		}

		// hydrogens connected to other hydrogens, e.g., molecular hydrogen
		List<IAtom> list = atomContainer.getConnectedAtomsList(atom);
		for (IAtom connAtom: list)
		{
			if (connAtom.getSymbol().equals("H")) 
			{
				return true;
			}
		}

		// hydrogens connected to other than one other atom, e.g., bridging hydrogens
		if (atom.getFormalNeighbourCount() > 1)
		{
			return true;
		}

		//isotopic hydrogen specifications, e.g. deuterium [2H] or tritium etc
		if (atom.getMassNumber() != null) {
			if (getMassNumber().intValue() == atom.getMassNumber().intValue()) return true;
		} else {
		// target atom is [H], so make sure query atom has mass number = 1
			if (getMassNumber() == 1) return true;
		}

		return false;
	}

	public IAtomContainer getAtomContainer()
	{
		return atomContainer;
	}

	public void setAtomContainer(IAtomContainer atomContainer) 
	{
		this.atomContainer = atomContainer;
	}	
}
