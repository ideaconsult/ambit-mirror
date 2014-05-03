package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtomContainer;

public class StartingMaterialsDataBase 
{
		
	//It can be composed of several data sets
	
	
	//Flags that specify the search process
	boolean FlagCheckMolDescr = true;	
	boolean FlagCheckHashCode = false;
	boolean FlagUSMILESCheck = false;
	boolean FlagIsomorphismCheck = false;
	
	AccessType FlagDescriptorAccess = AccessType.MEMORY;
	AccessType FlagHashCodeAccess = AccessType.MEMORY;
	AccessType FlagUSMILESAccess = AccessType.FILE;
	AccessType FlagFingerprintAccess = AccessType.FILE;
	AccessType FlagStructureAccess = AccessType.FILE;
		
	
	public boolean isStartingMaterial(IAtomContainer str)
	{
		//TODO
		return false;
	}
	
	public static String getHash(IAtomContainer str)
	{
		//TODO
		return "";
	}
	
	
	public enum AccessType{
		MEMORY, FILE, DATABASE
	}
	
}
