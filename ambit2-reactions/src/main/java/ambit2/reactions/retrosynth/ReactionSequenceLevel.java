package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ReactionSequenceLevel 
{
	public int levelIndex = 0;
	public ReactionSequenceLevel previousLevel = null;
	public ReactionSequenceLevel nextLevel = null;
	public List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 
	
	public List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
}
