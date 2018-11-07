package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;


public class PhosphoricGroup implements IAcidicCenter 
{
	IAtom phosphorus = null; 
	IAtom oxygen1 = null; //P-OH   
	IAtom oxygen2 = null; //P=O
	IAtom hydrogen = null;
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
	
	public static PhosphoricGroup getCenter(IAtomContainer mol, IAtom atom)
	{
		if (atom.getAtomicNumber() != 15)
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
				//Recognize hydroxyl group P-[O]-[H] 
				List<IAtom> at_list = mol.getConnectedAtomsList(at);
				if (at_list.size() == 1)
				{
					int implicitH = 0;
					if (at.getImplicitHydrogenCount() != null) 
						implicitH = at.getImplicitHydrogenCount();
					if (implicitH == 1)
						o1 = at; //P-[OH]
					else
					{
						if (at.getFormalCharge() != null)
							if (at.getFormalCharge() == -1)
							{
								//P-[O-]
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
					o2 = at; //P=O	
		}
		
		if ((o1 != null) && (o2 != null))
		{
			PhosphoricGroup pg = new PhosphoricGroup();
			pg.phosphorus = atom;
			pg.oxygen1 = o1;
			pg.oxygen2 = o2;
			pg.hydrogen = h;
			pg.state = st;
			pg.explicitH = explH;
			return pg;
		}
		else
			return null;
	}
	
	public static List<PhosphoricGroup> findAllCenters(IAtomContainer mol)
	{
		List<PhosphoricGroup> centers = new ArrayList<PhosphoricGroup>();
		for (IAtom at : mol.atoms())
		{
			PhosphoricGroup center = getCenter(mol, at);
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
		atoms[0] = phosphorus;
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

