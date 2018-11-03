package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;

public class AminoGroup implements IBasicCenter 
{
	public IAtom nitrogen = null;
	public int numHAtoms = 0;
	public int numHeavyAtoms = 0;
	public int charge = 0;
	
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

	@Override
	public IAtom[] getAtoms() {
		return null;
	}

	@Override
	public boolean explicitHAtoms() {
		return false;
	}	
}
