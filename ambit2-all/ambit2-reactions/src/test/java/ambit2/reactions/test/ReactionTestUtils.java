package ambit2.reactions.test;

import java.util.ArrayList;

import ambit2.reactions.io.ReactionReadUtils;
import ambit2.reactions.sets.ReactionData;
import ambit2.reactions.sets.ReactionGroup;
import ambit2.reactions.sets.ReactionSet;

public class ReactionTestUtils 
{
	public static void main(String[] args) throws Exception 
	{
		testReadReactionFromRuleFormat("D:/reaction-database.txt");
	}
	
	public static void testReadReactionFromRuleFormat(String fileName) throws Exception
	{
		ReactionReadUtils rru = new ReactionReadUtils();
		ArrayList<ReactionSet> reactionSets =  rru.loadReactionsFromRuleFormat(fileName);
		for (int i = 0; i < reactionSets.size(); i++)
			System.out.println(reactionSetToString(reactionSets.get(i)));
	}
	
	public static String reactionSetToString(ReactionSet rs)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Reaction set:  " + rs.getName() + "\n");
		for (ReactionData rdata: rs.getReactions())
			sb.append(rdata.getName() + "  " + rdata.getSmirks() + "  " + rdata.getInfo() + "\n");
		
		for (ReactionGroup group : rs.getReactionGroups())
			sb.append(reactionGroupToString(group) + "\n");
		
		return (sb.toString());
	}
	
	public static String reactionGroupToString(ReactionGroup rg)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("  Group: " + rg.getName() + "\n");
		for (ReactionData rdata: rg.getReactions())
			sb.append("  " + rdata.getName() + "  " + rdata.getSmirks() + "  " + rdata.getInfo() + "\n");
		
		return (sb.toString());
	}
}
