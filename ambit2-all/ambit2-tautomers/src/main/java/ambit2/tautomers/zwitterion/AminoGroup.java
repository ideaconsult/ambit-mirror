package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AminoGroup implements IBasicCenter 
{
	public IAtom nitrogen = null;
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
