package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.reactions.GenericReaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.smarts.SMIRKSManager;

/**
 * 
 * @author nick
 *
 * This is sequence of reactions represented in tree data structure
 * and it represents a partucula solution of the retro-synthesis problem   
 */

public class ReactionSequence 
{
	ReactionDataBase reactDB = null;
	IAtomContainer target = null;
	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	List<ReactionSequenceLevel> levels = new ArrayList<ReactionSequenceLevel>(); 
	
	//List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 
	//TODO make functionality level based (not step based)
	
	
	/*
	 * Function is applied for the input molecule from the last step 
	 
	public Map<GenericReaction,List<List<IAtom>>> generateAllReactionInstances()
	{
		IAtomContainer mol = steps.get(steps.size()-1).getInputMolecule();
		Map<GenericReaction,List<List<IAtom>>> maps = new HashMap<GenericReaction,List<List<IAtom>>>();
		for (GenericReaction reaction: reactDB.genericReactions)
		{	
			List<List<IAtom>> instances = reaction.findReactionInstances(mol, smrkMan);
			if (instances != null)
				if (!instances.isEmpty())
					maps.put(reaction, instances);
		}	
		return maps;
	}
	*/
	
	/*
	 * Function is applied for the input molecule from the last step 
	 * new step is registered
	 
	public void generatedNextStepWithMaxProduct(GenericReaction reaction, List<IAtom> reactionInstance) throws Exception
	{
		ReactionSequenceStep lastStep = steps.get(steps.size()-1);
		IAtomContainer products = 
				reaction.applyAtInstance(lastStep.getInputMolecule(), reactionInstance, smrkMan, true);
		lastStep.setOutputMolecule(products);		
		IAtomContainer nextInputMol = getMaxProductFragment(products);
		ReactionSequenceStep nextStep = new ReactionSequenceStep();
		nextStep.setInputMolecule(nextInputMol);
		steps.add(nextStep);
	}
	
	*/
	
		
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

	void initilize()
	{	
		//Adding first level
		ReactionSequenceLevel level = new ReactionSequenceLevel();
		level.molecules.add(target);	
		level.levelIndex = 1;
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
}
