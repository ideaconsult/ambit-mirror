package ambit2.tautomers;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import java.util.Vector;
import ambit2.smarts.DoubleNonAromaticBond;
import ambit2.smarts.DoubleBondAromaticityNotSpecified;
import ambit2.smarts.SmartsBondExpression;
import ambit2.smarts.SmartsToChemObject;
import org.openscience.cdk.silent.SilentChemObjectBuilder;


public class RuleStateBondDistribution 
{
	int DBPositions[]; //double bond positions
	int TBPositions[]; //triple bond positions
	
	//ring closure info
	boolean hasRingClosure = false;	
	int ringClosureFA, ringClosureSA, ringClosureBondType, ringClosureBondIndex;   
	
	public void setRingClosure(int rcFA, int rcSA, int rcBondType, int rcBondIndex)
	{
		hasRingClosure = true;
		ringClosureFA = rcFA;
		ringClosureSA = rcSA;
		ringClosureBondIndex = rcBondIndex;		
		ringClosureBondType = rcBondType;
	}
	
	public void calcDistribution(QueryAtomContainer statePattern)
	{
		int n = 0; 
		SmartsToChemObject stco = new SmartsToChemObject(SilentChemObjectBuilder.getInstance());
		Vector<Integer> v2 = new Vector<Integer>();
		Vector<Integer> v3 = new Vector<Integer>();
		for (int i = 0; i < statePattern.getBondCount(); i++)
		{	
			if (statePattern.getBond(i) instanceof DoubleNonAromaticBond)
			{	
				v2.add(new Integer(i));
				continue;
			}
			
			if (statePattern.getBond(i) instanceof DoubleBondAromaticityNotSpecified)
			{	
				v2.add(new Integer(i));
				continue;
			}
			
			if (statePattern.getBond(i) instanceof OrderQueryBond)
			{	
				OrderQueryBond bo = (OrderQueryBond)statePattern.getBond(i);
				if (bo.getOrder() == IBond.Order.DOUBLE)
					v2.add(new Integer(i));
				
				if (bo.getOrder() == IBond.Order.TRIPLE)
					v3.add(new Integer(i));
				continue;
			}
			
			if (statePattern.getBond(i) instanceof SmartsBondExpression)
			{
				IBond bo = stco.smartsExpressionToBond((SmartsBondExpression)statePattern.getBond(i));
				if (bo != null)
				{	
					if (bo.getOrder() == IBond.Order.DOUBLE)
						v2.add(new Integer(i));
					
					if (bo.getOrder() == IBond.Order.TRIPLE)
						v3.add(new Integer(i));
				}
				continue;
			}
		}
		
		DBPositions = new int[v2.size()];
		for (int i = 0; i <v2.size(); i++)
			DBPositions[i] = v2.get(i).intValue();
		
		TBPositions = new int[v3.size()];
		for (int i = 0; i <v3.size(); i++)
			TBPositions[i] = v3.get(i).intValue();
		
	}
	
	public String toString()
	{
		String s = "DB: ";		
		for (int i = 0; i < DBPositions.length; i++)
			s += DBPositions[i] + " ";
		
		s+= "   TB: ";
		for (int i = 0; i < TBPositions.length; i++)
			s += TBPositions[i] + " ";
		return(s);
	}
}
