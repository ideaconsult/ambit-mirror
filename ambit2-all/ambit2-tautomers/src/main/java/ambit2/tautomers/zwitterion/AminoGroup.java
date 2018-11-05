package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
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
	public void setState(State state) 
	{
		if (this.state == state)
			return; //nothing is done
		
		switch (state)
		{
		case CATION:
			nitrogen.setFormalCharge(+1);
			if (explicitH)
			{
				hydrogen = new Atom("H");
				molecule.addAtom(hydrogen);
				molecule.addBond(new Bond(nitrogen,hydrogen));
			}
			else
				nitrogen.setImplicitHydrogenCount(nitrogen.getImplicitHydrogenCount()+1);
			break;
			
		case NEUTRAL:
			nitrogen.setFormalCharge(0);
			if (explicitH)
			{
				molecule.removeAtomAndConnectedElectronContainers(hydrogen);
				hydrogen = null;
			}
			else
				nitrogen.setImplicitHydrogenCount(nitrogen.getImplicitHydrogenCount()-1);
			break;
		}
		
		this.state = state;		
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
		ag.molecule = mol;
		
		return ag;
	}
	
	public static List<AminoGroup> findAllCenters(IAtomContainer mol)
	{
		List<AminoGroup> centers = new ArrayList<AminoGroup>();
		for (IAtom at : mol.atoms())
		{
			AminoGroup center = getCenter(mol, at);
			if (center != null)
				centers.add(center);
		}
		return centers;
	}

	@Override
	public IAtom[] getAtoms() {
		IAtom[] atoms;
		if (explicitH && hydrogen != null)
			atoms = new IAtom[2];
		else
			atoms = new IAtom[1];
		
		atoms[0] = nitrogen;
		if (explicitH && hydrogen != null)
			atoms[1] = hydrogen;
		
		return atoms;
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
