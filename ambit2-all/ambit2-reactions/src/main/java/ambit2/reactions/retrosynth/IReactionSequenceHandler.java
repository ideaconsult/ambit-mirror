package ambit2.reactions.retrosynth;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IReactionSequenceHandler 
{
	public ReactionSequence getReactionSequence();
	
	public void setReactionSequence(ReactionSequence sequence);
	
	public void addMoleculeToLevelEvent(IAtomContainer mol, int levelIndex);
	
}
