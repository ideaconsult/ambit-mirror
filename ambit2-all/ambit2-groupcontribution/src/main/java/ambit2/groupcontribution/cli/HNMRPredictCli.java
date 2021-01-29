package ambit2.groupcontribution.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.groupcontribution.nmr.HNMRShifts;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;
import ambit2.smarts.SmartsHelper;


public class HNMRPredictCli {

	private static final String title = "HNMR shifts predict";
	public String defaultConfigFile = "./hnmr-knowledgebase.txt";
	public String configFile = null;
	public String inputFileName = null;
	public String outputFileName = null;
	public String inputSmiles = null;
	public String printLogString = null;
	public Boolean printLog = false;
	public String printExplanationString = null;
	public Boolean printExplanation = true;
	
	HNMRShifts hnmrShifts = null;

	public static void main(String[] args) 
	{
		HNMRPredictCli hnmrCli = new HNMRPredictCli();
		hnmrCli.run(args);
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
		formatter.printHelp( GroupContributionCli.class.getName(), options );
	}

	enum _option {

		config {
			@Override
			public String getArgName() {
				return "config";
			}
			@Override
			public String getDescription() {
				return "HNMR database configuration file";
			}
			@Override
			public String getShortName() {
				return "c";
			}
		},
		
		smiles {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Input molecule smiles";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},
		
		log {
			@Override
			public String getArgName() {
				return "on|off";
			}
			@Override
			public String getDescription() {
				return "Switch on/off log printing. Deafult log is off";
			}
			@Override
			public String getShortName() {
				return "l";
			}
		},
		
		explanation {
			@Override
			public String getArgName() {
				return "on|off";
			}
			@Override
			public String getDescription() {
				return "Switch on/off H shift calculation explanation. Deafult explanation is on";
			}
			@Override
			public String getShortName() {
				return "e";
			}
		},
				
		input {
			@Override
			public String getArgName() {
				return "input";
			}
			@Override
			public String getDescription() {
				return "Input molecule file";
			}
			@Override
			public String getShortName() {
				return "i";
			}
		},
		
		/*
		output {
			@Override
			public String getArgName() {
				return "output";
			}
			@Override
			public String getDescription() {
				return "Output file name (*.csv)";
			}
			@Override
			public String getShortName() {
				return "o";
			}
		},
		*/

		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return "Shows this help info";
			}
			@Override
			public String getShortName() {
				return "h";
			}
			@Override
			public String getDefaultValue() {
				return null;
			}
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


	public void setOption(_option option, String argument) throws Exception 
	{
		if (argument != null)
			argument = argument.trim();
		switch (option) {

		case config:{ 
			if ((argument == null) || "".equals(argument.trim()))
				return;
			configFile = argument;
			break;
		}
		case smiles: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputSmiles = argument;
			break;
		}
		case log: {
			printLogString = argument;
			if (printLogString.equalsIgnoreCase("on"))
				printLog = true;
			else if (printLogString.equalsIgnoreCase("off"))
				printLog = false;
			else
				printLog = null;
			break;
		}
		case explanation: {
			printExplanationString = argument;
			if (printExplanationString.equalsIgnoreCase("on"))
				printExplanation = true;
			else if (printExplanationString.equalsIgnoreCase("off"))
				printExplanation = false;
			else
				printExplanation = null;
			break;
		}		
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputFileName = argument;
			break;
		}
		/*
		case output: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			outputFileName = argument;
			break;
		}
		*/
		}

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

			return runHNMR();	

		} catch (Exception x ) {
			System.out.println("**** HNMR " + x.getMessage());
			//x.printStackTrace();
			//printHelp(options,x.getMessage());
			return -1;
		} finally {
			try { 
				//run whatever cleanup is needed
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}
	
	protected int runHNMR() throws Exception
	{
		if (printLog == null)
		{
			System.out.println("Incorrect log option: " + printLogString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		if (printExplanation == null)
		{
			System.out.println("Incorrect explanation option: " + printExplanationString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if ((inputFileName == null) && (inputSmiles == null))
		{
			System.out.println("No input is given! \n"
					+ "Please assign input SMILES or input molecule file!");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		
		String knowledgeBaseFileName = configFile;
		if (knowledgeBaseFileName == null)
		{	
			knowledgeBaseFileName = defaultConfigFile;
			System.out.println("Using default HNMR database: " + defaultConfigFile);
		}
		
		
		try {
			hnmrShifts = new HNMRShifts(new  File(knowledgeBaseFileName));
		}
		catch (Exception x) {
			System.out.println(x.getMessage());
			return -1;
		}
		
		
		if (inputSmiles != null)
			return runForInputSmiles();	
		else
			return iterateInputMoleculesFile();
		
		//return 0;
	}
	
	
	public int runForInputSmiles() {
		System.out.println("Input smiles: " + inputSmiles);
		IAtomContainer mol = null;
		
		try {
			mol = SmartsHelper.getMoleculeFromSmiles(inputSmiles);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		}
		catch (Exception e) {
			System.out.println("Error on creating molecule from SMILES:\n" + e.getMessage());
			return -1;
		}
		
		return predictForMolecule(mol);		
	}
	
	public int predictForMolecule(IAtomContainer mol) {
		
		try {
			hnmrShifts.setStructure(mol);
			hnmrShifts.calculateHShifts();
		}
		catch(Exception x) {
			System.out.println("Calculation error:\n" + x.getMessage());
			return -1;
		}
		
		if (printLog) {
			System.out.println("Log:\n" + hnmrShifts.getCalcLog());
			System.out.println();
		}
		
		for (HShift hs : hnmrShifts.getHShifts())
			System.out.println(hs.toString(printExplanation));
		
		return 0;
	}
	
	public void performTask(IAtomContainer mol, String info)
	{	
		System.out.println("Molecule " + info); 
		predictForMolecule(mol);
	}
	
	public int iterateInputMoleculesFile() throws Exception
	{
		int records_read = 0;
		int records_error = 0;
		
		File file = new File(inputFileName);
		
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		try 
		{
			reader = getReader(in,file.getName());
			while (reader.hasNext()) 
			{
				IAtomContainer molecule  = reader.next();
				records_read++;
				
				if (molecule==null) {
					records_error++;
					System.out.println("Unable to read chemical object #" + records_read);
					continue;
				}
				
				if (molecule.getAtomCount() == 0)
				{
					records_error++;
					System.out.println("Empty chemical object #" + records_read);
					continue;
				}
								
				performTask(molecule, " " + records_read);				
			}
			
		}
		catch (Exception x1) {
			System.out.println("Error: " + x1.getMessage());
		} 
		finally {
			try { reader.close(); } catch (Exception x) {}
		}
		
		return records_error;
	}
	
	
	public IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
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