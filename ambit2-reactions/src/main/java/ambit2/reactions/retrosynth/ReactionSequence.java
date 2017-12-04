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
	
	public Map<Reaction,List<List<IAtom>>> generateAllReactionInstances()
	{
		IAtomContainer mol = steps.get(steps.size()-1).inputMolecule;
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
	
	public void applyReaction(Reaction reaction, List<IAtom> reactionMap)
	{
		//TODO
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
}
