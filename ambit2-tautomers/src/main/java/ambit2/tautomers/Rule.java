package ambit2.tautomers;


import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import java.util.Vector;
import java.util.List;
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
	int nMobileGroups = 1;
	boolean isMobileH[] = new boolean[1]; //by default this class is prepared for a rule with one mobile group
	boolean isStandardRule = true; //e.g. H-X-Y=Z  <-->  X=Y-Z-H 
	boolean isRuleActive = true;
	int mobileGroupPos[][] = null;   
	String RuleInfo = "";
	QueryAtomContainer stateQueries[] = null;
	String OriginalRuleString = "";
	
	//ring closure info
	int ringClosureState = -1;
	int ringClosureBondNum = -1;
	int ringClosureBondFA = -1;
	int ringClosureBondSA = -1;
	IBond.Order  ringClosureBondOrder = IBond.Order.SINGLE;
	
	
	public Vector<IRuleInstance>  applyRule(IAtomContainer mol) throws Exception
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
				Vector<IAtom> amap = maps.get(k);
				//This check currently is done only for one mobile group
				int mobCheck = checkMobileGroup(0, i, amap, mol); 
				if (mobCheck == -1)
					continue;
				
				RuleInstance rinst = new RuleInstance();
				if (mobCheck == TautomerConst.IHA_INDEX)
					rinst.FlagImplicitH = true;
				else
				{	
					rinst.FlagImplicitH = false;
					rinst.explicitH = mol.getAtom(mobCheck);
				}	
				
				rinst.atoms = amap;
				rinst.bonds = isoTester.generateBondMapping(mol, amap);
				rinst.foundState = i;
				rinst.curState = i;
				rinst.rule = this;
				rinst.molecule = mol;
				instances.add(rinst);
			}
		}
		return instances;
	}
	
	int checkMobileGroup(int mobGroupNum, int curState, Vector<IAtom> amap, IAtomContainer mol) throws Exception
	{
		if (mobileGroup.equals("H"))
		{
			int pos = mobileGroupPos[mobGroupNum][curState];
			IAtom atom = amap.get(pos-1);  //position in the rule is 1-base indexed
			
			//Check for implicit hydrogens
			if (atom.getImplicitHydrogenCount().intValue() > 0)
				return (TautomerConst.IHA_INDEX);
			
			//Check for explicit hydrogens
			List<IAtom> conAtoms = mol.getConnectedAtomsList(atom);
			for (int i = 0; i < conAtoms.size(); i++)
			{
				if (conAtoms.get(i).getSymbol().equals("H"))
					return(mol.getAtomNumber(conAtoms.get(i)));
			}
		}
		return -1;
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
