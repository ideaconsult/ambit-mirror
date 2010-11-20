package ambit2.tautomers;


import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;

import java.util.Vector;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.SmartsParser;
import ambit2.smarts.IsomorphismTester;

public class Rule 
{
	int HAtomMode = TautomerConst.HRM_Implicit;
	String name = null;	
	int type = TautomerConst.RT_MobileGroup;
	String mobileGroup = null;
	int nStates = 2;
	String smartsStates[] = null;
	RuleStateFlags stateFlags[] = null;
	RuleStateBondDistribution   stateBonds[] = null;
	int mobileGroupPos[] = null;
	String RuleInfo = "";
	QueryAtomContainer stateQueries[] = null;
	
	
	public Vector<IRuleInstance>  applyRule(IAtomContainer mol)
	{
		IsomorphismTester isoTester = new IsomorphismTester(); 
		Vector<IRuleInstance> instances = new Vector<IRuleInstance>();
		for (int i = 0; i < stateQueries.length; i++)
		{
			QueryAtomContainer query = stateQueries[i];			
			RuleStateFlags flags = stateFlags[i];			
			SmartsParser.prepareTargetForSMARTSSearch(
				flags.mNeedNeighbourData, 
				flags.mNeedValenceData, 
				flags.mNeedRingData, 
				flags.mNeedRingData2, 
				flags.mNeedExplicitHData , 
				flags.mNeedParentMoleculeData, mol);	
			
			isoTester.setQuery(query);
			Vector<Vector<IAtom>> maps = isoTester.getAllIsomorphismMappings(mol);		
			for (int k = 0; k < maps.size(); k++)
			{
				//TODO check for the mobile group
				//..
				RuleInstance rinst = new RuleInstance();
				rinst.atoms = maps.get(k);
				rinst.getBondMappings();
				rinst.foundState = i;
				rinst.rule = this;
				instances.add(rinst);
			}
		}
		return instances;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("TAUTOMER RULE\n");
		sb.append("NAME = " + name  + "\n");
		sb.append("TYPE = " + type  + "\n");
		sb.append("GROUP = " + mobileGroup  + "\n");		
		return(sb.toString());
	}
}
