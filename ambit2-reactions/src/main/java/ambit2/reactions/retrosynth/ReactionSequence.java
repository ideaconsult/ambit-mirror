package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ambit2.reactions.ReactionDataBase;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsHelper;
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
		ADDED_TO_LEVEL, UNRESOLVED, RESOLVED, STARTING_MATERIAL, EQUIVALENT_TO_OTHER_MOLECULE
	}
	
	ReactionDataBase reactDB = null;
	IAtomContainer target = null;
	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	List<ReactionSequenceLevel> levels = new ArrayList<ReactionSequenceLevel>(); 
	ReactionSequenceLevel firstLevel = null;
	Map<String,InchiEntry> usedInchies = new HashMap<String,InchiEntry>();
	List<INCHI_OPTION> inchiOptions = new ArrayList<INCHI_OPTION>();
	InChIGeneratorFactory inchiGeneratorFactory = null;
	
	//molecule pre-process
	boolean FlagExplicitHAtoms = true;
	
	public ReactionDataBase getReactDB() {
		return reactDB;
	}
	
	public void setReactDB(ReactionDataBase reactDB) {
		this.reactDB = reactDB;
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
		setMoleculeInchiKey(target, 1);
		level.levelIndex = 1;
		level.addMolecule(target, null, null);
		//levels.add(level);
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
		//if (FlagExplicitHAtoms)
		//	MoleculeTools.convertImplicitToExplicitHydrogens(mol);
		
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
	
	
	public void generatedSequenceStepForReactionInstance(ReactionSequenceLevel level, int moleculeIndex,
									GenericReaction reaction, List<IAtom> reactionInstance) throws Exception
	{
		ReactionSequenceStep step = new ReactionSequenceStep();
		IAtomContainer mol = level.molecules.get(moleculeIndex);
		
		IAtomContainer products = reaction.applyAtInstance(mol, reactionInstance, smrkMan, true);
		smrkMan.processProduct(products);
		IAtomContainerSet productFrags = ConnectivityChecker.partitionIntoMolecules(products);
		step.outputMolecules = new ArrayList<IAtomContainer>();
		for (IAtomContainer frag : productFrags.atomContainers())
		{	
			step.outputMolecules.add(frag);
			setMoleculeInchiKey(frag, level.levelIndex+1);
		}	
		level.associateStep( moleculeIndex, step);
	}
	
	public void iterateLevelMolecules(ReactionSequenceLevel level, SyntheticStrategy strategy) throws Exception
	{
		for (int i = 0; i<level.molecules.size(); i++)
		{
			IAtomContainer mol = level.molecules.get(i);
			
			MoleculeStatus status = getMoleculeStatus(mol);
			if (status == MoleculeStatus.ADDED_TO_LEVEL)
			{
				 Map<GenericReaction,List<List<IAtom>>> allInstances = generateAllReactionInstances(mol);
				 if (allInstances.isEmpty())
					 continue;
				 Object obj[] = SyntheticStrategy.getRandomSelection(allInstances);
				 GenericReaction gr = (GenericReaction) obj[0];
				 List<IAtom> inst = (List<IAtom>) obj[1];
				 generatedSequenceStepForReactionInstance(level, i, gr, inst);
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
	
	public void setMoleculeInchiKey(IAtomContainer mol, int level)
	{
		String inchiKey = getInchiKey(mol);
		if (inchiKey != null)
		{
			mol.setProperty(MoleculeInChIKeyProperty, inchiKey);
			registerInchiKey(mol, inchiKey, level);
		}
	}
	
	void registerInchiKey(IAtomContainer mol, String inchiKey, int level)
	{
		//TODO
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
