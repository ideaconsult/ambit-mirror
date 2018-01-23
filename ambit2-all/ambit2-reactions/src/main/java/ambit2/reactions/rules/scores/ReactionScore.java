package ambit2.reactions.rules.scores;

public class ReactionScore 
{
	public double basicScore = 0.0;
	public double classcScore = 0.0;
	public double transformScore = 0.0;
	public double conditionsScore = 0.0;
	public double experimentalConditionsScore = 0.0;
	public double yieldScore = 0.0;
	public double productComplexity = 0.0; 
	public double productSimilarity = 0.0;
	public double productStability = 0.0;
	public double reactionCenterPosition = 0.0;
	public double reactionCenterComplexity = 0.0;
	public double electronWithdrawingLevel = 0.0;
	
	public double totalScore = 0.0;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("basicScore = " + basicScore + "\n");
		sb.append("exp.cond.Score = " + experimentalConditionsScore + "\n");
		sb.append("yieldScore = " + yieldScore + "\n");
		sb.append("productComplexity = " + productComplexity + "\n");
		sb.append("reactionCenterComplexity = " + reactionCenterComplexity + "\n");
		
		return sb.toString();
	}
}
