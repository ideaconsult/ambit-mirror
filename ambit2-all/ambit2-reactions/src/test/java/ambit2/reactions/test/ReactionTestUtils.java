package ambit2.reactions.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.Reaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.io.ReactionReadUtils;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.reactions.sets.ReactionData;
import ambit2.reactions.sets.ReactionGroup;
import ambit2.reactions.sets.ReactionSet;
import ambit2.smarts.SmartsHelper;

public class ReactionTestUtils 
{
	public static boolean FlagPrintReactionDB = false;
	public static boolean FlagPrintReactionStrategy = false;
	
	public static void main(String[] args) throws Exception 
	{
		//testReadReactionFromRuleFormat("D:/reaction-database.txt");
		
		//testTreeSet();
		//testListSorting();
		
		FlagPrintReactionDB = true; 
		FlagPrintReactionStrategy = true;
		
		//testReactor("ClCCCN", "D:/Projects/Nina/Reactions/reactions.json");
		
		//testReactor("C1CCC1CCC", "D:/Projects/Nina/Reactions/reactions.json");
		
		testReactor("CC", "D:/Projects/Nina/Reactions/metabolism-reactions.json"); //!!!!!!!!!
		
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
	
	public static void testTreeSet()
	{
		TreeSet<String> ts = new TreeSet<String>();
		ts.add("b");
		ts.add("c");
		ts.add("a");
		ts.add("x");
		ts.add("e");
		ts.add("a");
		ts.add("b");
		ts.add("y");
		ts.add("1");
		
		String s[] = ts.toArray(new String[0]);
		
		for (int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	
	public static void testListSorting()
	{
		List<String> ts = new ArrayList<String>();
		ts.add("b");
		ts.add("c");
		ts.add("a");
		ts.add("x");
		ts.add("e");
		ts.add("a");
		ts.add("b");
		ts.add("y");
		ts.add("1");
		
		Collections.sort(ts);
		
		for (int i = 0; i < ts.size(); i++)
			System.out.println(ts.get(i));
	}
	
			
	public static void testReactor(String smiles, String reactionDBFile) throws Exception
	{
		System.out.println("Setting reactor and reaction database...");
		Reactor reactor = new Reactor();
		ReactionDataBase reactDB = new ReactionDataBase(new File(reactionDBFile));
		
		System.out.println("Configuring reaction database...");
		reactDB.configureReactions(reactor.getSMIRKSManager());
		reactor.setReactionDataBase(reactDB);
		
		System.out.println("Configuring reactor strategy ...");
		ReactorStrategy strategy = new ReactorStrategy(new File(reactionDBFile));  //strategy is in the same file
		
		strategy.FlagStoreFailedNodes = true;
		strategy.FlagStoreSuccessNodes = true;
		strategy.maxNumOfSuccessNodes = 0;  //if 0 then the reactor will stop after the first success node
		
		strategy.FlagCheckNodeDuplicationOnPush = true;
		strategy.FlagTraceReactionPath = true;
		strategy.FlagLogMainReactionFlow = true;
		strategy.FlagLogReactionPath = true;
		strategy.FlagLogNameInReactionPath = false;
		strategy.FlagLogExplicitHToImplicit = true;
		
		
		reactor.setStrategy(strategy);
		
		//Setup Smirks manager
		reactor.getSMIRKSManager().setFlagProcessResultStructures(true);
		reactor.getSMIRKSManager().setFlagClearImplicitHAtomsBeforeResultProcess(false);
		reactor.getSMIRKSManager().setFlagAddImplicitHAtomsOnResultProcess(false);
		reactor.getSMIRKSManager().setFlagConvertExplicitHToImplicitOnResultProcess(false);
		
		
		if (FlagPrintReactionDB)
		{	
			System.out.println("Reaction database:");
			for (int i = 0; i < reactDB.reactions.size(); i++)
			{	
				Reaction r = reactDB.reactions.get(i);
				System.out.println("  " + r.getName() + "  " + r.getSmirks() + "  " + r.getReactionClass());
			}
		}
		
		if (FlagPrintReactionStrategy)
		{
			System.out.println();
			System.out.println(strategy.toJSONString(""));
			System.out.println(strategy.toString());
		}
		
		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles, true);
		System.out.println();
		System.out.println("Reactor on target: " + smiles);
		System.out.println();
		
		ReactorResult result = reactor.react(mol);
		
	}
}
