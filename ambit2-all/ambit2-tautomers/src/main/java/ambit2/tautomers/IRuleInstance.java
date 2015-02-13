package ambit2.tautomers;

import java.util.List;
import org.openscience.cdk.interfaces.IAtom;

public interface IRuleInstance 
{
	public int firstState();
	public int nextState();	
	public int getNumberOfStates();
	public int getCurrentState();
	public Rule getRule();
	public List<IAtom> getAtoms();
}
