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
		
	public Rule getRule(){
		return null;
	}
	
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
	
	
	//Utilities for generation of combination of states -----------------------------
	
	
	void findAllDoubleBonds()
	{
		//TODO
	}
	
	int generateCombinedRuleStates()
	{	
		/*
		
		int n; 
		int instNumber;
		int nInst = instances.size();		
		boolean FlagOKComb = true;
		
		//Generation of the first combined state (0, 0,..., 0)
		for (int i = 0; i < nInst; i++)
		{	
			instances.get(i).firstStateSpecial();
			if (!instances.get(i).FlagGoToStateSpecialOK)
				FlagOKComb = false;
		}	
						
		do 	{			
			//Register Combined State
			if (FlagOKComb)
			{
				int combState[] = new int[nInst];
				for (int i = 0; i < nInst; i++)
					combState[i] = instances.get(i).getCurrentState();
				combinedStates.add(combState);
			}
			
			//Started generation of a new combined state
			FlagOKComb = true;
			
			n = instances.get(0).nextStateSpecial();
			instNumber = 0;
			if (!instances.get(0).FlagGoToStateSpecialOK)
				FlagOKComb = false;
			
			while(n == 0)
			{
				instNumber++;
				if (instNumber == nInst)
					break;
				n = instances.get(instNumber).nextStateSpecial();
				if (!instances.get(instNumber).FlagGoToStateSpecialOK)
					FlagOKComb = false;
				
				//It might be needed to check all other instances: instNumber+1, instNumber+2, ... 
			}
		} while (instNumber < nInst); 
						
		*/
		
		return(combinedStates.size());
	}
	
	
	/* 
	 * These function are transfered to class Rule
	 * If needed they can be returned to this class again
	 * 
	public int firstStateSpecial(RuleInstance r)
	{	
		gotoStateSpecial(0, r);
		return(0);
	}
	
	int gotoStateSpecial(int state, RuleInstance r)
	{
		if (curState == state)
			return(state); //It is already at this state
				
		//TODO
		
		return(0);
	}
	*/
	
}
