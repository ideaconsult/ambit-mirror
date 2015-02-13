package ambit2.tautomers.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
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
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerUtils;



public class TautomerAnalysis 
{
	private final static Logger logger = Logger.getLogger(TautomerAnalysis.class.getName());
	
	//Configuration variables
	public String inputFileName = null;
	public String outPrefix = "out";
	
	//Work variables
	protected FixBondOrdersTool kekulizer = new FixBondOrdersTool();
	
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int process() throws Exception 
	{	
		int records_read = 0;
		int records_processed = 0;
		int records_error = 0;
		
		String sep = "\t";
		String sep_exc = "\t";
		String generationError = null;
		
		File file = new File(inputFileName);
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		
		try 
		{
			reader = getReader(in,file.getName());
			logger.log(Level.INFO, String.format("Reading %s",file.getAbsoluteFile()));
			//LOGGER.log(Level.INFO, String.format("Writing %s tautomer(s)",all?"all":"best"));
			while (reader.hasNext()) 
			{	
				IAtomContainer molecule  = reader.next();
				records_read++;
				if (molecule==null) {
					records_error++;
					continue;
				}
				
				/*
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
				*/
				
				
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
					
					/*
					if (FlagUseMolFilterAfterProcessing)
						if (!molecularFilter.useMolecule(molecule, records_read))
							continue;
					*/
					
					

					/**
					 * ambit2-tautomers
					 * http://ambit.uni-plovdiv.bg:8083/nexus/index.html#nexus-search;quick~ambit2-tautomers
					 */
					List<IAtomContainer> resultTautomers = null;
					try {
						generationError = null;
						//resultTautomers = generateTautomers(molecule);
					} catch (Exception x) {
						/*
						 * java.lang.ArrayIndexOutOfBoundsException: 0 at ambit2.smarts.IsomorphismTester.generateNodes(IsomorphismTester.java:780)
						 */
						logger.log(Level.SEVERE, String.format("[Record %d] Error %s\t%s", records_read, file.getAbsoluteFile(), x.getMessage()));
						resultTautomers = null;
						generationError = x.getMessage();
					}	
					
					
					records_processed++;
				} catch (Exception x) {
					records_error++;
					logger.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x);
					
					
				}
			}//while
		} catch (Exception x1) {
			logger.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x1);
			
		} finally {
			try { reader.close(); } catch (Exception x) {}
			
		
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
	
}
