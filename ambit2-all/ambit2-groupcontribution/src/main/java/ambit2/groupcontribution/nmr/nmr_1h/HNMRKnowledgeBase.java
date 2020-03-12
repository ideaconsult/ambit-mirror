package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.groupcontribution.nmr.Substituent;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.groups.GroupMatch;

public class HNMRKnowledgeBase 
{
	public List<HAtomEnvironment> hAtomEnvironments = new ArrayList<HAtomEnvironment>();
	public Map<String, GroupMatch> groupMatchRepository = new HashMap<String, GroupMatch>();
	public List<Rule2J> rules2j = new ArrayList<Rule2J>();
	public List<Rule3J> rules3j = new ArrayList<Rule3J>();
	
	
	SmartsParser parser = new SmartsParser(); 
	IsomorphismTester isoTester = new IsomorphismTester(); 
	public List<String> errors = new ArrayList<String>();
	
	public HNMRKnowledgeBase()
	{	
	}
	
	public void configure() throws EmptyMoleculeException
	{
		for (int i = 0; i < hAtomEnvironments.size(); i++)
		{
			HAtomEnvironment hae = hAtomEnvironments.get(i);
			configure(hae, i+1);
		}
	}
	
	protected void configure(HAtomEnvironment hae, int num) throws EmptyMoleculeException
	{
		GroupMatch haeGM = new GroupMatch(hae.smarts, parser, isoTester);
		if (!haeGM.getError().equals(""))
		{
			errors.add("HAtomEnvironment #" + num + " " + hae.name + 
					":	SMARTS: " + haeGM.getError());
		}
		else		
			hae.groupMatch = haeGM;					
		
		//Handling substituents
		for (int i = 0; i< hae.substituents.size(); i++)
		{	
			Substituent subst = hae.substituents.get(i);			
			GroupMatch gm = groupMatchRepository.get(subst.name);
			
			if (gm == null)
			{
				//Register new GroupMatch
				gm = new GroupMatch(subst.smarts, parser, isoTester);
				if (!gm.getError().equals(""))
				{
					errors.add("HAtomEnvironment #" + num + " " + hae.name +  
							": Substituent[" + (i+1) + "] " + subst.name + ": " + 
							gm.getError());
				}
				else
				{
					groupMatchRepository.put(subst.name, gm);					
				}
			}
			else
			{	
				//Check GroupMatch: compare SMAARTS from repository
				if (!subst.smarts.equals("--"))
					if (!subst.smarts.equals(gm.getSmarts()))
					{
						errors.add("HAtomEnvironment #" + num + " " + hae.name +   
								": Substituent[" + (i+1) + "] " + subst.name + ": " + 
								"Smarts " + subst.smarts + 
								" is different than the already registered in repository: " + gm.getSmarts());
					}
			}
			
			subst.groupMatch = gm;
		}
	}
	
	public String getAllErrorsAsString() 
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hAtomEnvironments.size(); i++)
			sb.append(hAtomEnvironments.get(i).toString() + "\n");
		return sb.toString();
	}
	
}
