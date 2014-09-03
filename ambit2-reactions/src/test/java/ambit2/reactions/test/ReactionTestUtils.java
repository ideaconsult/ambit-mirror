package ambit2.reactions.test;

import ambit2.reactions.io.ReactionReadUtils;

public class ReactionTestUtils 
{
	public static void main(String[] args) throws Exception 
	{
		testReadReactionFromRuleFormat("D:/reaction-database.txt");
	}
	
	public static void testReadReactionFromRuleFormat(String fileName) throws Exception
	{
		ReactionReadUtils rru = new ReactionReadUtils();
		rru.loadReactionsFromRuleFormat(fileName);
	}
}
