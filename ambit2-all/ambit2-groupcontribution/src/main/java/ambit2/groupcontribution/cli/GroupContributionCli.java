package ambit2.groupcontribution.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import ambit2.groupcontribution.GCMParser;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.Learner;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.fragmentation.Fragmentation;



public class GroupContributionCli 
{
	private static final String title = "Group contribution modeling";
	
	public String trainSetFile = null;
	public String gcmConfigFile = null;
	public String localDescriptors = null;
	public String targetProperty = null;
	
	public static void main(String[] args) {
		GroupContributionCli gcCli = new GroupContributionCli();
		gcCli.run(args);
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
		
		train {
			@Override
			public String getArgName() {
				return "train";
			}
			@Override
			public String getDescription() {
				return "Training data set";
			}
			@Override
			public String getShortName() {
				return "t";
			}
		},
		
		local_descriptors {
			@Override
			public String getArgName() {
				return "local-descriptors";
			}
			@Override
			public String getDescription() {
				return "Local atoms descriptors";
			}
			@Override
			public String getShortName() {
				return "l";
			}
		},
		
		property {
			@Override
			public String getArgName() {
				return "property";
			}
			@Override
			public String getDescription() {
				return "Target property";
			}
			@Override
			public String getShortName() {
				return "p";
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
		case train: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			trainSetFile = argument;
			break;
		}
		case config: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			gcmConfigFile = argument;
			break;
		}
		case local_descriptors: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			localDescriptors = argument;
			break;
		}
		case property: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			targetProperty = argument;
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

			return runGCM();	

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
	
	protected int runGCM() throws Exception
	{	
				
		if (gcmConfigFile == null)
		{	
			System.out.println("Configuration file not assigned. Getting data from command line options");
			
			if (trainSetFile == null)
				throw new Exception("Training set file not assigned! Use -t command line option.");
			
			if (trainSetFile != null) 
				System.out.println("train set file: " + trainSetFile);
			if (gcmConfigFile != null)
				System.out.println("gcm config: " + gcmConfigFile);
			if (localDescriptors != null)
				System.out.println("Local descriptors: " + localDescriptors);
			if (targetProperty != null)
				System.out.println("Target property: " + targetProperty);
		}
		else
		{
			
		}
		
		
		DataSet trainDataSet = new DataSet(new File(trainSetFile));
		
		GroupContributionModel gcm = new GroupContributionModel();
		gcm.setTargetEndpoint(targetProperty);
		
		GCMParser gcmParser = new GCMParser();
		
		List<ILocalDescriptor> locDescriptors = gcmParser.getLocalDescriptorsFromString(localDescriptors);
		if (!gcmParser.getErrors().isEmpty())
		{
			System.out.println("Errors:\n" + gcmParser.getAllErrorsAsString());
			return -1;
		}
		else
			gcm.setLocalDescriptors(locDescriptors);
		
		//Fragmentation.makeFragmentation(trainDataSet, gcm);
		
		Learner learner = new Learner();
		learner.setModel(gcm);
		learner.setTrainDataSet(trainDataSet);
		learner.train();
		
		
		return 0;
	}	
}
