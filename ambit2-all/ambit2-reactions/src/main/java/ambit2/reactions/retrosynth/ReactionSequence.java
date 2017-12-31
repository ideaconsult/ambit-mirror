package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.reactions.GenericReaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsHelper;

/**
 * 
 * @author nick
 *
 * This is sequence of reactions represented in tree data structure
 * and it represents a partucula solution of the retro-synthesis problem   
 */

public class ReactionSequence 
{
	//Molecule properties
	public static final String MoleculeStatusProperty = "MOLECULE_STATUS";
	public static final String MoleculeInChIProperty = "MOLECULE_INCHI";

	public static enum MoleculeStatus {
		ADDED_TO_LEVEL, UNRESOLVED, RESOLVED, STARTING_MATERIAL
	}
	
	ReactionDataBase reactDB = null;
	IAtomContainer target = null;
	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	List<ReactionSequenceLevel> levels = new ArrayList<ReactionSequenceLevel>(); 
	ReactionSequenceLevel firstLevel = null;
	
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

	public void initilize()
	{	
		//Adding first level
		ReactionSequenceLevel level = new ReactionSequenceLevel();
		firstLevel = level;
		level.levelIndex = 1;
		level.addMolecule(target, null);
		//levels.add(level);
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
		if (FlagExplicitHAtoms)
			MoleculeTools.convertImplicitToExplicitHydrogens(mol);
		
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
