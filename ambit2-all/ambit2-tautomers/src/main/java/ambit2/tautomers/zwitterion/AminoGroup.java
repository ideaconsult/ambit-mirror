package ambit2.tautomers.zwitterion;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AminoGroup implements IBasicCenter 
{
	IAtom nitrogen = null;
	IAtom hydrogen = null;
	int heavyNeighborCount = 0;
	IAtomContainer molecule = null;
	boolean explicitH = false;
	State state = State.NEUTRAL;
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public void setState(State state) {
		this.state = state;
		// TODO 		
	}
	
	@Override
	public void shiftState() {
		switch (state)
		{
		case CATION:
			setState(State.NEUTRAL);
			break;
		case NEUTRAL:
			setState(State.CATION);
			break;
		} 
	}
	
	public static AminoGroup getCenter(IAtomContainer mol, IAtom atom)
	{
		if (atom.getAtomicNumber() != 7)
			return null;
		
		List<IAtom> neigh_atoms = mol.getConnectedAtomsList(atom); 
		IAtom h = null;
		int heavyAtCount = 0;
		State st = State.NEUTRAL;
		boolean explH = false;
		
		//Check center neighbors
		for (IAtom at : neigh_atoms)
		{		
			if (at.getAtomicNumber() == 1)
			{
				if (!explH);
				{
					explH = true;
					h = at;
				}
			}
			else
				heavyAtCount++;
		}
		
		if (atom.getFormalCharge() == +1)
		{	
			//At least one H atom is needed
			if (!explH)
				if (atom.getImplicitHydrogenCount() == 0)
					return null;
			st = State.CATION;
		}	
		else
		{
			if (atom.getFormalCharge() == 0)
			{
				if (explH)
					h = null; //No explicit hydrogen is attached for NEUTRAL 
			}
			else
				return null; //unusual N charge
		}
		
		AminoGroup ag = new AminoGroup();
		ag.nitrogen = atom;
		ag.hydrogen = h;
		ag.heavyNeighborCount = heavyAtCount;
		ag.state = st;
		ag.explicitH = explH;
		
		return ag;
	}

	@Override
	public IAtom[] getAtoms() {
		return null;
	}

	@Override
	public boolean explicitHAtoms() {
		return explicitH;
	}
	
	@Override
	public IAtomContainer getMolecule() {		
		return molecule;
	}
}
