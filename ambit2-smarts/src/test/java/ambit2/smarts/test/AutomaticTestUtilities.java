package ambit2.smarts.test;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

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
	
	public static final int LPM_SSS_AMBIT = 0;
	public static final int LPM_SSS_CDK = 1;
	public static final int LPM_SSS_AMBIT_CDK = 2;
	public static final int LPM_SSS_ALL = 100;
	
	
	
	public static final int STAT_SINGLE_DBSTR = 1;
	public static final int STAT_ENTIRE_DB = 2;
	public static final int STAT_OCCURENCES = 10;
	
	
	String outFileName = "";
	String inFileName = "";
	String dbFileName = "";
	String command = "";
	String configFileName = "";
		
	RandomAccessFile outFile = null;
	
	int maxNumSeqSteps = 4;
	int maxStrSize = 30;
	int nDBStr = 1000;
	int nInputStr = 10;
	int nOutputStr = 0;
	int portionSize = 100;
	int lineProcessMode = 0;
	int statisticsType = STAT_SINGLE_DBSTR;
		
	
	boolean FlagStat_SingleDBStr_Ambit = false;
	boolean FlagStat_SingleDBStr_CDK = false;
	boolean FlagStat_SingleDBStr_Ambit_CDK = false;
	
	SmartsParser sp = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	
	
	public static void main(String[] args)
	{
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		//atu.handleArguments(args);		
		
		atu.handleArguments(new String[] {"-db","/einecs_structures_V13Apr07.sdf", "-o","/test-out--.txt", 
				"-i","/input.txt","-nDBStr", "100", "-maxSeqStep", "10", "-c", "sss-ambit" });
		
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
		
		if (command.equals("sss-all"))
		{
			System.out.println("Running sss with all isomprphims algorithm:");
			lineProcessMode = LPM_SSS_ALL;
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
		//System.out.println("-nOutStr      the number of used output structures");	
		System.out.println("-maxSeqStep   maximal number of sequence steps");
		System.out.println("-maxStrSize   maximal size of the generated structures");
		System.out.println("-statType     the type of statistics saved in the output file");
		System.out.println("                 single-str       ");
		System.out.println("                 entire-db        ");
		System.out.println("-c            command: ");
		System.out.println("                 random-str       generates random structures");
		System.out.println("                 exhaustive-str   generates structures exhaustively");
		System.out.println("                 sss-ambit        substructure searching with Ambit Algorithm");
		System.out.println("                 sss-cdk          substructure searching with CDK Parser and Algorithm");
		System.out.println("                 sss-ambit-cdk    substructure searching with Ambit Parser and CDK Algorithm");
		System.out.println("                 sss-all          substructure searching with all algorithms");
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
				String line = f.readLine();
				System.out.println("line " + n + "  " + line);
				processLine(line);
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
		if (statisticsType == STAT_SINGLE_DBSTR)
		{
			switch(this.lineProcessMode)
			{
			case LPM_SSS_AMBIT:			
				FlagStat_SingleDBStr_Ambit = true;
				break;
			case LPM_SSS_CDK:
				FlagStat_SingleDBStr_CDK = true;
				break;	
			case LPM_SSS_AMBIT_CDK:
				FlagStat_SingleDBStr_Ambit_CDK = true;
				break;
			case LPM_SSS_ALL:
				FlagStat_SingleDBStr_Ambit = true;
				FlagStat_SingleDBStr_CDK = true;
				FlagStat_SingleDBStr_Ambit_CDK = true;
				break;
			}
			
			sss_SingleDBStrStat(line);
			
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
		//It could be applied for several algorithms simultaneously
		
		QueryAtomContainer query  = sp.parse(line);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return -1;
		}						
		
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
						isoTester.setQuery(query);
						sp.setSMARTSData(mol);
						boolean hasIso = isoTester.hasIsomorphism(mol);
						System.out.println("record " + record+ "  " + hasIso);
					}
					
					//TODO
					if (FlagStat_SingleDBStr_CDK)
					{
						
					}
					
										
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
	
	//Matching functions for sss
	
	int matchAmbit()
	{
		return(0);
	}
	
	
	
	
	
}
