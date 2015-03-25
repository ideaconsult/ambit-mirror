package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

public class AtomCondition 
{
	//conditions defined by smarts queries
	public String smarts[] = null;
	public IQueryAtomContainer smartsQueries[] = null;
	public SmartsFlags smartsQueryFlags[] = null;
	public List<String> errors = null;
	
	public void makeSmartsQueries()
	{
		SmartsParser sp = new SmartsParser();
		makeSmartsQueries(sp);
	}
	
	public void makeSmartsQueries(SmartsParser sp)
	{
		if (smarts == null)
			return;
		
		handleLogicalPrefixes();
		
		int n = smarts.length;
		smartsQueries = new IQueryAtomContainer[n];
		smartsQueryFlags = new SmartsFlags[n];
		
		for (int i = 0; i < n; i++)
		{
			IQueryAtomContainer query = sp.parse(smarts[i]);			
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))	
			{
				String newError = "Incorrect smarts condition: " + smarts[i];
				addError(newError);
			}			
			else
			{
				smartsQueries[i] = query;
				SmartsFlags flags = new SmartsFlags();
				flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
				flags.mNeedExplicitHData = sp.needExplicitHData();
				flags.mNeedNeighbourData = sp.needNeighbourData();
				flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
				flags.mNeedRingData = sp.needRingData();
				flags.mNeedRingData2 = sp.needRingData2();
				flags.mNeedValenceData = sp.needValencyData();
				smartsQueryFlags[i] = flags;
			}
		}
	}
	
	
	public boolean checkConditionForAtom(IAtomContainer mol, int targetAtNum) throws Exception
	{	
		IsomorphismTester isoTester = null;
		if (smartsQueries != null)
			 isoTester = new IsomorphismTester();
		
		return checkConditionForAtom(mol, targetAtNum, isoTester);
	}
	
	public boolean checkConditionForAtom(IAtomContainer mol, int targetAtNum, IsomorphismTester isoTester) throws Exception
	{	
		//Check smarts queries	
		if (smartsQueries != null)
			for (int i = 0; i < smartsQueries.length; i++)
			{
				boolean res = checkSmartsConditionForAtom(i, mol, targetAtNum, isoTester);
				//System.out.println("*** checkSmartsCondition " + smarts[i] + "  for atom " + (targetAtNum + 1) + " --> " + res + "         " + SmartsHelper.moleculeToSMILES(mol, true) );
				if (!res)
					return false;
			}
		
		return true;
	}
	
	
	boolean checkSmartsConditionForAtom(int condNum, IAtomContainer mol, int targetAtNum, IsomorphismTester isoTester) throws Exception
	{	
		SmartsFlags flags = smartsQueryFlags[condNum];
		SmartsParser.prepareTargetForSMARTSSearch(
				flags.mNeedNeighbourData, 
				flags.mNeedValenceData, 
				flags.mNeedRingData, 
				flags.mNeedRingData2, 
				flags.mNeedExplicitHData , 
				flags.mNeedParentMoleculeData, mol);
		
		isoTester.setQuery(smartsQueries[condNum]);
		return isoTester.checkIsomorphismAtPosition(mol, targetAtNum);
	}
	
	void handleLogicalPrefixes()
	{
		//TODO
	}
	
	void addError(String err)
	{
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(err);
	}
}
