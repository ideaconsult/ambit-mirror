package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class RuleInstance implements IRuleInstance
{
	Rule rule;
	int foundState = 0;
	int curState = 0;  //Current state is used to define the current position of the mobile group
	int beginState = 0;
	boolean FlagImplicitH = true;   //This flag is true when mobile h group is implicitly described
	IAtom explicitH = null;
	
	Vector<IAtom> atoms = new  Vector<IAtom>();
	Vector<IBond> bonds = new  Vector<IBond>();
	
	public int firstState()
	{	
		gotoState(0);
		return(0);
	}
	
	public int nextState()
	{	
		int nextState = curState + 1;
		if (nextState == rule.nStates)
			nextState = 0;
		
		gotoState(nextState);
	
		return(nextState);
	}
	
	public int getCurrentState()
	{
		return(curState);
	}
	
	int gotoState(int state)
	{
		if (curState == state)
			return(state); //It is already at this state
		
		//current state double bonds are made single
		//TODO
		
		//new state double bonds are set
		//TODO
		
		//mobile group is moved
		//TODO
		
		
		curState = state;
		return(state);
	}
	
	public int getNumberOfStates()
	{
		return rule.nStates;
	}
}
