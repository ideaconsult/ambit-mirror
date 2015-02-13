package ambit2.tautomers;


import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;

public class Rule 
{
	public int HAtomMode = TautomerConst.HRM_Implicit;
	public String name = null;	
	public int type = TautomerConst.RT_MobileGroup;
	public String mobileGroup = null;
	public int nStates = 2;
	public String smartsStates[] = null;
	public RuleStateFlags stateFlags[] = null;
	public RuleStateBondDistribution   stateBonds[] = null;
	public int nMobileGroups = 1;
	public boolean isMobileH[] = new boolean[1]; //by default this class is prepared for a rule with one mobile group
	public boolean isStandardRule = true; //e.g. H-X-Y=Z  <-->  X=Y-Z-H 
	public boolean isRuleActive = true;
	public int mobileGroupPos[][] = null;   
	public String RuleInfo = "";
	public IQueryAtomContainer stateQueries[] = null;
	public String OriginalRuleString = "";
	public RankingRule rankingRule = null;
	
	//ring closure info
	public int ringClosureState = -1;
	public int ringClosureBondNum = -1;
	public int ringClosureBondFA = -1;
	public int ringClosureBondSA = -1;
	public IBond.Order  ringClosureBondOrder = IBond.Order.SINGLE;
	
	
	public List<IRuleInstance>  applyRule(IAtomContainer mol) throws Exception
	{
		IsomorphismTester isoTester = new IsomorphismTester(); 
		List<IRuleInstance> instances = new ArrayList<IRuleInstance>();
		for (int i = 0; i < stateQueries.length; i++)
		{
			IQueryAtomContainer query = stateQueries[i];			
			RuleStateFlags flags = stateFlags[i];			
			SmartsParser.prepareTargetForSMARTSSearch(
				flags.mNeedNeighbourData, 
				flags.mNeedValenceData, 
				flags.mNeedRingData, 
				flags.mNeedRingData2, 
				flags.mNeedExplicitHData , 
				flags.mNeedParentMoleculeData, mol);	
			
			isoTester.setQuery(query);
			List<List<IAtom>> maps = isoTester.getAllIsomorphismMappings(mol);		
			for (int k = 0; k < maps.size(); k++)
			{
				List<IAtom> amap = maps.get(k);
				//This check currently is done only for one mobile group
				int mobCheck = checkMobileGroup(0, i, amap, mol); 
				if (mobCheck == -1)
					continue;
				
				RuleInstance rinst = new RuleInstance();
				if (mobCheck == TautomerConst.IHA_INDEX)
					rinst.FlagImplicitH = true;
				else
				{	
					if (mobileGroup.equals("H"))
					{	
						rinst.FlagImplicitH = false;
						rinst.explicitH = mol.getAtom(mobCheck);
					}
					else
					{
						rinst.mobileAtom = mol.getAtom(mobCheck);
					}
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
	
	int checkMobileGroup(int mobGroupNum, int curState, List<IAtom> amap, IAtomContainer mol) throws Exception
	{

		int pos = mobileGroupPos[mobGroupNum][curState];
		IAtom atom = amap.get(pos-1);  //position in the rule is 1-base indexed
		
		if (mobileGroup.equals("H"))
		{	
			//Check for implicit hydrogens
			if ((atom.getImplicitHydrogenCount()!=null) && atom.getImplicitHydrogenCount().intValue() > 0)
				return (TautomerConst.IHA_INDEX);

			//Check for explicit hydrogens
			List<IAtom> conAtoms = mol.getConnectedAtomsList(atom);
			for (int i = 0; i < conAtoms.size(); i++)
			{
				if (conAtoms.get(i).getSymbol().equals("H"))
					return(mol.getAtomNumber(conAtoms.get(i)));
			}
		}
		else
		{
			//Handling other types of mobile groups
			List<IAtom> conAtoms = mol.getConnectedAtomsList(atom);
			for (int i = 0; i < conAtoms.size(); i++)
			{
				if (conAtoms.get(i).getSymbol().equals(mobileGroup))
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
