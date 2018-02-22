package ambit2.reactions.syntheticaccessibility;

import java.util.ArrayList;
import java.util.List;

import ambit2.reactions.rules.scores.ReactionSequenceScoreSchema;
import ambit2.rules.weight.DescriptorWeight;

public class SyntheticAccessibilityStrategy 
{
	
	public List<DescriptorWeight> descirptors = new ArrayList<DescriptorWeight>();
	public double startMatrialeSimilarityWeight = 0.0;
	public double retroSynthesisWeight = 0.0;
	public ReactionSequenceScoreSchema reactionSequenceScoreSchema = null;
	
}
