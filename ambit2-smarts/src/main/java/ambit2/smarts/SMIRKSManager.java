package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.Bond;


public class SMIRKSManager 
{
	SmartsParser parser = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	SmartsToChemObject stco = new SmartsToChemObject();
	
	Vector<String> parserErrors = new Vector<String>();
	
	public int FlagSSMode = SmartsConst.SSM_NON_OVERLAPPING; 
	
	
	
	public SMIRKSManager()
	{
		parser.setComponentLevelGrouping(true);
		parser.mSupportSmirksSyntax = true;
	}
	
	public boolean hasErrors()
	{
		if (parserErrors.isEmpty())
			return false;
		else
			return true;
	}
	
	public String getErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parserErrors.size(); i++)
			sb.append(parserErrors.get(i) + "\n");
		return(sb.toString());
	}
	
	public SMIRKSReaction parse(String smirks)
	{
		parserErrors.clear();
		SMIRKSReaction reaction = new SMIRKSReaction();
				
		//Separate the components of the SMIRKS string
		int sep1Pos = smirks.indexOf(">");
		if (sep1Pos == -1)
		{
			parserErrors.add("Invalid SMIRKS: missing separators '>'");
			return reaction;
		}
		
		
		int sep2Pos = smirks.indexOf(">", sep1Pos+1);
		if (sep2Pos == -1)
		{
			parserErrors.add("Invalid SMIRKS: missing second separator '>'");
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
			parserErrors.addAll(reaction.mapErrors);
			return (reaction);
		}
		
		reaction.generateTransformationData();
		
		//Check the components
		//TODO
				
		
		return reaction;
	}
	
	
	
	
	
	public QueryAtomContainer parseComponent(String smarts, String compType, SmartsFlags flags,
			Vector<QueryAtomContainer> fragments, Vector<Integer> CLG)
	{
		QueryAtomContainer fragment = parser.parse(smarts);
		parser.setNeededDataFlags();
		String errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			parserErrors.add("Invalid " + compType + " part in SMIRKS: " + smarts
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
	
	public void applyTransformation(IAtomContainer target, SMIRKSReaction reaction)
	{
		isoTester.setQuery(reaction.reactant);
		
		if (FlagSSMode ==  SmartsConst.SSM_SINGLE)
		{
			return;
		}
		
		
		if (FlagSSMode ==  SmartsConst.SSM_NON_OVERLAPPING)
		{	
			Vector<Vector<IAtom>> rMaps = getNonOverlappingMappings(target);
			for (int i = 0; i < rMaps.size(); i++)
				applyTransformAtLocation(target, rMaps.get(i), reaction);
		}
		
	}
	
	public Vector<Vector<IAtom>> getNonOverlappingMappings(IAtomContainer target)
	{
		//Special treatment for fragmented reactants
		//TODO
		
		Vector<Vector<IAtom>> rMaps = isoTester.getNonOverlappingMappings(target);
		return(rMaps);
	}
	
	
	
	public void applyTransformAtLocation(IAtomContainer target, Vector<IAtom> rMap, SMIRKSReaction reaction)
	{
				
		//Create Non Existing Atoms
		Vector<IAtom> newAtoms = new Vector<IAtom>();
		for (int i = 0; i < reaction.productNotMappedAt.size(); i++)
		{
			
			int pAtNum = reaction.productNotMappedAt.get(i).intValue();
			IAtom a = reaction.product.getAtom(pAtNum);
			IAtom a0 = stco.toAtom(a);  //Also atom charge is set here
			newAtoms.add(a0);
			target.addAtom(a0);
		}
		
		//Atom Transformation		
		//Setting atom charges for 'SMIRKS' mapped atoms
		for (int i = 0; i < reaction.reactant.getAtomCount(); i++)
		{
			IAtom rAt = reaction.reactant.getAtom(i);
			Integer raMapInd = (Integer)rAt.getProperty("SmirksMapIndex");
			if (raMapInd != null)
			{
				int pAtNum = reaction.getMappedProductAtom(raMapInd);
				Integer charge = reaction.productAtCharge.get(pAtNum);
				IAtom tAt = rMap.get(i);
				tAt.setFormalCharge(charge);
			}	
		}
		
		
		
		//Bond Transformations
		for (int i = 0; i < reaction.reactBo.size(); i++)
		{
			int nrAt1 = reaction.reactAt1.get(i).intValue();
			int nrAt2 = reaction.reactAt2.get(i).intValue();
			
			if ((nrAt1 >= 0) && (nrAt2 >= 0))
			{	
				if (reaction.reactBo.get(i) == null)
				{
					//New bond must be created in the target.
					//This happens when two fragments from the reactant are connected.
					//TODO
				}
				else
				{
					IAtom tAt1 = rMap.get(nrAt1);
					IAtom tAt2 = rMap.get(nrAt2);
					IBond tBo = target.getBond(tAt1, tAt2);
					if (reaction.prodBo.get(i) == null)
						target.removeBond(tBo); //Target bond is deleted
					else
						tBo.setOrder(reaction.prodBo.get(i)); //Target bond is updated
				}
			}
			else
			{
				if ((nrAt1 == SmartsConst.SMRK_UNSPEC_ATOM) || (nrAt2 == SmartsConst.SMRK_UNSPEC_ATOM))
				{
					//This is the case when in the created bond in the target (product) 
					//contains at least one not mapped atom
					
					IAtom tAt1 = null;
					IAtom tAt2 = null;
					
					if (nrAt1 == SmartsConst.SMRK_UNSPEC_ATOM)
					{	
						int pAt1tNotMapIndex = -1;
						int npAt1 = reaction.prodAt1.get(i).intValue();
						for (int k = 0; k < reaction.productNotMappedAt.size(); k++)
							if (reaction.productNotMappedAt.get(k).intValue() == npAt1)
							{
								pAt1tNotMapIndex = k;
								break;
							}
						
						tAt1 = newAtoms.get(pAt1tNotMapIndex);
					}
					else 
					{
						//rAt1 is a mapped atom
						tAt1 = rMap.get(nrAt1);
					}
					

					if (nrAt2 == SmartsConst.SMRK_UNSPEC_ATOM)
					{
						int pAt2tNotMapIndex = -1;
						int npAt2 = reaction.prodAt2.get(i).intValue();
						for (int k = 0; k < reaction.productNotMappedAt.size(); k++)
							if (reaction.productNotMappedAt.get(k).intValue() == npAt2)
							{
								pAt2tNotMapIndex = k;
								break;
							}
						
						tAt2 = newAtoms.get(pAt2tNotMapIndex);
					}
					else
					{
						//rAt2 is a mapped atom
						tAt2 = rMap.get(nrAt2);
					}

					
					
					IBond tb = new Bond();
					tb.setAtoms(new IAtom[] {tAt1, tAt2});
					tb.setOrder(reaction.prodBo.get(i));
					target.addBond(tb);


				}
				
				
				//Some other possible cases if needed. 
				//TODO  
			}
			
			
		}
	}
}
