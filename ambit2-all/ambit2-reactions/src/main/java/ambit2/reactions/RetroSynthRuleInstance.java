package ambit2.reactions;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class RetroSynthRuleInstance implements IRetroSynthRuleInstance 
{	
	RetroSynthRule rule;	
	public ArrayList<IAtom> atoms = new ArrayList<IAtom>();	
	
	
	public ArrayList<IAtomContainer> apply(IAtomContainer mol)
	{	
		//TODO
		return null;
	}

	
}
