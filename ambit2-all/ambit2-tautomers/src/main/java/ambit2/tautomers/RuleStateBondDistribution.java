package ambit2.tautomers;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import java.util.Vector;

public class RuleStateBondDistribution 
{
	int DBPositions[]; //double bond positions
	
	public void calcDistribution(QueryAtomContainer statePattern)
	{
		int n = 0; 
		Vector<Integer> v = new Vector<Integer>(); 
		for (int i = 0; i < statePattern.getBondCount(); i++)
		{	
			if (statePattern.getBond(i) instanceof OrderQueryBond)
			{	
				OrderQueryBond bo = (OrderQueryBond)statePattern.getBond(i);
				if (bo.getOrder() == IBond.Order.DOUBLE)
					v.add(new Integer(i));
			}	
		}
		
		DBPositions = new int[v.size()];
		for (int i = 0; i <v.size(); i++)
			DBPositions[i] = v.get(i).intValue();
	}
	
	public String toString()
	{
		String s = "";		
		for (int i = 0; i < DBPositions.length; i++)
			s += DBPositions[i] + " ";
		return(s);
	}
}
