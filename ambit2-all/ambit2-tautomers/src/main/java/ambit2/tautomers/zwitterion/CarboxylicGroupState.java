package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class CarboxylicGroupState implements IAcidicCenter
{	
	IAtom carbon = null;
	IAtom oxygen1 = null; //hydroxyl group oxygen   
	IAtom oxygen2 = null; //carbonil group oxygen
	int numHAtoms = 0;
	int charge = 0;
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
	
	public static List<CarboxylicGroupState> findAllCenters(IAtomContainer mol)
	{
		List<CarboxylicGroupState> centers = new ArrayList<CarboxylicGroupState>();
		//TODO
		return centers;
	}
	
}
