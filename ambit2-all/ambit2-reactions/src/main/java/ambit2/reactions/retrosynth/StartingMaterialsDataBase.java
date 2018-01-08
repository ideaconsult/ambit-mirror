package ambit2.reactions.retrosynth;

import java.io.File;
import java.io.RandomAccessFile;
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
		public String id = null;
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
	
	public StartingMaterialsDataBase(File file)
	{	
		loadStartingMaterials(file);
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
	
	void loadStartingMaterials(File file)
	{
		try
		{	
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();
				line = line.trim();
				if (line.equals(""))
					continue;
				String tokens[] = line.split("\t");
				if (tokens.length >=3)
				{
					StartMaterialData smd = new StartMaterialData();
					smd.id = tokens[0];
					smd.smiles = tokens[1];
					materials.put(tokens[2], smd);
				}
				else
				{
					//error
				}
			}
			
			f.close();
		}
		catch (Exception e)
		{	
		}
	}
	
	public static void createStartingMaterialsFile(File sourceFile, File outputFile, 
				Map<String,Integer> column)
	{
		try
		{	
			RandomAccessFile sf = new RandomAccessFile(sourceFile,"r");			
			long length = sf.length();
			
			while (sf.getFilePointer() < length)
			{	
				String line = sf.readLine();
				line = line.trim();
				if (line.equals(""))
					continue;
				
				//TODO
			}
			
			sf.close();
		}
		catch (Exception e)
		{	
		}
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
