package ambit2.reactions.rules.scores;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ReactionScore 
{
	public double basicScore = 0.0;
	public double classcScore = 0.0;
	public double transformScore = 0.0;
	public double conditionsScore = 0.0;
	public double experimentalConditionsScore = 0.0;
	public double yieldScore = 0.0;
	public double productComplexityScore = 0.0; 
	public double productSimilarityScore = 0.0;
	public double productStabilityScore = 0.0;
	public double reactionCenterPositionScore = 0.0;
	public double reactionCenterComplexityScore = 0.0;
	public double electronWithdrawingLevelScore = 0.0;
	public double priorityScore = 0.0;
	
	public double totalScore = 0.0;
	
	public String toString()
	{
		NumberFormat f = new DecimalFormat("#0.00"); 
		StringBuffer sb = new StringBuffer();
		sb.append("basicScore = " + basicScore + "\n");
		sb.append("exp.cond.Score = " + experimentalConditionsScore + "\n");
		sb.append("yieldScore = " + yieldScore + "\n");
		sb.append("prodComplScore = " + f.format(productComplexityScore) + "\n");
		sb.append("reactionCenterComplScore = " + f.format(reactionCenterComplexityScore) + "\n");
		sb.append("priorityScore = " + f.format(priorityScore) + "\n");
		
		return sb.toString();
	}
	
	public String toStringLine()
	{
		NumberFormat f = new DecimalFormat("#0.00");
		StringBuffer sb = new StringBuffer();
		sb.append("basicScore = " + basicScore);
		sb.append("  exp.cond.Score = " + experimentalConditionsScore);
		sb.append("  yieldScore = " + yieldScore);
		sb.append("  productComplScore = " + f.format(productComplexityScore));
		sb.append("  reactionCenterComplScore = " + f.format(reactionCenterComplexityScore));
		sb.append("  priorityScore = " + f.format(priorityScore));
		return sb.toString();
	}
}
