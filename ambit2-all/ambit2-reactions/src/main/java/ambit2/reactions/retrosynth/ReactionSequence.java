package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

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
		ReactionSequenceStep step = new ReactionSequenceStep();
		step.inputMolecule = target;		
	}
}
