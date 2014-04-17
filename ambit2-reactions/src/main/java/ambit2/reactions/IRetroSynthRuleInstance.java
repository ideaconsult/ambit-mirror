package ambit2.reactions;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public interface IRetroSynthRuleInstance 
{	
	public ArrayList<IAtom> getAtoms();
	public IRetroSynthRule getRule();
	public IAtomContainer getContainer();
}
