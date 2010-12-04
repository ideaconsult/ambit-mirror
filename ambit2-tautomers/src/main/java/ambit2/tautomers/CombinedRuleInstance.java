package ambit2.tautomers;

import java.util.Vector;
import org.openscience.cdk.interfaces.IAtomContainer;

//This class currently is not used since the combined states generate
//a new rule and an instance for it.
public class CombinedRuleInstance implements IRuleInstance
{	
	IAtomContainer molecule;
	int foundState = 0;
	int curState = 0;  
	int beginState = 0;
	
	Vector<RuleInstance> instances = new Vector<RuleInstance>(); 
	Vector<int[]> combinedStates = new Vector<int[]>(); 
		
	
	public int firstState()
	{	
		gotoState(0);
		return(0);
	}
	
	public int nextState()
	{
		int nextState = curState + 1;
		if (nextState == combinedStates.size())
			nextState = 0;
		
		gotoState(nextState);
	
		return(nextState);
	}
	
	public int getNumberOfStates()
	{
		return(combinedStates.size());
	}
	public int getCurrentState()
	{
		return(curState);
	}
	
	int gotoState(int state)
	{
		if (curState == state)
			return(state); //It is already at this state
		
		
		//TODO
		
		return(0);
	}	
	
}
