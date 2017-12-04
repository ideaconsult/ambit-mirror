package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.reactions.Reaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.smarts.SMIRKSManager;

/**
 * 
 * @author nick
 *
 * This is a helper for retro-synthesis driven by user interaction  
 */

public class ReactionSequence 
{
	ReactionDataBase reactDB = null;
	IAtomContainer target = null;
	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 
	
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
	public void applyReaction(Reaction reaction, List<IAtom> reactionInstance) throws Exception
	{
		ReactionSequenceStep lastStep = steps.get(steps.size()-1);
		IAtomContainer products = 
				reaction.applyAtInstance(lastStep.getInputMolecule(), reactionInstance, smrkMan, true);
		lastStep.setOutputMolecule(products);
		IAtomContainer nextInputMol = getProductMoleculeForNextStep(products);
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
	
	IAtomContainer getProductMoleculeForNextStep(IAtomContainer products)
	{
		//TODO
		return null;
	}
}
