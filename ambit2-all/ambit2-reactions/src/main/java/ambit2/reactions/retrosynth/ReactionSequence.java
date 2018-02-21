package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.reactions.GenericReaction;
import ambit2.reactions.GenericReactionInstance;
import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.retrosynth.ReactionSequence.MoleculeStatus;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.TopLayer;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

/**
 * 
 * @author nick
 *
 * This is sequence of reactions represented in tree data structure
 * and it represents a partucula solution of the retro-synthesis problem   
 */

public class ReactionSequence 
{	
	public static class InchiEntry {
		public List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
		public List<Integer> levels = new ArrayList<Integer>();
	}
	
	//Molecule properties
	public static final String MoleculeStatusProperty = "MOLECULE_STATUS";
	public static final String MoleculeInChIKeyProperty = "MOLECULE_INCHI_KEY";

	public static enum MoleculeStatus {
		ADDED_TO_LEVEL, UNRESOLVED, RESOLVED, STARTING_MATERIAL, STARTING_MATERIAL_RESOLVED, EQUIVALENT_TO_OTHER_MOLECULE;
		
		public static String getShortString(MoleculeStatus status)
		{
			if (status == null)
				return "--";
			switch (status)
			{
			case ADDED_TO_LEVEL:
				return "Added";
			case UNRESOLVED:
				return "UnRes";
			case RESOLVED:
				return "Res";	
			case STARTING_MATERIAL:
				return "StMat";
			case STARTING_MATERIAL_RESOLVED:
				return "StMatRes";
			case EQUIVALENT_TO_OTHER_MOLECULE:
				return "Eq";	
			}
			return null;
		}
	}
	
	public static enum SortingMethod {
		NONE, REACTION_SCORE, REACTION_PRIORITY
	}
	
	ReactionDataBase reactDB = null;
	StartingMaterialsDataBase startMatDB = new StartingMaterialsDataBase(); //empty materials DB
	SyntheticStrategy strategy = null;
	IAtomContainer target = null;
	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	List<ReactionSequenceLevel> levels = new ArrayList<ReactionSequenceLevel>(); 
	ReactionSequenceLevel firstLevel = null;
	Map<String,InchiEntry> usedInchies = new HashMap<String,InchiEntry>();
	List<INCHI_OPTION> inchiOptions = new ArrayList<INCHI_OPTION>();
	InChIGeneratorFactory inchiGeneratorFactory = null;
	IReactionSequenceHandler reactionSequenceHandler = null;
	
	//molecule pre-process
	boolean FlagExplicitHAtoms = true;
	
	public ReactionDataBase getReactDB() {
		return reactDB;
	}
	
	public void setReactDB(ReactionDataBase reactDB) {
		this.reactDB = reactDB;
	}
	
	public StartingMaterialsDataBase getStartMatDB() {
		return startMatDB;
	}

	public void setStartMatDB(StartingMaterialsDataBase startMatDB) {
		this.startMatDB = startMatDB;
	}
	
	public SyntheticStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SyntheticStrategy strategy) {
		this.strategy = strategy;
	}

	public IAtomContainer getTarget() {
		return target;
	}
	
	public void setTarget(IAtomContainer target) {
		this.target = target;
	}
	
	public List<ReactionSequenceLevel> getLevels() {
		return levels;
	}

	public void setLevels(List<ReactionSequenceLevel> levels) {
		this.levels = levels;
	}

	public ReactionSequenceLevel getFirstLevel() {
		return firstLevel;
	}
	
	public Map<String, InchiEntry> getUsedInchies() {
		return usedInchies;
	}

	public SMIRKSManager getSmrkMan() {
		return smrkMan;
	}

	public void setSmrkMan(SMIRKSManager smrkMan) {
		this.smrkMan = smrkMan;
	}
	
	public boolean isFlagExplicitHAtoms() {
		return FlagExplicitHAtoms;
	}

	public void setFlagExplicitHAtoms(boolean flagExplicitHAtoms) {
		FlagExplicitHAtoms = flagExplicitHAtoms;
	}
	
	public List<INCHI_OPTION> getInchiOptions() {
		return inchiOptions;
	}

	public void setInchiOptions(List<INCHI_OPTION> inchiOptions) {
		this.inchiOptions = inchiOptions;
	}
	
	public IReactionSequenceHandler getReactionSequenceHandler() {
		return reactionSequenceHandler;
	}

	public void setReactionSequenceHandler(IReactionSequenceHandler reactionSequenceHandler) {
		this.reactionSequenceHandler = reactionSequenceHandler;
	}

	public void initilize()
	{	
		//Target pre-processing
		if (FlagExplicitHAtoms)
			MoleculeTools.convertImplicitToExplicitHydrogens(target);
		try
		{
			defaultInchiSetup();
		}
		catch (Exception e)
		{
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getMessage());
		}
		
		//Adding first level
		ReactionSequenceLevel level = new ReactionSequenceLevel();
		firstLevel = level;
		String inchiKey = setMoleculeInchiKey(target);
		registerMolInchiKey(target, inchiKey, 0);
		level.levelIndex = 0;
		level.addMolecule(target, null, null);
		setMoleculeStatus(target, MoleculeStatus.ADDED_TO_LEVEL);
		//levels.add(level);
	}
	
	public ReactionSequenceLevel getLevel(int levIndex)
	{
		int index = 0;
		ReactionSequenceLevel level = firstLevel;
		while ((index < levIndex) && (level != null))
		{
			level = level.nextLevel;
			index++;
		}
		return level;
	}
	
	public void reset()
	{
		usedInchies.clear();
		ReactionSequenceLevel level = new ReactionSequenceLevel();
		firstLevel = level;
		level.levelIndex = 0;
		level.addMolecule(target, null, null);
		setMoleculeStatus(target, MoleculeStatus.ADDED_TO_LEVEL);
	}
	
	void defaultInchiSetup() throws Exception
	{
		inchiOptions.add(INCHI_OPTION.FixedH);
		inchiOptions.add(INCHI_OPTION.SAbs);
		inchiOptions.add(INCHI_OPTION.SAsXYZ);
		inchiOptions.add(INCHI_OPTION.SPXYZ);
		inchiOptions.add(INCHI_OPTION.FixSp3Bug);
		inchiOptions.add(INCHI_OPTION.AuxNone);
		inchiGeneratorFactory = InChIGeneratorFactory.getInstance();
	}
	
	void addEmptyLevels(int nLevels)
	{
		ReactionSequenceLevel level = levels.get(levels.size()-1);
		for (int i = 0; i < nLevels; i++)
		{
			ReactionSequenceLevel newLevel = new ReactionSequenceLevel();
			newLevel.levelIndex = level.levelIndex + 1;
			level.nextLevel = newLevel;
			newLevel.previousLevel = level;
			level = newLevel;
		}
	}
	
	public Map<GenericReaction,List<List<IAtom>>> generateAllReactionInstances(IAtomContainer mol)
	{
		return generateAllReactionInstances(mol, reactDB.genericReactions);
	}
	
	
	public Map<GenericReaction,List<List<IAtom>>> generateAllReactionInstances(IAtomContainer mol, List<GenericReaction> reactions)
	{
		//Pre-processing should not be needed
		//it is expected to be done via reaction products processing from previous steps
		
		Map<GenericReaction,List<List<IAtom>>> maps = new HashMap<GenericReaction,List<List<IAtom>>>();
		for (GenericReaction reaction: reactions)
		{	
			List<List<IAtom>> instances = reaction.findReactionInstances(mol, smrkMan);
			if (instances != null)
				if (!instances.isEmpty())
					maps.put(reaction, instances);
		}	
		return maps;
	}
	
	public List<GenericReactionInstance> handleReactionInstances(Map<GenericReaction,List<List<IAtom>>> instances, 
					IAtomContainer target) throws Exception
	{
		List<GenericReactionInstance> griList = new ArrayList<GenericReactionInstance>();
		Set<GenericReaction> grKeys = instances.keySet();
		for (GenericReaction gr : grKeys)
		{
			List<List<IAtom>> grInst = instances.get(gr);
			for (int i = 0; i < grInst.size(); i++)
			{	
				IAtomContainer products = gr.applyAtInstance(target, grInst.get(i), smrkMan, true);
				smrkMan.processProduct(products);
				//calculate reaction score
				TopLayer.setAtomTopLayers(products);
				GenericReactionInstance gri = new GenericReactionInstance(gr, target, grInst.get(i), products);
				gri.reactionScore = strategy.calcReactionScore(gri);
				griList.add(gri);
			}
			//TODO take into account reaction instance occurrences
		}
		return griList;
	}
	
	
	public static int[] sortReactionInstances(List<GenericReactionInstance> griList) 
	{
		return sortReactionInstances(griList, SortingMethod.REACTION_SCORE);
	}
	
	public static int[] sortReactionInstances(List<GenericReactionInstance> griList, SortingMethod sortMethod) 
	{
		int n = griList.size();
		int sort[] = new int[n];		
		for (int i=0; i<n; i++)
			sort[i] = i;
		
		if (n == 1)
			return sort; //no sorting is needed
		
		//Sorting in descending order (from max to min)
		int tmp;
		for (int i = n-1; i >= 1; i--)
			for (int k = 0; k < i; k++)
			{
				if (compare(griList.get(sort[k]), griList.get(sort[k+1]), sortMethod) < 0)
				{
					tmp = sort[k];
					sort[k] = sort[k+1];
					sort[k+1] = tmp;
				}
			}
		return sort;
	}
	
	/**
	 * 
	 * @param inst0
	 * @param inst1
	 * @param sortMethod
	 * @return negatine value if inst0 < inst1, 0 if inst0 = inst1, positive value if inst0 > inst1
	 */
	public static int compare(GenericReactionInstance inst0, GenericReactionInstance inst1, SortingMethod sortMethod)
	{
		switch (sortMethod)
		{
		case REACTION_SCORE:
			return Double.compare(inst0.reactionScore.totalScore, inst1.reactionScore.totalScore);
			
		case REACTION_PRIORITY:
			return Double.compare(inst0.reactionScore.priorityScore, inst1.reactionScore.priorityScore);
		}
		
		return 0;
	}
	
	public GenericReactionInstance getBestInstance(List<GenericReactionInstance> griList)
	{
		GenericReactionInstance bestGri = griList.get(0);
		for (int i = 1; i < griList.size(); i++)
		{
			GenericReactionInstance gri = griList.get(i);
			if (gri.reactionScore.totalScore > bestGri.reactionScore.totalScore)
				bestGri = gri;
		}
		return bestGri;
	}
	
	
	public void generateSequenceStepForReactionInstance(ReactionSequenceLevel level, int moleculeIndex,
									GenericReaction reaction, List<IAtom> reactionInstance) throws Exception
	{
		ReactionSequenceStep step = new ReactionSequenceStep();
		step.reaction = reaction;
		IAtomContainer mol = level.molecules.get(moleculeIndex);
		
		IAtomContainer products = reaction.applyAtInstance(mol, reactionInstance, smrkMan, true);
		smrkMan.processProduct(products);
		//calculate reaction score
		TopLayer.setAtomTopLayers(products);
		GenericReactionInstance gri = new GenericReactionInstance(reaction, mol, reactionInstance, products);
		step.reactionScore = strategy.calcReactionScore(gri);
		
		IAtomContainerSet productFrags = ConnectivityChecker.partitionIntoMolecules(products);
		step.outputMolecules = new ArrayList<IAtomContainer>();
		for (IAtomContainer frag : productFrags.atomContainers())
		{	
			step.outputMolecules.add(frag);
			String inchiKey = setMoleculeInchiKey(frag);
			registerMolInchiKey(frag, inchiKey, level.levelIndex+1);
			//Set new molecule status
			if (usedInchies.get(inchiKey).molecules.size()>1)
				setMoleculeStatus(frag, MoleculeStatus.EQUIVALENT_TO_OTHER_MOLECULE);
			else
			{	
				if (startMatDB.isStartingMaterial(inchiKey))
					setMoleculeStatus(frag, MoleculeStatus.STARTING_MATERIAL);
				else
					setMoleculeStatus(frag, MoleculeStatus.ADDED_TO_LEVEL);
			}	
		}	
		level.associateStep(moleculeIndex, step);
	}
	
	public void generateSequenceStepForReactionInstance(ReactionSequenceLevel level, int moleculeIndex,
			GenericReactionInstance gri) throws Exception
	{
		ReactionSequenceStep step = new ReactionSequenceStep();
		step.reaction = gri.reaction;
		//IAtomContainer mol = level.molecules.get(moleculeIndex);
		step.reactionScore = gri.reactionScore;
		
		IAtomContainerSet productFrags = ConnectivityChecker.partitionIntoMolecules(gri.products);
		step.outputMolecules = new ArrayList<IAtomContainer>();
		for (IAtomContainer frag : productFrags.atomContainers())
		{	
			step.outputMolecules.add(frag);
			String inchiKey = setMoleculeInchiKey(frag);
			registerMolInchiKey(frag, inchiKey, level.levelIndex+1);
			//Set new molecule status
			if (usedInchies.get(inchiKey).molecules.size()>1)
				setMoleculeStatus(frag, MoleculeStatus.EQUIVALENT_TO_OTHER_MOLECULE);
			else
			{	
				if (startMatDB.isStartingMaterial(inchiKey))
					setMoleculeStatus(frag, MoleculeStatus.STARTING_MATERIAL);
				else
					setMoleculeStatus(frag, MoleculeStatus.ADDED_TO_LEVEL);
			}	
		}	
		level.associateStep(moleculeIndex, step);
	}
	
	public void iterateLevelMolecules(ReactionSequenceLevel level) throws Exception
	{
		for (int i = 0; i<level.molecules.size(); i++)
		{
			IAtomContainer mol = level.molecules.get(i);
			
			MoleculeStatus status = getMoleculeStatus(mol);
			if (status == MoleculeStatus.ADDED_TO_LEVEL)
			{
				//check for starting material is performed on product generation
				
				Map<GenericReaction,List<List<IAtom>>> allInstances = generateAllReactionInstances(mol);
				if (allInstances.isEmpty())
				{	
					setMoleculeStatus(mol, MoleculeStatus.UNRESOLVED);
					continue;
				}
				
				List<GenericReactionInstance> griList = handleReactionInstances(allInstances, mol);
				GenericReactionInstance bestInst = getBestInstance(griList);				
				generateSequenceStepForReactionInstance(level, i, bestInst);
				setMoleculeStatus(mol, MoleculeStatus.RESOLVED);
			}
		}
	}
	
	
	
	public void iterateLevelMoleculesRandomly(ReactionSequenceLevel level) throws Exception
	{	
		for (int i = 0; i<level.molecules.size(); i++)
		{
			IAtomContainer mol = level.molecules.get(i);
			
			MoleculeStatus status = getMoleculeStatus(mol);
			if (status == MoleculeStatus.ADDED_TO_LEVEL)
			{
				//check for starting material is performed on product generation

				Map<GenericReaction,List<List<IAtom>>> allInstances = generateAllReactionInstances(mol);
				if (allInstances.isEmpty())
				{	
					setMoleculeStatus(mol, MoleculeStatus.UNRESOLVED);
					continue;
				}
				
				Object obj[] = SyntheticStrategy.getRandomSelection(allInstances);
				GenericReaction gr = (GenericReaction) obj[0];
				List<IAtom> inst = (List<IAtom>) obj[1];
				generateSequenceStepForReactionInstance(level, i, gr, inst);
				setMoleculeStatus(mol, MoleculeStatus.RESOLVED);
			}
		}
	}
	
	IAtomContainer getMaxProductFragment(IAtomContainer products)
	{
		int maxSize = -1;
		IAtomContainer maxFrag = null;
		IAtomContainerSet productFrags = ConnectivityChecker.partitionIntoMolecules(products);
		for (IAtomContainer frag : productFrags.atomContainers())
			if (maxSize < frag.getAtomCount())
			{
				maxFrag = frag;
				maxSize = frag.getAtomCount();
			}
		return maxFrag;
	}
	
	public static MoleculeStatus getMoleculeStatus(IAtomContainer mol)
	{
		return (MoleculeStatus) mol.getProperty(MoleculeStatusProperty);
	}
	
	public static void setMoleculeStatus(IAtomContainer mol, MoleculeStatus status)
	{
		mol.setProperty(MoleculeStatusProperty, status);
	}
	
	public String getInchiKey(IAtomContainer mol)
	{	
		try
		{	
			InChIGenerator ig = inchiGeneratorFactory.getInChIGenerator(mol, inchiOptions);
			INCHI_RET returnCode = ig.getReturnStatus();
			if (INCHI_RET.ERROR == returnCode) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,ig.getMessage());
			}
			return ig.getInchiKey();
		}
		catch (Exception e)
		{
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getMessage());
		}
		
		return null;
	}
	
	public String setMoleculeInchiKey(IAtomContainer mol)
	{
		String inchiKey = getInchiKey(mol);
		if (inchiKey != null)
			mol.setProperty(MoleculeInChIKeyProperty, inchiKey);
		return inchiKey;
	}
	
	void registerMolInchiKey(IAtomContainer mol, String inchiKey, int level)
	{
		InchiEntry entry = usedInchies.get(inchiKey);
		if (entry == null)
		{
			entry = new InchiEntry();
			usedInchies.put(inchiKey, entry);
		}
		entry.molecules.add(mol);
		entry.levels.add(level);
	}
	
	void unregisterMolInchiKey(IAtomContainer mol, String inchiKey)
	{
		InchiEntry entry = usedInchies.get(inchiKey);
		if (entry != null)
		{
			int index = entry.molecules.indexOf(mol);
			if (index >= 0)
			{
				entry.molecules.remove(index);
				entry.levels.remove(index);
				if (entry.molecules.isEmpty())
					usedInchies.remove(inchiKey); //no molecules associated to the inchi-key
			}
		}
	}
	
	void preProcess(IAtomContainer mol) throws Exception
	{	
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

		CDKHydrogenAdder adder = CDKHydrogenAdder
				.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		MoleculeTools.convertImplicitToExplicitHydrogens(mol);

		CDKHueckelAromaticityDetector.detectAromaticity(mol);
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		ReactionSequenceLevel level = firstLevel;
		while (level != null)
		{
			sb.append(level.toString() + "\n");
			level = level.nextLevel;
		}
		return sb.toString();
	}
}
