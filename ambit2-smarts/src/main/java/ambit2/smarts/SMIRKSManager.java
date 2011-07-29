package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.AtomConfigurator;


public class SMIRKSManager 
{
	SmartsParser parser = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	SmartsToChemObject stco = new SmartsToChemObject();
	EquivalenceTester eqTester = new EquivalenceTester();
	
	Vector<String> parserErrors = new Vector<String>();
	
	public int FlagSSMode = SmartsConst.SSM_NON_OVERLAPPING; 
	public boolean FlagFilterEquivalentMappings = false;
	
	
	
	public SMIRKSManager()
	{
		parser.setComponentLevelGrouping(true);
		parser.mSupportSmirksSyntax = true;
	}
	
	public void setSSMode(int mode)
	{
		FlagSSMode = mode;
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
	
	public boolean applyTransformation(IAtomContainer target,  SMIRKSReaction reaction) {
		return applyTransformation(target, null, reaction);
	}
	
	public boolean applyTransformation(IAtomContainer target, IAcceptable selection, SMIRKSReaction reaction)
	{
		isoTester.setQuery(reaction.reactant);
		SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
		
				
		if (FlagSSMode ==  SmartsConst.SSM_SINGLE)
		{
			return false;
		}
		
		
		if (FlagSSMode ==  SmartsConst.SSM_NON_IDENTICAL)
		{	
			Vector<Vector<IAtom>> rMaps = getNonIdenticalMappings(target);
			if (rMaps.size()==0) return false;
			
			if (FlagFilterEquivalentMappings)
			{	
				eqTester.setTarget(target);
				eqTester.quickFindEquivalentTerminalHAtoms();
				rMaps = eqTester.filterEquivalentMappings(rMaps);				
			}
			
			boolean applied = false;
			for (int i = 0; i < rMaps.size(); i++) {
				if ((selection==null) || ((selection!=null) && (selection.accept(rMaps.get(i))))) {
						applyTransformAtLocation(target, rMaps.get(i), reaction);
						applied = true;
				}
			}
			
			AtomConfigurator  cfg = new AtomConfigurator();
			try {
				cfg.process(target);
			} 
			catch (AmbitException e) { }
			
			return applied;
		}
		
		if (FlagSSMode ==  SmartsConst.SSM_NON_IDENTICAL_FIRST)
		{	
			Vector<Vector<IAtom>> rMaps = getNonIdenticalMappings(target);
			if (rMaps.size()==0) return false;
			
			//Map filtering here is not needed
			
			boolean applied = false;
			for (int i = 0; i < rMaps.size(); i++) {
				if ((selection==null) || ((selection!=null) && (selection.accept(rMaps.get(i))))) {
						applyTransformAtLocation(target, rMaps.get(i), reaction);
						applied = true;
						//The first acceptable is found and stopped
						
						AtomConfigurator  cfg = new AtomConfigurator();
						try {
							cfg.process(target);
						} 
						catch (AmbitException e) { }
						
						return applied;
				}
			}
			return applied;
		}
		
		
		if (FlagSSMode ==  SmartsConst.SSM_NON_OVERLAPPING)
		{	
			Vector<Vector<IAtom>> rMaps = getNonOverlappingMappings(target);
			if (rMaps.size()==0) return false;
			
			//Map filtering here is applied here (it should be not needed)
			
			boolean applied = false;
			for (int i = 0; i < rMaps.size(); i++) {
				if ((selection==null) || ((selection!=null) && (selection.accept(rMaps.get(i))))) {
						applyTransformAtLocation(target, rMaps.get(i), reaction);
						applied = true;
				}
			}
			
			AtomConfigurator  cfg = new AtomConfigurator();
			try {
				cfg.process(target);
			} 
			catch (AmbitException e) { }
			
			return applied;
		}
		
		return false;
	}
	
	/*
	 *  This transformation is applied in SSM_NON_IDENTICAL mode where
	 *  the overlapping mappings at particular site produce multiple copies of the molecule.
	 */
	public IAtomContainerSet applyTransformationWithCombinedOverlappedPos(IAtomContainer target, IAcceptable selection, SMIRKSReaction reaction)
	{
		isoTester.setQuery(reaction.reactant);
		SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
		
		
		Vector<Vector<IAtom>> rMaps0 = getNonIdenticalMappings(target);
		if (rMaps0.size()==0) 
			return null;
		
		Vector<Vector<IAtom>> rMaps;
		
		//Preliminary filtration by means of IAcceptable
		if (selection == null)
			rMaps = rMaps0;
		else
		{
			rMaps = new Vector<Vector<IAtom>>(); 
			for (int i = 0; i < rMaps0.size(); i++)
			{
				if (selection.accept(rMaps0.get(i)))
					rMaps.add(rMaps0.get(i));
			}
		}
		
		if (rMaps.size()==0) 
			return null;
		
		//Print mappings
		//for (int i = 0; i < rMaps.size(); i++)
		//	printSSMap(target, rMaps.get(i));
		
		
		if (FlagFilterEquivalentMappings)
		{	
			eqTester.setTarget(target);
			eqTester.quickFindEquivalentTerminalHAtoms();
			rMaps = eqTester.filterEquivalentMappings(rMaps);

			//System.out.println("FilteredMappings");
			//for (int i = 0; i < rMaps2.size(); i++)
			//	printSSMap(target, rMaps2.get(i));
		}
		
		
		IAtomContainerSet resSet = new AtomContainerSet();
		
		if (rMaps.size()==1)
		{	
			IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, rMaps, reaction);  
			resSet.addAtomContainer(product);
			return(resSet);
		}
		
		
		//Make mapping clusters/groups 
		Vector<Vector<Integer>> clusterIndexes = isoTester.getOverlappedMappingClusters(rMaps);
		
		//printMappingClusters(clusterIndexes, target);	
		
		
		//Generate all combinations:
		//Each combination is represented as a number where each digit is represents the choice from each cluster
		int comb[] = new int[clusterIndexes.size()];
		for (int i = 0; i < comb.length; i++)
			comb[i] = 0;
		
		int digit = 0;
		do 
		{
			//Prepare current combination
			Vector<Vector<IAtom>> combMaps = new Vector<Vector<IAtom>>();
			for (int i = 0; i < comb.length; i++)
			{	
				int index = clusterIndexes.get(i).get(comb[i]).intValue();				
				combMaps.add(rMaps.get(index));
			}	
			
			//Apply the transformation for the particular combination of locations with cloning
			IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, combMaps, reaction);  
			resSet.addAtomContainer(product);
			
			
			//Generation of next combination
			digit = 0;
			while (digit < comb.length)
			{
				comb[digit]++;
				if(comb[digit] == clusterIndexes.get(digit).size())
				{
					comb[digit] = 0;
					digit++;
				}
				else
					break;
			}
			
		}
		while (digit < comb.length);		
		
		
		return resSet;
	}
	
	
	public IAtomContainerSet applyTransformationWithSingleCopyForEachPos(IAtomContainer target, IAcceptable selection, SMIRKSReaction reaction)
	{
		isoTester.setQuery(reaction.reactant);
		SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
		
		
		Vector<Vector<IAtom>> rMaps0 = getNonIdenticalMappings(target);
		if (rMaps0.size()==0) 
			return null;
		
		Vector<Vector<IAtom>> rMaps;
		
		//Preliminary filtration by means of IAcceptable
		if (selection == null)
			rMaps = rMaps0;
		else
		{
			rMaps = new Vector<Vector<IAtom>>(); 
			for (int i = 0; i < rMaps0.size(); i++)
			{
				if (selection.accept(rMaps0.get(i)))
					rMaps.add(rMaps0.get(i));
			}
		}
		
		if (rMaps.size()==0) 
			return null;
		
		if (FlagFilterEquivalentMappings)
		{	
			eqTester.setTarget(target);
			eqTester.quickFindEquivalentTerminalHAtoms();
			rMaps = eqTester.filterEquivalentMappings(rMaps);
		}
		
		IAtomContainerSet resSet = new AtomContainerSet();
		
		for (int i = 0; i < rMaps.size(); i++)
		{	
			Vector<Vector<IAtom>> vMaps = new Vector<Vector<IAtom>>(); 
			vMaps.add(rMaps.get(i));
			IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, vMaps, reaction);  
			resSet.addAtomContainer(product);
		}
		
		return resSet;
	}
	
	
	public Vector<Vector<IAtom>> getNonOverlappingMappings(IAtomContainer target)
	{
		//Special treatment for fragmented reactants
		//TODO
		
		Vector<Vector<IAtom>> rMaps = isoTester.getNonOverlappingMappings(target);
		return(rMaps);
	}
	
	public Vector<Vector<IAtom>> getNonIdenticalMappings(IAtomContainer target)
	{
		//Special treatment for fragmented reactants 
		//TODO
		
		Vector<Vector<IAtom>> rMaps = isoTester.getNonIdenticalMappings(target);
		return(rMaps);
	}
	
	
	
	public void applyTransformAtLocation(IAtomContainer target, Vector<IAtom> rMap, SMIRKSReaction reaction)
	{	
		//printSSMap(target,rMap);
		
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
		//Setting atom charges for 'SMIRKS' mapped atoms and deleting unmapped atoms
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
			else
			{
				//Atom is deleted from 
				IAtom tAt = rMap.get(i);
				target.removeAtomAndConnectedElectronContainers(tAt);
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
					//This happens when two atoms from the reactant are connected.
					IAtom tAt1 = rMap.get(nrAt1);
					IAtom tAt2 = rMap.get(nrAt2);
					IBond tb = new Bond();
					tb.setAtoms(new IAtom[] {tAt1, tAt2});
					tb.setOrder(reaction.prodBo.get(i));
					target.addBond(tb);
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
	
		
	public IAtomContainer applyTransformationsAtLocationsWithCloning(IAtomContainer target, 
															Vector<Vector<IAtom>> rMaps, SMIRKSReaction reaction)
	{	
		//Create a target clone 
		IAtomContainer clone =  getCloneStructure(target);
		
		//Create mappings clones (according to the new atoms of the clone)
		Vector<Vector<IAtom>> cloneMaps = new Vector<Vector<IAtom>>(); 
		for (int i = 0; i < rMaps.size(); i++)
			cloneMaps.add(getCloneMapping(target, clone, rMaps.get(i)));
		
		//Apply transformation
		for (int i = 0; i < cloneMaps.size(); i++)	
			this.applyTransformAtLocation(clone, cloneMaps.get(i), reaction);
		
		AtomConfigurator  cfg = new AtomConfigurator();
		try {
			cfg.process(clone);
		} 
		catch (AmbitException e) { }
		
		return clone;
	}
	
	IAtomContainer getCloneStructure(IAtomContainer target)
	{
		IAtomContainer mol = new AtomContainer();
		
		IAtom newAtoms[] = new IAtom[target.getAtomCount()];
		IBond newBonds[] = new IBond[target.getBondCount()];
		
		//Clone atoms
		for (int i = 0; i < target.getAtomCount(); i++)
		{
			IAtom a = target.getAtom(i);
			IAtom a1 = cloneAtom(a);
			mol.addAtom(a1);
			newAtoms[i] = a1;
		}
		
		//Clone bonds
		for (int i = 0; i < target.getBondCount(); i++)
		{
			IBond b = target.getBond(i);
			IBond b1 = new Bond();
			IAtom a01[] = new IAtom[2];
			int ind0 = target.getAtomNumber(b.getAtom(0));
			int ind1 = target.getAtomNumber(b.getAtom(1));;
			a01[0] = mol.getAtom(ind0);
			a01[1] = mol.getAtom(ind1);
			b1.setAtoms(a01);
			b1.setOrder(b.getOrder());
			boolean FlagArom = b.getFlag(CDKConstants.ISAROMATIC);
			b1.setFlag(CDKConstants.ISAROMATIC, FlagArom);
			mol.addBond(b1);
			newBonds[i] = b1;
		}		
		
		return mol;
	}
	
	IAtom cloneAtom(IAtom a)
	{
		try
		{
			IAtom a1 = (IAtom)a.clone();
			return (a1);
		}	
		catch(Exception e)
		{	
		}
		
		return(null);
	}
	
	
	Vector<IAtom> getCloneMapping(IAtomContainer target, IAtomContainer clone, Vector<IAtom> map)
	{
		Vector<IAtom> cloneMap = new Vector<IAtom>();
		for (int i = 0; i < map.size(); i++)
		{
			IAtom at = map.get(i);
			int targetIndex = target.getAtomNumber(at);
			cloneMap.add(clone.getAtom(targetIndex));
		}
		
		return(cloneMap);
	}
	
	
	//Helper
	
	public void printSSMap(IAtomContainer target, Vector<IAtom> rMap)
	{	
		System.out.print("Map: ");
		for (int i = 0; i < rMap.size(); i++)
		{
			IAtom tAt = rMap.get(i);
			System.out.print(" "+target.getAtomNumber(tAt));
		}
		System.out.println();
	}	
	
	public void printMappingClusters(Vector<Vector<Integer>> clusterIndexes, IAtomContainer target)
	{
		for (int i = 0; i < clusterIndexes.size(); i++)
		{
			System.out.print("Cluster #" + i + " : ");
			Vector<Integer> v = clusterIndexes.get(i);
			for (int k = 0; k < v.size(); k++)
				System.out.print(v.get(k) + " ");
			System.out.println();
		}
	}
}
