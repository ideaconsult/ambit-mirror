package ambit2.reactions.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.reactions.GenericReaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.io.ReactionReadUtils;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorNode;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.reactions.retrosynth.ReactionSequence;
import ambit2.reactions.retrosynth.ReactionSequenceLevel;
import ambit2.reactions.sets.ReactionData;
import ambit2.reactions.sets.ReactionGroup;
import ambit2.reactions.sets.ReactionSet;
import ambit2.smarts.SMIRKSManager;
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
		
		//testReactor("CC", "D:/Projects/Nina/Reactions/metabolism-reactions.json", 10); //!!!!!!!!!
		
		//testReactor("C", "/Volumes/Data/Projects/reactor-config1.json"); 
		
		testReactionSequence();
		
		
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
		testReactor(smiles, reactionDBFile, 0);
	}
			
	public static void testReactor(String smiles, String reactionDBFile, int reactorStepSize) throws Exception
	{
		System.out.println("Setting reactor and reaction database...");
		Reactor reactor = new Reactor();
		ReactionDataBase reactDB = new ReactionDataBase(new File(reactionDBFile));
		
		System.out.println("Configuring reaction database...");
		//reactDB.configureReactions(reactor.getSMIRKSManager());
		reactDB.configureGenericReactions(reactor.getSMIRKSManager());
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
			for (int i = 0; i < reactDB.genericReactions.size(); i++)
			{	
				GenericReaction r = reactDB.genericReactions.get(i);
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
		
		if (reactorStepSize <= 0)
		{	
			ReactorResult result = reactor.react(mol);
		}	
		else
		{
			reactor.initializeReactor(mol);
			List<ReactorNode> nodes = reactor.reactNext(reactorStepSize);
			System.out.println("Handled " + nodes.size() + " nodes");
			
			while (!nodes.isEmpty())
			{
				nodes = reactor.reactNext(reactorStepSize);
				System.out.println("Handled " + nodes.size() + " nodes");
			}
			
		}
		
	}
	
	public static void testReactionSequence() throws Exception
	{
		String smi = "CCCCCO";
		List<String> smirks = new ArrayList<String>();
		smirks.add("[C:1]Cl>>[C:1]");
		//smirks.add("[C:1][H]>>[C:1]O[H]");
		smirks.add("[H][C:1][C:2][H]>>[H][C:1][H].[H][C:2][H]");
		//smirks.add("[C:1][C:2]>>[C:1][H].[C:2][H]");
				
		ReactionDataBase rdb = new ReactionDataBase(smirks);
		System.out.println("ReactionDB:\n" + rdb.toString());
		System.out.println("Target: " + smi);
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(smi);
		
		SMIRKSManager smrkMan0 = new SMIRKSManager(SilentChemObjectBuilder.getInstance()); 
		rdb.configureGenericReactions(smrkMan0);
		
		ReactionSequence rseq = new ReactionSequence();
		SMIRKSManager smrkMan = rseq.getSmrkMan();
		//setup smrkMan
		smrkMan.setFlagProcessResultStructures(true);
		smrkMan.setFlagClearHybridizationBeforeResultProcess(true);
		smrkMan.setFlagClearImplicitHAtomsBeforeResultProcess(false);
		smrkMan.setFlagClearAromaticityBeforeResultProcess(true);
		smrkMan.setFlagAddImplicitHAtomsOnResultProcess(false);
		smrkMan.setFlagConvertAddedImplicitHToExplicitOnResultProcess(false);
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
		smrkMan.setFlagApplyStereoTransformation(false);
		smrkMan.setFlagHAtomsTransformation(false);
		//smrkMan.setFlagHAtomsTransformationMode(FlagHAtomsTransformationMode);
		smrkMan.setFlagAromaticityTransformation(false);
		
		rseq.setReactDB(rdb);
		rseq.setTarget(target);
		rseq.initilize();
		
		ReactionSequenceLevel level = rseq.getFirstLevel();
		rseq.iterateLevelMolecules(level, null);
		
		level = level.nextLevel;
		rseq.iterateLevelMolecules(level, null);
		
		level = level.nextLevel;
		rseq.iterateLevelMolecules(level, null);
		
		System.out.println("ReactionSequence:\n" + rseq.toString());
		
		
	}
}
