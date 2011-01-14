package ambit2.smarts.test;

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
	
	
	public static void main(String[] args)
	{
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		//atu.handleArguments(args);
		
		
		atu.handleArguments(new String[] {"-i","input-file.txt","-o","output-file.txt"});
		
		//atu.produceRandomStructures();
	}
	
	
	public int handleArguments(String[] args)
	{
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
		
		
		System.out.println("input file: " + inFile);
		System.out.println("output file: " + outFile);
		
		return(0);
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
	
	
	
	
	
	
	
	public void produceRandomStructures() 
	{
		ChemObjectFactory cof = new ChemObjectFactory();
		Vector<StructInfo> vStr = new Vector<StructInfo>();
		cof.produceRandomStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					30, 50000, vStr, "/my-test---.txt");
	}
	
	
	
}
