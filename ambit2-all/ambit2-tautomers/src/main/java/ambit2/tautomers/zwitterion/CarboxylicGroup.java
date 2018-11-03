package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class CarboxylicGroup implements IAcidicCenter
{	
	IAtom carbon = null;
	IAtom oxygen1 = null; //hydroxyl oxygen   
	IAtom oxygen2 = null; //carbonyl oxygen
	IAtom hydrogen = null;
	IAtomContainer molecule = null;
	boolean explicitH = false;
	State state = State.NEUTRAL;
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public void setState(State state) {
		if (this.state == state)
			return; //nothing is done
		
		switch (state)
		{
		case ANION:
			oxygen1.setFormalCharge(-1);
			if (explicitH)
			{
				molecule.removeAtomAndConnectedElectronContainers(hydrogen);
				hydrogen = null;
			}
			else
				oxygen1.setImplicitHydrogenCount(oxygen1.getImplicitHydrogenCount()-1);
			break;
		case NEUTRAL:
			oxygen1.setFormalCharge(0);
			if (explicitH)
			{
				hydrogen = new Atom("H");
				molecule.addAtom(hydrogen);
				molecule.addBond(new Bond(oxygen1,hydrogen));
			}
			else
				oxygen1.setImplicitHydrogenCount(oxygen1.getImplicitHydrogenCount()+1);
			break;
		}
		
		this.state = state;
	}
	
	@Override
	public void shiftState() {
		switch (state)
		{
		case ANION:
			setState(State.NEUTRAL);
			break;
		case NEUTRAL:
			setState(State.ANION);
			break;
		}
	}
	
	public static CarboxylicGroup getCenter(IAtomContainer mol, IAtom atom)
	{
		if (atom.getAtomicNumber() != 6)
			return null;
		
		List<IBond> bonds = mol.getConnectedBondsList(atom); 
		if (bonds.size() < 2)
			return null;
				
		IAtom o1 = null;
		IAtom o2 = null;
		IAtom h = null;
		State st = State.NEUTRAL;
		boolean explH = false;
		
		//Check center neighbors
		for (IBond bo : bonds)
		{
			IAtom at;
			if (bo.getAtom(0) != atom)
				at = bo.getAtom(0);
			else
				at = bo.getAtom(1);
				
			if (at.getAtomicNumber() != 8)
				continue;
			
			if (bo.getOrder() == IBond.Order.SINGLE)
			{
				//Recognize hydroxyl group  or C-[O]-[H] 
				List<IAtom> at_list = mol.getConnectedAtomsList(at);
				if (at_list.size() == 1)
				{
					int implicitH = 0;
					if (at.getImplicitHydrogenCount() != null) 
						implicitH = at.getImplicitHydrogenCount();
					if (implicitH == 1)
						o1 = at; //C-[OH]
					else
					{
						if (at.getFormalCharge() != null)
							if (at.getFormalCharge() == -1)
							{
								//C-[O-]
								o1 = at;
								st = State.ANION;
							}
					}
				}
				else
					if (at_list.size() == 2)
					{					
						if ((at_list.get(0) != at) && (at_list.get(0).getAtomicNumber() == 1) )
						{	
							o1 = at;
							h = at_list.get(0);
							explH = true;
						}
						else
							if ((at_list.get(1) != at) && (at_list.get(1).getAtomicNumber() == 1) )
							{	
								o1 = at;
								h = at_list.get(1);
								explH = true;
							}
					}
			}
			else
				if (bo.getOrder() == IBond.Order.DOUBLE)
					o2 = at; //C=O	
		}
		
		if ((o1 != null) && (o2 != null))
		{
			CarboxylicGroup cg = new CarboxylicGroup();
			cg.carbon = atom;
			cg.oxygen1 = o1;
			cg.oxygen2 = o2;
			cg.state = st;
			cg.explicitH = explH;
			return cg;
		}
		else
			return null;
	}
	
	public static List<CarboxylicGroup> findAllCenters(IAtomContainer mol)
	{
		List<CarboxylicGroup> centers = new ArrayList<CarboxylicGroup>();
		for (IAtom at : mol.atoms())
		{
			CarboxylicGroup center = getCenter(mol, at);
			if (center != null)
				centers.add(center);
		}
		return centers;
	}

	@Override
	public IAtom[] getAtoms() {		
		IAtom[] atoms;
		if (explicitH && hydrogen != null)
			atoms = new IAtom[4];
		else
			atoms = new IAtom[3];
		atoms[0] = carbon;
		atoms[1] = oxygen1;
		atoms[2] = oxygen2;
		
		if (explicitH && hydrogen != null)
			atoms[3] = hydrogen;
		
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
