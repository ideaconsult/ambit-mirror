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

import ambit2.reactions.Reaction;
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
	List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 
	List<ReactionSequenceLevel> levels = new ArrayList<ReactionSequenceLevel>(); 
	//TODO make functionality level based (not step based)
	
	
	/*
	 * Function is applied for the input molecule from the last step 
	 */
	public Map<Reaction,List<List<IAtom>>> generateAllReactionInstances()
	{
		IAtomContainer mol = steps.get(steps.size()-1).getInputMolecule();
		Map<Reaction,List<List<IAtom>>> maps = new HashMap<Reaction,List<List<IAtom>>>();
		for (Reaction reaction: reactDB.reactions)
		{	
			List<List<IAtom>> instances = reaction.findReactionInstances(mol, smrkMan);
			if (instances != null)
				if (!instances.isEmpty())
					maps.put(reaction, instances);
		}	
		return maps;
	}
	
	/*
	 * Function is applied for the input molecule from the last step 
	 * new step is registered
	 */
	public void generatedNextStepWithMaxProduct(Reaction reaction, List<IAtom> reactionInstance) throws Exception
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
	public List<ReactionSequenceStep> getSteps() {
		return steps;
	}
	public void setSteps(List<ReactionSequenceStep> steps) {
		this.steps = steps;
	}
	
	void initilize()
	{
		steps.clear();
		//Adding first step
		ReactionSequenceStep step = new ReactionSequenceStep();
		step.inputMolecule = target;	
		steps.add(step);
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
