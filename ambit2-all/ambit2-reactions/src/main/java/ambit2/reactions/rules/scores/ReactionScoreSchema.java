package ambit2.reactions.rules.scores;

public class ReactionScoreSchema 
{
	public double basicScoreWeight = 0.0;
	public double classcScoreWeight = 0.0;
	public double transformScoreWeight = 0.0;
	public double conditionsScoreWeight = 0.0;
	public double experimentalConditionsScoreWeight = 0.0;
	public double yieldScoreWeight = 0.0;
	public double productComplexityWeight = 0.0; 
	public double productSimilarityWeight = 0.0;
	public double productStabilityWeight = 0.0;
	public double reactionCenterPositionWeight = 0.0;
	public double reactionCenterComplexityWeight = 0.0;
	public double electronWithdrawingLevelWeight = 0.0;
	
	public static ReactionScoreSchema getDefaultReactionScoreSchema()
	{
		ReactionScoreSchema rss = new ReactionScoreSchema();
		rss.basicScoreWeight = 0.30;
		rss.transformScoreWeight = 0.20;
		rss.experimentalConditionsScoreWeight = 0.20;
		rss.yieldScoreWeight = 0.20;
		rss.productComplexityWeight = 0.30;
		rss.reactionCenterComplexityWeight = 0.20;
		return rss;
	}
	
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("basicScoreWeight = " + basicScoreWeight + "\n");
		sb.append("transformScoreWeight = " + transformScoreWeight + "\n");
		sb.append("experimentalConditionsScoreWeight = " + experimentalConditionsScoreWeight + "\n");
		sb.append("yieldScoreWeight = " + yieldScoreWeight + "\n");
		sb.append("productComplexityWeight = " + productComplexityWeight + "\n");
		sb.append("reactionCenterComplexityWeight = " + reactionCenterComplexityWeight + "\n");
		
		return sb.toString();
	}
}
