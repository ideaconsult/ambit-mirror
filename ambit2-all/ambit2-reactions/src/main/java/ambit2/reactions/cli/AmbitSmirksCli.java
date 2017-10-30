package ambit2.reactions.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;


public class AmbitSmirksCli 
{
	private static final String title = "Ambit SMIRKS CLI";
	public String inputArg = null;
	public String smirksArg = null;
	public String modeArg = null;
	public String cloneArg = null;
	

	public static void main(String[] args) 
	{
		AmbitSmirksCli cli = new AmbitSmirksCli();
		cli.run(args);
	}

	enum _option {

		smirks {
			@Override
			public String getArgName() {
				return "smirk";
			}
			@Override
			public String getDescription() {
				return "SMIRKS notation";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},
		
		input {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Input target molecule smiles";
			}
			@Override
			public String getShortName() {
				return "i";
			}
		},
		
		mode {
			@Override
			public String getArgName() {
				return "mode";
			}
			@Override
			public String getDescription() {
				return "Match mode: all, non-overlapping, non-identical";
			}
			@Override
			public String getShortName() {
				return "m";
			}
		},
		
		copy {
			@Override
			public String getArgName() {
				return "true/false";
			}
			@Override
			public String getDescription() {
				return "Determines wether each reaction site "
						+ "transformation is applied on a new molecule copy. "
						+ "Default value is true.";
			}
			@Override
			public String getShortName() {
				return "c";
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
		formatter.printHelp( AmbitSmirksCli.class.getName(), options );
	}
	
	public void setOption(_option option, String argument) throws Exception {
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputArg = argument;
			break;
		}
		case smirks: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			smirksArg = argument;
			break;
		}
		case mode: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			modeArg = argument;
			break;
		}
		}
	}
	
	public int run(String[] args) 
	{
		Options options = createOptions();
		
		printHelp(options, null);
		
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

			return runSmirks();	

		} catch (Exception x ) {
			System.out.println(x.getMessage());
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
	
	protected int runSmirks() throws Exception
	{	
		//TODO
		return 0;
	}



}
