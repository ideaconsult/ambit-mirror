package ambit2.reactions.cli;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.smarts.SmartsHelper;

public class ReactorCli 
{
	private static final String title = "Virtual Chemical Reactor";
	
	public static void main(String[] args) {
		ReactorCli app = new ReactorCli();
    	app.run(args);
	}
	
	public String inputSmiles = null;
	public String reactorConfigFile = null;
	
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

			return runReactor();	

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
	    formatter.printHelp( ReactorCli.class.getName(), options );
	}
	
	enum _option {
		
		config {
			@Override
			public String getArgName() {
				return "config";
			}
			@Override
			public String getDescription() {
				return "Reactor configuration (.json) file";
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
				return "Input smiles for starting molecule";
			}
			@Override
			public String getShortName() {
				return "s";
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
	
	public void setOption(_option option, String argument) throws Exception {
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		case config: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			reactorConfigFile = argument;
			break;
		}
		case smiles: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputSmiles = argument;
			break;
		}
		}
	}
	
	protected int runReactor() throws Exception
	{
		if (inputSmiles == null)
			throw new Exception("Smiles not assigned! Use -s command line option.");
		if (reactorConfigFile == null)
			throw new Exception("Reactor configuratio file not assigned! Use -c command line option.");
		
		System.out.println("input smiles: " + inputSmiles);
		System.out.println("reactor config: " + reactorConfigFile);
		
		System.out.println("Setting reactor and reaction database...");
		Reactor reactor = new Reactor();
		ReactionDataBase reactDB = new ReactionDataBase(reactorConfigFile);
		
		System.out.println("Configuring reaction database...");
		//reactDB.configureReactions(reactor.getSMIRKSManager());
		reactDB.configureGenericReactions(reactor.getSMIRKSManager());
		reactor.setReactionDataBase(reactDB);
		
		System.out.println("Configuring reactor strategy ...");
		ReactorStrategy strategy = new ReactorStrategy(new File(reactorConfigFile));  //strategy is in the same file
		
		strategy.FlagStoreFailedNodes = true;
		strategy.FlagStoreSuccessNodes = true;
		strategy.maxNumOfSuccessNodes = 0;  //if 0 then the reactor will stop after the first success node
		
		strategy.FlagCheckNodeDuplicationOnPush = true;
		strategy.FlagTraceReactionPath = true;
		strategy.FlagLogMainReactionFlow = true;
		strategy.FlagLogReactionPath = true;
		strategy.FlagLogNameInReactionPath = false;
		strategy.FlagLogExplicitHToImplicit = true;
		
		
		reactor.setStrategy(strategy);
		
		//Setup Smirks manager
		reactor.getSMIRKSManager().setFlagProcessResultStructures(true);
		reactor.getSMIRKSManager().setFlagClearImplicitHAtomsBeforeResultProcess(false);
		reactor.getSMIRKSManager().setFlagAddImplicitHAtomsOnResultProcess(false);
		reactor.getSMIRKSManager().setFlagConvertExplicitHToImplicitOnResultProcess(false);
		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(inputSmiles, true);
		System.out.println();
		System.out.println("Reactor on target: " + inputSmiles);
		System.out.println();
		
		ReactorResult result = reactor.react(mol);
		return 0;
	}
}
