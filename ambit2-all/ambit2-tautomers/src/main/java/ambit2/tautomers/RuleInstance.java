package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class RuleInstance 
{
	Rule rule;
	int state = 0;
	Vector<IAtom> atoms = new  Vector<IAtom>();
	Vector<IBond> bond = new  Vector<IBond>();
	
	public void nextState()
	{
		
	}
}
