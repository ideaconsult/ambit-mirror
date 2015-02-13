package ambit2.tautomers.test;

import ambit2.tautomers.tools.TautomerAnalysis;
import ambit2.tautomers.tools.TautomerAnalysis.Task;

public class TestTautomerAnalysis 
{
	
	public static void main(String[] args) throws Exception
	{
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
		ta.setMoleculeFilter("#Mol=[1,2]");
		ta.task = Task.GEN_RULE_PAIR;
		
		ta.process();
	}

}
