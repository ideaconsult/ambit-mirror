package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import net.sf.jniinchi.INCHI_OPTION;


public class StartingMaterialsDataBase 
{		
	public enum AccessType{
		MEMORY, FILE, DATABASE
	}
	
	public static class StartMaterialData {
		public String smiles = null;
		public int id = 0;
	}
	
	Map<String,StartMaterialData> materials = new HashMap<String,StartMaterialData>();
	List<INCHI_OPTION> inchiOptions = new ArrayList<INCHI_OPTION>();
	InChIGeneratorFactory inchiGeneratorFactory = null;
	
	/*
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
	*/
	
	public StartingMaterialsDataBase()
	{	
	}
	
	public boolean isStartingMaterial(String inchiKey)
	{
		return materials.containsKey(inchiKey);
	}
	
	public boolean isStartingMaterial(IAtomContainer str)
	{
		//TODO
		return false;
	}
	
	void inchiSetup() throws Exception
	{
		inchiOptions.add(INCHI_OPTION.FixedH);
		inchiOptions.add(INCHI_OPTION.SAbs);
		inchiOptions.add(INCHI_OPTION.SAsXYZ);
		inchiOptions.add(INCHI_OPTION.SPXYZ);
		inchiOptions.add(INCHI_OPTION.FixSp3Bug);
		inchiOptions.add(INCHI_OPTION.AuxNone);
		inchiGeneratorFactory = InChIGeneratorFactory.getInstance();
	}
	
		
	/*
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
	
	*/
	
	
	
}
