package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class SMIRKSManager 
{
	SmartsParser parser = new SmartsParser();
	
	Vector<String> errors = new Vector<String>();
	
	public SMIRKSManager()
	{
		parser.setComponentLevelGrouping(true);
		parser.mSupportSmirksSyntax = true;
	}
	
	public boolean hasErrors()
	{
		if (errors.isEmpty())
			return false;
		else
			return true;
	}
	
	public String getErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return(sb.toString());
	}
	
	public SMIRKSReaction parse(String smirks)
	{
		errors.clear();
		SMIRKSReaction reaction = new SMIRKSReaction();
				
		//Separate the components of the SMIRKS string
		int sep1Pos = smirks.indexOf(">");
		if (sep1Pos == -1)
		{
			errors.add("Invalid SMIRKS: missing separators '>'");
			return reaction;
		}
		
		
		int sep2Pos = smirks.indexOf(">", sep1Pos+1);
		if (sep2Pos == -1)
		{
			errors.add("Invalid SMIRKS: missing second separator '>'");
			return reaction;
		}		
		
		
		//Parse the components
		int res = 0;
		reaction.reactantsSmarts = smirks.substring(0, sep1Pos).trim();
		res += parseComponent(reaction.reactantsSmarts, "Reactants", reaction,
				reaction.reactants, reaction.reactantCLG);
		
		reaction.agentsSmarts = smirks.substring(sep1Pos+1, sep2Pos).trim();
		if (!reaction.agentsSmarts.equals(""))
			res += parseComponent(reaction.agentsSmarts, "Agents", reaction,
					reaction.agents, reaction.agentsCLG);
		
		reaction.productsSmarts = smirks.substring(sep2Pos+1).trim();
		res += parseComponent(reaction.productsSmarts, "Products", reaction,
				reaction.products, reaction.productsCLG);
		
		
		//Check the mapping
		reaction.checkMappings();
		if (reaction.mapErrors.size() > 0)
		{
			errors.addAll(reaction.mapErrors);
			return (reaction);
		}
		
		//Check the components
		//TODO
		
		
		return null;
	}
	
	public int parseComponent(String smarts, String compType, SMIRKSReaction reaction,
			Vector<QueryAtomContainer> fragments, Vector<Integer> CLG)
	{
		parser.parse(smarts);
		parser.setNeededDataFlags();
		String errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			errors.add("Invalid " + compType + " part in SMIRKS: " + smarts
					+ "   "+errorMsg);
			return (-1);
		}
		
		reaction.reactantFlags.hasRecursiveSmarts = parser.hasRecursiveSmarts;
		reaction.reactantFlags.mNeedExplicitHData = parser.needExplicitHData();
		reaction.reactantFlags.mNeedNeighbourData = parser.needNeighbourData();
		reaction.reactantFlags.mNeedParentMoleculeData = parser.needParentMoleculeData();
		reaction.reactantFlags.mNeedRingData = parser.needRingData();
		reaction.reactantFlags.mNeedRingData2 = parser.needRingData2();
		reaction.reactantFlags.mNeedValenceData = parser.needValencyData();
		
		for (int i = 0; i < parser.fragments.size(); i++)
			fragments.add(parser.fragments.get(i));
		
		for (int i = 0; i < parser.fragmentComponents.size(); i++)
			CLG.add(parser.fragmentComponents.get(i));
		
		return (0);
	}
}
