package ambit2.tautomers.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.TautomerRanking;



public class AutomaticTautomerTests 
{
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};
	
	public static final int LPM_TEST_PRINT = 0;
	public static final int LPM_PROCESS_NCI = 1;
	public static final int LPM_FILTER = 2;
	public static final int LPM_TAUTOMER_COUNT = 3;
	public static final int LPM_TAUTOMER_EQUIVALENCE = 4;
	public static final int LPM_TAUTOMER_COMPARISON = 5;
	public static final int LPM_CACTVS_COUNT = 6;
	public static final int LPM_TAUTOMER_COUNT_COMBINATORIAL = 7;
	public static final int LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED = 8;
	public static final int LPM_STRUCTURE_STAT = 9;
	public static final int LPM_EQUIVALENCE_STAT = 10;
	public static final int LPM_TAUTOMER_FULL_INFO = 11;
	public static final int LPM_TAUTOMER_DESCR_STAT = 12;
	public static final int LPM_TAUTOMER_FP_STAT = 13;
	public static final int LPM_TAUTOMER_CALC_DESCR_AVERAGE = 14;
	public static final int LPM_TAUTOMER_DESCR_STAT2 = 15;
	public static final int LPM_TAUTOMER_FP_STAT2 = 16;
	
	
	//public static final int LPM_COMPARE_AMBIT_INTERNAL = 11;
	//public static final int LPM_COMPARE_AMBIT_EXTERNAL = 12;
	
	
	TautomerRanking tautomerRanking = new TautomerRanking(); 
	
	public boolean FlagHandleCommand = true;
	public boolean FlagFiltrateSmilesToLargestFragment = false;   //currently used only for structure-stat command
	public boolean FlagWorkWithWholeDirectory = true;  //If this flag is true and input file is a directory then all file in it are used for the input
	public boolean FlagSkipFirstLineInDirIteration = false;  //if true the first line is skipped for the all files except the first file in the directory
	public boolean FlagUseCACTVSRank = false;
	public int FlagGenerationAlgorithm = TautomerConst.GAT_Incremental; //GAT_Comb_Pure  GAT_Comb_Improved  
	
	
	
	public boolean FlagDescrAverageForCloseEnergies = true;  //The ranking is relative to the energy of the tautomer which is the original structure
	
	
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
	
	String cactvsExecPath = "C:/Program Files/cactvs/lib/tclcactvs.exe";
	String cactvsOutPath = "D:/Projects/data012-tautomers/cactvs";
	String descrTestSepareator = ",";
	String temporaryOutFileName = "D:/Projects/data016/temporal-out-file-123456789.txt";
	
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
	
	int tutomerErrorSum = 0;
	
	//Filters
	int fMinNA = -1;
	int fMaxNA = -1;
	int fMinNDB = -1;
	int fMaxNDB = -1;
	int fMinCyclo = -1;
	int fMaxCyclo = -1;
	
	
	//Structure statistics helpers
	int StrSizeBins[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100};
	int NumRingBins[] = {0,1,2,3,4,5,6,7,8,9,10};
	int freqStrSizeBins[];
	int freqNumRingBins[];
	
	//Equivalnece statistics helpers
	double RelNumUneqBins[] = {0,1,2,3,4,5,6,7,8,9,10,15,20,25,30,40,50,60,70,80,90,100};  //in percent
	int AbsNumUneqBins[] = {0,1,2,3,4,5,6,7,8,9,10,15,20,25,30,40,50,60,70,80,90,100};
	double UneqDiffBins [] = {0,1, 2, 3, 4, 5, 6 , 7, 8, 9, 10, 15, 20, 30};
	
	int freqRelNumUneqBins[];
	int freqAbsNumUneqBins[];
	int freqUneqDiffBins [];
	int numOfIdenticalCanonicalTautomers = 0;
	
	
	public static boolean FlagCheckMemory = false;
	public static boolean FlagCheckNumAndSMILESinComparison = true;
	
	//Token numbers to be compared (zero - based indexing)
	int numTokenAtFile1 = 2;  
	int numTokenAtFile2 = 2;
	int numErrorToken = 1;
	String errSubStr = "XXX";
	String separator1 = "\t";
	String separator2 = "\t";
	boolean FlagUseOnlyFirstAlgorithm = false;  //essentially if true this makes distribution of the tautomer count for the first algorithm
	boolean FlagCompareCanonicalTautomer = true;
	
	//helpers for tautomer descr/fp stat
	DescriptorStatShort descrStat0[];
	DescriptorStatInfo descrStat[];
	int Temperatures[] = {300, 500, 1000, 2000} ;
	int curStruct = -1;
	int numTautomers = 0;
	String curSMILES = "";
	String curTautomerSmiles = "";
	RandomAccessFile tempOutFile = null;
	String extractError = "";
	
	RandomAccessFile fSchemes[] = null;
	String firstLineForSeparatedWeightingSchemes = null;
	
	
	
	public static void main(String[] args)
	{
		AutomaticTautomerTests att = new AutomaticTautomerTests();
				
		try {
			
			//att.handleArguments(args);
			//if (att.FlagHandleCommand) att.handleCommand();
			//if (true) return;
			
			att.handleArguments(new String[] {
					
					//"-i","D:/temp2/test",
					//"-i","D:/Projects/data015/LogP/XlogP.csv",
					"-i","D:/Projects/data016/logP2-kekule-taut-EPI_Suite-LogP-CLEANED.csv",
					"-i2","D:/Projects/data016/ext-validation-set02-activity.csv",
					
					
					//"-i","D:/Projects/data012-tautomers/nci-filtered_max_cyclo_4.smi",					
					//"-i","D:/Projects/data012-tautomers/nci-filtered_max_cyclo_4.smi",
					//"-i2","D:/Projects/data012-tautomers/results-final/AMBIT-Tautomer-Count-Comb-Improved-FULL.txt",
					//"-i2","D:/Projects/data012-tautomers/results-final/Ambit-Tautomer-Count-Comb-FULL.txt",
					//"-i2","D:/Projects/data012-tautomers/results-final/cactvs-tautomer-count-canonical-FULL.txt",
					//"-i2","D:/Projects/data012-tautomers/results-final/marvin-tautomer-count.txt",
					
					"-nInpStr","0",
					"-nStartStr","0",
					"-c","tautomer-calc-descr-average",					
					//"-c","test-print",
					//"-o","D:/Projects/data015/LogP/xlogp-test-average-descr.csv",
					"-o","D:/Projects/data016/test.csv",
					"-fMinNDB", "1",
					"-fMaxCyclo", "4",
			});
			
			if (att.FlagHandleCommand)
				att.handleCommand();
		} 
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public void printHelp()
	{
		System.out.println("Program for automatic testing of tautomer generation algorithms");
		System.out.println("-h            print help info");
		System.out.println("-i            input file");
		System.out.println("-i<n>         additional input files n = 2,3,4,5");
		System.out.println("-o            output file");
		System.out.println("-nInpStr      the number of used input structures /or processed lines/");
		System.out.println("-nStartStr    the number of starting structure /or line to be processed/");
		
		System.out.println("-c            command: ");
		System.out.println("                 test-print            Test printint of the lines");
		System.out.println("                 process-nci           nci file is processed");
		System.out.println("                 filter                input file is filtered");
		System.out.println("                 tautomer-count        counts the number of tautomers");
		System.out.println("                 tautomer-count-comb   counts the number of tautomers /combinatorial algorithm/");
		System.out.println("                 tautomer-count-comb-improved   counts the number of tautomers /improved comb. algorithm/");
		System.out.println("                 tautomer-equivalence  check the equivalence of each tautomer");
		System.out.println("                 cactvs-count          counts the number of cactvs tautomers");
		System.out.println("                 structure-stat        calculates structure statistics");		
		System.out.println("                 equivalence-stat      calculates tautomer equivaence statistics");
		System.out.println("                 tautomer-full-info    generates and stores all tautomers and ranks");
		System.out.println("                 tautomer-descr-stat   calculates descirptor statisics as function of the tautomers");
		System.out.println("                 tautomer-fp-stat      calculates fingerprint statisics as function of the tautomers");
		System.out.println("                 tautomer-descr-stat2  calculates 2nd order descirptor statisics");
		System.out.println("                 tautomer-calc-descr-average  calculates descirptors' average values");
		System.out.println("                 tautomer-fill-exp-values  fill experimental value for each tautomer");
		System.out.println("                 concat-descr-files    Concatinates the descriptior files listed in the input file");
		System.out.println("                 separate-weighted-descr  Calculated weighted descriptors are split into separate files");
		
	}	
	
		
	public int handleArguments(String[] args) throws Exception
	{
		if (args.length == 0)
		{	
			printHelp();
			FlagHandleCommand = false;
			return(0);
		}	
		
		List<CmdOption> options = extractOptions(args);
		
		System.out.println("AutomaticTautomerTests  started with options:");
		for (int i = 0; i < options.size(); i++)
			System.out.println("  " + options.get(i).option + "  " + options.get(i).value);
		
		CmdOption opt;
		
		opt = getOption("h", options);
		if (opt != null)
		{
			printHelp();
			FlagHandleCommand = false;
			return(0); 
		}
		
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
		
		
		
		
		if (command.equals("test-print"))
		{
			System.out.println("Processing nci file: " + inFileName);			
			lineProcessMode = LPM_TEST_PRINT;
			iterateInputFile();
			return(0);
		}
		
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
		
		if (command.equals("tautomer-count-comb"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT_COMBINATORIAL;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-count-comb-improved"))
		{
			System.out.println("tautomer counting: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED;
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
		
		
		if (command.equals("cactvs-count"))
		{
			System.out.println("Counting cactvs tautomers: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_CACTVS_COUNT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("structure-stat"))
		{
			System.out.println("Making structure statistics: " + inFileName);
			openOutputFile();
			initStructureStatistics();
			lineProcessMode = LPM_STRUCTURE_STAT;
			iterateInputFile();
			printStructureStatistics();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("equivalence-stat"))
		{
			System.out.println("Making equivalence statistics: " + inFileName);
			openOutputFile();
			initEquivalenceStatistics();
			lineProcessMode = LPM_EQUIVALENCE_STAT;
			iterateInputFile();
			printEquivalenceStatistics();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-full-info"))
		{
			System.out.println("Calculating tautomer full info: " + inFileName);
			openOutputFile();
			setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_FULL_INFO;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-descr-stat"))
		{
			System.out.println("Calculating tautomer descriptor statistics: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_TAUTOMER_DESCR_STAT;
			iterateInputFile();
			performSecondDescriptorScan(); //This is done for the last compound and its tautomers
			closeOutputFile();
			return(0);
		}
						
		if (command.equals("tautomer-fp-stat"))
		{
			System.out.println("Calculating tautomer fingerprint statistics: " + inFileName);
			openOutputFile();
			lineProcessMode = LPM_TAUTOMER_FP_STAT;
			iterateInputFile();			
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-descr-stat2"))
		{
			System.out.println("Calculating tautomer descriptor statistics second order: " + inFileName);
			openOutputFile();			
			lineProcessMode = LPM_TAUTOMER_DESCR_STAT2;
			iterateInputFile();
			finalizeDescrStat2Order();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("tautomer-fill-exp-values"))
		{
			System.out.println("Add experimental data for each tautomer to a data file with all tatuomers : " + inFileName);
			fillTautomerExperimentalValues();
			return(0);
		}
		
		if (command.equals("concat-descr-files"))
		{
			System.out.println("Concatinates the descriptior files listed in the input file: " + inFileName);
			concatinateDescriptorFiles();
			return(0);
		}
		
		if (command.equals("tautomer-calc-descr-average"))
		{
			System.out.println("Calculating tatomer descriptor average values based on different weigting schemes: " + inFileName);
			openOutputFile();
			//setTautomerManager();
			lineProcessMode = LPM_TAUTOMER_CALC_DESCR_AVERAGE;
			iterateInputFile();
			finalizeTautomerAverageCalculation();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("separate-weighted-descr"))
		{
			System.out.println("Separation of the calculated weighted descriptors from " + inFileName 
					+ " into several out files for each weighting scheme");
			separateDescriptorWeightingSchemes();
			return(0);
		}
		
		
		
		
		
		if (command.equals("compare-algorithms"))
		{
			System.out.println("compare + \n first input file: " + inFileName);
			System.out.println("second input file: " + inFileName2);
			
			openOutputFile();			
			//lineProcessMode = LPM_COMPARE_AMBIT_INTERNAL;
			
			compareAlgorithms();
			
			closeOutputFile();
			return(0);
		}
		
				
		System.out.println("Unknown command: " + command);
		
		return(-1);
	}	
	
	
	
	
	
	public List<CmdOption> extractOptions(String[] args)
	{		
		CmdOption curOption = null;		
		List<CmdOption> v = new ArrayList<CmdOption>();
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
	
	
	CmdOption getOption(String opt, List<CmdOption> options)
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
	
	int openTempOutputFile(boolean FlagReadOnly)
	{
		try
		{
			File file = new File(temporaryOutFileName);
			if (FlagReadOnly)
				tempOutFile = new RandomAccessFile(file,"r");
			else
			{	
				tempOutFile = new RandomAccessFile(file,"rw");
				tempOutFile.setLength(0);
			}	
		}
		catch(Exception e)
		{
			System.out.println("Problem opening the temporal out file " + temporaryOutFileName);
			System.out.println(e.toString());
		}	
		
		return(0);
	}
	
	
	int closeTempOutputFile() 
	{
		try
		{
			if (tempOutFile != null)
				tempOutFile.close();
		}
		catch(Exception e)
		{
			System.out.println("Problem closing the temporal out file");
			System.out.println(e.toString());
			return -1;
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
	
	int tempOutput(String data)
	{
		//System.out.println("***");
		try
		{
			tempOutFile.write(data.getBytes());
		}
		catch (Exception e)
		{
			System.out.println("temporal output error: " + e.toString());
			return(-1);
		}
		return(0);
	}
	
	int output(String data, RandomAccessFile f)
	{
		try
		{
			f.write(data.getBytes());
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
			if (FlagWorkWithWholeDirectory)
				if (file.isDirectory())
				{	
					iterateDir(file);
					return;
				}	
			
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
			
				
				if (FlagCheckMemory)
				{
					checkMemory();				
					Runtime.getRuntime().gc();
				}	
				
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
	
	void iterateDir(File dir) throws Exception
	{	
		System.out.println("Iterating directory " + dir.getPath());
		System.out.println("-----------------------------------------------");
		
		curProcessedStr = 0;
		int nFile = 0;
		for (File child : dir.listFiles()) 
		{
			if (child.isFile())
			{	
				System.out.println("Iterating file: " + child.getPath());
				nFile++;
				
				if(FlagSkipFirstLineInDirIteration && (nFile > 1))
					iterateFile(child, true);
				else
					iterateFile(child, false);
			}	
		}
	}
	
	void iterateFile(File file, boolean SkipFirstLine) throws Exception
	{	
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		
		int n = 0;
		while (f.getFilePointer() < length)
		{	
			n++;
			curLine = n;
			
			String line = f.readLine();
			//System.out.println("line " + n + "  " + line);
			
			if (n==1)
				if (SkipFirstLine)
				continue;
			
			if (n < nStartStr)
				continue;
			
			curProcessedStr++;
			
			if (nInputStr > 0)
				if (curProcessedStr > nInputStr) 
					break;
											
			
			processLine(line.trim());
			
			if (FlagCheckMemory)
			{
				checkMemory();				
				Runtime.getRuntime().gc();
			}	
			
			if (n % this.traceFrequency == 0)
				System.out.println(n);
		}
		
		f.close();
	}
	
	
	int processLine(String line)
	{	
		
		if (lineProcessMode == LPM_TEST_PRINT)
		{
			processTestPrint(line);
			return(0);
		}
		
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
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT_COMBINATORIAL)
		{
			tautomerCountCombinatorial(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_COUNT_COMBINATORIAL_IMPROVED)
		{
			tautomerCountCombinatorialImproved(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_EQUIVALENCE)
		{
			tautomerEquivalence(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_CACTVS_COUNT)
		{
			cactvsCount(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_STRUCTURE_STAT)
		{
			structureStatistics(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_EQUIVALENCE_STAT)
		{
			equivalenceStatistics(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_FULL_INFO)
		{
			tautomerFullInfo(line);
			return(0);
		}
		
		
		if (lineProcessMode == LPM_TAUTOMER_DESCR_STAT)
		{
			tautomerDescrStat(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_FP_STAT)
		{
			tautomerFPStat(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_DESCR_STAT2)
		{
			tautomerDescrStat2Order(line);
			return(0);
		}
		
		if (lineProcessMode == LPM_TAUTOMER_CALC_DESCR_AVERAGE)
		{	
			tautomerCalcDescrAverage(line);
			return(0);
		}
				
		return 0;
	}
	
	
	//------------- process line functions (implementation of different commands) ------------------
	
	int processTestPrint(String line)
	{
		System.out.println(line);
		return 0;
	}
	
	int processNCILine(String line)
	{
		//System.out.println(line);
		List<String> tokens = filterTokens(line.split(" "));
		
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
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			List<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + "  " +  
					SmartsHelper.moleculeToSMILES(can_t,false)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (0);
	}
	
	int tautomerCountCombinatorial(String line)
	{
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			List<IAtomContainer> resultTautomers = tman.generateTautomers();
			//IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + endLine);   
				//	+ "  " + SmartsHelper.moleculeToSMILES(can_t)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return (0);
	}
	
	int tautomerCountCombinatorialImproved(String line)
	{
		System.out.println("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);
			List<IAtomContainer> resultTautomers = tman.generateTautomers_ImprovedCombApproach();
			//IAtomContainer can_t = TautomerManager.getCanonicTautomer(resultTautomers);
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + endLine);   
				//	+ "  " + SmartsHelper.moleculeToSMILES(can_t)  /* +  endLine */);
		}	
		catch(Exception e){
			System.out.println(e.toString());
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
			List<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			if (resultTautomers.size() < minNumOfTautomerForChecking)
				return 0;
			
			if (resultTautomers.size() > maxNumOfTautomerForChecking)
				return 0;
			
			int checkRes = checkTautomerEquivalence(resultTautomers);
			nEquivalenceTests++;
			
			DecimalFormat df = new DecimalFormat("##0.0");
			
			double meanTErr = 0;
			if (checkRes > 0 )
				meanTErr = (1.0*tutomerErrorSum / checkRes);
			String meanTErr_fs = df.format(meanTErr);
			
			
			output(line + "  " + resultTautomers.size() +  "   " + checkRes + "   "  + meanTErr_fs + endLine);
			
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
	
	int checkTautomerEquivalence(List<IAtomContainer> tautomers) throws Exception
	{
		//List<String> codes = new List<String>();
		//for (int i = 0; i < tautomers.size(); i++)
		//	codes.add(TautomerManager.getTautomerCodeString(tautomers.get(i)));
		
		int numErr = 0;
		tutomerErrorSum = 0;
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			if (i > this.maxNumOfTautomerForFullCheck)
				break;
			
			tman.setStructure(tautomers.get(i));
			List<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
					
			//boolean FlagOK = compareTautomerSets(codes, resultTautomers);
			int numOfDiffTautomers = compareTautomerSetsIsomorphismCheck(tautomers, resultTautomers);
			
			
			if (numOfDiffTautomers > 1)
			{
				numErr++;
				tutomerErrorSum += numOfDiffTautomers;
			}
				
		}
		
		
		return numErr;
	}
	
	boolean compareTautomerSets(List<String> codes, List<IAtomContainer> tautomers)
	{
		//This check is very simple and give wrong results when there are 
		//isomorphic tautomers (typically filtered in the results) 
		
		if (codes.size() != tautomers.size())
			return false;
		
		int CheckedTautomers = 0;
		for (int i = 0; i < tautomers.size(); i++)
		{
			String code = TautomerManager.getTautomerCodeString(tautomers.get(i), false);
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
	
	
	int compareTautomerSetsIsomorphismCheck(List<IAtomContainer> tautomers0, List<IAtomContainer> tautomers)
	{
		//if (tautomers0.size() != tautomers.size())
		//	return false;
		
		int numberOfDiff = 0;
		
		for (int i = 0; i < tautomers0.size(); i++)
		{
			IQueryAtomContainer query = SmartsHelper.getQueryAtomContainer(tautomers0.get(i), false);
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
			
			//if (!FlagFound)
			//	return(false);
			if (!FlagFound)
				numberOfDiff++;
		}
		
		return numberOfDiff;
	}
	
	
	int cactvsCount(String line)
	{
		System.out.println(line);
		
		try
		{	
			//Prepare cactvs tcl script
			String tclFile = cactvsOutPath + "/cactvs-work-temp" + curLine + ".tcl";
			String cactvsOutFile = cactvsOutPath + "/cactvs-work-temp" + curLine + ".smi";			
			createCactvsTclScript(line, tclFile, cactvsOutFile);
			
			//Execute cactvs command			
			Runtime rt = Runtime.getRuntime();
			
			String command = "cmd /c start \"" + "122"  + "\" /wait " + "\"" + cactvsExecPath + "\" -f " + tclFile;
			
			System.out.println(command);
			
			Process cactvs = rt.exec(command);
			
			//Handle cactvs out
			//int nTautomers = handleCactvsOut(cactvsOutFile);			
			//System.out.println("nTautomers = " + nTautomers);
			
		}	
		catch(Exception e){
			return (-1);
		}
		
		return (0);
	}
	
	int createCactvsTclScript(String smiles, String fileName, String cactvsOutFile)
	{
		try
		{
			File file = new File(fileName);
			RandomAccessFile f = new RandomAccessFile(fileName,"rw");
			f.setLength(0);
			String data = "molfile write [molfile create " 
				+ cactvsOutFile + " a] [ens get [ens create {" 
				+ smiles + "}] E_TAUTOSET]";
			f.write(data.getBytes());
			f.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			return(-1);
		}
		
		return (0);
	}
	
	int handleCactvsOut(String fileName) 
	{
		int nT = 0;
		try
		{
			File file = new File(fileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			curProcessedStr = 0;

			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();
				//System.out.println(">>> " + line);
				nT++;
			}

			f.close();
		}
		catch(Exception e)
		{	
		}
		
		return (nT);
	}
	
	
	//-------
	
	int tautomerFullInfo(String line)
	{
		System.out.print("" + curLine + "   " + line);
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(line.trim());
			
			tman.setStructure(mol);			
			List<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			
			switch (FlagGenerationAlgorithm)
			{
			case TautomerConst.GAT_Comb_Pure:
				resultTautomers = tman.generateTautomers();	
				break;
			
			case TautomerConst.GAT_Incremental:
				resultTautomers = tman.generateTautomersIncrementaly();
				break;
			
			case TautomerConst.GAT_Comb_Improved:
				resultTautomers = tman.generateTautomers_ImprovedCombApproach();
				break;
			}
			
			
			output("" + curLine + "   " + line + "  " + resultTautomers.size() + "  "  +  endLine);
			System.out.println("    -->  " + resultTautomers.size() + " tautomers");
			
			
			if (resultTautomers.size() > 1)  
				for (int i = 0; i < resultTautomers.size(); i++)
				{
					IAtomContainer tautomer = resultTautomers.get(i);
					double rank;
					if (FlagUseCACTVSRank)
						rank = ((Double)tautomer.getProperty("CACTVS_ENERGY_RANK")).doubleValue();
					else
						rank = ((Double)tautomer.getProperty("TAUTOMER_RANK")).doubleValue();
					
					String smiles = SmartsHelper.moleculeToSMILES(tautomer, false).trim();
					output("" + curLine + "   " + smiles + "  " + rank  +  endLine);
				}
			
		}	
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return 0;
	}
	
	//------- commands: tautomer-descr-stat   and  tautomer-fp-stat--------------------
	
	int tautomerDescrStat(String line)
	{
		if (curLine == 1)
		{
			initDescriptorStatistics0(line);
			return 0;
		}
		
		String tokens[] = line.split(descrTestSepareator);
		try
		{
			int strNum = Integer.parseInt(tokens[0]);
			if (strNum != curStruct)
			{	
				//Make second scan and finalize statistics 
				if (curStruct != -1)					
					performSecondDescriptorScan();
				
				//Start New Compound making statistics for its tautomers 
				curStruct = strNum;
				initTautomerStatistics(tokens);
				//System.out.println("New structs " + strNum + "  at line " + curLine);
			}
			else
				processDescrLineFirstScan(tokens);
			
			if (descrStat0[0].nTautomers > 1) //The file is not opened when there is only one tautomer  
				tempOutput(line+endLine);
		}
		catch(Exception e)
		{
			System.out.println("Error on line " + curLine);
			System.out.println(e.toString());
		}
		
		return 0;
	}
		
	void initDescriptorStatistics0(String firstLine)
	{
		String tokens[] = firstLine.split(descrTestSepareator);
		int nDescr = tokens.length - 3;
		descrStat0 = new DescriptorStatShort[nDescr];
		for (int i = 0; i < nDescr; i++)
		{	
			descrStat0[i] = new DescriptorStatShort();
			descrStat0[i].name = tokens[i+3];
		}
	}
	
	
	void initDescriptorStatistics(String firstLine)
	{
		String tokens[] = firstLine.split(descrTestSepareator);
		int nDescr = tokens.length - 3;
		descrStat = new DescriptorStatInfo[nDescr];
		for (int i = 0; i < nDescr; i++)
		{	
			descrStat[i] = new DescriptorStatInfo();
			descrStat[i].name = tokens[i+3];
		}
	}
	
	
	void initTautomerStatistics(String tokens[])
	{
		int n = 1;
		
		try 
		{
			n = Integer.parseInt(tokens[2]);
		}
		catch (Exception e)
		{	
			System.out.println("Incorrect number of tautomers in token 3  -->  '" + tokens[2] + "'  on line " + curLine);
		}
		
		curSMILES = tokens[1];
		for (int i = 0; i < descrStat0.length; i++)
		{
			descrStat0[i].valueSum = 0;
			descrStat0[i].valueSDSum = 0;
			descrStat0[i].nTautomers = 0;
		}
		
		if (n <= 1)
		{
			//Only one tautomer is present 
			processDescrLineFirstScan(tokens);
			return;
		}
				
		//The structure contains more than one tautomer
		openTempOutputFile(false); //read/write mode
		
	}
	
	
	void performSecondDescriptorScan()
	{
		if (descrStat0[0].nTautomers == 1)
		{
			//Finalize statistics for this compound for the case of one tautomer
			StringBuffer sb = new StringBuffer();
			sb.append(curStruct);
			sb.append(",");
			sb.append(curSMILES);
			sb.append(",1");
			for (int i = 0; i < descrStat0.length; i++)
				sb.append(",0");
			
			output(sb.toString() + endLine);
			
			return;
		}
		
		closeTempOutputFile();
		
		//valueSum is converted to mean value for each descriptor
		for (int i = 0; i < descrStat0.length; i++)
		{
			if (descrStat0[i].nTautomers > 0)
				descrStat0[i].valueSum = descrStat0[i].valueSum / descrStat0[i].nTautomers;
		}
		
		openTempOutputFile(true); //read only mode	
		
		try {
			long length = tempOutFile.length();
			int n = 0;

			while (tempOutFile.getFilePointer() < length)
			{	
				n++;
				String line = tempOutFile.readLine();
				String tokens[] = line.split(descrTestSepareator);
				processDescrLineSecondScan(tokens, n);
			}
		}
		catch(Exception e)
		{	
			System.out.println("File error in the second scan ");
			System.out.println(e.toString());
		}
		
		closeTempOutputFile();
		
		//Finalize statistics for this compound
		StringBuffer sb = new StringBuffer();
		sb.append(curStruct);
		sb.append(",");
		sb.append(curSMILES);
		sb.append(",");
		sb.append(descrStat0[0].nTautomers);
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			double rsd = Math.sqrt(descrStat0[i].valueSDSum/descrStat0[i].nTautomers);
			double m = Math.abs(descrStat0[i].valueSum);
			if (m < 1.0e-30)
				m = 1.0e-30; 
			
			rsd = rsd / m;
			sb.append(",");
			sb.append(rsd);
		}
		
		output(sb.toString() + endLine);
	}
	
	
	void processDescrLineFirstScan(String tokens[])
	{
		if (tokens.length != (descrStat0.length + 3))
		{
			System.out.println("Incorrect number of tokens on line " + curLine);
			return;
		}
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			try 
			{
				double dVal = Double.parseDouble(tokens[3+i]);
				if (dVal != -999)
				{
					descrStat0[i].valueSum += dVal;
					descrStat0[i].nTautomers++;
				}
				else
					System.out.println("Incorrect value for token " + (3+i) + " -->  '" + tokens[3+i] + "'  on line " + curLine);
			}
			catch (Exception e)
			{	
				System.out.println("Incorrect token " + (3+i) + " -->  '" + tokens[3+i] + "'  on line " + curLine);
			}
		}
	}
	
	void processDescrLineSecondScan(String tokens[], int lineNum)
	{	
		if (tokens.length != (descrStat0.length + 3))
		{
			System.out.println("Incorrect number of tokens on line " + curLine);
			return;
		}
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			try 
			{
				double dVal = Double.parseDouble(tokens[3+i]);
				if (dVal != -999)
				{
					descrStat0[i].valueSDSum += (dVal - descrStat0[i].valueSum)*(dVal - descrStat0[i].valueSum);
				}
			}
			catch (Exception e)
			{	
				//The error messages are output in the first scan
				//System.out.println("Incorrect token " + (3+i) + " -->  '" + tokens[3+i] + "'  on line " + curLine);
			}
		}
	}
	
	//---------------fp-functionality----------------
	
	
	int tautomerFPStat(String line)
	{
		if (curLine == 1)
		{
			initDescriptorStatistics0(line);  
			return 0;
		}
		
		String tokens[] = line.split(descrTestSepareator);
		try
		{
			int strNum = Integer.parseInt(tokens[0]);
			if (strNum != curStruct)
			{	
				//Finalize statistics 
				if (curStruct != -1)					
					finalizeFPTautomerStatistics(); //first line is processed here
				
				//Start New Compound making statistics for its tautomers 
				curStruct = strNum;
				initFPTautomerStatistics(tokens);
			}
			else
			{	
				processFPLine(tokens); 
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Error on line " + curLine);
			System.out.println(e.toString());
		}
		
		return 0;
	}
	
	
	void processFPLine(String tokens[])
	{
		if (tokens.length != (descrStat0.length + 3))
		{
			System.out.println("Incorrect number of tokens on line " + curLine);
			return;
		}
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			if (!tokens[3+i].equals("0"))			
					descrStat0[i].valueSum += 1.0;
			descrStat0[i].nTautomers++;
		}
	}
	
	void processFPFirstLine(String tokens[])
	{
		if (tokens.length != (descrStat0.length + 3))
		{
			System.out.println("Incorrect number of tokens on line " + curLine);
			return;
		}
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			
			if (tokens[3+i].equals("0"))			
				descrStat0[i].originalValue = 0.0;
			else
				descrStat0[i].originalValue = 1.0;
		}
	}
	
	void initFPTautomerStatistics(String tokens[])
	{
		int n = 1;
		
		try 
		{
			n = Integer.parseInt(tokens[2]);
		}
		catch (Exception e)
		{	
			System.out.println("Incorrect number of tautomers in token 3  -->  '" + tokens[2] + "'  on line " + curLine);
		}
		
		curSMILES = tokens[1];
		for (int i = 0; i < descrStat0.length; i++)
		{
			descrStat0[i].valueSum = 0;
			descrStat0[i].valueSDSum = 0;
			descrStat0[i].nTautomers = 0;
		}		
		
		processFPFirstLine(tokens);
	}
	
	
	void finalizeFPTautomerStatistics()
	{	
		StringBuffer sb = new StringBuffer();
		sb.append(curStruct);
		sb.append(",");
		sb.append(curSMILES);
		sb.append(",");
		if (descrStat0[0].nTautomers == 0)
			descrStat0[0].nTautomers = 1; //This is performed since no tautomers are cycled and hence the counter is 0
		sb.append(descrStat0[0].nTautomers);
		
		if (descrStat0[0].nTautomers <= 1)
		{
			for (int i = 0; i < descrStat0.length; i++)
				sb.append(",0"); //automatically rsd = 0 is assigned
		}
		else
		{
			for (int i = 0; i < descrStat0.length; i++)
			{
				double pdb;    //percent (part) of bits which are different from the originbal bit 
				double pb1 = descrStat0[i].valueSum / descrStat0[i].nTautomers;  //this is the percent of 1-bits       
				
				if (descrStat0[i].originalValue == 0.0)
					pdb = pb1;  
				else
					pdb = (1.00 - pb1); //(1.00-pb1) is the percent of 0-bits i.e. those which are different from the original 1-bit in this case
					
				sb.append(",");
				sb.append(pdb);
			}
		}

		output(sb.toString() + endLine);
	}

	//----------------------------------------------------------------	
	
	int tautomerDescrStat2Order(String line)
	{
		
		if (curLine == 1)
		{
			initDescriptorStatistics0(line);  
			
			for (int i = 0; i < descrStat0.length; i++)
			{
				//System.out.println("name = " + descrStat0[i].name);
				descrStat0[i].valueSum = 0;
				descrStat0[i].valueSDSum = 0;
				descrStat0[i].nTautomers = 0;
			}
			
			//The first line does not contain the descriptor names
		}
		
		
		String tokens[] = line.split(descrTestSepareator);
		processDescrStat2Order(tokens);
		
		return 0;
	}
	
	
	void processDescrStat2Order(String tokens[])
	{
		if (tokens.length != (descrStat0.length + 3))
		{
			System.out.println("Incorrect number of tokens on line " + curLine);
			return;
		}
		
		for (int i = 0; i < descrStat0.length; i++)
		{
			try 
			{
				double dVal = Double.parseDouble(tokens[3+i]);
				//check for -999 value is not performed				
				
				if (dVal > 1.0e-8)  //otherwise some strange effects with NaN values are obtained
					descrStat0[i].valueSum += dVal;
				descrStat0[i].nTautomers++;
				
			}
			catch (Exception e)
			{	
				System.out.println("Incorrect token " + (3+i) + " -->  '" + tokens[3+i] + "'  on line " + curLine);
			}
		}
	}
	
	void finalizeDescrStat2Order()
	{
		for (int i = 0; i < descrStat0.length; i++)
		{
			double mean = descrStat0[i].valueSum / descrStat0[i].nTautomers; 
			output("" + (i+1) + "\t" + mean + endLine);
			//System.out.println("" + (i+1) + "\t" + mean + "   " + descrStat0[i].valueSum + "   " + descrStat0[i].nTautomers );
		}
	}
	
	/*
	int tautomerFPStat2Order(String line)
	{	
		return 0;
	}
	*/
	
	//------- command: tautomer-calc-descr-average --------------------
	
	
	int tautomerCalcDescrAverage(String line)
	{
		//System.out.println("" + curLine + "   " + line);
		
		
		if (curLine == 1)
		{	
			initDescriptorStatistics(line);
			output(getFirstOutputLine_CalcAverageDescr() + "\n");
			return 0;
		}
		
		String tokens[] = line.split(descrTestSepareator);
		
		
		int strNum = extractInt(tokens[0]);
		if (!extractError.equals(""))
		{
			System.out.println("Error on line " + curLine + "  token " + 1);
			System.out.println(extractError);
			return -1;
		}
		
		if (strNum != curStruct)
		{	
			if (curStruct != -1)
				if (numTautomers > 1)
					finalizeTautomerAverageCalculation();

			curStruct = strNum;
			
			numTautomers = extractInt(tokens[2]);
			if (!extractError.equals(""))
			{
				System.out.println("Error on line " + curLine + "  token " + 3);
				System.out.println(extractError);
				return -1;
			}
			
			if (numTautomers <= 1)
				outputAverageValuesForSingleTautomer(tokens);
			else	
				startNewTautomerAverageCalculation(tokens);
		}
		else
			addDescriptorValuesToAverage(tokens);			
	
		return 0;
	}
	
	void startNewTautomerAverageCalculation(String tokens[])
	{	
		if (tokens.length != (descrStat.length + 3))
		{
			System.out.println("Error on line " + curLine);
			System.out.println("Incorrect number of tokens!");
			return;
		}
		
		curTautomerSmiles = tokens[1];		
		
		for (int i = 0; i < descrStat.length; i++)
		{
			double value = extractDouble(tokens[i+3]);
			if (!extractError.equals(""))
			{
				System.out.println("Error on line " + curLine + "  token " + (i+4));  //error message is 1-base indexed 
				System.out.println(extractError);
				return;
			}
			descrStat[i].initWeightedAverageCalculation(10); //maxNumRanks = 10
			descrStat[i].originalValue = value;
			
			if (FlagDescrAverageForCloseEnergies)
			{
				descrStat[i].allValues.clear();
				descrStat[i].allRanks.clear();
			}
				
		}	
		
		for (int i = 0; i < descrStat.length; i++)		
			descrStat[i].originalMoleculeIndex = -1; 
			
	}
	
	void addDescriptorValuesToAverage(String tokens[])
	{
		if (tokens.length != (descrStat.length + 3))
		{
			System.out.println("Error on line " + curLine);
			System.out.println("Incorrect number of tokens!");
			return;
		}
		
		
		double rank = extractDouble(tokens[2]);
		if (!extractError.equals(""))
		{
			System.out.println("Error on line " + curLine + "  token 3");
			System.out.println(extractError);
			return;
		}
		
		boolean FlagAllValuesEqualToOriginal = true;
		
		for (int i = 0; i < descrStat.length; i++)
		{
			double value = extractDouble(tokens[i+3]);			
			if (!extractError.equals(""))
			{
				System.out.println("Error on line " + curLine + "  token " + (i+3));  //error message is 1-base indexed
				System.out.println(extractError);
				return;
			}
			
			
			if (FlagDescrAverageForCloseEnergies)
			{					
				descrStat[i].addNewRankValue_ClosesEnergy(value, rank);
				
				if (FlagAllValuesEqualToOriginal)  
					if (Math.abs(value - descrStat[i].originalValue) > 1.0E-8)
						FlagAllValuesEqualToOriginal = false;
			}
			else
				descrStat[i].calcAverageAddNewValue(value, rank);
		}
		
		
		//checkTautomerForOriginalMolecule(tokens); --> Not used for the moment
		if (FlagDescrAverageForCloseEnergies)
		{	
			if (FlagAllValuesEqualToOriginal)  
				for (int i = 0; i < descrStat.length; i++)				
					descrStat[i].originalMoleculeIndex = descrStat[i].nTautomers-1;   //nTautomers has been increased in the cycle
		}
		
	}
	
	void finalizeTautomerAverageCalculation()
	{
		if (FlagDescrAverageForCloseEnergies)
		{
			if (descrStat[0].originalMoleculeIndex == -1)
				return;  //This situation is counted asn an error i.e. it is not found a tautomer which is the original structure
		}
			
		StringBuffer sb = new StringBuffer();
		sb.append(curStruct);
		sb.append("," + curTautomerSmiles + "," + numTautomers);
			
		
		for (int i = 0; i < descrStat.length; i++)
		{
			if (FlagDescrAverageForCloseEnergies)
				descrStat[i].iterateValuesAndRerank_CloseEnergy();  
				
			sb.append("," + descrStat[i].originalValue);
			sb.append("," + descrStat[i].getAverageAll());
			for (int k = 0; k < Temperatures.length; k++)
				sb.append("," + descrStat[i].getWeightedAverageAll(k));

			sb.append("," + descrStat[i].getAverageTop(5));
			for (int k = 0; k < Temperatures.length; k++)
				sb.append("," + descrStat[i].getWeightedAverageTop(5,k));

			sb.append("," + descrStat[i].getAverageTop(10));
			for (int k = 0; k < Temperatures.length; k++)
				sb.append("," + descrStat[i].getWeightedAverageTop(10,k));
			
		}
		sb.append("\n");
		output(sb.toString());
	}
	
	void outputAverageValuesForSingleTautomer(String tokens[])
	{
		
		if (tokens.length != (descrStat.length + 3))
		{
			System.out.println("Error on line " + curLine);
			System.out.println("Incorrect number of tokens!");
			return;
		}
		
		int numAverageValues = 3*(1 + Temperatures.length);
		
		StringBuffer sb = new StringBuffer();
		sb.append(tokens[0]+"," +tokens[1]+"," +tokens[2]);
		for (int i = 0; i < descrStat.length; i++)
		{
			for(int k = 0; k < (numAverageValues+1); k++)  //+1 for the original descr value
				sb.append(","+tokens[i+3]);
		}
		sb.append("\n");
		output(sb.toString());
	}
	
	String getFirstOutputLine_CalcAverageDescr()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("strNum,SMILES,numOfTaut");
		
		for (int i = 0; i < descrStat.length; i++)
		{
			if (this.FlagDescrAverageForCloseEnergies)
			{	
				sb.append("," + descrStat[i].name);
				sb.append("," + descrStat[i].name+"-CE");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WCE-" +Temperatures[k]+"K");
				
				sb.append("," + descrStat[i].name+"-CE5");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WCE5-" +Temperatures[k]+"K");
				
				sb.append("," + descrStat[i].name+"-CE10");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WCE10-" +Temperatures[k]+"K");
				
			}
			else
			{	
				sb.append("," + descrStat[i].name);
				sb.append("," + descrStat[i].name+"-AV");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WAV-" +Temperatures[k]+"K");

				sb.append("," + descrStat[i].name+"-AV5");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WAV5-"+Temperatures[k]+"K");

				sb.append("," + descrStat[i].name+"-AV10");
				for (int k = 0; k < Temperatures.length; k++)
					sb.append("," + descrStat[i].name+"-WAV10-"+Temperatures[k]+"K");
			}
		}
		return sb.toString();
	}
	
	/*
	void checkTautomerForOriginalMolecule(String tokens[])
	{
		//not used for the moment
	}
	*/
	
	//---------------- statistics and comparison methods ----------------------
	
	
	int getBinIndex(int value, int bins[])
	{
		for (int i = 0; i < bins.length; i++)
		{
			if (value <= bins[i])
				return i;
		}
		return (bins.length-1); //last bin by default - it means the the value is larger the last bin value
	}
	
	int getBinIndex(double value, double bins[])
	{
		for (int i = 0; i < bins.length; i++)
		{
			if (value <= bins[i])
				return i;
		}
		return (bins.length-1); //last bin by default - it means the the value is larger the last bin value
	}
	
	
	//---------------
	
	void initStructureStatistics()
	{
		freqNumRingBins = new int [NumRingBins.length];
		freqStrSizeBins = new int [StrSizeBins.length];
		
		for (int  i = 0; i < freqNumRingBins.length; i++)
			freqNumRingBins[i] = 0;
		
		for (int  i = 0; i < freqStrSizeBins.length; i++)
			freqStrSizeBins[i] = 0;
	}
	
	int structureStatistics(String line)
	{	
		try
		{
			IMolecule mol = null;
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
			
			String smiles = line.trim();
			if (FlagFiltrateSmilesToLargestFragment)
			{
				smiles = this.getLargestFragment(smiles);
			}
			
			mol = sp.parseSmiles(smiles);
			
			int nAt = mol.getAtomCount();
			int nRing = mol.getBondCount() - mol.getAtomCount() + 1;
			
			int nAtBinIndex = getBinIndex(nAt, StrSizeBins);
			freqStrSizeBins[nAtBinIndex]++;
			
			int nRingBinIndex = getBinIndex(nRing,NumRingBins);
			freqNumRingBins[nRingBinIndex]++;
						
			output(line + "  " + nAt + "   " + nRing + endLine);
			
			//System.out.println(mol.getAtomCount());
		}	
		catch(Exception e){
			System.out.println("Exception at structut " + line );
			return (-1);
		}
		return 0;
	}
	
	void printStructureStatistics()
	{
		System.out.println("Structure Size Histogram:  bin   frequency");
		for (int i = 0; i < StrSizeBins.length; i++)
			System.out.println("  " + StrSizeBins[i] + "  " + freqStrSizeBins[i]);
		
		System.out.println("Number of Rings Histogram:  bin   frequency");
		for (int i = 0; i < NumRingBins.length; i++)
			System.out.println("  " + NumRingBins[i] + "  " + freqNumRingBins[i]);
	}
	
	//---------------
	
	void initEquivalenceStatistics()
	{	
		freqAbsNumUneqBins = new int [AbsNumUneqBins.length];
		freqRelNumUneqBins = new int [RelNumUneqBins.length];
		freqUneqDiffBins = new int [UneqDiffBins.length];
		
		
		for (int  i = 0; i < freqAbsNumUneqBins.length; i++)
			freqAbsNumUneqBins[i] = 0;
		
		for (int  i = 0; i < freqRelNumUneqBins.length; i++)
			freqRelNumUneqBins[i] = 0;
		
		for (int  i = 0; i < freqUneqDiffBins.length; i++)
			freqUneqDiffBins[i] = 0;
	}
	
	int equivalenceStatistics(String line)
	{	
		try
		{
			List<String> tokens = filterTokens(line.split(" "));
			int n = tokens.size();
			
			if (n != 4)
			{	
				System.out.println("Error at " + line);
				return -1;
			
			}

			int nTaut = Integer.parseInt(tokens.get(1));
			int nUneq = Integer.parseInt(tokens.get(2));
			double relNUneq = (100.0*nUneq)/nTaut;  //in percent
			double meanDiff = Double.parseDouble(tokens.get(3));
			
			//Updating the bins frequencies 
			int ANUBinIndex = getBinIndex(nUneq, AbsNumUneqBins);
			freqAbsNumUneqBins[ANUBinIndex]++;
			
			int RNUBinIndex = getBinIndex(relNUneq, RelNumUneqBins);
			freqRelNumUneqBins[RNUBinIndex]++;
			
			int diffBinIndex = getBinIndex(meanDiff, UneqDiffBins);
			freqUneqDiffBins[diffBinIndex]++;
			

		}	
		catch(Exception e){
			System.out.println("Exception at struct " + line + endLine + e.toString());
			return (-1);
		}
		
		return 0;
	}
	
	void printEquivalenceStatistics()
	{	
		System.out.println("Histogram(Absolute Number of Unequivalent Tautomers):  bin   frequency");
		for (int i = 0; i < AbsNumUneqBins.length; i++)
			System.out.println("  " + AbsNumUneqBins[i] + "  " + freqAbsNumUneqBins[i]);
		
		System.out.println("Histogram(Relative Number of Unequivalent Tautomers):  bin   frequency");
		for (int i = 0; i < RelNumUneqBins.length; i++)
			System.out.println("  " + RelNumUneqBins[i] + "  " + freqRelNumUneqBins[i]);
		
		System.out.println("Histogram(Unequivalent Mean Difference):  bin   frequency");
		for (int i = 0; i < UneqDiffBins.length; i++)
			System.out.println("  " + UneqDiffBins[i] + "  " + freqUneqDiffBins[i]);
		
	}
	
	
	int compareAlgorithms()
	{	
		int numStructsInStat = 0;
		//Creating bins
		//double RelDiffBins[] = {-50, -40, -30, -20,-15, -10, -9, -8, -7, -6,-5,-4,-3,-2,-1,0, 
		//						1,2,3,4, 5,6,7,8,9, 10, 15,  20, 30, 40, 50}; 
		
		double RelDiffBins[] = {-1, - 0.8, -0.6, -0.4, -0.2, -0.1, -0.05, -0.0001, 0, 
										0.0001, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1};
		int freqRelDiffBins [] = new int [RelDiffBins.length];
		for (int i = 0; i < freqRelDiffBins.length; i++)
			freqRelDiffBins[i] = 0;
		
		
		int n = 30;
		
		int DiffBins[] = new int[2*n+1];
		int freqDiffBins[] = new int [2*n+1];
				
		int bNum = 0;
		for (int i = -n; i<=n; i++)
		{	
			DiffBins[bNum] = i;
			freqDiffBins[bNum] = 0;
			bNum++;
		}
		
		
		//Bins used for tautomer distribution (single file test)
		//int DiffBins[] = {0,1,2,5,10,20,50,100,200};
		//int freqDiffBins[] = new int [DiffBins.length];
		
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			File file2 = new File(inFileName2);
			RandomAccessFile f2 = new RandomAccessFile(file2,"r");			
			long length2 = f2.length();
			
			n = 0;
			curProcessedStr = 0;
			
			while ((f.getFilePointer() < length) && (f2.getFilePointer() < length2) )
			{	
				n++;
				curLine = n;
				
				
				String line = f.readLine();
				String line2 = f2.readLine();				
				//System.out.println("line " + n + "\n" + line + "\n" + line2);
				
				
				if (n < nStartStr)
					continue;
				
				curProcessedStr++;
				
				if (nInputStr > 0)
					if (curProcessedStr > nInputStr) 
						break;
												
				
				
				int res = processLines_CompareAlgorithms(line, line2, 
								DiffBins,freqDiffBins, 
								RelDiffBins, freqRelDiffBins);
				if (res == 0)
					numStructsInStat++;
				
				//if (res == -3 || res == -4)
				//	System.out.println("XXXXXXXXXXXXXXXXXXXX");
				
				if (FlagCheckMemory)
				{
					checkMemory();				
					Runtime.getRuntime().gc();
				}	
				
				if (n % traceFrequency == 0)
				{	
					System.out.println(n);
					if (FlagCompareCanonicalTautomer)
						System.out.println("  nICT = " + numOfIdenticalCanonicalTautomers);
				}	
			}
			
			f.close();
			f2.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
		
		//Print bins and frequenecies
		
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("Histogram(Diff of Number of  Tautomers):  bin   frequency");
		for (int i = 0; i < DiffBins.length; i++)
			System.out.println("  " + DiffBins[i] + "  " + freqDiffBins[i]);
		
		System.out.println();
		System.out.println("Histogram(Relative Diff of Number of  Tautomers):  bin   frequency");
		for (int i = 0; i < RelDiffBins.length; i++)
			System.out.println("  " + RelDiffBins[i] + "  " + freqRelDiffBins[i]);
		
		System.out.println("\n Tested " + numStructsInStat + "  structs.");
		if (FlagCompareCanonicalTautomer)
			System.out.println("\n numOfIdenticalCanonicalTautomers = " + numOfIdenticalCanonicalTautomers);
		return 0;
	}
	
	int processLines_CompareAlgorithms(String line, String line2, 
			int diffBins[], int freqDiff[], double relBins[], int freqRel[] )
	{		
		List<String> tokens = filterTokens(line.split(separator1));
		List<String> tokens2 = filterTokens(line2.split(separator2));
		
		//System.out.println(line);System.out.println(line2);
		//System.out.println(" #### " + tokens.size() + "  " + tokens2.size());
		
		if (tokens.size() < numTokenAtFile1+1)
		{	
			if (tokens.get(numErrorToken).startsWith(errSubStr))
				System.out.println("XXXXXX");
			return -1;
		}
		
		
		if (tokens2.size() < numTokenAtFile2+1)
		{	
			if (tokens2.get(numErrorToken).startsWith(errSubStr))
				System.out.println("XXXXXX");
			return -2;
		}
		if (!tokens.get(0).equals(tokens2.get(0)))
		{
			System.out.println("Line paier with different structure numbers");
			System.out.println(line);
			System.out.println(line2);
			return -200;			
		}
		
		if (tokens.get(numErrorToken).startsWith(errSubStr))
			return -3;
		if (tokens2.get(numErrorToken).startsWith(errSubStr))
			return -4;
		
		
		
		try{
			String tok = tokens.get(numTokenAtFile1).trim();
			String tok2 = tokens2.get(numTokenAtFile2).trim();
			int val = Integer.parseInt(tok);
			int val2 = Integer.parseInt(tok2);
			
			if (FlagUseOnlyFirstAlgorithm)
				val2 = 0;
			
			int diff = val - val2;
			double relDiff = (1.0*diff)/((val+val2)/2.0);
			
			int diffB = getBinIndex(diff, diffBins);
			freqDiff[diffB]++;
			int relB = getBinIndex(relDiff, relBins);
			freqRel[relB]++;
			
			if (FlagCompareCanonicalTautomer)
			{
				tok = tokens.get(numTokenAtFile1+1).trim();
				tok2 = tokens2.get(numTokenAtFile2+1).trim();
				
				IMolecule mol = null;
				SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
				mol = sp.parseSmiles(tok);
				
				IMolecule mol2 = null;
				sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
				mol2 = sp.parseSmiles(tok2);
				
				IQueryAtomContainer query = SmartsHelper.getQueryAtomContainer(mol2, false);
				isoTester.setQuery(query);
				if (isoTester.hasIsomorphism(mol))
				{
					//System.out.println(tok);
					numOfIdenticalCanonicalTautomers++;
					//System.out.println("  nICT = " + numOfIdenticalCanonicalTautomers);
				}
				
			}
		}
		catch(Exception e)
		{
			return -100;
		}
		
		return 0;
	}
	
	
	
	
	public void fillTautomerExperimentalValues()
	{
		String splitter = ",";  //it is for csv file format
		String propertyName = "Activity";
		
		openOutputFile();
		
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			File file2 = new File(inFileName2);
			RandomAccessFile f2 = new RandomAccessFile(file2,"r");			
			long length2 = f2.length();
			
			String line;
			String newLine;
			int n = 0;
			String curPropertyValue = "0.0";			
			int curCompound = 0;
			
			if (f2.getFilePointer() < length2)
			{
				//reading first line (descriptor names) and then follows the first compound
				f2.readLine();
			}
			
			while (f.getFilePointer() < length)
			{	
				n++;								
				line = f.readLine();
				
				System.out.println("line " + n);
				
				
				if (n == 1)  //This is the first line with the descriptor names
				{					
					newLine = propertyName + "," + line;  
					output(newLine + endLine);
					continue;
				}
				
				if (line.trim().equals("")) //empty line
					continue;
				
				//Get the compound number for particular tautomer
				int split_pos = line.indexOf(splitter);
				if (split_pos == -1)
				{
					System.out.println("Error on line (incorrect splitting) " + n + endLine + line);
					continue;
				}
				
				String str_compNum = line.substring(0, split_pos);
				int tempN =  Integer.parseInt(str_compNum);
				if (tempN != curCompound)
				{
					//read next compound value
					if (f2.getFilePointer() < length2)
					{	
						curCompound++;
						String line2 = f2.readLine();
						String tokens[] = line2.split(splitter);
						curPropertyValue = tokens[tokens.length-1];
						System.out.println("Compound " + curCompound + "   property = " + curPropertyValue);
					}
					else
					{
						System.out.println("Compounds are finished!");
						break;
					}
				}
				
				newLine = curPropertyValue + "," + line;  
				output(newLine + endLine);
				
			}
			
			f.close();
			f2.close();
		}
		catch (Exception e)
		{	
			System.out.println("~~~~~~~~~~~");
			System.out.println(e.toString());
		}
		
		
		
		closeOutputFile();	
	}
	
	
	public void concatinateDescriptorFiles()
	{	
		openOutputFile();
		
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			int n = 0;
			
			while (f.getFilePointer() < length)
			{									
				String fileLine = f.readLine().trim();
				if (fileLine.equals(""))
					continue;
				
				n++;	
				System.out.println("Processing file " + fileLine);
				boolean FlagExcludeFirstLine = false;
				if (n==1)
					FlagExcludeFirstLine = true;
				addDescriptorFileToOutputFile(fileLine, FlagExcludeFirstLine);				
			}
			
			f.close();
			
		}
		catch (Exception e)
		{				
			System.out.println(e.toString());
		}
		
		closeOutputFile();	
	}
	
	public void addDescriptorFileToOutputFile(String fileName, boolean FlagExcludeFirstLine)
	{
		try
		{	
			File file = new File(fileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			int n = 0;
			
			while (f.getFilePointer() < length)
			{									
				String line = f.readLine().trim();
				n++;	
				
				
				if (n==1)
					if (FlagExcludeFirstLine)
						continue;
				
				output(line + endLine);
			}
			
			f.close();
			
		}
		catch (Exception e)
		{				
			System.out.println(e.toString());
		}
	}
	
	public void separateDescriptorWeightingSchemes()
	{	
		try
		{	
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			int n = 0;
			
						
			while (f.getFilePointer() < length)
			{									
				String fileLine = f.readLine().trim();
				if (fileLine.equals(""))
					continue;
				
				n++;	
				if ((n % 5) == 0)
					System.out.println("line " + n);
				
				if (n==1)
				{
					String schemes[] = analyzeDescritporsForSeparation(fileLine);
					if (schemes == null)
					{
						System.out.println("Incorrect descriptors and header line");
						break;
					}
					
					
					//Creating an output file for each weighting scheme and writing the first line
					fSchemes = new RandomAccessFile[schemes.length];
					for (int i = 0; i < fSchemes.length; i++)					{	
						fSchemes[i] = new RandomAccessFile(outFileName + "_"+schemes[i] + ".csv","rw");
						output(firstLineForSeparatedWeightingSchemes + endLine,fSchemes[i]);
					}	
					continue;
				}
						
				processLineToSeparateSchemes(fileLine);
			}
			
			if (fSchemes != null)
				for (int i = 0; i < fSchemes.length; i++)
					fSchemes[i].close();
			f.close();
			
		}
		catch (Exception e)
		{				
			System.out.println(e.toString());
		}
	}
	
	public String[] analyzeDescritporsForSeparation(String line)
	{
		StringBuffer sb = new StringBuffer();
		ArrayList<String> mySchemes = new ArrayList<String>();
		String tokens[] = line.split(",");
		if (tokens.length <= 3)
			return null;
		
		String headerToken = tokens[0]+","+tokens[1]+","+tokens[2];		
		sb.append(headerToken);
			
		String baseDescr = tokens[3];
		sb.append("," + baseDescr);
		mySchemes.add("");
		
		for (int i = 4; i < tokens.length; i++)
		{	
			if (tokens[i].startsWith(baseDescr))
			{	
				mySchemes.add(tokens[i].substring(baseDescr.length()));
			}
			else
				break;
		}
		
		int n = 3 + mySchemes.size();
		while (n < tokens.length)
		{
			baseDescr = tokens[n];
			sb.append("," + baseDescr);
			n += mySchemes.size();
		}			
				
		firstLineForSeparatedWeightingSchemes = sb.toString(); 
		return mySchemes.toArray(new String[]{});
	}
	
	public void processLineToSeparateSchemes(String line)
	{
		String tokens[] = line.split(",");
		
		String headerToken = tokens[0]+","+tokens[1]+","+tokens[2];
		for (int i = 0; i < fSchemes.length; i++)
			output(headerToken,fSchemes[i]);
			
		for (int i = 3; i < tokens.length; i++)
		{
			int index = (i-3)%fSchemes.length;
			output(","+tokens[i],fSchemes[index]);
		}
		
		for (int i = 0; i < fSchemes.length; i++)
			output(endLine,fSchemes[i]);
	}
	
	
	//--------------------------------------------------------
	
	public List<String> filterTokens(String tokens[])
	{	
		List<String> v = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!tokens[i].equals(""))
				v.add(tokens[i]);
		return v;
	}
	
	void setTautomerManager()
	{
		tman = new TautomerManager();
		tman.getKnowledgeBase().use15ShiftRules(true);
		tman.getKnowledgeBase().use17ShiftRules(false);
		tman.maxNumOfBackTracks = 10000;
		tman.FlagCalculateCACTVSEnergyRank = FlagUseCACTVSRank;
	}
	
	void checkMemory()
	{
		long totalMem = Runtime.getRuntime().totalMemory() / 1000000;
		long freeMem = Runtime.getRuntime().freeMemory() / 1000000;
		long maxMem = Runtime.getRuntime().maxMemory() / 1000000;
		
		long usedMem = totalMem - freeMem;
		
		System.out.println("usedMem = " + usedMem + "   freeMem = " + freeMem + "  totalMem = " + totalMem 
				+ "    maxMem = " + maxMem);
	}
	
	int extractInt(String token)
	{
		try
		{
			int value = Integer.parseInt(token);
			extractError = "";
			return value;			
		}
		catch(Exception e)
		{
			extractError = e.toString();
		}
		
		return 0;
	}
	
	double extractDouble(String token)
	{
		try
		{
			double value = Double.parseDouble(token);
			extractError = "";
			return value;			
		}
		catch(Exception e)
		{
			extractError = e.toString();
		}
		
		return 0.0;
	}
	
	
	
	//Helper classes
	
	public class DescriptorStatShort
	{
		public String name = "";		
		public double originalValue;
		public double valueSum = 0;
		public double valueSDSum = 0;
		public int nTautomers = 0;
	}
	
	public class DescriptorStatInfo
	{	
		public String name;
		public double originalValue;
		public double valueSum = 0;
		public double valueSDSum = 0;
		
		//variables for the weighting scheme based on probabilities calculated for different temperatures
		public double valueSumProbWeighted[] = new double[Temperatures.length];
		public double sumProbWeights[] = new double[Temperatures.length];
		public double topValues[];
		public double topRanks[];
		int nUsedTopRanks;
		int maxRankIndex;	//this is the worst rank
		int nTautomers;
		int originalMoleculeIndex;  //This is the index of the tautomer which is identical to the target molecule
		
		ArrayList<Double> allValues = new  ArrayList<Double>();
		ArrayList<Double> allRanks = new  ArrayList<Double>();
		
		
		public void initWeightedAverageCalculation(int maxNumRanks)
		{
			nTautomers = 0;
			valueSum = 0.0;
			for (int i = 0; i < valueSumProbWeighted.length;  i++)
			{	
				valueSumProbWeighted[i] = 0.0;
				sumProbWeights[i] = 0.0;
			}	
			topValues = new double[maxNumRanks];
			topRanks = new double[maxNumRanks];
			nUsedTopRanks = 0;
			maxRankIndex = -1;
		}
		
		public void calcAverageAddNewValue(double value, double rank)
		{
			nTautomers++;
			valueSum += value;
			
			for (int i = 0; i < valueSumProbWeighted.length;  i++)
			{	
				tautomerRanking.setT(Temperatures[i]);
				double p = tautomerRanking.getProbability(rank);
				valueSumProbWeighted[i] += p*value;
				sumProbWeights[i] += p;
			}
			addNewRankValue(value, rank);
		}
		
		public void addNewRankValue_ClosesEnergy(double value, double rank)
		{
			nTautomers++;
			allRanks.add(rank);
			allValues.add(value);
		}
		
		public void iterateValuesAndRerank_CloseEnergy()
		{
			if (originalMoleculeIndex == -1)
			{
				System.out.println("originalMoleculeIndex = -1  for " + curLine);
				originalMoleculeIndex = 0;
			}
				
			//Reranking and registering statistics info 	
			nTautomers = 0;
			double originalMolRank = allRanks.get(originalMoleculeIndex);
			double newRank = 0.0;
			for (int i = 0; i < allRanks.size(); i++)
			{
				newRank = Math.abs(originalMolRank - allRanks.get(i));
				calcAverageAddNewValue(allValues.get(i), newRank);
			}
		}
		
		void addNewRankValue(double value, double rank)
		{
			int n = topRanks.length;
			if (nUsedTopRanks < n)
			{				
				//add new rank and value
				topRanks[nUsedTopRanks] = rank;
				topValues[nUsedTopRanks] = value;
				
				//determine the new index for the maximal rank
				if (maxRankIndex == -1)
					maxRankIndex = nUsedTopRanks;  //which should be zero in this case
				else
				{	
					if (topRanks[maxRankIndex] < rank)  
						maxRankIndex = nUsedTopRanks;
				}	
				
				nUsedTopRanks++;
				
				return;
			}
			
			if (topRanks[maxRankIndex] < rank)
			{	
				topRanks[maxRankIndex] = rank;
				topValues[maxRankIndex] = value;
				
				//find new maximal rank index
				maxRankIndex = 0;				
				for (int i = 1; i < n; i++)
					if (topRanks[maxRankIndex] < topRanks[i])
						maxRankIndex = i;
			}
			
		}
		
		public void sortRanks()
		{
			if (nUsedTopRanks <= 1)
				return;
			
			double tmp;
			for (int k = nUsedTopRanks-1; k>=0; k--)
				for (int j = 0; j < k; j++)
					if (topRanks[j] >  topRanks[j+1])  //the maximal is put at the end
					{
						tmp = topRanks[j];
						topRanks[j] = topRanks[j+1];
						topRanks[j+1] = tmp;
						
						tmp = topValues[j];
						topValues[j] = topValues[j+1];
						topValues[j+1] = tmp;
					}
		}
		
		public double getAverageAll()
		{
			return valueSum / nTautomers; 
		}
		
		public double getWeightedAverageAll(int tIndex)
		{
			return valueSumProbWeighted[tIndex] / sumProbWeights[tIndex];
		}
		
		
		public double getAverageTop(int n)
		{	
			int n1;
			if (n < nUsedTopRanks)  //This is possible when there are fewer tautomers
				n1 = n;
			else
				n1 = nUsedTopRanks;
			
			double sum = 0;
			for (int i = 0; i < n1; i++)
				sum+= topValues[i];
								
			return sum / n1; 
		}
		
		public double getWeightedAverageTop(int n, int tIndex)
		{	
			int n1;
			if (n < nUsedTopRanks)
				n1 = n;
			else
				n1 = nUsedTopRanks;
			
			tautomerRanking.setT(Temperatures[tIndex]); //currently
			
			double sum = 0;
			double weightSum = 0;
			double p;
			for (int i = 0; i < n1; i++)
			{	
				p = tautomerRanking.getProbability(topRanks[i]);
				sum+= topValues[i] * p;
				weightSum += p;
			}					
			return sum / weightSum;			 
		}
		
	}
	
	
	public String getLargestFragment(String smiles)
	{
		String tok1[] = smiles.split("\\.");    //"." is a special symbol in java
		
					
		//Take the longest token is used as a main fragment (component)		
		String smi;
		if (tok1.length > 0)
		{	
			smi = tok1[0];
			for (int i = 1; i < tok1.length; i++)
				if (smi.length() < tok1[i].length())
					smi = tok1[i];
		}
		else
			smi = smiles;
		
		return smi;
	}
	
}
