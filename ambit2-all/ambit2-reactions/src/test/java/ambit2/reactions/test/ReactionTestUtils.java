package ambit2.reactions.test;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.MoleculeTools;
import ambit2.reactions.GenericReaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.io.ReactionReadUtils;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorNode;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.reactions.retrosynth.ReactionSequence;
import ambit2.reactions.retrosynth.ReactionSequenceLevel;
import ambit2.reactions.retrosynth.StartingMaterialsDataBase;
import ambit2.reactions.retrosynth.StartingMaterialsDataBase.StartMaterialData;
import ambit2.reactions.rules.scores.AtomComplexity;
import ambit2.reactions.sets.ReactionData;
import ambit2.reactions.sets.ReactionGroup;
import ambit2.reactions.sets.ReactionSet;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.TopLayer;

public class ReactionTestUtils 
{
	public static boolean FlagPrintReactionDB = false;
	public static boolean FlagPrintReactionStrategy = false;
	public static boolean FlagExcplicitHAtoms = false;
	
	
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
		
		//testReactionSequence();
		
		//testCreateStartingMaterialsFile();
		
		//testStartingMaterialsDataBase("/starting-materials-db_v01.txt", 100);
		
		//testStartingMaterialsDataBase(new String[] {"CCC","CCCC","CCCCCO","NC(C)C"});
		
		testAtomComplexity();
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
		ReactionDataBase reactDB = new ReactionDataBase(reactionDBFile);
		
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
		String startMatSmi[] =  {"CC","CCC","CO","NC(C)C"};
		StartingMaterialsDataBase smdb = new StartingMaterialsDataBase(startMatSmi);
		
		String smi = "CCCCCO";
		List<String> smirks = new ArrayList<String>();
		smirks.add("[C:1]Cl>>[C:1]");
		//smirks.add("[C:1][H]>>[C:1]O[H]");
		smirks.add("[H][C:1][C:2][H]>>[H][C:1][H].[H][C:2][H]");
		//smirks.add("[C:1][C:2]>>[C:1][H].[C:2][H]");
				
		ReactionDataBase rdb = new ReactionDataBase(smirks);
		System.out.println("ReactionDB:\n" + rdb.toString());
		System.out.println("Staring material DB:");
		for (String s : startMatSmi)
			System.out.println("   " + s);
		System.out.println();
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
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(false);
		smrkMan.setFlagApplyStereoTransformation(false);
		smrkMan.setFlagHAtomsTransformation(false);
		//smrkMan.setFlagHAtomsTransformationMode(FlagHAtomsTransformationMode);
		smrkMan.setFlagAromaticityTransformation(false);
		
		rseq.setReactDB(rdb);
		rseq.setStartMatDB(smdb);
		rseq.setTarget(target);
		rseq.initilize();
		
		ReactionSequenceLevel level = rseq.getFirstLevel();
		rseq.iterateLevelMolecules(level, null);
		
		for (int i = 0; i < 10; i++)
		{	
			level = level.nextLevel;
			if (level == null)
				break;
			rseq.iterateLevelMolecules(level, null);
		}
		
		System.out.println("ReactionSequence:\n" + rseq.toString());
	}
	
	public static void testCreateStartingMaterialsFile() throws Exception
	{
		String sourceFileName = "/sialbb_prop-sorted_mw.txt";
		String outFileName = "/starting-materials-db.txt";
		Map<String,Integer> columnIndices = new HashMap<String,Integer>(); 		
		columnIndices.put("id", 0);
		columnIndices.put("smiles", 10);
		columnIndices.put("mw", 1);
		
		StartingMaterialsDataBase.createStartingMaterialsFile(new File(sourceFileName), new File(outFileName),
				columnIndices, null);
	}
	
	public static void testStartingMaterialsDataBase(String smDBFileName, int n) throws Exception
	{
		StartingMaterialsDataBase smdb = new StartingMaterialsDataBase(new File(smDBFileName), n);
		Map<String, StartMaterialData> materials = smdb.getMaterials();
		
		for (Entry entry : materials.entrySet())
		{
			String inchiKey = (String)entry.getKey();
			StartMaterialData sm = (StartMaterialData)entry.getValue();
			System.out.println(inchiKey + "  " + sm.id + "   " + sm.smiles);
		}
	}
	
	public static void testStartingMaterialsDataBase(String smiles[]) throws Exception
	{
		StartingMaterialsDataBase smdb = new StartingMaterialsDataBase(smiles);
		Map<String, StartMaterialData> materials = smdb.getMaterials();
		
		for (Entry entry : materials.entrySet())
		{
			String inchiKey = (String)entry.getKey();
			StartMaterialData sm = (StartMaterialData)entry.getValue();
			System.out.println(inchiKey + "  " + sm.id + "   " + sm.smiles);
		}
	}
	
	public static void testAtomComplexity(String smiles, boolean includeImplicitHAtoms) throws Exception
	{
		System.out.println("Testing atom complexity: " + smiles);
		NumberFormat formatter = new DecimalFormat("#0.00"); 
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		if (FlagExcplicitHAtoms)
			MoleculeTools.convertImplicitToExplicitHydrogens(mol);
		TopLayer.setAtomTopLayers(mol);
		AtomComplexity atomCompl = new AtomComplexity();
		atomCompl.setIncludeImplicitHAtoms(includeImplicitHAtoms);
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			double c = atomCompl.calcAtomComplexity(atom, mol);
			System.out.println("" + (i+1) + "  " + atom.getSymbol() + "  " + formatter.format(c));
		}
		/*
		System.out.println("\nAtom paths details:");
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			double c = atomCompl.calcAtomComplexity(atom, mol);
			System.out.println("" + (i+1) + "  " + atom.getSymbol() + "  " + c);
			System.out.println(atomCompl.getAtomPathsAsString(atom, mol));
		}
		*/
		
	}
	
	public static void testAtomComplexity() throws Exception
	{
		//FlagExcplicitHAtoms = true;
		//R1
		testAtomComplexity("COc1ccccc1", true);
		testAtomComplexity("COc1ccc2cc1.C2(=O)C", true);
		//R2
		testAtomComplexity("OCC=C", true);
		testAtomComplexity("OCC1CO1", true);
		//R3
		testAtomComplexity("CC(=O)c1ccccc1", true);
		testAtomComplexity("CC(O)c1ccccc1", true);
		//R4
		testAtomComplexity("C1CCC=CC1", true);
		testAtomComplexity("C1CCCCC1", true);
		//R5
		testAtomComplexity("N#Cc1ccccc1", true);
		testAtomComplexity("NC(=O)c1ccccc1", true);
		//R6
		testAtomComplexity("C=CC=C", true);
		testAtomComplexity("C=C", true);
		testAtomComplexity("C1CCC=CC1", true);
		//R7
		testAtomComplexity("NNc1ccccc1", true);
		testAtomComplexity("O=C1CCCCC1", true);
		testAtomComplexity("C1CCC2=C3C1.N3C4C=CC=CC2=4", true);
		//R8
		testAtomComplexity("O=Cc1ccccc1", true);
		testAtomComplexity("CCC(=O)OC", true);
		testAtomComplexity("COC(=O)C(C)C(O)c1ccccc1", true);
		
	}
	
	
}
