package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class RuleInstance implements IRuleInstance
{
	Rule rule;
	int foundState = 0;
	int curState = 0;
	int beginState = 0;
	
	Vector<IAtom> atoms = new  Vector<IAtom>();
	Vector<IBond> bond = new  Vector<IBond>();
	
	public int firstState()
	{	
		return(0);
	}
	
	public int nextState()
	{	
		return(0);
	}
	
	public int getNumberOfStates()
	{
		return rule.nStates;
	}
	
	public void getBondMappings()
	{
		//TODO
	}
}
