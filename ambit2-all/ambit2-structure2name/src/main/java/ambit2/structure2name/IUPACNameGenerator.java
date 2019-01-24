package ambit2.structure2name;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.structure2name.components.IIUPACComponent;
import ambit2.structure2name.rules.IUPACRuleDataBase;

public class IUPACNameGenerator 
{	
	protected IUPACRuleDataBase ruleDataBase = null;
	
	protected IAtomContainer originalMolecule = null;
	protected IAtomContainer molecule = null;
	
	protected List<IIUPACComponent> initialComponents = new ArrayList<IIUPACComponent>();
	protected List<IIUPACComponent> components = new ArrayList<IIUPACComponent>();
	
	public IUPACNameGenerator() throws Exception
	{
		ruleDataBase = IUPACRuleDataBase.getDefaultRuleDataBase();
	}
	
	public IUPACRuleDataBase getRuleDataBase() {
		return ruleDataBase;
	}
	
	public String generateIUPACName(IAtomContainer mol) throws Exception
	{
		initialComponents.clear();
		components.clear();
		originalMolecule = mol;
		molecule = mol;
				
		generateComponents(molecule);
		
		
		IIUPACComponent mainComp = getBestRankComponent();
		
		String iupac = mainComp.getMainToken();
		
		return iupac;
	}
	
	protected void generateComponents(IAtomContainer mol)
	{
		//findCyclicAndAcyclicComponets();
		//processAcyclicComponets();
		//makeComponentLogicalRelations();
	}

	protected void findCyclicAndAcyclicComponets()
	{
		//TODO
	}
	
	protected void processAcyclicComponets()
	{
		//TODO
	}
	
	protected void makeComponentLogicalRelations()
	{
		//TODO
	}
	
	protected IIUPACComponent getBestRankComponent()
	{
		if (components.isEmpty())
			return null;
		
		double maxRank = components.get(0).getRank();
		int maxIndex = 0;
		
		for (int i = 1; i < components.size(); i++) 
		{
			if (components.get(i).getRank() > maxRank)
			{
				maxRank = components.get(i).getRank();
				maxIndex = i;
			}
		}
		
		return components.get(maxIndex);
	}
	
}
