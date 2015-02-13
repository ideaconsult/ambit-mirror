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
		
		ta.filePath = "D:/Projects/Nina/Tautomers/test-druglike-str/";
		ta.inputFileName = "TOX21S_v4b_CID_structures_v02.smi";
		ta.outFileType = "csv";
		ta.outFilePrefix = "out";
		ta.setMoleculeFilter("#Mol=[1,100]");
		ta.task = Task.GEN_RULE_COUPLES;
		
		ta.process();
	}

}
