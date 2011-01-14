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
	
	
	public static void main(String[] args)
	{
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		atu.produceRandomStructures();
	}
	
	
	public int handleArguments(String[] args)
	{
		Vector<CmdOption> v = extractOptions(args);
		
		
		return(0);
		
	}
	
	public Vector<CmdOption> extractOptions(String[] args)
	{
		CmdOption curOption = null;
		
		Vector<CmdOption> v = new Vector<CmdOption>();
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("--"))
			{
				if (curOption == null)
				{	
					curOption = new CmdOption();
					curOption.option = args[i].substring(2);
				}
				else
				{	
					if (curOption.value == null)
						curOption.value = args[i];
					else					
						curOption.value += " " + args[i];
				}	
			}
			else
				if (args[i].startsWith("-"))
				{
					if (curOption == null)
					{
						curOption = new CmdOption();
						curOption.option = args[i].substring(1);
					}
					else
					{	
						if (curOption.value == null)
							curOption.value = args[i];
						else					
							curOption.value += " " + args[i];
					}
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
		
		return(v);
	}
	
	
	
	
	public void produceRandomStructures() 
	{
		ChemObjectFactory cof = new ChemObjectFactory();
		Vector<StructInfo> vStr = new Vector<StructInfo>();
		cof.produceRandomStructsFromMDL("../src/test/resources/einecs/einecs_structures_V13Apr07.sdf", 
					30, 50000, vStr, "/my-test---.txt");
	}
	
	
	
}
