package ambit2.tautomers.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.filter.MoleculeFilter;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.Rule;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.TautomerUtils;



public class TautomerAnalysis 
{
	public static enum Task {
		PRINT_SMILES, GEN_RULE_PAIR;
	}
	
	private final static Logger logger = Logger.getLogger(TautomerAnalysis.class.getName());
	
	//Configuration variables
	public String filePath = "";
	public String outFilePath = null; //If null it is treated as filePath
	public String inputFileName = null;
	public String outFilePrefix = "out";
	public String outFileType = "csv";
	public String molFilterString = null;
	public Task task = Task.PRINT_SMILES; 
	
	
	//Work variables
	protected TautomerManager tman = new TautomerManager();
	protected FixBondOrdersTool kekulizer = new FixBondOrdersTool();
	protected MoleculeFilter molecularFilter = null;
	protected int records_read = 0;
	protected int records_processed = 0;
	protected int records_error = 0;
	protected FileWriter outWriters[] = null;
	protected File outFiles[] = null;
	
	protected ArrayList<String> fileRuleNames = new ArrayList<String>();
	String sep = "\t";
	
	/**
	 * 
	 * @param molFilterString expression which defines the filter e.g. "#Mol=[1,100];NA=[1,10]"
	 * @throws Exception
	 */
	public void setMoleculeFilter(String molFilterString) throws Exception
	{
		this.molFilterString = molFilterString;
		try {
			MoleculeFilter filter = MoleculeFilter.parseFromCommandLineString(molFilterString);
			molecularFilter = filter;
		} catch (Exception x) {
			throw new Exception("Incorrect molecule filter: " + x.getMessage());
		}
	}
	
	/**
	 * 
	 * @return number of read structure records
	 * @throws Exception
	 */
	public int process() throws Exception 
	{	
		records_read = 0;
		records_processed = 0;
		records_error = 0;
		
		File file = new File(filePath + inputFileName);
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		
		try 
		{
			reader = getReader(in,file.getName());
			logger.log(Level.INFO, String.format("Reading %s",file.getAbsoluteFile()));
			//LOGGER.log(Level.INFO, String.format("Writing %s tautomer(s)",all?"all":"best"));
			
			createWriters();
			
			while (reader.hasNext()) 
			{	
				IAtomContainer molecule  = reader.next();
				records_read++;
				if (molecule==null) {
					records_error++;
					continue;
				}
				
				
				boolean FlagUseMolFilterAfterProcessing = false; 
				
				if (molecularFilter != null)
				{
					if (molecularFilter.isAromaticityInfoNeeded())
						FlagUseMolFilterAfterProcessing = true;
					else
					{	
						if (!molecularFilter.useMolecule(molecule, records_read))
							continue;
					}	
				}
				
				
				try {
					
					try {
						AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
						CDKHueckelAromaticityDetector.detectAromaticity(molecule);
						//implicit H count is NULL if read from InChI ...
						molecule = AtomContainerManipulator.removeHydrogens(molecule);
						CDKHydrogenAdder.getInstance(molecule.getBuilder()).addImplicitHydrogens(molecule);
						boolean aromatic = false;
						for (IBond bond : molecule.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
						if (aromatic)
							molecule = kekulizer.kekuliseAromaticRings((IMolecule)molecule);
					} catch (Exception x) {
						logger.log(Level.WARNING, String.format("[Record %d] Error %s\t%s", records_read, file.getAbsoluteFile(), x.getMessage()));
					}
					
					
					if (FlagUseMolFilterAfterProcessing)
						if (!molecularFilter.useMolecule(molecule, records_read))
							continue;
					
					
					doTask(molecule);
					
					
					records_processed++;
				} 
				catch (Exception x) {
					records_error++;
					logger.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x);
				}
			}//while
		} catch (Exception x1) {
			logger.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x1);
			
		} finally {
			try { reader.close(); } catch (Exception x) {}
			
			closeWriters();
		
			/*
			if (writer != null)
				try { writer.close(); } catch (Exception x) {}
				
				String totalTimeStat = "Total time" + sep + t_total + sep + "s\n" +
						"Generation" + sep +  globalCalcTime + sep + "s" + sep + (100.0*globalCalcTime/t_total)+sep + "%\n"+
						"IO/convert" + sep + (t_total-globalCalcTime) + sep + "s" + sep + (100.0*(t_total-globalCalcTime)/t_total) + sep + "%\n";
				
				benchmarkOut.write(totalTimeStat);
				LOGGER.info(totalTimeStat);
				try { benchmarkOut.close(); } catch (Exception x) {}
				
			*/	
			
		}
		
		logger.log(Level.INFO, String.format("[Records read/processed/error %d/%d/%d] %s", 
						records_read,records_processed,records_error,file.getAbsoluteFile()));
		
		return records_read;
	}
	
	
	protected IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
		FileInputState instate = new FileInputState();
		IIteratingChemObjectReader<IAtomContainer> reader ;
		if (extension.endsWith(FileInputState.extensions[FileInputState.SDF_INDEX])) {
			reader = new InteractiveIteratingMDLReader(in,SilentChemObjectBuilder.getInstance());
			((InteractiveIteratingMDLReader) reader).setSkip(true);
		} else reader = instate.getReader(in,extension);
		
		reader.setReaderMode(Mode.RELAXED);
		reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
				System.out.println(message);
			}
			
			@Override
			public void handleError(String message, Exception exception) {
				exception.printStackTrace();				
			}
			
			@Override
			public void handleError(String message) {
				System.out.println(message);
			}
		});
		return reader;
	}	
	
	
	protected void createWriters() throws Exception
	{
		switch (task)
		{
		case PRINT_SMILES:
			//no writer;
			outWriters = null;
			break;
			
		case GEN_RULE_PAIR:
			createRulePairsWriters();
			break;
		}
	}
	
	protected FileWriter createWriter(String fname) throws Exception
	{
		FileWriter writer = null;
		try {
			writer = new FileWriter(fname);
			
		}catch (Exception x) {
			//in case smth's wrong with the writer file, close it and throw an error
			try {writer.close(); } catch (Exception xx) {}
			throw x;
		} finally { }
		
		return writer;
	}
	
	protected void createRulePairsWriters() throws Exception
	{
		String path = outFilePath;
		if (path == null)
			path = filePath;
		
		int nRules = tman.getKnowledgeBase().rules.size();
		outWriters = new FileWriter[nRules];
		outFiles = new File[nRules];
		for (int i = 0; i < nRules; i++)
		{
			Rule rule = tman.getKnowledgeBase().rules.get(i);
			System.out.println(rule.name + "  --> " + getFileRuleName(rule.name));
			outFiles[i] = new File(path + outFilePrefix + getFileRuleName(rule.name)+"." + outFileType);
			outWriters[i] = new FileWriter(outFiles[i]);
		}
	}
	
	
	protected void closeWriters()
	{
		if (outWriters != null)
			for (int i = 0; i < outWriters.length; i++)
			{
				try { 
					outWriters[i].close();
					//logger.info("closed: " + outFiles[i].getAbsolutePath());
				} catch (Exception x) {
					logger.info("Error on clossing: " + outFiles[i].getAbsolutePath() + "\n" + x.getMessage());
				}
			}
	}
	
	
		
	protected void doTask(IAtomContainer mol) throws Exception
	{
		switch (task)
		{
		case PRINT_SMILES:
			printSmiles(mol);
			break;
			
		case GEN_RULE_PAIR:
			generateRulePairs(mol);
			break;
		}
	}
	
	protected void printSmiles(IAtomContainer mol) throws Exception
	{
		String smiles = SmartsHelper.moleculeToSMILES(mol, false);
		System.out.println(smiles);		
	
	}
	
	protected void generateRulePairs(IAtomContainer mol) throws Exception
	{
		String smiles = SmartsHelper.moleculeToSMILES(mol, false);
		System.out.println(smiles);	
	}
	
	protected String getFileRuleName(String rname)
	{	
		String fname = new String (rname);
		fname = fname.replace('/', '_');
		//fname = fname.replace("#", "b3");
		return fname;
	}
	
}
