package ambit2.tautomers.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.tautomers.TautomerManager;



public class AutomaticTautomerTests 
{
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};	
	
	
	public static final int LPM_PROCESS_NCI = 1;
	public static final int LPM_FILTER = 2;
	public static final int LPM_TAUTOMER_COUNT = 3;
	public static final int LPM_TAUTOMER_EQUIVALENCE = 4;
	public static final int LPM_TAUTOMER_COMPARISON = 5;
	
	
	int lineProcessMode = 0;
		
	String endLine = "\r\n";
	String outFileName = "";
	String inFileName = "";
	String inFileName2 = "";
	String inFileName3 = "";
	String inFileName4 = "";
	String inFileName5 = "";
	
	String command = "";
	String dbFileName = "";
	
	RandomAccessFile outFile = null;
	TautomerManager tman;
	IsomorphismTester isoTester = new IsomorphismTester();
	//SmartsParser sp = new SmartsParser();
	
	//Tracing variables
	int curLine = 0;	
	int curProcessedStr = 0;
	int nInputStr = 0;
	int nStartStr = 0;
	int traceFrequency = 10;
	
	int minNumOfTautomerForChecking = 2;
	int maxNumOfTautomerForChecking = 20;
	int maxNumOfTautomerForFullCheck = 10;
	int nEquivalenceErrors = 0;
	int nEquivalenceTests = 0;
	
	
	//Filters
	int fMinNA = -1;
	int fMaxNA = -1;
	int fMinNDB = -1;
	int fMaxNDB = -1;
	int fMinCyclo = -1;
	int fMaxCyclo = -1;
	
	
	public static void main(String[] args)
	{
		AutomaticTautomerTests att = new AutomaticTautomerTests();
				
		try {
			att.handleArguments(new String[] {
					"-i","D:/Projects/data012-tautomers/nci-filtered_max_cyclo_4.smi",
					"-nInpStr","0",
					"-nStartStr","0",
					"-c","tautomer-equivalence",
					"-o","D:/Projects/data012-tautomers/tautomer-equivalence.txt",
					"-fMinNDB", "1",
					"-fMaxCyclo", "4",
			});
			
			att.handleCommand();
		} 
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
		
	public int handleArguments(String[] args) throws Exception
	{
		if (args.length == 0)
		{	
			printHelp();
			return(0);
		}	
		
		Vector<CmdOption> options = extractOptions(args);
		
		System.out.println("AutomaticTautomerTests  started with options:");
		for (int i = 0; i < options.size(); i++)
			System.out.println("  " + options.get(i).option + "  " + options.get(i).value);
		
		CmdOption opt;
		
		opt = getOption("i", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName = opt.value; 
		}
		
		opt = getOption("i2", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName2 = opt.value; 
		}
		
		opt = getOption("i3", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName3 = opt.value; 
		}
		
		opt = getOption("4", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName4 = opt.value; 
		}
		
		opt = getOption("i5", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName5 = opt.value; 
		}
		
		opt = getOption("o", options);
		if (opt != null)
		{
			if (opt.value != null)
				outFileName = opt.value; 
		}
		
		opt = getOption("db", options);
		if (opt != null)
		{
			if (opt.value != null)
				dbFileName = opt.value; 
		}	
		
		opt = getOption("c", options);
		if (opt != null)
		{
			if (opt.value != null)
				command = opt.value; 
		}	
		
		opt = getOption("nInpStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nInputStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nInpStr value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("nStartStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nStartStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nStartStr value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		//filters
		opt = getOption("fMinNA", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinNA = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinNA value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMaxNA", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxNA = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxNA value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMinNDB", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinNDB = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinNDB value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMaxNDB", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxNDB = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxNDB value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		opt = getOption("fMinCyclo", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMinCyclo = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMinCyclo value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		
		opt = getOption("fMaxCyclo", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					fMaxCyclo = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect fMaxCyclo value: "+ e.toString());
					return -1;
				}
			}	
		}
		
		
		return (0);
	}
	
	
	
	public int handleCommand() throws Exception
	{
		if (command == null)
			return(0);
		
		
		if (command.equals("process-nci"))
		{
			System.out.println("Processing nci file: " + inFileName);
			openOutputFile();
			lineProcessMode = LPM_PROCESS_NCI;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("filter"))
		{
			System.out.println("Filtering file: " + inFileName);
			openOutputFile();
			lineProcessMode = LPM_FILTER;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-count"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-equivalence"))
		{
			System.out.println("Checking tautomer equivalence: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_TAUTOMER_EQUIVALENCE;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		
		System.out.println("Unknown command: " + command);
		
		return(-1);
	}	
	
	
	public void printHelp()
	{
		System.out.println("Program for automatic testing of tautomer generation algorithms");
		System.out.println("-h            print help info");
		System.out.println("-i            input file");
		System.out.println("-i<n>         additional input files n = 2,3,4,5");
		System.out.println("-o            output file");
		System.out.println("-nInpStr      the number of used input structures");
		System.out.println("-nStartStr    the number of starting structure");
		
		System.out.println("-c            command: ");
		System.out.println("                 process-nci           nci file is processed");
		System.out.println("                 filter                input file is filtered");
		System.out.println("                 tautomer-count        counts the number of tautomers");
		System.out.println("                 tautomer-equivalence  check the equivalence of each tautomer");
		
	}	
	
	
	public Vector<CmdOption> extractOptions(String[] args)
	{		
		CmdOption curOption = null;		
		Vector<CmdOption> v = new Vector<CmdOption>();
		if (args.length == 0)
			return(v);
		
		
		for (int i = 0; i < args.length; i++)
		{	
			if (args[i].startsWith("--"))
			{
				if (curOption != null)				
					v.add(curOption);
				
				curOption = new CmdOption();
				curOption.option = args[i].substring(2);
			}
			else
				if (args[i].startsWith("-"))
				{
					if (curOption != null)				
						v.add(curOption);
					
					curOption = new CmdOption();
					curOption.option = args[i].substring(1);
				}
				else
				{
					if (curOption == null) //This is the first argument and it is not an option
					{
						curOption = new CmdOption();
						curOption.option = "";
						curOption.value = args[i];
					}
					else
					{	
						if (curOption.value == null)
							curOption.value = args[i];
						else					
							curOption.value += " " + args[i];
					}	
				}
		}
		
		v.add(curOption);
		
		
		return(v);
	}
	
	
	CmdOption getOption(String opt, Vector<CmdOption> options)
	{
		for (int i = 0; i < options.size(); i++)
		{
			if (options.get(i).option.equals(opt))
				return (options.get(i));
		}
		
		return null;
	}
	
	
	//Input - Output handling 
	
	int openOutputFile()
	{
		try
		{
			File file = new File(outFileName);
			outFile = new RandomAccessFile(file,"rw");
			outFile.setLength(0);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		if (outFile == null)
			System.out.println("Incorrect outFile");
		
		return(0);
	}
	
	
	int closeOutputFile() 
	{
		try
		{
			if (outFile != null)
				outFile.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		return(0);
	}
	
	int output(String data)
	{
		try
		{
			outFile.write(data.getBytes());
		}
		catch (Exception e)
		{
			System.out.println("output error: " + e.toString());
			return(-1);
		}
		return(0);
	}
	
	void iterateInputFile()
	{	
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			int n = 0;
			curProcessedStr = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				curLine = n;
				
				String line = f.readLine();
				//System.out.println("line " + n + "  " + line);
				
				if (n < nStartStr)
					continue;
				
				curProcessedStr++;
				
				if (nInputStr > 0)
					if (curProcessedStr > nInputStr) 
						break;
				
								
				
				processLine(line.trim());
				
				if (n % this.traceFrequency == 0)
					System.out.println(n);
			}
			
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
	}
	
	int processLine(String line)
	{	
		if (lineProcessMode == LPM_PROCESS_NCI)
		{
			processNCILine(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_FILTER)
		{
			filterLine(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT)
		{
			tautomerCount(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_EQUIVALENCE)
		{
			tautomerEquivalence(line);
			return(0);
		}
		
		return 0;
	}
	
	
	//------------- process line functions (implementation of different commands) ------------------
	
	int processNCILine(String line)
	{
		//System.out.println(line);
		Vector<String> tokens = filterTokens(line.split(" "));
		
		if (tokens.size() < 2)
		{	
			System.out.println("Line " + curLine + "  has less than 2 tokens.");
			return (-1);
		}
		
		if (tokens.size() > 2)
		{	
			System.out.println("Line " + curLine + "  has more than 2 tokens.");
		}	
		
		
		try
		{
			//Create molecule from SMILES
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(tokens.get(1));
			
			//Generate new SMILES
			java.io.StringWriter result =  new java.io.StringWriter();
			SMILESWriter writer = new SMILESWriter(result);			
			writer.write(mol);
			writer.close();
			
			//System.out.println(tokens.get(1) + "  -->  " + result.toString());
			String smiles = result.toString();
			int dot_index = smiles.indexOf(".");
			if (dot_index == -1)
				output(smiles);
			else
				output(smiles.substring(0, dot_index));
			
		}
		catch(Exception e){			
		}
		
		return (0);
	}
	
	int filterLine(String line)
	{
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			if (isFilterOK(mol))
				output(line + endLine);
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	
	boolean isFilterOK(IAtomContainer mol)
	{
		if (fMinNA != -1)
		{
			if (mol.getAtomCount() < fMinNA)
				return(false);
		}
		
		if (fMaxNA != -1)
		{
			if (mol.getAtomCount() > fMaxNA)
				return(false);
		}
		
		
		int NDB = -1; //this value means that NDB has not been evaluated yet
		
		if (fMinNDB != -1)
		{
			//calculation of NDB
			NDB = 0;
			for (IBond bond: mol.bonds())
			{
				if (bond.getOrder() == IBond.Order.DOUBLE)
					NDB++;
			}
			
			if (NDB < fMinNDB)
				return(false);
		}
		
		if (fMaxNDB != -1)
		{
			if (NDB == -1)
			{
				//calculation of NDB
				NDB = 0;
				for (IBond bond: mol.bonds())
				{
					if (bond.getOrder() == IBond.Order.DOUBLE)
						NDB++;
				}
			}
			
			if (NDB > fMaxNDB)
				return(false);
		}
		
		
		if (fMinCyclo != -1)
		{
			if ((mol.getBondCount() - mol.getAtomCount() + 1) < fMinCyclo)
				return(false);
		}
		
		if (fMaxCyclo != -1)
		{
			if ((mol.getBondCount() - mol.getAtomCount() + 1) > fMaxCyclo)
				return(false);
		}
		
		return true;
	}
	
	int tautomerCount(String line)
	{
		System.out.println(line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			output(line + "  " + resultTautomers.size() +  endLine);
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	int tautomerEquivalence(String line)
	{
		System.out.println(line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			setTautomerManager();
			tman.setStructure(mol);
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			if (resultTautomers.size() < minNumOfTautomerForChecking)
				return 0;
			
			if (resultTautomers.size() > maxNumOfTautomerForChecking)
				return 0;
			
			int checkRes = checkTautomerEquivalence(resultTautomers);
			nEquivalenceTests++;
			output(line + "  " + resultTautomers.size() +  "   " + checkRes + endLine);
			
			if (checkRes > 0)
				nEquivalenceErrors++;
			
			System.out.println("n = " + curLine + "   nTests = " + nEquivalenceTests + 
					"   nErrors = " + nEquivalenceErrors);
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	int checkTautomerEquivalence(Vector<IAtomContainer> tautomers) throws Exception
	{
		//Vector<String> codes = new Vector<String>();
		//for (int i = 0; i < tautomers.size(); i++)
		//	codes.add(TautomerManager.getTautomerCodeString(tautomers.get(i)));
		
		int numErr = 0;
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			if (i > this.maxNumOfTautomerForFullCheck)
				break;
			
			tman.setStructure(tautomers.get(i));
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
					
			//boolean FlagOK = compareTautomerSets(codes, resultTautomers);
			boolean FlagOK = compareTautomerSetsIsomorphismCheck(tautomers, resultTautomers);
			
			if (!FlagOK)
				numErr++;
		}
		
		return numErr;
	}
	
	boolean compareTautomerSets(Vector<String> codes, Vector<IAtomContainer> tautomers)
	{
		//This check is very simple and give wrong results when there are 
		//isomorphic tautomers (typically filtered in the results) 
		
		if (codes.size() != tautomers.size())
			return false;
		
		int CheckedTautomers = 0;
		for (int i = 0; i < tautomers.size(); i++)
		{
			String code = TautomerManager.getTautomerCodeString(tautomers.get(i));
			for (int k = 0; k < codes.size(); k++)
				if (code.equals(codes.get(k)))
				{
					CheckedTautomers++;
					break;
				}
		}
		
		if (CheckedTautomers < codes.size() )
			return false;
		else
			return true;
	}
	
	
	boolean compareTautomerSetsIsomorphismCheck(Vector<IAtomContainer> tautomers0, Vector<IAtomContainer> tautomers)
	{
		if (tautomers0.size() != tautomers.size())
			return false;
		
		for (int i = 0; i < tautomers0.size(); i++)
		{
			QueryAtomContainer query = SmartsHelper.getQueryAtomContainer(tautomers0.get(i), false);
			isoTester.setQuery(query);
			
			boolean FlagFound = false;
			for (int k = 0; k < tautomers.size(); k++)
			{
				//sp.setSMARTSData() is not needed for this isomorphism
				boolean res = isoTester.hasIsomorphism(tautomers.get(k));
				if (res)
				{
					FlagFound = true;
					tautomers.remove(k);
					break;
				}
			}
			
			if (!FlagFound)
				return(false);
		}
		
		return true;
	}
	
	//--------------------------------------------------------
	
	public Vector<String> filterTokens(String tokens[])
	{
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!tokens[i].equals(""))
				v.add(tokens[i]);
		return v;
	}
	
	void setTautomerManager()
	{
		tman = new TautomerManager();
		tman.use15ShiftRules(true);
		tman.use17ShiftRules(false);
		tman.maxNumOfBackTracks = 10000;
	}
}
