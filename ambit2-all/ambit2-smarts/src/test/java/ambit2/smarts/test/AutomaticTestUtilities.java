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
	
	String outFile = null;
	String inFile = null;
	String dbFile = null;
	String command = null;
	String configFile = null;
	
	
	int maxNumSeqSteps = 4;
	int maxStrSize = 30;
	int nDBStr = 1000;
	int nInputStr = 0;
	int nOutputStr = 0;
	int portionSize = 100;
	
	
	
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
				inFile = opt.value; 
		}
		
		opt = getOption("o", options);
		if (opt != null)
		{
			if (opt.value != null)
				outFile = opt.value; 
		}	
		
		opt = getOption("db", options);
		if (opt != null)
		{
			if (opt.value != null)
				dbFile = opt.value; 
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
			cof.produceRandomStructsFromMDL(dbFile, maxNumSeqSteps, nDBStr, vStr, outFile);
			return(0);
		}
		
		if (command.equals("exhaustive-str"))
		{
			System.out.println("Running exhaustive generation structures:");
			ChemObjectFactory cof = new ChemObjectFactory();
			Vector<StructInfo> vStr = new Vector<StructInfo>();			
			cof.produceStructsFromMDL(dbFile, maxNumSeqSteps, nDBStr, maxStrSize, vStr, outFile);
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
	
	void iterateInputFileQueries()
	{
		try
		{	
			File file = new File(inFile);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			Vector<String> lines = new Vector<String>();
			Vector<String> allSmiles = new Vector<String>();
			Vector<Integer> allStat = new Vector<Integer>();
			int n = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				//System.out.print(" " + n);
				String line = f.readLine();
				lines.add(line);
				if (lines.size() % portionSize == 0)
				{
					
					System.out.println("line " + n);
					/*
					Vector<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
					for (int i = 0; i < smiles.size(); i++)
					{
						allSmiles.add(smiles.get(i));
						allStat.add(stat.get(i));
					}
					smiles.clear();
					saveStructStatistics(allSmiles, allStat, outFile);
					*/
				}
			}
			
			/*
			//What left from smiles vector is processed as well
			if (!smiles.isEmpty())
			{	
				Vector<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
				for (int i = 0; i < smiles.size(); i++)
				{
					allSmiles.add(smiles.get(i));
					allStat.add(stat.get(i));
				}			
				saveStructStatistics(allSmiles, allStat, outFile);
			}
			*/
			
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	int doSSS_Ambit()
	{
		return(0);
	}
	
	
}
