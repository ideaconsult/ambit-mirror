package ambit2.smarts.test;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;



import ambit2.core.io.MyIteratingMDLReader;
import ambit2.smarts.ChemObjectFactory;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.StructInfo;


//This class provides utilities for automatic testing of Substructure Searching algorithms
public class AutomaticTestUtilities 
{	
	
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};
	
		
	class StatisticsData
	{
		String info = "";
		int numObjects = 0;
		double sum = 0.0;
		//double average = 0.0;
		//double std = 0.0;
		
		public void addValue(double value)
		{
			sum += value;
			numObjects++;
		}
		
		public double getAverage()
		{
			if (numObjects == 0)
				return(0);
			else
				return(sum / numObjects);
		}
	};
	
	
	public static final int LPM_SSS_AMBIT = 0;
	public static final int LPM_SSS_CDK = 1;
	public static final int LPM_SSS_AMBIT_CDK = 2;
	public static final int LPM_SSS_CDK_AMBIT = 3;
	public static final int LPM_SSS_AMBIT_SMSD = 4;
	public static final int LPM_SSS_CDK_SMSD = 5;
	public static final int LPM_SSS_ALL = 100;
	public static final int LPM_PARSERS_ALL = 101;
	public static final int LPM_CALC_STAT = 102;
	public static final int LPM_SIZE_STAT = 103;
	
	
	public static final int STAT_SINGLE_DBSTR = 1;
	public static final int STAT_ENTIRE_DB = 2;
	public static final int STAT_OCCURENCES = 10;
	
	String endLine = "\r\n";
	String outFileName = "";
	String inFileName = "";
	String dbFileName = "";
	String command = "";
	String configFileName = "";
		
	RandomAccessFile outFile = null;
	
	int maxNumSeqSteps = 4;
	int maxStrSize = 30;
	int minGenStrSize = 5;
	int nDBStr = 1000;
	int nInputStr = 10;
	int nOutputStr = 0;
	int portionSize = 100;
	int lineProcessMode = 0;
	int statisticsType = STAT_SINGLE_DBSTR;
		
	
	boolean FlagStat_SingleDBStr_Ambit = false;       //ambit parser and isomorphims
	boolean FlagStat_SingleDBStr_CDK = false;         //cdk parser and isomorphims
	boolean FlagStat_SingleDBStr_Ambit_CDK = false;   //ambit parser + cdk isomorphims
	boolean FlagStat_SingleDBStr_CDK_Ambit = false;	  //cdk parser + ambit isomorphism
	boolean FlagStat_SingleDBStr_Ambit_SMSD = false;   //ambit parser + SMSD isomorphims
	boolean FlagStat_SingleDBStr_CDK_SMSD = false;	  //cdk parser + SMSD isomorphism
	
	
	SmartsParser spAmbit = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	
	//Statistics variables
	String statLinePrefix = "###";
	String[] statMethods;
	StatisticsData statAllObjs[];
	int binsStrSize[] =  {5,10,20,30,40,50};
	Vector<StatisticsData[]> statBins = new Vector<StatisticsData[]>();
	int statCurSrtSize = 0;
	int statCurBinIndex = 0;
	
	
	
	public static void main(String[] args)
	{
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		//atu.handleArguments(args);		
		
		atu.handleArguments(new String[] {"-db","/einecs_structures_V13Apr07.sdf", "-i","D:/out-sss-test-db2000.txt", 
				"-o","/output.txt","-nDBStr", "5000", "-maxSeqStep", "40", "-c", "calc-stat" });
		
		//atu.produceRandomStructures();
	}
	
	
	public int handleArguments(String[] args)
	{
		if (args.length == 0)
		{	
			printHelp();
			return(0);
		}	
		
		Vector<CmdOption> options = extractOptions(args);
		//for (int i = 0; i < v.size(); i++)
		//	System.out.println(v.get(i).option + "  " +v.get(i).value);
		
		CmdOption opt;
		
		opt = getOption("i", options);
		if (opt != null)
		{
			if (opt.value != null)
				inFileName = opt.value; 
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
		
		opt = getOption("nDBStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nDBStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nDBStr value: "+ e.toString());
				}
			}	
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
				}
			}	
		}
		
		opt = getOption("nOutStr", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					nOutputStr = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect nOutStr value: "+ e.toString());
				}
			}	
		}
		
		opt = getOption("maxSeqStep", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					this.maxNumSeqSteps = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect maxSeqStep value: "+ e.toString());
				}
			}	
		}
		
		opt = getOption("statType", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				int stT = -1;
				if (opt.value.equals("single-str"))
					stT = STAT_SINGLE_DBSTR;
				
				if (opt.value.equals("entire-db"))
					stT = STAT_ENTIRE_DB;
				
				if (stT == -1)
					System.out.println("Incorrect statistics type: " + opt.value);
				else
					statisticsType = stT;
			}	
		}
		
		handleCommand();
		
		return(0);
	}
	
	public int handleCommand()
	{
		if (command == null)
			return(0);
			
		
		if (command.equals("random-str"))
		{
			System.out.println("Running generation of random structures:");
			ChemObjectFactory cof = new ChemObjectFactory();
			Vector<StructInfo> vStr = new Vector<StructInfo>();
			cof.produceRandomStructsFromMDL(dbFileName, maxNumSeqSteps, minGenStrSize, nDBStr, vStr, outFileName);
			return(0);
		}
		
		if (command.equals("exhaustive-str"))
		{
			System.out.println("Running exhaustive generation structures:");
			ChemObjectFactory cof = new ChemObjectFactory();
			Vector<StructInfo> vStr = new Vector<StructInfo>();			
			cof.produceStructsFromMDL(dbFileName, maxNumSeqSteps, nDBStr, maxStrSize, vStr, outFileName);
			return(0);
		}
		
		if (command.equals("sss-ambit"))
		{
			System.out.println("Running sss with Ambit Parser and Isomorphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_AMBIT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("sss-cdk"))
		{
			System.out.println("Running sss with CDK Parser and Isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_CDK;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("sss-ambit-cdk"))
		{
			System.out.println("Running sss with Ambit Parser and CDK isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_AMBIT_CDK;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("sss-cdk-ambit"))
		{
			System.out.println("Running sss with CDK Parser and Ambit isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_CDK_AMBIT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("sss-all"))
		{
			System.out.println("Running sss with all isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_ALL;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("parsers-all"))
		{
			System.out.println("Running comparison of the SMARTS parsers: Ambit, CDK");
			openOutputFile();			
			lineProcessMode = LPM_PARSERS_ALL;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}		
		
		if (command.equals("calc-stat"))
		{
			System.out.println("Calculates statistics");
			openOutputFile();
			lineProcessMode = LPM_CALC_STAT;
			//iterateInputFile();
			makeStatistics(inFileName);
			closeOutputFile();
			return(0);
		}
		
		if (command.equals("size-stat"))
		{
			System.out.println("Calculates statistics");
			openOutputFile();
			lineProcessMode = LPM_SIZE_STAT;
			iterateInputFile();
			closeOutputFile();
			return(0);
		}
		
		
		System.out.println("Unknown command: " + command);
		
		return(-1);
	}
	
	
	
	public void printHelp()
	{
		System.out.println("Program for automatic testing of structure searching algorithms");
		System.out.println("-h            print help info");
		System.out.println("-i            input file");
		System.out.println("-o            output file");
		System.out.println("-db           db file");
		System.out.println("-cfg          config file");
		System.out.println("-nDBStr       the number of used structures from DB");
		System.out.println("-nInpStr      the number of used input structures");
		System.out.println("-nOutStr      the number of used output structures");	
		System.out.println("-maxSeqStep   maximal number of sequence steps");
		System.out.println("-maxStrSize   maximal size of the generated structures");
		System.out.println("-statType     the type of statistics saved in the output file");
		System.out.println("                 single-str       ");
		System.out.println("                 entire-db        ");
		System.out.println("-c            command: ");
		System.out.println("                 random-str       generates random structures");
		System.out.println("                 exhaustive-str   generates structures exhaustively");
		System.out.println("                 sss-ambit        substructure searching with Ambit Aprser and Algorithm");
		System.out.println("                 sss-cdk          substructure searching with CDK Parser and Algorithm");
		System.out.println("                 sss-ambit-cdk    substructure searching with Ambit Parser and CDK Algorithm");
		System.out.println("                 sss-cdk-ambit    substructure searching with CDK Parser and Ambit Algorithm");
		System.out.println("                 sss-all          substructure searching with all algorithms");
		System.out.println("                 parsers-all      comparison of the parsing time");
		System.out.println("                 cals-stat        calculates statistics from previous tests");
		System.out.println("                 size-stat        calculates the sizes of input structures");
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
			while (f.getFilePointer() < length)
			{	
				n++;
				String line = f.readLine();
				System.out.println("line " + n + "  " + line);
				processLine(line.trim());
			}
			
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
	}
	
	
	//Substructure search comparison and statistics utilities
	
	int processLine(String line)
	{
		if (lineProcessMode == LPM_PARSERS_ALL)
		{
			compareParsers(line);
			return(0);
		}
		
		if (statisticsType == STAT_SINGLE_DBSTR)
		{
			switch(this.lineProcessMode)
			{
			case LPM_SSS_AMBIT:			
				FlagStat_SingleDBStr_Ambit = true;
				sss_SingleDBStrStat(line);
				break;
			case LPM_SSS_CDK:
				FlagStat_SingleDBStr_CDK = true;
				sss_SingleDBStrStat(line);
				break;	
			case LPM_SSS_AMBIT_CDK:
				FlagStat_SingleDBStr_Ambit_CDK = true;
				sss_SingleDBStrStat(line);
				break;
			case LPM_SSS_CDK_AMBIT:
				FlagStat_SingleDBStr_CDK_Ambit = true;
				sss_SingleDBStrStat(line);
				break;	
				
			case LPM_SSS_ALL:
				FlagStat_SingleDBStr_Ambit = true;
				FlagStat_SingleDBStr_CDK = true;
				FlagStat_SingleDBStr_Ambit_CDK = true;
				FlagStat_SingleDBStr_CDK_Ambit = true;
				FlagStat_SingleDBStr_Ambit_SMSD = true;
				FlagStat_SingleDBStr_CDK_SMSD = true;
				sss_SingleDBStrStat(line);
				break;
				
			case LPM_CALC_STAT:
				//It is not done here.
				//A separate input file iteration function is used.
				break;
				
			case LPM_SIZE_STAT:
				processLineForSizeStatistics(line);
				break;	
			}
			
			
			
			return(0);
		}
		
		
		if (statisticsType == STAT_ENTIRE_DB)
		{
			switch(this.lineProcessMode)
			{
			case LPM_SSS_AMBIT:			
				sss_Ambit(line);
				break;
			case LPM_SSS_CDK:
				sss_CDK(line);
				break;	
			case LPM_SSS_AMBIT_CDK:
				sss_Ambit_CDK(line);
				break;
			}
		}
		
		return(0);
	}
	
	
	int sss_Ambit(String line)
	{	
		try
		{
			IChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(dbFileName),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				record++;				
				if (record > this.nDBStr)
					break;
				
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					
					//TODO
										
					//System.out.println("record " + record+ "  " + vStr.size());
				}
			}	
		}
		catch(Exception e){
			System.out.println(e.toString());
			return(-1);
		}
		
		return(0);
	}
	
	int sss_CDK(String line)
	{
		return(0);
	}
	
	int sss_Ambit_CDK(String line)
	{
		return(0);
	}
	
	int sss_SingleDBStrStat(String line)
	{
		//Performs statistics for each structure from the DB
		//It is applied for several algorithms simultaneously
		
		long startTime, endTime;
		long timeAmbit = 0;
		long timeCDK = 0;
		long timeAmbitCDK = 0;
		long timeCDKAmbit = 0;
		long timeAmbitSMSD = 0;
		long timeCDKSMSD = 0;
		
		//Ambit parser
		QueryAtomContainer query_ambit  = spAmbit.parse(line);
		spAmbit.setNeededDataFlags();
		String errorMsg = spAmbit.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return -1;
		}	
		
		//CDK parser
		QueryAtomContainer query_CDK = null;
		try
		{	
			query_CDK  =  SMARTSParser.parse(line);
		}
		catch(Exception e)
		{
			System.out.println("CDK parsing error: " + e.toString());
			return -1;
		}
		
		//Initial line in the output
		output("###test  Ambit  CDK  AmbitCDK  CDKAmbit  AmbitSMSD CDKSMSD " +  endLine);
		
		
		int sssResult = 0;
		boolean bondSensitive = true;
        boolean removeHydrogen = true;
		Isomorphism comparisonSMSD = new Isomorphism(Algorithm.SubStructure, bondSensitive);

				
		output("###  " + query_ambit.getAtomCount() + "   "+line + endLine);
		
		try
		{
			IChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(dbFileName),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				record++;				
				if (record > this.nDBStr)
					break;
				
				
				
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					
					if (FlagStat_SingleDBStr_Ambit)
					{
						isoTester.setQuery(query_ambit);
						
						startTime = System.nanoTime();
						spAmbit.setSMARTSData(mol);
						boolean hasIso = isoTester.hasIsomorphism(mol);
						endTime = System.nanoTime();
						timeAmbit = endTime - startTime;
						if (hasIso)
							sssResult = 1;
						else
							sssResult = 0;
						//System.out.println("record " + record+ "  " + hasIso + "   " + startTime + "  " + endTime);
					}
					
					
					if (FlagStat_SingleDBStr_CDK)
					{
						startTime = System.nanoTime();
						boolean res = UniversalIsomorphismTester.isSubgraph(mol, query_CDK);
						endTime = System.nanoTime();
						timeCDK = endTime - startTime;
					}
					
					if (FlagStat_SingleDBStr_Ambit_CDK)
					{
						startTime = System.nanoTime();
						spAmbit.setSMARTSData(mol);
						boolean res = UniversalIsomorphismTester.isSubgraph(mol, query_ambit);
						endTime = System.nanoTime();
						timeAmbitCDK = endTime - startTime;
					}
					
					if (FlagStat_SingleDBStr_CDK_Ambit)
					{
						isoTester.setQuery(query_CDK);
						startTime = System.nanoTime();
						boolean hasIso = isoTester.hasIsomorphism(mol);
						endTime = System.nanoTime();
						timeCDKAmbit = endTime - startTime;
					}
					
					if (FlagStat_SingleDBStr_Ambit_SMSD)
					{	
						try
						{
							startTime = System.nanoTime();
							spAmbit.setSMARTSData(mol);
							comparisonSMSD.init(query_ambit,mol, removeHydrogen,true);
							//comparisonSMSD.setChemFilters(false, false, false);
							//boolean res = comparisonSMSD.isSubgraph();
							endTime = System.nanoTime();						
							timeAmbitSMSD = endTime - startTime;
							//System.out.println("record " + record+ "  " + res + "   " + startTime + "  " + endTime);
						}
						catch(Exception e)
						{
							System.out.println("SMSD error: " );
							continue;
						}
					}
					
					if (FlagStat_SingleDBStr_CDK_SMSD)
					{	
						try
						{
							startTime = System.nanoTime();							
							comparisonSMSD.init(query_CDK,mol, removeHydrogen,true);							
							endTime = System.nanoTime();						
							timeCDKSMSD = endTime - startTime;
							//System.out.println("record " + record+ "  " + res + "   " + startTime + "  " + endTime);
						}
						catch(Exception e)
						{
							System.out.println("SMSD error: " );
							continue;
						}
					}
					
					
					//TODO
					
					output("db-" + record+"  "+mol.getAtomCount()+"  " + sssResult + "       "  
							+ timeAmbit + "  " + timeCDK + "  " + timeAmbitCDK + "  " 
							+timeCDKAmbit + "  " +timeAmbitSMSD + "  " +timeCDKSMSD +  endLine);
										
					//System.out.println("record " + record+ "  " + mol.getAtomCount());
				}
			}	
		}
		
		catch(Exception e){
			System.out.println(e.toString());
			return(-1);
		}
		
		return(0);
		
	}
	
	
	int compareParsers(String line)
	{		
		long timeAmbit, timeCDK;
		long startTime, endTime;
		boolean Flag_OK = true;
		
		//Ambit parser
		startTime = System.nanoTime();
		QueryAtomContainer query_ambit  = spAmbit.parse(line);
		spAmbit.setNeededDataFlags();
		endTime = System.nanoTime();
		timeAmbit = endTime - startTime;
		
		
		//CDK parser
		try
		{
			startTime = System.nanoTime();
			QueryAtomContainer query_CDK  =  SMARTSParser.parse(line);
			endTime = System.nanoTime();
			timeCDK = endTime - startTime;
		}
		catch(Exception e)
		{
			System.out.println("CDK parsing error: " + e.toString());
			Flag_OK = false;
			timeCDK = 0;
		}
		
		
		if (Flag_OK)
		{	
			String out_line = line + "  " + timeAmbit + "  "+timeCDK; 
			output(out_line + endLine);
			System.out.println(out_line);
		}
		
		return(0);
	}
	
	
	public int makeStatistics(String fname)
	{			
		try
		{	
			File file = new File(fname);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			int curStatLine = 1;
			String line = f.readLine();
			int res = processFirstLineStatistics(line);			
			if (res != 0)
			{
				System.out.println("Error at the initializing line " + curStatLine + "  "+line);
				return(-1);
			}	
			
			
			while (f.getFilePointer() < length)
			{	
				curStatLine++;
				if ((curStatLine % 1000) == 0)
					System.out.println("line "+ curStatLine);
				
				if (curStatLine > 10000)
					break;
				
				line = f.readLine();				
				res = processLineStatistics(line);
				
				if (res < 0)
				{
					//Errors >= 1 do not stop the statistics process 
					System.out.println("Error at line " + curStatLine + "  "+line);
					break;
				}
			}
			
			f.close();
			
			
			
			
			//Print statistics
			output("StrSize" + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statMethods[k]+"\t");
			output(endLine);
			
			output("all" + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(this.statAllObjs[k].getAverage()+"\t");
			output(endLine);
			
			for (int i = 0; i < binsStrSize.length ; i++)
			{	
				output(binsStrSize[i] + "\t");
				
				for (int k = 0; k < statMethods.length; k++)
					output(statBins.get(i)[k].getAverage()+"\t");
				output(endLine);
			}	
			
			
			
		}
		catch(Exception e)
		{
			System.out.println("Statistic file error: " + e.toString());
			e.printStackTrace();
		}
		
		return(0);
	}
	
	
	public int processFirstLineStatistics(String line)
	{		
		//The statistics process is initialized by the first line data (methods info/names)
		
		Vector<String> tokens = filterTokens(line.split(" "));
		
		int n = tokens.size()- 1;
		if (n <= 0)
			return -1;
		
		statMethods = new String[n];
		for (int k = 0; k < n; k++)	
		{	
			statMethods[k] = tokens.get(k+1);
			System.out.println(statMethods[k]);
		}	
		
		statAllObjs = new StatisticsData[n];
		for (int k = 0; k < n; k++)
			statAllObjs[k] = new StatisticsData();
		
		
		for (int i = 1; i <= binsStrSize.length +1; i++)
		{	
			StatisticsData bStat[] = new StatisticsData[n];
			for (int k = 0; k < n; k++)
				bStat[k] = new StatisticsData();
			
			statBins.add(bStat);
		}	
				
		return(0);
	}
	
	
	public int processLineStatistics(String line)
	{
		//System.out.println("Processing line: " + line);
		
		Vector<String> tokens = filterTokens(line.split(" "));
		
		try
		{
			if (line.startsWith("###"))
			{
				if (!tokens.get(0).equals("###"))  //This is needed since inside the test file initilizin line is repeted
					return(0);
					
				statCurSrtSize =  Integer.parseInt(tokens.get(1));
				statCurBinIndex = getBinIndex(statCurSrtSize);
			}
			else
			{
				if (tokens.size() != (3 + statMethods.length))
				{
					System.out.println("Wrong number of tokens at line " + line);
					return(1);
				}

				//Handling the time for each method
				for (int i = 0; i < statMethods.length; i++)
				{
					long methodTime =  Long.parseLong(tokens.get(3+i));						
					statAllObjs[i].addValue(methodTime);
					statBins.get(statCurBinIndex)[i].addValue(methodTime);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error at line " + line);
			//e.printStackTrace();
			return(2);
		}
		
		return(0);
	}
	
	
	public Vector<String> filterTokens(String tokens[])
	{
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!tokens[i].equals(""))
				v.add(tokens[i]);
		return v;
	}
	
	public int getBinIndex(int value)
	{
		int binIndex = binsStrSize.length;
		for (int i = 0; i < binsStrSize.length; i++)
		{	
			if (value <= binsStrSize[i])
			{	
				binIndex = i;
				break;
			}
		}
		
		return binIndex;
	}
	
	
	public int processLineForSizeStatistics(String line)
	{
		QueryAtomContainer query_ambit  = spAmbit.parse(line);
		output(line+"  "+query_ambit.getAtomCount() + endLine);
		System.out.println(line+"  "+query_ambit.getAtomCount());
		return(0);
	}
	
	
	
}
