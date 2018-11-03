package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class CarboxylicGroup implements IAcidicCenter
{	
	IAtom carbon = null;
	IAtom oxygen1 = null; //hydroxyl group oxygen   
	IAtom oxygen2 = null; //carbonil group oxygen
	//int numHAtoms = 0;
	//int charge = 0;
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
		//TODO 
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
							explH = true;
						}
						else
							if ((at_list.get(1) != at) && (at_list.get(1).getAtomicNumber() == 1) )
							{	
								o1 = at;
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
		return null;
	}

	@Override
	public boolean explicitHAtoms() {
		return false;
	}
	
}
