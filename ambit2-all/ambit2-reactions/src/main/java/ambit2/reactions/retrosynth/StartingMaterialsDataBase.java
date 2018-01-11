package ambit2.reactions.retrosynth;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.io.ReactionWriteUtils;
import ambit2.smarts.SmartsHelper;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;


public class StartingMaterialsDataBase 
{		
	public enum AccessType{
		MEMORY, FILE, DATABASE
	}
	
	public static class StartMaterialData {
		public String smiles = null;
		public String id = null;
	}
	
	public static class StartMaterialStandartization {
		public boolean FlagClearStereo = false;
		public boolean FlagAddNonStereoSmiles = false;
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
		try {
			loadStartingMaterials(file);
		}
		catch (Exception e) {}
	}
	
	public StartingMaterialsDataBase(File file, int numMaterials)
	{	
		try {
			loadStartingMaterials(file,  numMaterials);
		}
		catch (Exception e) {}
	}
		
	public Map<String, StartMaterialData> getMaterials() {
		return materials;
	}

	public void setMaterials(Map<String, StartMaterialData> materials) {
		this.materials = materials;
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
	
	public static List<INCHI_OPTION> getBacisInchiOptions() 
	{
		List<INCHI_OPTION> inchiOpt = new ArrayList<INCHI_OPTION>();
		inchiOpt.add(INCHI_OPTION.FixedH);
		inchiOpt.add(INCHI_OPTION.SAbs);
		inchiOpt.add(INCHI_OPTION.SAsXYZ);
		inchiOpt.add(INCHI_OPTION.SPXYZ);
		inchiOpt.add(INCHI_OPTION.FixSp3Bug);
		inchiOpt.add(INCHI_OPTION.AuxNone);
		
		return inchiOpt;
	}
	
	void loadStartingMaterials(File file) throws Exception
	{
		loadStartingMaterials(file,-1);
	}
	
	void loadStartingMaterials(File file, int numMaterials) throws Exception
	{
		RandomAccessFile f = ReactionWriteUtils.createReader(file);
		String splitter = "\t";
		try
		{		
			long length = f.length();
			int n = 0;
			
			while (f.getFilePointer() < length)
			{	
				if (numMaterials > -1)
				{	
					//Condition for loading only the first numMaterials
					if (n >= numMaterials)
						break;
				}
				
				String line = f.readLine();
				line = line.trim();
				if (line.equals(""))
					continue;
				String tokens[] = line.split(splitter);
				if (tokens.length >=3)
				{
					StartMaterialData smd = new StartMaterialData();
					smd.id = tokens[0];
					smd.smiles = tokens[1];
					materials.put(tokens[2], smd);
					n++;
				}
				else
				{
					//error
				}
			}
		}
		catch (Exception e)	{}
		
		ReactionWriteUtils.closeReader(f);
	}
	
	public static void createStartingMaterialsFile(File sourceFile, File outputFile, 
				Map<String,Integer> columnIndices, StartMaterialStandartization standartization) throws Exception
	{		
		//Setup input columns indices
		Integer idCol = columnIndices.get("id");
		Integer smilesCol = columnIndices.get("smiles");
		Integer mwCol = columnIndices.get("mw");
		if (idCol == null || smilesCol == null)
			throw new Exception("Incorrect columns indices!");
		int maxIndex = idCol;
		if (maxIndex < smilesCol)
			maxIndex = smilesCol;
		if (mwCol != null)
			if (maxIndex < mwCol)
				maxIndex = mwCol;
		//Inchi generator
		List<INCHI_OPTION> inchiOpt = getBacisInchiOptions();
		InChIGeneratorFactory inchiGF = InChIGeneratorFactory.getInstance();
		//Setup file reader and writer
		RandomAccessFile sf = ReactionWriteUtils.createReader(sourceFile);
		FileWriter outf = ReactionWriteUtils.createWriter(outputFile.getAbsolutePath());
		String splitter = "\t";			
		long length = sf.length();
		int lineNum = 0;
		try
		{	
			while (sf.getFilePointer() < length)
			{	
				String line = sf.readLine();
				lineNum++;
				line = line.trim();
				if (line.equals(""))
					continue;
				String tokens[] = line.split(splitter);
				if (maxIndex >= tokens.length)
					continue; //error some of the column are missing
				String id = tokens[idCol].trim();
				String smiles = tokens[smilesCol].trim();
				String inchiKey = null;
				try
				{	
					IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
					InChIGenerator ig = inchiGF.getInChIGenerator(mol, inchiOpt);
					INCHI_RET returnCode = ig.getReturnStatus();
					if (INCHI_RET.ERROR == returnCode) {
						//Error
					}
					inchiKey = ig.getInchiKey();
				}
				catch (Exception e)
				{
					//SEVERE error;
					continue;
				}
								
				String s = String.format("%s\t%s\t%s",id,smiles,inchiKey);
				if (mwCol != null)
					s = s + "\t" + tokens[mwCol].trim();
				s = s + "\n";
				System.out.println("Line " + lineNum + "   " + s);
				outf.write(s);
				outf.flush();
				
			}			
		}
		catch (Exception e) 	{}
		
		ReactionWriteUtils.closeReader(sf);
		ReactionWriteUtils.closeWriter(outf);
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
