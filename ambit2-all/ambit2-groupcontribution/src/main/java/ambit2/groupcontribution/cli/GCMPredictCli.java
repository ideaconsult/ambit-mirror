package ambit2.groupcontribution.cli;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.groupcontribution.Calculator;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.io.GCM2Json;
import ambit2.smarts.SmartsHelper;


public class GCMPredictCli {

	private static final String title = "GCM Predict";
	
	public String gcmConfigFile = null;
	public String inputFileName = null;
	public String outputFileName = null;
	public String inputSmiles = null;	
	
	public static void main(String[] args) {
		GCMPredictCli gcmPredict = new GCMPredictCli();
		gcmPredict.run(args);
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
				return "GCM configuration (.json) file";
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
		
		output {
			@Override
			public String getArgName() {
				return "output";
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
		
		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return title;
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
		case config: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			gcmConfigFile = argument;
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

			return runGCMPredict();	

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
	
	protected int runGCMPredict() throws Exception
	{
		GroupContributionModel gcm = null;
		
		if ((inputFileName == null) && (inputSmiles == null))
		{
			System.out.println("No input is given! \n"
					+ "Please assign input SMILES or input molecules file)!");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (gcmConfigFile == null)
		{	
			System.out.println("GCM configuration file not assigned!");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		else
		{
			System.out.println("GCM config: " + gcmConfigFile);
			
			GCM2Json g2j = new GCM2Json();
			gcm = g2j.loadFromJSON(new File(gcmConfigFile));
			
			if (!g2j.configErrors.isEmpty())
			{	
				System.out.println(g2j.getAllErrorsAsString());
				return -1;
			}	
			else if (!g2j.configErrors.isEmpty())
				System.out.println(g2j.getAllErrorsAsString());
			
			String gcm_json = gcm.toJsonString();
			System.out.println(gcm_json);
		}
		
		if (inputSmiles != null)
		{
			IAtomContainer mol = null;
			try {
				mol = SmartsHelper.getMoleculeFromSmiles(inputSmiles);	
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			}
			catch (Exception x) {
				System.out.println("Error on creating input molecule: " + x.getMessage());
				return -2;
			}
			
			if (mol == null)
			{
				System.out.println("Unable to create and configure input molecule from : " + inputSmiles);
				return -3;
			}
			
			//This is critical to be set false.
			gcm.setAllowGroupRegistration(false);
			
			double modelVal = gcm.calcModelValue(mol);
			System.out.println("GCM value " + modelVal);
			return 0;
		}
		
		//TODO handle input file
		
		return 0;
	}

}
