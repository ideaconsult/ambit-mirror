package ambit2.reactions.syntheticaccessibility;

import ambit2.reactions.rules.scores.ReactionSequenceScoreSchema;

public class SyntheticAccessibilityStrategy 
{
	public static enum SynthAccessMethod {
		DESCRIPTORS, 
		START_MATERIALS,
		RETRO_SYNTHESIS,
		COMBINED_METHOD,
		UNKNOWN,
	}
	
	public SynthAccessMethod synthAccessMethod = SynthAccessMethod.RETRO_SYNTHESIS;
	public ReactionSequenceScoreSchema reactionSequenceScoreSchema = null;
	
	//weights used for COMBINED_METHOD
	public double combinedDescrWeight = 0.5;
	public double combinedStartMatWeight = 0.0;
	public double combinedRetroSynthWeight = 0.5;
}
