package ambit2.reactions.syntheticaccessibility;

import java.util.List;

import ambit2.reactions.ReactionConst.SynthAccessMethod;
import ambit2.reactions.retrosynth.ReactionSequence;
import ambit2.reactions.rules.scores.ReactionSequenceScoreSchema;

public class SyntheticAccessibilityManager 
{
	protected SynthAccessMethod synthAccessMethod = SynthAccessMethod.RETRO_SYNTHESIS;
	protected ReactionSequenceScoreSchema reactionSequenceScoreSchema = null;

	public SynthAccessMethod getSynthAccessMethod() {
		return synthAccessMethod;
	}

	public void setSynthAccessMethod(SynthAccessMethod synthAccessMethod) {
		this.synthAccessMethod = synthAccessMethod;
	}

	public ReactionSequenceScoreSchema getReactionSequenceScoreSchema() {
		return reactionSequenceScoreSchema;
	}

	public void setReactionSequenceScoreSchema(ReactionSequenceScoreSchema reactionSequenceScoreSchema) {
		this.reactionSequenceScoreSchema = reactionSequenceScoreSchema;
	}
	
	public double calcSyntheticAccessibilityScore(ReactionSequence reactSeq)
	{
		//TODO
		return 0.0;
	}
	
	public double calcSyntheticAccessibilityScore(List<ReactionSequence> reactSeqList)
	{
		//TODO
		return 0.0;
	}
	
	
}
