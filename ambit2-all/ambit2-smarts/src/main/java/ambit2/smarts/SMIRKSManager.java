package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;


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
		QueryAtomContainer fragment;
		reaction.reactantsSmarts = smirks.substring(0, sep1Pos).trim();
		fragment = parseComponent(reaction.reactantsSmarts, "Reactants", reaction.reactantFlags,
				reaction.reactants, reaction.reactantCLG);
		if (fragment == null)
			res++;
		else
			reaction.reactant = fragment;
		
		
		reaction.agentsSmarts = smirks.substring(sep1Pos+1, sep2Pos).trim();
		if (!reaction.agentsSmarts.equals(""))
		{	
			fragment = parseComponent(reaction.agentsSmarts, "Agents", reaction.agentFlags,
					reaction.agents, reaction.agentsCLG);
			if (fragment == null)
				res++;
			else
				reaction.agent = fragment;
		}
		
		
		reaction.productsSmarts = smirks.substring(sep2Pos+1).trim();
		fragment = parseComponent(reaction.productsSmarts, "Products", reaction.productFlags,
				reaction.products, reaction.productsCLG);
		if (fragment == null)
			res++;
		else
			reaction.product = fragment;
		
		
		if (res > 0)
			return reaction;
		
		//Check the mapping
		reaction.checkMappings();
		if (reaction.mapErrors.size() > 0)
		{
			errors.addAll(reaction.mapErrors);
			return (reaction);
		}
		
		reaction.generateTransformationData();
		
		//Check the components
		//TODO
		
		
		
		
		return reaction;
	}
	
	
	public void applyTransformation(IAtomContainer mol, SMIRKSReaction reaction)
	{
		//Search all locations/instances  (modes of searching)
		
		//Apply transformation at particular location
	}
	
	
	public QueryAtomContainer parseComponent(String smarts, String compType, SmartsFlags flags,
			Vector<QueryAtomContainer> fragments, Vector<Integer> CLG)
	{
		QueryAtomContainer fragment = parser.parse(smarts);
		parser.setNeededDataFlags();
		String errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			errors.add("Invalid " + compType + " part in SMIRKS: " + smarts
					+ "   "+errorMsg);
			return (null);
		}
		
		flags.hasRecursiveSmarts = parser.hasRecursiveSmarts;
		flags.mNeedExplicitHData = parser.needExplicitHData();
		flags.mNeedNeighbourData = parser.needNeighbourData();
		flags.mNeedParentMoleculeData = parser.needParentMoleculeData();
		flags.mNeedRingData = parser.needRingData();
		flags.mNeedRingData2 = parser.needRingData2();
		flags.mNeedValenceData = parser.needValencyData();
		
		for (int i = 0; i < parser.fragments.size(); i++)
			fragments.add(parser.fragments.get(i));
		
		for (int i = 0; i < parser.fragmentComponents.size(); i++)
			CLG.add(parser.fragmentComponents.get(i));
		
		return (fragment);
	}
}
