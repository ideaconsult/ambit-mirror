package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ReactionSequenceLevel 
{
	ReactionSequenceLevel previousLevel = null;
	ReactionSequenceLevel nextLevel = null;
	
	List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 	
	
	List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
}
