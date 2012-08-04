package ambit2.tautomers.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;



public class AutomaticTautomerTests 
{
	class CmdOption
	{	
		String option = null;
		String value = null;		
	};	
	
	
	public static final int LPM_PROCESS_NCI = 1;
	
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
	
	int nInputStr = 0;
	
	
	public static void main(String[] args)
	{
		AutomaticTautomerTests att = new AutomaticTautomerTests();
				
		try {
			att.handleArguments(new String[] {
					"-i","D:/Projects/data012-tautomers/NCISMA99.sdz",
					"-nInpStr","10",
					//"-db","D:/Projects/data012-tautomers/nci.smi",
					"-c","process-nci",
					"-o","D:/Projects/data012-tautomers/nci.smi",
					"-nDBStr","1"
			});		
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
				}
			}	
		}
		
		handleCommand();
		
		return (0);
	}
	
	public int handleCommand() throws Exception
	{
		if (command == null)
			return(0);
		
		
		if (command.equals("process-nci"))
		{
			System.out.println("Processing nci file:");
			openOutputFile();
			lineProcessMode = LPM_PROCESS_NCI;
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
		
		System.out.println("-c            command: ");
		System.out.println("                 process-nci           nci row file is processed");
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
				
				if (nInputStr > 0)
					if (n > nInputStr) 
						break;
				
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
	
	int processLine(String line)
	{	
		if (lineProcessMode == LPM_PROCESS_NCI)
		{
			processNCILine(line);
			return(0);
		}
		
		return 0;
	}
	
	
	//------------- process line functions ------------------
	
	int processNCILine(String line)
	{
		//System.out.println(line);
		Vector<String> tokens = filterTokens(line.split(" "));
		//TODO
		
		output(line + endLine);
		return (0);
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
}
