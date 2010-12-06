package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

public class RuleInstance implements IRuleInstance
{
	Rule rule;
	IAtomContainer molecule;
	int foundState = 0;
	int curState = 0;  //Current state is used to define the current position of the mobile group
	int beginState = 0;
	boolean FlagImplicitH = true;   //This flag is true when mobile h group is implicitly described
	IAtom explicitH = null;
	boolean FlagOverlapMode = false; //This is true when this instance overlaps with another one i.e. it is a part from combination
	
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
		RuleStateBondDistribution bondDistr = rule.stateBonds[curState];
		for (int i = 0; i < bondDistr.DBPositions.length; i++)
		{
			int bpos = bondDistr.DBPositions[i];
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.SINGLE);
		}
		
		
		//new state double bonds are set
		bondDistr = rule.stateBonds[state];
		for (int i = 0; i < bondDistr.DBPositions.length; i++)
		{
			int bpos = bondDistr.DBPositions[i];
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.DOUBLE);
		}
		
		//mobile groups are moved
		for (int i = 0; i < rule.nMobileGroups; i++)
		{	
			if (rule.isMobileH[i] == true)
			{	
				int curHPos = rule.mobileGroupPos[i][curState];
				int newHPos = rule.mobileGroupPos[i][state];
				IAtom curAt = atoms.get(curHPos-1);
				IAtom newAt = atoms.get(newHPos-1);

				if (FlagImplicitH)
				{	
					curAt.setImplicitHydrogenCount(curAt.getImplicitHydrogenCount()-1);				
					newAt.setImplicitHydrogenCount(newAt.getImplicitHydrogenCount()+1);
				}
				else
				{	
					//handling explicit H atom
					IBond b = molecule.getBond(curAt,explicitH);
					b.setAtoms(new IAtom[]{newAt, explicitH});
				}
			}
			else
			{	
				//TODO handle other types of mobiles groups 
			}	
		}
		
		curState = state;
		return(state);
	}
	
	public int getNumberOfStates()
	{
		return rule.nStates;
	}
}
