package ambit2.smarts.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import ambit2.smarts.ChemObjectFactory;
import ambit2.smarts.StructInfo;


//This class provides utilities for automatic testing of Substructure Searching algorithms
public class AutomaticTestUtilities 
{	
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};
	
	public static final int LPM_SSS_AMBIT = 0;
	public static final int LPM_SSS_CDK = 1;
	public static final int LPM_SSS_AMBIT_CDK = 2;
	
	
	public static final int STAT_TYPE_SSS_TIME_SINGLE_DBSTR = 1;
	public static final int STAT_TYPE_SSS_TIME_ENTIRE_DB = 2;
	public static final int STAT_TYPE_OCCURENCES = 10;
	
	
	String outFileName = null;
	String inFileName = null;
	String dbFileName = null;
	String command = null;
	String configFileName = null;
		
	RandomAccessFile outFile = null;
	
	int maxNumSeqSteps = 4;
	int maxStrSize = 30;
	int nDBStr = 1000;
	int nInputStr = 0;
	int nOutputStr = 0;
	int portionSize = 100;
	int lineProcessMode = 0;
	int statisticsType = STAT_TYPE_SSS_TIME_SINGLE_DBSTR;
		
	
	
	public static void main(String[] args)
	{
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		atu.handleArguments(args);		
		
		//atu.handleArguments(new String[] {"-db","/einecs_structures_V13Apr07.sdf", "-o","/test-out2.txt", 
		//		"-nDBStr", "100", "-maxSeqStep", "10", "-c", "exhaustive-str" });
		
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
		
		opt = getOption("maxStrSize", options);
		if (opt != null)
		{
			if (opt.value != null)
			{	
				try
				{
					Integer intObj = new Integer(0);
					this.maxStrSize = Integer.parseInt(opt.value);
				}
				catch(Exception e)
				{
					System.out.println("Incorrect maxStrSize value: "+ e.toString());
				}
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
			cof.produceRandomStructsFromMDL(dbFileName, maxNumSeqSteps, nDBStr, vStr, outFileName);
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
			System.out.println("Running sss with Ambit isomorphims:");
			lineProcessMode = LPM_SSS_AMBIT;
			iterateInputFile();
			return(0);
		}
		
		if (command.equals("sss-cdk"))
		{
			System.out.println("Running sss with CDK isomprphims algorithm:");
			lineProcessMode = LPM_SSS_CDK;
			iterateInputFile();
			return(0);
		}
		
		if (command.equals("sss-ambit-cdk"))
		{
			System.out.println("Running sss with Ambit Parser and CDK isomprphims algorithm:");
			lineProcessMode = LPM_SSS_CDK;
			iterateInputFile();
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
		System.out.println("-c            command: ");
		System.out.println("                 random-str       generates random structures");
		System.out.println("                 exhaustive-str   generates structures exhaustively");
		System.out.println("                 sss-ambit        substructure searching with Ambit Algorithm");
		System.out.println("                 sss-cdk          substructure searching with CDK Parser and Algorithm");
		System.out.println("                 sss-ambit-cdk    substructure searching with Ambit Parser and CDK Algorithm");
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
	
	
	//Substructure search comparison and statistics utilities
	
	int openOutputFile()
	{
		try
		{
			File file = new File(outFileName);
			RandomAccessFile outFile = new RandomAccessFile(file,"rw");
			outFile.setLength(0);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
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
				System.out.print(" " + n);
				String line = f.readLine();
				System.out.print("line " + n + "  " + line);
				processLine(line);
			}
			
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	
	int processLine(String line)
	{
		return(0);
	}
	
	
	int doSSS_Ambit()
	{
		return(0);
	}
	
	
}
