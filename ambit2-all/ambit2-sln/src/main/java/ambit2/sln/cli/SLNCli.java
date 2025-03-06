package ambit2.sln.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.PropertyConfigurator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.dictionary.Expander;
import ambit2.sln.dictionary.SLNDictionary;
import ambit2.sln.io.SLN2ChemObject;
import ambit2.sln.io.SLN2SMARTS;
import ambit2.sln.io.SLN2ChemObjectConfig.ComparisonConversion;
import ambit2.sln.search.SLNSearchManager;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;


public class SLNCli {

	private static final String title = "SLN Command Line App";

	public String inputFileName = null;
	public String outputFileName = null;
	public String dictionaryFileName = null;
	public String inputSmiles = null;
	public String sln = null;
	public String operationString = null;
	public _operation operation = _operation.convert;
	public String outFormatString = null;
	public _out_format outFormat = _out_format.smiles;
	public String comparisonConversionString = null;
	public ComparisonConversion comparisonConversion = ComparisonConversion.convert_as_equal;
	public String warningsString = null;
	public Boolean warnings = true;
	public String expandString = null;
	public Boolean expand = false;
	
	public SLNParser slnParser = new SLNParser();
	public SLNHelper slnHelper = new SLNHelper();
	public SLN2ChemObject slnConverter = new SLN2ChemObject();
	public Expander expander = new Expander();
	public IsomorphismTester isoTester = new IsomorphismTester();
	public SmartsHelper smartsHelper = new SmartsHelper(SilentChemObjectBuilder.getInstance());
	public SLNSearchManager man = new SLNSearchManager();
	
	public SLNContainer slnContainer = null;
	public IAtomContainer inputMol = null;
	
	public FileWriter outWriter = null;
	public String endLine = System.getProperty("line.separator");
	
	
	
	public static void main(String[] args) 
	{		
		setupLogging();
		SLNCli sclCli = new SLNCli();
		sclCli.run(args);
	}
	
	public static void setupLogging()
	{	
		//Suppress INCHI logging on the console 
		Properties prop = new Properties();
		prop.put("log4j.logger.net.sf.jnati", "ERROR");
		PropertyConfigurator.configure(prop);
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
		formatter.printHelp( SLNCli.class.getName(), options );
	}

	enum _option {
		sln {
			@Override
			public String getArgName() {
				return "sln";
			}
			@Override
			public String getDescription() {
				return "Input SLN string (specify molecule or query)";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},
		smiles {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Input single molecule as smiles";
			}
			@Override
			public String getShortName() {
				return "m";
			}
		},

		input {
			@Override
			public String getArgName() {
				return "file name";
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

		output {
			@Override
			public String getArgName() {
				return "file name";
			}
			@Override
			public String getDescription() {
				return "Output file name";
			}
			@Override
			public String getShortName() {
				return "o";
			}
		},
		
		operation {
			@Override
			public String getArgName() {
				return "operation";
			}
			@Override
			public String getDescription() {
				return "Operation: convert, ss_match, expand, comb_library. Default operation is convert";
			}
			@Override
			public String getShortName() {
				return "p";
			}
		},
		
		out_format {
			@Override
			public String getArgName() {
				return "out_format";
			}
			@Override
			public String getDescription() {
				return "Output format: smiles, smarts, ct, extended_ct";
			}
			@Override
			public String getShortName() {
				return "f";
			}
		},
		
		comparison_convert {
			@Override
			public String getArgName() {
				return "mode";
			}
			@Override
			public String getDescription() {
				return "Comparison conversion modes: omit, convert_as_equal (default), "
						+ "convert_as_equal_if_eqaul_is_present, convert_as_value_list";
			}
			@Override
			public String getShortName() {
				return "r";
			}
		},
		
		dictionary {
			@Override
			public String getArgName() {
				return "file name";
			}
			@Override
			public String getDescription() {
				return "A text file with SLN dictionary objects (e.g. Macro or Markush atoms)";
			}
			@Override
			public String getShortName() {
				return "d";
			}
		},
		
		warnings {
			@Override
			public String getArgName() {
				return "on|off";
			}
			@Override
			public String getDescription() {
				return "Switch on/off conversion warnings. Default warnings is on";
			}
			@Override
			public String getShortName() {
				return "w";
			}
		},
		
		expand {
			@Override
			public String getArgName() {
				return "on|off";
			}
			@Override
			public String getDescription() {
				return "Switch on/off SLN expansion for Macro/Markush atoms. By befault expand option is off";
			}
			@Override
			public String getShortName() {
				return "x";
			}
		},

		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return "Prints this help";
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
	
	enum _operation {
		convert, ss_match, expand, comb_library;
		
		public static _operation fromString(String text) {
	        for (_operation  x : _operation.values()) {
	            if (x.name().equalsIgnoreCase(text)) {
	                return x;
	            }
	        }
	        return null;
	    }
	}
	
	enum _out_format {
		smiles, smarts, ct, extended_ct;
		
		public static _out_format fromString(String text) {
	        for (_out_format  x : _out_format.values()) {
	            if (x.name().equalsIgnoreCase(text)) {
	                return x;
	            }
	        }
	        return null;
	    }
	}

	public void setOption(_option option, String argument) throws Exception 
	{
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		case sln: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			sln = argument;
			break;
		}
		case smiles: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputSmiles = argument;
			break;
		}
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputFileName = argument;
			break;
		}
		case output: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			outputFileName = argument;
			break;
		}
		case dictionary: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			dictionaryFileName = argument;
			break;
		}
		case operation: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			operationString = argument;
			operation = _operation.fromString(operationString);
			break;
		}
		case out_format: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			outFormatString = argument;
			outFormat = _out_format.fromString(outFormatString);
			break;
		}
		
		case comparison_convert: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			comparisonConversionString = argument;
			comparisonConversion = ComparisonConversion.fromString(comparisonConversionString);
			break;
		}
		
		case warnings: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			warningsString = argument;
			if (warningsString.equalsIgnoreCase("on"))
				warnings = true;
			else if (warningsString.equalsIgnoreCase("off"))
				warnings = false;
			else
				warnings = null;
			break;
		}
		
		case expand: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			expandString = argument;
			if (expandString.equalsIgnoreCase("on"))
				expand = true;
			else if (expandString.equalsIgnoreCase("off"))
				expand = false;
			else
				expand = null;
			break;
		}

		}	
	}
	
	public int run(String[] args) 
	{		
		Options options = createOptions();
		
		if (args == null || args.length == 0)
		{
			printHelp(options, null);
			return 0;
		}		
		
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

			return runSLNApp();	

		} catch (Exception x ) {
			System.out.println("**********" + x.getMessage());
			x.printStackTrace();
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
	
	
	protected int runSLNApp() throws Exception
	{		
		if (operation == null)
		{
			System.out.println("Incorrect operation: " + operationString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (outFormat == null)
		{
			System.out.println("Incorrect out format: " + outFormatString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (comparisonConversion == null)
		{
			System.out.println("Incorrect comparison conversion mode: " + comparisonConversionString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		slnConverter.getConversionConfig().FlagComparisonConversion = comparisonConversion;
		
		if (warnings == null)
		{
			System.out.println("Incorrect warnings option: " + warningsString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (expand == null)
		{
			System.out.println("Incorrect expand option: " + expandString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (sln == null && inputFileName == null && inputSmiles == null)
		{
			System.out.println("No input specified.\n"
					+ "Specify input SLN, ipnput SMILES or input files with molecules");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (dictionaryFileName != null)
		{
			try {
				SLNDictionary dict = SLNDictionary.getDictionary(dictionaryFileName);
				if (dict.getParserErrors().isEmpty() && dict.getCheckErrors().isEmpty())
					slnParser.setGlobalDictionary(dict);
				else
				{
					System.out.println("Global SLN dictionary errors:");
					System.out.println(dict.getParserErrorMessages());
					System.out.println(dict.getCheckErrorMessages());
					return -1;
				}
				System.out.println("Global SLN dictionary loaded sucessfully (" + dict.getNames().size() + " objects)");
			}
			catch (Exception x) {
				System.out.println("Global SLN dictionary loading problem: " + x.getMessage());
				return -1;
			}
		}
		else
			slnParser.setPredefinedGlobalDictionary();
		
				
		if (inputSmiles != null) {
			try {
				inputMol = SmartsHelper.getMoleculeFromSmiles(inputSmiles);
			}
			catch (Exception x) {
				System.out.println("Smiles parsing error: " + x.getMessage());
				return -1;
			}
		}
		
		if (outputFileName != null) {
			int fileRes = openFileWriter();
			if (fileRes != 0)
				return -1;
		}
		
		int res = 0;
				
		
		if (sln != null) 
		{	
			//SLN (-s/--sln) option takes precedence over Input file (-i/--input) 
			slnContainer = slnParser.parse(sln);
			if (!slnParser.getErrorMessages().equals(""))
			{
				outputLine("Original sln:    " + sln); 
				outputLine("SLN Parser errors:\n" + slnParser.getErrorMessages());			
				return -1;
			}

			switch (operation) {
			case convert:
				res = convert(slnContainer);
				break;
			
			case ss_match:
				if (inputFileName == null)
				{
					if (inputSmiles == null)
					{
						System.out.println("Neither SMILES nor Input File is specified!"); 
						return -1;
					}
					else
					{
						boolean ssRes = ssMatch(slnContainer, inputMol);
						outputLine("Matching " + sln + " against " + inputSmiles + "  " + ssRes);
					}
				}
				else
				{
					outputLine("Matching " + sln + " against:");
					res = iterateInputMoleculesFile();
				}
				break;
			case expand:
				res = expandSLNContainer(slnContainer);
				break;
			case comb_library:
				res = generateCombLibrary(slnContainer);
				break;	
			}
		}
		else
		{
			//SLN is not specified
			switch (operation) {
			case convert:
				if (inputFileName == null)
				{
					//Working with input smiles which is converted to SLN					 
					res = convertToSLN(inputMol);
				}
				else
				{
					if (inputFileName.endsWith("sln"))
						res = iterateInputFileWithSLNs(); //SLN --> Molecule format
					else
					{	
						outputLine("Converting molecules to SLN:");
						res = iterateInputMoleculesFile(); //Molecules --> SLN
					}	
				}
				break;
			
			case ss_match:
				System.out.println("SLN is not specified. Can not perform substructure matching!");
				return -1;	
				
			case expand:
			case comb_library:	
				if (inputFileName != null)
				{
					if (inputFileName.endsWith("sln"))
					{	
						if (operation == _operation.expand)
							outputLine("Expanding Macro and Markush atoms in SLNs:");
						else
							outputLine("Generating Markush combintations:");
						
						res = iterateInputFileWithSLNs(); //expand or comb_library operation is performed
					}
					else
						System.out.println("Operations expand and comb_library are applicable only for SLN files!");
				}
				break;
			}
			
		}
		
		if (outWriter != null) {
			closeFileWriter();
		}	
		
		return res;
	}
	
	public int convert(SLNContainer container0) throws Exception
	{		 
		SLNContainer container = container0; 
		if (expand)
			container = expander.generateExpandedSLNContainer(container0);
				
		switch (outFormat)
		{
			case ct:
				outputLine(SLNHelper.getCTString(container));
				break;
			case extended_ct:
				printExtendedCT(container);
				break;
				
			case smiles:
				IAtomContainer mol = slnConverter.slnContainerToAtomContainer(container);
				if (slnConverter.hasConversionErrors())
				{	
					outputLine("Conversion errors:");
					outputLine(slnConverter.getAllErrors());
				}	
				else
				{	
					try {
						String smiles = SmartsHelper.moleculeToSMILES(mol, true);
						outputLine(smiles);
					}
					catch (Exception x) {
						outputLine("Error generating SMILES: " + x.getMessage());
					}
					
					if (warnings)
						if (!slnConverter.getConversionWarnings().isEmpty())
						{
							//Warning are output only to the console
							System.out.println("Conversion warnings: ");
							for (String w: slnConverter.getConversionWarnings())
								System.out.println(w);
						}
				}
				break;
				
			case smarts:				
				IQueryAtomContainer query = slnConverter.slnContainerToQueryAtomContainer(container);
				if (slnConverter.hasConversionErrors())
				{	
					outputLine("Conversion errors:");
					outputLine(slnConverter.getAllErrors());
				}	
				else
				{	
					String smarts = smartsHelper.toSmarts(query);
					outputLine(smarts);
					
					if (warnings)
						if (!slnConverter.getConversionWarnings().isEmpty())
						{
							//Warning are output only to the console
							System.out.println("Conversion warnings: ");
							for (String w: slnConverter.getConversionWarnings())
								System.out.println(w);
						}
				}	
				break;
		}		
		
		return 0;
	}
	
	public int expandSLNContainer(SLNContainer container) throws Exception
	{
		SLNContainer container2 = expander.generateExpandedSLNContainer(container);
		outputLine(slnHelper.toSLN(container2));
		return 0;
	}
	
	public int generateCombLibrary(SLNContainer container) throws Exception
	{	
		List<SLNContainer> list = expander.generateMarkushCombinatorialList(container);
		for (int i = 0; i < list.size(); i++)
			outputLine(slnHelper.toSLN(list.get(i)));
		return 0;
	}
	

	public int convertToSLN(IAtomContainer container) throws Exception
	{	
		SLNContainer slnCon = slnConverter.atomContainerToSLNContainer(container);
		if (slnConverter.hasConversionErrors())
		{	
			outputLine("Conversion errors:");
			outputLine(slnConverter.getAllErrors());
			return -1;
		}
		
		outputLine("Input  smiles: " + inputSmiles); 
		outputLine(slnHelper.toSLN(slnCon));
		
		return 0;
	}
	
	
	public void printExtendedCT(SLNContainer container) throws Exception
	{	 
		outputLine("Atom list:");		
		outputLine(SLNHelper.getAtomsAttributes(container));
		outputLine("Bond list:");
		outputLine(SLNHelper.getBondsAttributes(container));
		if (container.getAttributes().getNumOfAttributes() > 0)
		{
			outputLine("Molecule attributes:");
			outputLine(SLNHelper.getMolAttributes(container));
		}
	}
	
	
	public int iterateInputFileWithSLNs()
	{
		int res = 0;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(inputFileName));
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return -1;
		}		
		
		try {
			int nLine = 0;
			String line;
			
			while ( (line = br.readLine()) != null ) 
			{
				nLine++;
				String slnString = line.trim();
				
				if (slnString.isEmpty())
				{
					outputLine("Empty SLN string in line #" + nLine);
					continue;
				}				
				
				slnContainer = slnParser.parse(slnString);
				if (!slnParser.getErrorMessages().equals(""))
				{
					outputLine("SLN: " + slnString); 
					outputLine("SLN Parser errors:\n" + slnParser.getErrorMessages());			
					return -1;
				}
				
				System.out.println("mol #" + nLine);
				switch (operation) {
				case convert:	
					convert (slnContainer);
					break;
				case expand:
					outputLine(slnString + " --> ");
					expandSLNContainer(slnContainer);
					break;
				case comb_library:
					outputLine(slnString + " combinations:");
					generateCombLibrary(slnContainer);
					break;	
				}
			}	
			
			
		}
		catch (Exception e) {			
			System.out.println(e.getMessage());
			res = -1;
		}
		
		try {
			br.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			res = -1;
		}
		
		return res;
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
					outputLine("Unable to read chemical object #" + records_read);
					continue;
				}
				
				if (molecule.getAtomCount() == 0)
				{
					records_error++;
					outputLine("Empty chemical object #" + records_read);
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
	
	
	public void performTask(IAtomContainer mol, String info) throws Exception
	{	
		switch (operation) {
		case convert:
			SLNContainer slnCon = slnConverter.atomContainerToSLNContainer(mol);
			if (slnConverter.hasConversionErrors())
			{	
				outputLine(info + " conversion errors:");
				outputLine(slnConverter.getAllErrors());
				return;
			}
			
			outputLine(info + "  " + slnHelper.toSLN(slnCon));
			break;
		
		case ss_match:
			try {
				boolean ssRes = ssMatch(slnContainer, mol);
				String smi = SmartsHelper.moleculeToSMILES(mol, true);
				outputLine(info + "  " + smi + "  " + ssRes);
			}
			catch(Exception x) {
				outputLine(info + "  Error: " + x.getMessage()); 
			}
			break;
		}	
	}
	
	public boolean ssMatch(SLNContainer query, IAtomContainer mol) throws Exception
	{	
		man.setQuerySLNContainer(query);
		SmartsParser.prepareTargetForSMARTSSearch(true, true, true, true, true, true, mol); //flags are set temporary
		
		return man.matches(mol);
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

	public int openFileWriter() {
		try {
			outWriter = new FileWriter(outputFileName);
		}
		catch (Exception x) {
			System.out.println("Unable to create output file: " + outputFileName 
					+ " error: " + x.getMessage());
			return -1;
		}
		return 0;
	}

	public int closeFileWriter() {
		try {
			outWriter.close();
		}
		catch (Exception x) {
			System.out.println("Error on closing output file: " + outputFileName
					+ ": " + x.getMessage());
			return -1;
		}
		return 0;
	}
	
	public void output(String s) throws Exception 
	{
		output(s, true);
	}
	
	public void output(String s, boolean consoleOutput) throws Exception 
	{
		if (consoleOutput)
			System.out.print(s);
		
		if (outWriter != null)
			outWriter.write(s);
	}
	
	public void outputLine(String s) throws Exception
	{
		outputLine(s,true);
	}
	
	public void outputLine(String s, boolean consoleOutput) throws Exception
	{
		if (consoleOutput)
			System.out.println(s);
		
		if (outWriter != null) 
		{
			outWriter.write(s);
			outWriter.write(endLine);
			outWriter.flush();
		}
	}
	
}
