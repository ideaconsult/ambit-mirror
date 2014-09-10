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
		
	}
	
	public static String reactionSetToString(ReactionSet rs)
	{
		StringBuffer sb = new StringBuffer();
		
		for (ReactionData rdata: rs.getReactions())
			sb.append(rdata.getName() + "  " + rdata.getSmirks() + "  " + rdata.getInfo());
		
		for (ReactionGroup group : rs.getReactionGroups())
			;
		
		return (sb.toString());
	}
	
	public static String reactionGroupToString(ReactionGroup rg)
	{
		StringBuffer sb = new StringBuffer();
		for (ReactionData rdata: rg.getReactions())
			sb.append(rdata.getName() + "  " + rdata.getSmirks() + "  " + rdata.getInfo());
		
		return (sb.toString());
	}
}
