package ambit2.sln.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;


public class SLNCli {

	private static final String title = "SLN Command Line App";

	public String inputFileName = null;
	public String outputFileName = null;
	public String inputSmiles = null;
	public String sln = null;
	public String operationString = null;
	public _operation operation = _operation.convert;
	public String outFormatString = null;
	public _out_format outFormat = _out_format.smiles;
	
	public SLNParser slnParser = new SLNParser();
	public SLNHelper slnHelper = new SLNHelper();
	
	
	public static void main(String[] args) 
	{
		SLNCli sclCli = new SLNCli();
		sclCli.run(args);
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
				return "SLN string";
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
				return "Input molecule as smiles";
			}
			@Override
			public String getShortName() {
				return "m";
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
		
		operation {
			@Override
			public String getArgName() {
				return "operation";
			}
			@Override
			public String getDescription() {
				return "Operation: convert, ss_match";
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
				return "Output format: smiles, ct";
			}
			@Override
			public String getShortName() {
				return "f";
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
	
	enum _operation {
		convert, ss_match;
		
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
		smiles, ct;
		
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

			return runSLN();	

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
	
	
	protected int runSLN() throws Exception
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
		
		if (sln == null)
		{
			System.out.println("SLN is not specified. Use option -s (--sln)");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		int res = 0;
		
		switch (operation) {
		case convert:
			res = convert();
			break;
		}
		
		return res;
	}
	
	public int convert()
	{
		SLNContainer container = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			System.out.println("Original sln:    " + sln); 
			System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
			return -1;
		}
		 
		System.out.println("Input  sln: " + sln); 
		System.out.println("Atom attributes:");		
		System.out.println(SLNHelper.getAtomsAttributes(container));
		System.out.println("Bond attributes:");
		System.out.println(SLNHelper.getBondsAttributes(container));
		if (container.getAttributes().getNumOfAttributes() > 0)
		{
			System.out.println("Molecule attributes:");
			System.out.println(SLNHelper.getMolAttributes(container));
		}
		
		return 0;
	}
	
	

}
