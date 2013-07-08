package ambit2.reactions;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class RetroSynthRuleInstance 
{	
	public int priority = 0;
	RetroSynthRule rule;	
	public Vector<IAtom> atoms = new Vector<IAtom>();	
	
	
	public Vector<IAtomContainer> apply(IAtomContainer mol)
	{	
		//TODO
		return null;
	}

	
}
