package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.AtomConfigurator;

public class SMIRKSManager {

    private static SMIRKSManager defaultSMIRKSManager = null;

    protected SmartsParser parser = new SmartsParser();
    protected IsomorphismTester isoTester = new IsomorphismTester();
    protected SmartsToChemObject stco;
    protected EquivalenceTester eqTester = new EquivalenceTester();

    protected List<String> parserErrors = new ArrayList<String>();

    protected int FlagSSMode = SmartsConst.SSM_NON_OVERLAPPING; // This flag is
							     // used by the
							     // function
							     // applyTransformation()

    public int getFlagSSMode() {
		return FlagSSMode;
	}

	public void setFlagSSMode(int flagSSMode) {
		FlagSSMode = flagSSMode;
	}

	public boolean isFlagFilterEquivalentMappings() {
		return FlagFilterEquivalentMappings;
	}

	public void setFlagFilterEquivalentMappings(boolean flagFilterEquivalentMappings) {
		FlagFilterEquivalentMappings = flagFilterEquivalentMappings;
	}

	public boolean isFlagProcessResultStructures() {
		return FlagProcessResultStructures;
	}
	/**
	 * Triggers an optional (pre)processing for further use of reaction result
	 * @param flagProcessResultStructures
	 */
	public void setFlagProcessResultStructures(boolean flagProcessResultStructures) {
		FlagProcessResultStructures = flagProcessResultStructures;
	}

	public boolean isFlagClearHybridizationBeforeResultProcess() {
		return FlagClearHybridizationBeforeResultProcess;
	}
	/**
	 *  Typically this flag should be true in order to correctly  detect the new atom types of the transformed molecules
	 * @param flagClearHybridizationBeforeResultProcess
	 */
	public void setFlagClearHybridizationBeforeResultProcess(boolean flagClearHybridizationBeforeResultProcess) {
		FlagClearHybridizationBeforeResultProcess = flagClearHybridizationBeforeResultProcess;
	}

	public boolean isFlagClearAromaticityBeforeResultProcess() {
		return FlagClearAromaticityBeforeResultProcess;
	}
	/**
	 *  Typically this flag should be true since some aromatic atoms may have lost their aromaticity
	 * @param flagClearAromaticityBeforeResultProcess
	 */
	public void setFlagClearAromaticityBeforeResultProcess(boolean flagClearAromaticityBeforeResultProcess) {
		FlagClearAromaticityBeforeResultProcess = flagClearAromaticityBeforeResultProcess;
	}

	public boolean isFlagClearImplicitHAtomsBeforeResultProcess() {
		return FlagClearImplicitHAtomsBeforeResultProcess;
	}
	/**
	 * Typically this flag should be true in order to correctly detect the new atom types of the transformed molecules
	 * @param flagClearImplicitHAtomsBeforeResultProcess
	 */
	public void setFlagClearImplicitHAtomsBeforeResultProcess(boolean flagClearImplicitHAtomsBeforeResultProcess) {
		FlagClearImplicitHAtomsBeforeResultProcess = flagClearImplicitHAtomsBeforeResultProcess;
	}

	public boolean isFlagAddImplicitHAtomsOnResultProcess() {
		return FlagAddImplicitHAtomsOnResultProcess;
	}

	public void setFlagAddImplicitHAtomsOnResultProcess(boolean flagAddImplicitHAtomsOnResultProcess) {
		FlagAddImplicitHAtomsOnResultProcess = flagAddImplicitHAtomsOnResultProcess;
	}
	
	public boolean isFlagConvertAddedImplicitHToExplicitOnResultProcess() {
		return FlagConvertAddedImplicitHToExplicitOnResultProcess;
	}
	/**
	 *Typically if this flag is  true it is expected that FlagAddImplicitHAtomsOnResultProcess = false
	 * @param flagConvertAddedImlicitHToExplicitOnResultProcess
	 */
	public void setFlagConvertAddedImplicitHToExplicitOnResultProcess(
			boolean flagConvertAddedImplicitHToExplicitOnResultProcess) {
		FlagConvertAddedImplicitHToExplicitOnResultProcess = flagConvertAddedImplicitHToExplicitOnResultProcess;
	}

	public boolean isFlagCheckAromaticityOnResultProcess() {
		return FlagCheckAromaticityOnResultProcess;
	}

	public void setFlagCheckAromaticityOnResultProcess(boolean flagCheckAromaticityOnResultProcess) {
		FlagCheckAromaticityOnResultProcess = flagCheckAromaticityOnResultProcess;
	}

	public boolean isFlagConvertExplicitHToImplicitOnResultProcess() {
		return FlagConvertExplicitHToImplicitOnResultProcess;
	}
	/**
	 * Typically if this flag is true it is expected that FlagAddImlicitHAtomsOnResultProcess=  false
	 * @param flagConvertExplicitHToImplicitOnResultProcess
	 */
	public void setFlagConvertExplicitHToImplicitOnResultProcess(boolean flagConvertExplicitHToImplicitOnResultProcess) {
		FlagConvertExplicitHToImplicitOnResultProcess = flagConvertExplicitHToImplicitOnResultProcess;
	}

	protected boolean FlagFilterEquivalentMappings = false;

    // public boolean FlagCheckAromaticityInPreprocessing = true;
    // public boolean FlagAddImplicitHAtomsInPreprocessing = true;

    // Result processing flags
	protected boolean FlagProcessResultStructures = false; 
	protected boolean FlagClearHybridizationBeforeResultProcess = true;
	protected boolean FlagClearAromaticityBeforeResultProcess = true;
	protected boolean FlagClearImplicitHAtomsBeforeResultProcess = true; 
	protected boolean FlagAddImplicitHAtomsOnResultProcess = false;
	protected boolean FlagConvertAddedImplicitHToExplicitOnResultProcess = false; 
	protected boolean FlagCheckAromaticityOnResultProcess = true;
	protected boolean FlagConvertExplicitHToImplicitOnResultProcess = false; 

    public SMIRKSManager(IChemObjectBuilder builder) {
	parser.setComponentLevelGrouping(true);
	parser.mSupportSmirksSyntax = true;
	stco = new SmartsToChemObject(builder);
    }

    public static SMIRKSManager getDefaultSMIRKSManager() {
	if (defaultSMIRKSManager == null)
	    defaultSMIRKSManager = new SMIRKSManager(SilentChemObjectBuilder.getInstance());

	return defaultSMIRKSManager;
    }

    public IsomorphismTester getIsomorphismTester() {
	return isoTester;
    }

    public SmartsParser getSmartsParser() {
	return parser;
    }

    public void setSSMode(int mode) {
	FlagSSMode = mode;
    }

    public boolean hasErrors() {
	if (parserErrors.isEmpty())
	    return false;
	else
	    return true;
    }

    public String getErrors() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < parserErrors.size(); i++)
	    sb.append(parserErrors.get(i) + "\n");
	return (sb.toString());
    }

    public SMIRKSReaction parse(String smirks) {
	parserErrors.clear();
	SMIRKSReaction reaction = new SMIRKSReaction(stco.getBuilder());

	// Separate the components of the SMIRKS string
	int sep1Pos = smirks.indexOf(">");
	if (sep1Pos == -1) {
	    parserErrors.add("Invalid SMIRKS: missing separators '>'");
	    return reaction;
	}

	int sep2Pos = smirks.indexOf(">", sep1Pos + 1);
	if (sep2Pos == -1) {
	    parserErrors.add("Invalid SMIRKS: missing second separator '>'");
	    return reaction;
	}

	// Parse the components
	int res = 0;
	IQueryAtomContainer fragment;
	reaction.reactantsSmarts = smirks.substring(0, sep1Pos).trim();
	fragment = parseComponent(reaction.reactantsSmarts, "Reactants", reaction.reactantFlags, reaction.reactants,
		reaction.reactantCLG);
	if (fragment == null)
	    res++;
	else {
	    reaction.reactant = fragment;
	    if (reaction.reactantFlags.hasRecursiveSmarts)
		reaction.reactantRecursiveAtoms = getRecursiveAtoms(fragment);
	}

	reaction.agentsSmarts = smirks.substring(sep1Pos + 1, sep2Pos).trim();
	if (!reaction.agentsSmarts.equals("")) {
	    fragment = parseComponent(reaction.agentsSmarts, "Agents", reaction.agentFlags, reaction.agents,
		    reaction.agentsCLG);
	    if (fragment == null)
		res++;
	    else
		reaction.agent = fragment;
	}

	reaction.productsSmarts = smirks.substring(sep2Pos + 1).trim();
	fragment = parseComponent(reaction.productsSmarts, "Products", reaction.productFlags, reaction.products,
		reaction.productsCLG);
	if (fragment == null)
	    res++;
	else {
	    reaction.product = fragment;
	    if (reaction.productFlags.hasRecursiveSmarts)
		reaction.productRecursiveAtoms = getRecursiveAtoms(fragment);
	}

	// System.out.println("Reactant Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.reactant));
	// System.out.println("Product Atoms: "+SmartsHelper.getAtomsExpressionTokens(reaction.product));

	if (res > 0)
	    return reaction;

	reaction.checkMappings();

	// Check for mapping errors
	if (reaction.mapErrors.size() > 0) {
	    parserErrors.addAll(reaction.mapErrors);
	    return (reaction);
	}

	reaction.generateTransformationData();

	// Check for errors produced by the generation of transformation data
	if (reaction.mapErrors.size() > 0) {
	    parserErrors.addAll(reaction.mapErrors);
	    return (reaction);
	}

	// Check the components
	// TODO

	return reaction;
    }

    public IQueryAtomContainer parseComponent(String smarts, String compType, SmartsFlags flags,
	    List<IQueryAtomContainer> fragments, List<Integer> CLG) {
	IQueryAtomContainer fragment = parser.parse(smarts);
	parser.setNeededDataFlags();
	String errorMsg = parser.getErrorMessages();
	if (!errorMsg.equals("")) {
	    parserErrors.add("Invalid " + compType + " part in SMIRKS: " + smarts + "   " + errorMsg);
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
    
    /**
     * 
     * @param reaction - this must be a valid SMIRKSReaction object created by parsing a valid SMIRKS expression
     * @param targetReactants - the target reactants represented as a single atom container
     * @param targetProducts - the target products represented as a single atom container
     * @param tagetAgents - the target agents may null (not specified)- in this case the agents are not matched if
     * @return true if reaction is matched against the targets
     * 
     * All targets (reactants, products, agents) may be fragmented e.g. to represent several molecules/fragments
     * 
     */
    public boolean matchReaction(SMIRKSReaction reaction, 
    						IAtomContainer targetReactants, 
    						IAtomContainer targetProducts, 
    						IAtomContainer targetAgents)
    {
    	if (targetReactants == null)
    		return false;
    	if (targetProducts == null)
    		return false;
    	
    	//Check for multiple fragments within reactants, products or agents
    	if (reaction.reactants.size() > 1 || reaction.products.size() > 1 || 
    			(reaction.agents.size() > 1 && targetAgents != null) )
    	{
    		IAtomContainerSet reactFrags = ConnectivityChecker.partitionIntoMolecules(targetReactants);
    		IAtomContainerSet prodFrags = ConnectivityChecker.partitionIntoMolecules(targetProducts);
    		IAtomContainerSet agentFrags = null;
    		if (targetAgents != null)
    			agentFrags = ConnectivityChecker.partitionIntoMolecules(targetAgents);
    		
    		//Calling this function to check the component level grouping
    		return matchReaction(reaction, reactFrags, prodFrags, agentFrags);
    	}
    	
    	//Match reactants
    	SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, targetReactants);
    	if (reaction.reactantFlags.hasRecursiveSmarts)
    	    mapRecursiveAtomsAgainstTarget(reaction.reactantRecursiveAtoms, targetReactants);
    	isoTester.setQuery(reaction.reactant);    	
    	
    	boolean res = isoTester.hasIsomorphism(targetReactants);
    	if (!res)
    		return false;
    	
    	//Match products
    	SmartsParser.prepareTargetForSMARTSSearch(reaction.productFlags, targetProducts);
    	if (reaction.productFlags.hasRecursiveSmarts)
    	    mapRecursiveAtomsAgainstTarget(reaction.productRecursiveAtoms, targetProducts);
    	isoTester.setQuery(reaction.product);    	
    	
    	res = isoTester.hasIsomorphism(targetProducts);
    	if (!res)
    		return false;
    	
    	//Match agents
    	if (targetAgents != null)
    	{
    		//TODO
    	}
    	
    	
    	return true;
    }
    
    /**
     * 
     * @param reaction
     * @param targetReactants
     * @param targetProducts
     * @param tagetAgents
     * @return true if reaction is matched against the targets
     * 
     * This function check component level grouping if specified within the SMIRKS definition
     */
    public boolean matchReaction(SMIRKSReaction reaction, 
    					IAtomContainerSet targetReactants, 
    					IAtomContainerSet targetProducts, 
    					IAtomContainerSet tagetAgents)
    {
    	//TODO
    	return false;
    }
    

    public boolean applyTransformation(IAtomContainer target, SMIRKSReaction reaction) throws Exception {
	return applyTransformation(target, null, reaction);
    }

    public boolean applyTransformation(IAtomContainer target, IAcceptable selection, SMIRKSReaction reaction)
	    throws Exception {
	SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
	if (reaction.reactantFlags.hasRecursiveSmarts)
	    mapRecursiveAtomsAgainstTarget(reaction.reactantRecursiveAtoms, target);

	// It is absolutely needed that setQuery() function is called after
	// recursive atom mapping
	// because the recursive mapping calls setQuery() as well
	isoTester.setQuery(reaction.reactant);

	if (FlagSSMode == SmartsConst.SSM_SINGLE) {
	    // TODO - currently nothing is done
	    return false;
	}

	if (FlagSSMode == SmartsConst.SSM_NON_IDENTICAL) {
	    List<List<IAtom>> rMaps = getNonIdenticalMappings(target);
	    if (rMaps.size() == 0)
		return false;

	    if (FlagFilterEquivalentMappings) {
		eqTester.setTarget(target);
		eqTester.quickFindEquivalentTerminalHAtoms();
		rMaps = eqTester.filterEquivalentMappings(rMaps);
	    }

	    boolean applied = false;
	    for (int i = 0; i < rMaps.size(); i++) {
		if ((selection == null) || ((selection != null) && (selection.accept(rMaps.get(i))))) {
		    applyTransformAtLocation(target, rMaps.get(i), reaction);
		    applied = true;
		}
	    }

	    AtomConfigurator cfg = new AtomConfigurator();
	    try {
		cfg.process(target);
	    } catch (AmbitException e) {
		throw e;
	    }

	    if (FlagProcessResultStructures)
		processProduct(target);

	    return applied;
	}

	if (FlagSSMode == SmartsConst.SSM_NON_IDENTICAL_FIRST) {
	    List<List<IAtom>> rMaps = getNonIdenticalMappings(target);
	    if (rMaps.size() == 0)
		return false;

	    // Map filtering here is not needed

	    boolean applied = false;
	    for (int i = 0; i < rMaps.size(); i++) {
		if ((selection == null) || ((selection != null) && (selection.accept(rMaps.get(i))))) {
		    applyTransformAtLocation(target, rMaps.get(i), reaction);
		    applied = true;
		    // The first acceptable is found and stopped

		    AtomConfigurator cfg = new AtomConfigurator();
		    try {
			cfg.process(target);
		    } catch (AmbitException e) {
			throw e;
		    }

		    if (FlagProcessResultStructures)
			processProduct(target);

		    return applied;
		}
	    }
	    return applied;
	}

	if (FlagSSMode == SmartsConst.SSM_NON_OVERLAPPING) {
	    List<List<IAtom>> rMaps = getNonOverlappingMappings(target);
	    if (rMaps.size() == 0)
		return false;

	    // Map filtering here is applied here (it should be not needed)

	    boolean applied = false;
	    for (int i = 0; i < rMaps.size(); i++) {
		if ((selection == null) || ((selection != null) && (selection.accept(rMaps.get(i))))) {
		    applyTransformAtLocation(target, rMaps.get(i), reaction);
		    applied = true;
		}
	    }

	    AtomConfigurator cfg = new AtomConfigurator();
	    cfg.process(target);

	    if (FlagProcessResultStructures)
		processProduct(target);

	    return applied;
	}

	return false;
    }

    /*
     * This transformation is applied in SSM_NON_IDENTICAL mode where the
     * overlapping mappings at particular site produce multiple copies of the
     * molecule.
     */
    public IAtomContainerSet applyTransformationWithCombinedOverlappedPos(IAtomContainer target, IAcceptable selection,
	    SMIRKSReaction reaction) throws Exception {
	SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
	if (reaction.reactantFlags.hasRecursiveSmarts)
	    mapRecursiveAtomsAgainstTarget(reaction.reactantRecursiveAtoms, target);

	isoTester.setQuery(reaction.reactant);

	List<List<IAtom>> rMaps0 = getNonIdenticalMappings(target);
	if (rMaps0.size() == 0)
	    return null;

	List<List<IAtom>> rMaps;

	// Preliminary filtration by means of IAcceptable
	if (selection == null)
	    rMaps = rMaps0;
	else {
	    rMaps = new ArrayList<List<IAtom>>();
	    for (int i = 0; i < rMaps0.size(); i++) {
		if (selection.accept(rMaps0.get(i)))
		    rMaps.add(rMaps0.get(i));
	    }
	}

	if (rMaps.size() == 0)
	    return null;

	// Print mappings
	// for (int i = 0; i < rMaps.size(); i++)
	// printSSMap(target, rMaps.get(i));

	if (FlagFilterEquivalentMappings) {
	    eqTester.setTarget(target);
	    eqTester.quickFindEquivalentTerminalHAtoms();
	    rMaps = eqTester.filterEquivalentMappings(rMaps);

	    // System.out.println("FilteredMappings");
	    // for (int i = 0; i < rMaps2.size(); i++)
	    // printSSMap(target, rMaps2.get(i));
	}

	IAtomContainerSet resSet = new AtomContainerSet();

	if (rMaps.size() == 1) {
	    IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, rMaps, reaction);
	    if (FlagProcessResultStructures)
		processProduct(product);
	    resSet.addAtomContainer(product);
	    return (resSet);
	}

	// Make mapping clusters/groups
	List<List<Integer>> clusterIndexes = isoTester.getOverlappedMappingClusters(rMaps);

	// printMappingClusters(clusterIndexes, target);

	// Generate all combinations:
	// Each combination is represented as a number where each digit is
	// represents the choice from each cluster
	int comb[] = new int[clusterIndexes.size()];
	for (int i = 0; i < comb.length; i++)
	    comb[i] = 0;

	int digit = 0;
	do {
	    // Prepare current combination
	    List<List<IAtom>> combMaps = new ArrayList<List<IAtom>>();
	    for (int i = 0; i < comb.length; i++) {
		int index = clusterIndexes.get(i).get(comb[i]).intValue();
		combMaps.add(rMaps.get(index));
	    }

	    // Apply the transformation for the particular combination of
	    // locations with cloning
	    IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, combMaps, reaction);
	    if (FlagProcessResultStructures)
		processProduct(product);
	    resSet.addAtomContainer(product);

	    // Generation of next combination
	    digit = 0;
	    while (digit < comb.length) {
		comb[digit]++;
		if (comb[digit] == clusterIndexes.get(digit).size()) {
		    comb[digit] = 0;
		    digit++;
		} else
		    break;
	    }

	} while (digit < comb.length);

	return resSet;
    }

    /*
     * This transformation is applied by default in SSM_NON_IDENTICAL
     */
    public IAtomContainerSet applyTransformationWithSingleCopyForEachPos(IAtomContainer target, IAcceptable selection,
	    SMIRKSReaction reaction) throws Exception {
	return applyTransformationWithSingleCopyForEachPos(target, selection, reaction, SmartsConst.SSM_NON_IDENTICAL);
    }

    public IAtomContainerSet applyTransformationWithSingleCopyForEachPos(IAtomContainer target, IAcceptable selection,
	    SMIRKSReaction reaction, int SSMode) throws Exception {
	SmartsParser.prepareTargetForSMARTSSearch(reaction.reactantFlags, target);
	if (reaction.reactantFlags.hasRecursiveSmarts)
	    mapRecursiveAtomsAgainstTarget(reaction.reactantRecursiveAtoms, target);

	isoTester.setQuery(reaction.reactant);

	List<List<IAtom>> rMaps0;

	switch (SSMode) {
	case SmartsConst.SSM_NON_OVERLAPPING:
	    rMaps0 = getNonOverlappingMappings(target);
	    break;

	case SmartsConst.SSM_NON_IDENTICAL:
	    rMaps0 = getNonIdenticalMappings(target);
	    break;

	case SmartsConst.SSM_ALL:
	    rMaps0 = getAllMappings(target);
	    break;

	default:
	    rMaps0 = getNonIdenticalMappings(target);
	}

	if (rMaps0.size() == 0)
	    return null;

	List<List<IAtom>> rMaps;

	// Preliminary filtration by means of IAcceptable
	if (selection == null)
	    rMaps = rMaps0;
	else {
	    rMaps = new ArrayList<List<IAtom>>();
	    for (int i = 0; i < rMaps0.size(); i++) {
		if (selection.accept(rMaps0.get(i)))
		    rMaps.add(rMaps0.get(i));
	    }
	}

	if (rMaps.size() == 0)
	    return null;

	if (FlagFilterEquivalentMappings) {
	    eqTester.setTarget(target);
	    eqTester.quickFindEquivalentTerminalHAtoms();
	    rMaps = eqTester.filterEquivalentMappings(rMaps);
	}

	IAtomContainerSet resSet = new AtomContainerSet();

	for (int i = 0; i < rMaps.size(); i++) {
	    List<List<IAtom>> vMaps = new ArrayList<List<IAtom>>();
	    vMaps.add(rMaps.get(i));
	    IAtomContainer product = applyTransformationsAtLocationsWithCloning(target, vMaps, reaction);
	    if (FlagProcessResultStructures)
		processProduct(product);
	    resSet.addAtomContainer(product);
	}

	return resSet;
    }

    public List<List<IAtom>> getNonOverlappingMappings(IAtomContainer target) {
	// Special treatment for fragmented reactants
	// TODO

	List<List<IAtom>> rMaps = isoTester.getNonOverlappingMappings(target);
	return (rMaps);
    }

    public List<List<IAtom>> getNonIdenticalMappings(IAtomContainer target) {
	// Special treatment for fragmented reactants
	// TODO

	List<List<IAtom>> rMaps = isoTester.getNonIdenticalMappings(target);
	return (rMaps);
    }

    public List<List<IAtom>> getAllMappings(IAtomContainer target) {
	// Special treatment for fragmented reactants
	// TODO

	List<List<IAtom>> rMaps = isoTester.getAllIsomorphismMappings(target);
	return (rMaps);
    }

    public void applyTransformAtLocation(IAtomContainer target, List<IAtom> rMap, SMIRKSReaction reaction) {
	// printSSMap(target,rMap);

	// Create Non Existing Atoms
	List<IAtom> newAtoms = new ArrayList<IAtom>();
	for (int i = 0; i < reaction.productNotMappedAt.size(); i++) {
	    int pAtNum = reaction.productNotMappedAt.get(i).intValue();
	    IAtom a = reaction.product.getAtom(pAtNum);
	    IAtom a0 = stco.toAtom(a); // Also atom charge is set here
	    // a0.setImplicitHydrogenCount(new Integer(0)); //This is added as a
	    // quick patch for some CKD methods that does not check for null
	    // pointer
	    newAtoms.add(a0);
	    target.addAtom(a0);
	}

	// Atom Transformation
	// Setting atom charges for 'SMIRKS' mapped atoms and deleting unmapped
	// atoms together with the associated bonds
	for (int i = 0; i < reaction.reactant.getAtomCount(); i++) {
	    IAtom rAt = reaction.reactant.getAtom(i);
	    Integer raMapInd = (Integer) rAt.getProperty("SmirksMapIndex");
	    if (raMapInd != null) {
		int pAtNum = reaction.getMappedProductAtom(raMapInd);
		Integer charge = reaction.productAtCharge.get(pAtNum);
		IAtom tAt = rMap.get(i);
		
		//Handle the cases when no charge transformation is to be done 
		if (charge == null)
		{
			Integer reactantCharge = reaction.reactantAtCharge.get(i);
			if (reactantCharge == null)
				continue; //Both reactant and product charges are not specified. Therefore nothing is done
			if (reactantCharge == 0)
				continue; //Both reactant is not specified product charge is 0 (counted as non specified ) then nothing is done
		}
		else
		{
			if (charge == 0)
			{
				Integer reactantCharge = reaction.reactantAtCharge.get(i);
				if (reactantCharge == null)
					continue; //Reactant charge is 0 and product is null. Therefore nothing is done
				if (reactantCharge == 0)
					continue; //Both reactant and products charges are 0 (counted as non specified ) then nothing is done
			}
		}
		
		
		tAt.setFormalCharge(charge);
	    } else {
		// Atom is deleted from
		IAtom tAt = rMap.get(i);
		target.removeAtomAndConnectedElectronContainers(tAt);
	    }
	}

	// Bond Transformations
	for (int i = 0; i < reaction.reactBo.size(); i++) {
	    int nrAt1 = reaction.reactAt1.get(i).intValue();
	    int nrAt2 = reaction.reactAt2.get(i).intValue();

	    if ((nrAt1 >= 0) && (nrAt2 >= 0)) {
		if (reaction.reactBo.get(i) == null) {
		    // New bond must be created in the target.
		    // This happens when two atoms from the reactant are not
		    // connected.
		    IAtom tAt1 = rMap.get(nrAt1);
		    IAtom tAt2 = rMap.get(nrAt2);
		    IBond tb = MoleculeTools.newBond(target.getBuilder());

		    tb.setAtoms(new IAtom[] { tAt1, tAt2 });
		    tb.setOrder(reaction.prodBo.get(i));
		    target.addBond(tb);
		} else {
		    IAtom tAt1 = rMap.get(nrAt1);
		    IAtom tAt2 = rMap.get(nrAt2);
		    IBond tBo = target.getBond(tAt1, tAt2);
		    if (reaction.prodBo.get(i) == null)
			target.removeBond(tBo); // Target bond is deleted
		    else {
			tBo.setOrder(reaction.prodBo.get(i)); // Target bond is
							      // updated
		    }
		}
	    } else {
		if ((nrAt1 == SmartsConst.SMRK_UNSPEC_ATOM) || (nrAt2 == SmartsConst.SMRK_UNSPEC_ATOM)) {
		    // This is the case when the created bond in the target
		    // (product)
		    // contains at least one not mapped atom

		    IAtom tAt1 = null;
		    IAtom tAt2 = null;

		    if (nrAt1 == SmartsConst.SMRK_UNSPEC_ATOM) {
			int pAt1tNotMapIndex = -1;
			int npAt1 = reaction.prodAt1.get(i).intValue();
			for (int k = 0; k < reaction.productNotMappedAt.size(); k++)
			    if (reaction.productNotMappedAt.get(k).intValue() == npAt1) {
				pAt1tNotMapIndex = k;
				break;
			    }

			tAt1 = newAtoms.get(pAt1tNotMapIndex);
		    } else {
			// rAt1 is a mapped atom
			tAt1 = rMap.get(nrAt1);
		    }

		    if (nrAt2 == SmartsConst.SMRK_UNSPEC_ATOM) {
			int pAt2tNotMapIndex = -1;
			int npAt2 = reaction.prodAt2.get(i).intValue();
			for (int k = 0; k < reaction.productNotMappedAt.size(); k++)
			    if (reaction.productNotMappedAt.get(k).intValue() == npAt2) {
				pAt2tNotMapIndex = k;
				break;
			    }

			tAt2 = newAtoms.get(pAt2tNotMapIndex);
		    } else {
			// rAt2 is a mapped atom
			tAt2 = rMap.get(nrAt2);
		    }

		    IBond tb = MoleculeTools.newBond(target.getBuilder());
		    tb.setAtoms(new IAtom[] { tAt1, tAt2 });
		    tb.setOrder(reaction.prodBo.get(i));
		    target.addBond(tb);
		}

		// Some other possible cases if needed.
	    }

	}

    }

    public IAtomContainer applyTransformationsAtLocationsWithCloning(IAtomContainer target, List<List<IAtom>> rMaps,
	    SMIRKSReaction reaction) throws Exception {
	// Create a target clone
	IAtomContainer clone = getCloneStructure(target);

	// Create mappings clones (according to the new atoms of the clone)
	List<List<IAtom>> cloneMaps = new ArrayList<List<IAtom>>();
	for (int i = 0; i < rMaps.size(); i++)
	    cloneMaps.add(getCloneMapping(target, clone, rMaps.get(i)));

	// Apply transformation
	for (int i = 0; i < cloneMaps.size(); i++)
	    this.applyTransformAtLocation(clone, cloneMaps.get(i), reaction);

	AtomConfigurator cfg = new AtomConfigurator();
	cfg.process(clone);

	return clone;
    }

    IAtomContainer getCloneStructure(IAtomContainer target) throws Exception {
	IAtomContainer mol = new AtomContainer();

	IAtom newAtoms[] = new IAtom[target.getAtomCount()];
	IBond newBonds[] = new IBond[target.getBondCount()];

	// Clone atoms
	for (int i = 0; i < target.getAtomCount(); i++) {
	    IAtom a = target.getAtom(i);
	    IAtom a1 = cloneAtom(a);
	    mol.addAtom(a1);
	    newAtoms[i] = a1;
	}

	// Clone bonds
	for (int i = 0; i < target.getBondCount(); i++) {
	    IBond b = target.getBond(i);
	    IBond b1 = MoleculeTools.newBond(target.getBuilder());
	    IAtom a01[] = new IAtom[2];
	    int ind0 = target.getAtomNumber(b.getAtom(0));
	    int ind1 = target.getAtomNumber(b.getAtom(1));
	    ;
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

    IAtom cloneAtom(IAtom a) throws Exception {
	IAtom a1 = (IAtom) a.clone();
	return a1;
    }

    List<IAtom> getCloneMapping(IAtomContainer target, IAtomContainer clone, List<IAtom> map) {
	List<IAtom> cloneMap = new ArrayList<IAtom>();
	for (int i = 0; i < map.size(); i++) {
	    IAtom at = map.get(i);
	    int targetIndex = target.getAtomNumber(at);
	    cloneMap.add(clone.getAtom(targetIndex));
	}

	return (cloneMap);
    }

    public List<SmartsAtomExpression> getRecursiveAtoms(IQueryAtomContainer query) {
	List<SmartsAtomExpression> recursiveAtoms = new ArrayList<SmartsAtomExpression>();
	for (int i = 0; i < query.getAtomCount(); i++) {
	    if (query.getAtom(i) instanceof SmartsAtomExpression) {
		SmartsAtomExpression sa = (SmartsAtomExpression) query.getAtom(i);
		if (sa.recSmartsStrings.size() > 0) {
		    recursiveAtoms.add(sa);
		    // System.out.println(SmartsHelper.atomToString(sa));

		}
	    }
	}

	return recursiveAtoms;
    }

    public void mapRecursiveAtomsAgainstTarget(List<SmartsAtomExpression> recursiveAtoms, IAtomContainer target) {
	// Reset for new mapping
	for (int i = 0; i < recursiveAtoms.size(); i++)
	    recursiveAtoms.get(i).recSmartsMatches = new ArrayList<List<IAtom>>();

	// The mapping info is stored "inside" each recursive atom
	List<IQueryAtomContainer> vRecCon;
	for (int i = 0; i < recursiveAtoms.size(); i++) {
	    vRecCon = recursiveAtoms.get(i).recSmartsContainers;
	    for (int j = 0; j < vRecCon.size(); j++) {
		isoTester.setQuery(vRecCon.get(j));
		List<Integer> pos = isoTester.getIsomorphismPositions(target);
		List<IAtom> v = new ArrayList<IAtom>();

		for (int k = 0; k < pos.size(); k++)
		    v.add(target.getAtom(pos.get(k).intValue()));

		recursiveAtoms.get(i).recSmartsMatches.add(v);
	    }
	}
    }

    // Helper functions

    public void printSSMap(IAtomContainer target, List<IAtom> rMap) {
	System.out.print("Map: ");
	for (int i = 0; i < rMap.size(); i++) {
	    IAtom tAt = rMap.get(i);
	    System.out.print(" " + target.getAtomNumber(tAt));
	}
	System.out.println();
    }

    public void printMappingClusters(List<List<Integer>> clusterIndexes, IAtomContainer target) {
	for (int i = 0; i < clusterIndexes.size(); i++) {
	    System.out.print("Cluster #" + i + " : ");
	    List<Integer> v = clusterIndexes.get(i);
	    for (int k = 0; k < v.size(); k++)
		System.out.print(v.get(k) + " ");
	    System.out.println();
	}
    }

    protected void processProduct(IAtomContainer mol) throws Exception {

	if (FlagClearHybridizationBeforeResultProcess)
	    for (IAtom atom : mol.atoms())
		atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);

	if (FlagClearAromaticityBeforeResultProcess) {
	    for (IAtom atom : mol.atoms())
		if (atom.getFlag(CDKConstants.ISAROMATIC))
		    atom.setFlag(CDKConstants.ISAROMATIC, false);
	    for (IBond bond : mol.bonds())
		if (bond.getFlag(CDKConstants.ISAROMATIC))
		    bond.setFlag(CDKConstants.ISAROMATIC, false);
	}

	if (FlagClearImplicitHAtomsBeforeResultProcess)
	    for (IAtom atom : mol.atoms())
		atom.setImplicitHydrogenCount(null);

	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

	if (isFlagAddImplicitHAtomsOnResultProcess()) {
	    CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    adder.addImplicitHydrogens(mol);

	    if (FlagConvertAddedImplicitHToExplicitOnResultProcess)
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
	}

	if (FlagCheckAromaticityOnResultProcess)
	    CDKHueckelAromaticityDetector.detectAromaticity(mol);

	if (FlagConvertExplicitHToImplicitOnResultProcess)
	    SmartsHelper.convertExcplicitHAtomsToImplicit(mol);
    }

}
