package ambit2.reactions.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Kekulization;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.reactions.syntheticaccessibility.SyntheticAccessibilityManager;
import ambit2.reactions.syntheticaccessibility.SyntheticAccessibilityStrategy;
import ambit2.reactions.syntheticaccessibility.SyntheticAccessibilityManager.DescrData;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.TopLayer;


public class SyntheticAccessibilityCli 
{
	private static final String title = "Synthetic Accessibility Prediction";
	
	public String targetSmiles = null;
	public String configFile = null;
	public String inputFile = null;
	public boolean flagVerbose = false;
	SmilesGenerator smiGen = SmilesGenerator.generic();
	
	SyntheticAccessibilityManager saMan; 
	SyntheticAccessibilityStrategy saStrategy;
	
	public static void main(String[] args) 
	{
		SyntheticAccessibilityCli saCli = new SyntheticAccessibilityCli();
		saCli.run(args);
	}
	
	public int run(String[] args) 
	{
		Options options = createOptions();
		final CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse( options, args,false );
			if (line.hasOption(_option.help.name())) {
				printHelp(options, null);
				return -1;
			}

			for (_option o: _option.values()) 
				if (line.hasOption(o.getShortName())) try {
					setOption(o,line.getOptionValue(o.getShortName()));
				} catch (Exception x) {
					printHelp(options,x.getMessage());
					return -1;
				}

			return runCalculation();	

		} catch (Exception x ) {
			printHelp(options,x.getMessage());
			return -1;
		} finally {
			try { 
				//run whatever cleanup is needed
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}
	
	protected static Options createOptions() {
    	Options options = new Options();
    	for (_option o: _option.values()) {
    		options.addOption(o.createOption());
    	}
    	return options;
	}
	
	protected static void printHelp(Options options,String message) {
		if (message!=null) System.out.println(message);
		System.out.println(title);
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( SyntheticAccessibilityCli.class.getName(), options );
	}
	
	enum _option {
		
		/*
		config {
			@Override
			public String getArgName() {
				return "config";
			}
			@Override
			public String getDescription() {
				return "Configuration (.json) file";
			}
			@Override
			public String getShortName() {
				return "c";
			}
		},
		*/
		
		smiles {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Target molecule smiles";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},
		
		input {
			@Override
			public String getArgName() {
				return "input-file";
			}
			@Override
			public String getDescription() {
				return "Input file with a molecule set";
			}
			@Override
			public String getShortName() {
				return "i";
			}
		},
		
		verbose {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return "Verbose output";
			}
			@Override
			public String getShortName() {
				return "v";
			}
			public Option createOption() {
		    	Option option   = OptionBuilder.withLongOpt(name())
		        .withDescription(getDescription())
		        .create(getShortName());
		    	return option;
			}
		},
		
		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return "Print help menu";
			}
			@Override
			public String getShortName() {
				return "h";
			}
			/*
			@Override
			public String getDefaultValue() {
				return null;
			}
			*/
			public Option createOption() {
		    	Option option   = OptionBuilder.withLongOpt(name())
		        .withDescription(getDescription())
		        .create(getShortName());
		    	return option;
			}
		};		
		
		public abstract String getArgName();
		public abstract String getDescription();
		public abstract String getShortName();
		public String getDefaultValue() { return null; }
			
		public Option createOption() {
			String defaultValue = getDefaultValue();
	    	Option option   = OptionBuilder.withLongOpt(name())
	        .hasArg()
	        .withArgName(getArgName())
	        .withDescription(String.format("%s %s %s",getDescription(),defaultValue==null?"":"Default value: ",defaultValue==null?"":defaultValue))
	        .create(getShortName());

	    	return option;
		}
	}
	
	public void setOption(_option option, String argument) throws Exception {
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		/*
		case config: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			configFile = argument;
			break;
		}
		*/
		case smiles: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			targetSmiles = argument;
			break;
		}
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputFile = argument;
			break;
		}
		case verbose: {
			flagVerbose = true;
			break;
		}
		}
	}
	
	protected int runCalculation() throws Exception
	{
		if (targetSmiles != null)
			return calculateSA(targetSmiles);
		
		if (inputFile != null)
			return calculateSAMoleculeSet(inputFile);
		
		throw new Exception("No target smiles, nor input file are specified!\n"
				+ "Use -s or -i command line option.");
	}	
	
	int calculateSA(String smiles) 
	{
		System.out.println("Calculating SA for: " + smiles);
		NumberFormat formatter = new DecimalFormat("#0.000"); 
		IAtomContainer mol = null;
		try
		{
			mol = SmartsHelper.getMoleculeFromSmiles(smiles);
			//if (FlagExcplicitHAtoms)
			//	MoleculeTools.convertImplicitToExplicitHydrogens(mol);
		}
		catch (Exception x){
			System.out.println(x.getMessage());
			return -1;
		}
		
		TopLayer.setAtomTopLayers(mol);
		saMan = new SyntheticAccessibilityManager(); 
		saStrategy = SyntheticAccessibilityStrategy.getDefaultStrategy();
		saMan.setStrategy(saStrategy);
		
		double sa = saMan.calcSyntheticAccessibility(mol);
		System.out.println("SA = " + formatter.format(sa));
		if (flagVerbose)
		{	
			System.out.println("SA details: ");
			System.out.println(saMan.getCalculationDetailsAsString());
		}
		return 0;
	}
	
	int calculateSAMoleculeSet(String molFile) throws Exception 
	{
		System.out.println("Calculating SA for molecule set: " + molFile);
		
		int records_read = 0;
		int records_processed = 0;
		int records_error = 0;
		
		File file = new File(molFile);
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		
		try 
		{
			reader = getReader(in,file.getName());
			System.out.println(String.format("Reading %s",file.getAbsoluteFile()));
			
			//Setup saMan 
			saMan = new SyntheticAccessibilityManager(); 
			saStrategy = SyntheticAccessibilityStrategy.getDefaultStrategy();
			saMan.setStrategy(saStrategy);
			
			//Setup first header line of output
			StringBuffer sb = new StringBuffer();
			sb.append("#");
			sb.append("\t");
			sb.append("smiles");
			sb.append("\t");
			sb.append("NumAtoms");
			sb.append("\t");
			sb.append("SA");
			if (flagVerbose)
				for (int i = 0; i < saStrategy.descirptors.size(); i++)
				{
					String dName = saStrategy.descirptors.get(i).descriptorName;
					sb.append("\t");
					sb.append(dName);
					sb.append("\t");
					sb.append(dName+"_score");
				}
			System.out.println(sb.toString());
			
			while (reader.hasNext()) 
			{	
				IAtomContainer molecule  = reader.next();
				records_read++;
				if (molecule==null) {
					records_error++;
					continue;
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
							Kekulization.kekulize(molecule);
							
					} catch (Exception x) {
						System.out.println(String.format("[Record %d] Error %s\t%s", records_read, file.getAbsoluteFile(), x.getMessage()));
					}
					
					performCalculation(molecule, (records_processed+1));
					records_processed++;
					//System.out.println("#" + records_processed + "  NA = " + molecule.getAtomCount());
				} 
				catch (Exception x) {
					records_error++;
					System.out.println(String.format("[Record %d] Error %s\n",
							records_read, file.getAbsoluteFile()) + "  " + x);
				}
			}//while
		} catch (Exception x1) {
			System.out.println(String.format("[Record %d] Error %s\n", 
					records_read, file.getAbsoluteFile()) + "  " + x1);
			
		} finally {
			try { reader.close(); } catch (Exception x) {}
			
		}
		
		System.out.println(String.format("[Records read/processed/error %d/%d/%d] %s", 
						records_read,records_processed,records_error,file.getAbsoluteFile()));
		
		return records_read;
	}
	
	void performCalculation(IAtomContainer mol, int num)
	{
		NumberFormat f = new DecimalFormat("#0.000");
		TopLayer.setAtomTopLayers(mol);
		String smiles = "null"; 
		try {
			//smiles = SmartsHelper.moleculeToSMILES(mol, true);
			smiles = smiGen.aromatic().create(mol);
			if (smiles.trim().equals(""))
				smiles = "*****";
		}
		catch(Exception x){
			System.out.println("moleculeToSMILES error:" + x.getMessage());
		}
		double sa = saMan.calcSyntheticAccessibility(mol);
		System.out.print(num);
		System.out.print("\t");
		System.out.print(smiles);
		System.out.print("\t");
		System.out.print(mol.getAtomCount());
		System.out.print("\t");
		System.out.print(f.format(sa));
		if (flagVerbose)
		{	
			//System.out.println("SA details: ");
			//System.out.println(saMan.getCalculationDetailsAsString());
			List<DescrData> listDD = saMan.getCalculatedDescrData();
			for (int i = 0; i < listDD.size(); i++)
			{
				DescrData dd = listDD.get(i);
				System.out.print("\t");
				System.out.print(f.format(dd.value));
				System.out.print("\t");
				System.out.print(f.format(dd.transformedValue));
			}
		}
		System.out.println();	
	}
		
	protected IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
		FileInputState instate = new FileInputState();
		IIteratingChemObjectReader<IAtomContainer> reader ;
		if (extension.endsWith(FileInputState._FILE_TYPE.SDF_INDEX.getExtension())) {
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
