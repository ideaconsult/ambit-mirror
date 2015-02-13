package ambit2.tautomers.test;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.TautomerUtils;
import ambit2.tautomers.tools.TautomerAnalysis;
import ambit2.tautomers.tools.TautomerAnalysis.Task;

public class TestTautomerAnalysis 
{
	
	public static void main(String[] args) throws Exception
	{
		//testTautomerPairs("NC=CCCCCCC=S");
		generateRuleCouples();
	}
	
	public static void generateRuleCouples() throws Exception
	{
		TautomerAnalysis ta = new TautomerAnalysis();
		
		ta.filePath = "D:/Projects/Nina/Tautomers/test-analysis/";
		//ta.outFilePath = "D:/Projects/Nina/Tautomers/test-analysis/out/";
		ta.inputFileName = "TOX21S_1-100.smi";
		ta.outFileType = "csv";
		ta.outFilePrefix = "pairs-";
		ta.setMoleculeFilter("#Mol=[1,100]");
		ta.task = Task.GEN_RULE_PAIR;
		ta.FlagFilter = false;
		
		ta.process();
	}
	
	public static void testTautomerPairs(String smi) throws Exception
	{
		System.out.println("testTautomerPairs  for " + smi);
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		TautomerManager tman = new TautomerManager();
		TautomerUtils.generatePairForEachRuleInstance(tman, mol, true);
	}

}
