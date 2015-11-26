package ambit2.reactions.retrosynth;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;


public class StartingMaterialsDataBase 
{		
	//It can be composed of several data sets
	
	ArrayList<String> errors = new ArrayList<String>(); 
	
	//Flags that configure the search process
	boolean FlagCheckMolDescr = true;	
	boolean FlagCheckHashCode = false;
	boolean FlagUSMILESCheck = false;
	boolean FlagIsomorphismCheck = false;
		
	AccessType FlagDescriptorAccess = AccessType.MEMORY;
	AccessType FlagHashCodeAccess = AccessType.MEMORY;
	AccessType FlagUSMILESAccess = AccessType.FILE;
	AccessType FlagFingerprintAccess = AccessType.FILE;
	AccessType FlagStructureAccess = AccessType.FILE;
	
	String sourceStructures = null;
	String sourceDescriptors = null;
	String sourceHashCodes = null;
	
	public StartingMaterialsDataBase()
	{	
	}
	
	public StartingMaterialsDataBase(String sourceStructures, String sourceDescriptors, String sourceHashCodes)
	{	
		setSources(sourceStructures, sourceDescriptors, sourceHashCodes);
		initialize();
	}
	
	public void setSources(String sourceStructures, String sourceDescriptors, String sourceHashCodes)
	{
		this.sourceStructures = sourceStructures;
		this.sourceDescriptors = sourceDescriptors;
		this.sourceHashCodes = sourceHashCodes;
	}
	
	public void initialize()
	{	
		//TODO
	}
	
	public boolean isStartingMaterial(IAtomContainer str)
	{
		//TODO
		return false;
	}
	
	public ArrayList<Integer> seachMolecularDescriptors()
	{
		//TODO
		return null;
	}
	
	public static String getHash(IAtomContainer str)
	{
		//TODO
		return "";
	}	
	
	//All Descriptors are united as one single string
	public static String getMolDescr(IAtomContainer str)
	{
		//TODO
		return "";
	}
	
	public static int calcMolDescrForFile(String structureFile, String descrFile)
	{
		//TODO
		return 0;
	}
	
	
	
	public enum AccessType{
		MEMORY, FILE, DATABASE
	}
	
}
