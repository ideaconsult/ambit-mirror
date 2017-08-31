package ambit2.smarts.smirks;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsParser;

public class ReactionSearch 
{
	public boolean FlagCheckReactionMapping = false;
	public boolean FlagAgentMatch = false;
	
	SmartsParser parser = new SmartsParser();
	SMIRKSManager smirksMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	SmartsIsomorphismTester smartsIsoTester = new SmartsIsomorphismTester();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	List<String> errors = new ArrayList<String>();
	
	String querySmarts = null;
	IQueryAtomContainer query = null;
	String querySmirks = null;
	SMIRKSReaction queryReaction = null;
	
	
	public void reset()
	{
		errors.clear();
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public String getAllErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
	public void setQuerySmarts(String querySmarts) {
		this.querySmarts = querySmarts;
		query = parser.parse(querySmarts);
		parser.setNeededDataFlags();
		String errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals("")) {
			errors.add(errorMsg);
			query = null;
			return;
		}

		if (parser.hasRecursiveSmarts) {
			//getRecursiveAtoms();			
		}
	}
	
	
	public void setQuerySmirks(String querySmirks) {
		this.querySmirks = querySmirks;
		
		queryReaction = smirksMan.parse(querySmirks);
		String parse_error = smirksMan.getErrors();
		if (!parse_error.equals(""))
			errors.add("Query Smirks parsing errors: " + parse_error);
	}
	
	
	public boolean matchReaction(String targetSmirks)
	{
		SMIRKSReaction targetReaction = smirksMan.parse(targetSmirks);
		String parse_error = smirksMan.getErrors();
		if (!parse_error.equals(""))
		{
			errors.add("Target Smirks parsing errors: " + parse_error);
			return false;
		}	
		
		return matchReaction(targetReaction);
	}
	
	public boolean matchReaction(SMIRKSReaction targetReaction)
	{
		//Match reactants
		if (queryReaction.reactants.size() != targetReaction.reactants.size())
			return false;
		
		if (queryReaction.reactants.size() == 1)
		{
			smartsIsoTester.setQuery(queryReaction.reactants.get(0));
			boolean res = smartsIsoTester.hasIsomorphism(targetReaction.reactants.get(0));
			if (!res)
				return false;
		}
		else
		{
			//TODO
		}
		
			
		//Match products	
		if (queryReaction.products.size() != targetReaction.products.size())
			return false;
		
		if (queryReaction.products.size() == 1)
		{
			smartsIsoTester.setQuery(queryReaction.products.get(0));
			boolean res = smartsIsoTester.hasIsomorphism(targetReaction.products.get(0));
			if (!res)
				return false;
		}
		else
		{
			//TODO
		}
		
		//Match Agents
		if (FlagAgentMatch)
		{	
			//TODO
		}	
		
		//Check reaction mapping
		if (FlagCheckReactionMapping)
		{	
			//TODO
		}	
		
		return true;
	}
	
	public boolean matchReaction(List<IAtomContainer> targetReactants, List<IAtomContainer> targetProducts)
	{
		//TODO
		return false;
	}
	
	/*
	boolean matchComponent(IAtomContainer comp)
	{
		//TODO
		return false;
	}
	*/
	
	
}
