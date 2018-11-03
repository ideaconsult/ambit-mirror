package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class SulfinicGroup implements IAcidicCenter 
{
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
	
	@Override
	public IAtomContainer getMolecule() {		
		return null;
	}
}
