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
import ambit2.groupcontribution.correctionfactors.DescriptorInfo;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.utils.math.CrossValidation;
import ambit2.groupcontribution.utils.math.ValidationConfig;



public class GroupContributionCli 
{
	private static final String title = "Group contribution modeling";
	
	public String trainSetFile = null;
	public String gcmConfigFile = null;
	public String localDescriptors = null;
	public String globalDescriptors = null;
	public String targetProperty = null;
	public Double threshold = null;
	public String gcmType = null;
	public String validation = null;
	
	
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
		
		descriptors {
			@Override
			public String getArgName() {
				return "descriptors";
			}
			@Override
			public String getDescription() {
				return "Global descriptors";
			}
			@Override
			public String getShortName() {
				return "d";
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
		
		threshold {
			@Override
			public String getArgName() {
				return "value in [0,1]";
			}
			@Override
			public String getDescription() {
				return "Column filtration threshold";
			}
			@Override
			public String getShortName() {
				return "r";
			}
		},
		
		model {
			@Override
			public String getArgName() {
				return "model type";
			}
			@Override
			public String getDescription() {
				return "Group contribution model type: atomic, bond_based, ...";
			}
			@Override
			public String getShortName() {
				return "m";
			}
		},
		
		validation {
			@Override
			public String getArgName() {
				return "validation";
			}
			@Override
			public String getDescription() {
				return "Validation specification: <self>,<LOO>,<YS-n>,<CV-n<-m>>,<vebose>,<boot-n>,<external>";
			}
			@Override
			public String getShortName() {
				return "v";
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
		case descriptors: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			globalDescriptors = argument;
			break;
		}
		case property: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			targetProperty = argument;
			break;
		}
		case threshold: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			try {
				threshold = Double.parseDouble(argument);
			}
			catch (Exception e) {
				throw new Exception ("Incorrect threshold value: " + argument);
			}
			break;
		}
		case model: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			gcmType = argument;
			break;
		}
		case validation: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			validation = argument;
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
			if (globalDescriptors != null)
				System.out.println("Global descriptors: " + globalDescriptors);
			if (threshold != null)
				System.out.println("Column filtration threshold: " + threshold);
			if (targetProperty != null)
				System.out.println("Target property: " + targetProperty);
			if (validation != null)
				System.out.println("Validation: " + validation);
				
		}
		else
		{
			//TODO handle config file
		}
		
		
		DataSet trainDataSet = new DataSet(new File(trainSetFile));
		
		GroupContributionModel gcm = new GroupContributionModel();
		gcm.setTargetEndpoint(targetProperty);
		if (gcmType != null)
		{
			GroupContributionModel.Type type = getModelType(gcmType);
			if (type != null)
				gcm.setModelType(type);
		}
		System.out.println("GCM type : " + gcm.getModelType().toString());
		
		GCMParser gcmParser = new GCMParser();
		
		List<ILocalDescriptor> locDescriptors = gcmParser.getLocalDescriptorsFromString(localDescriptors);
		if (!gcmParser.getErrors().isEmpty())
		{
			System.out.println("Errors:\n" + gcmParser.getAllErrorsAsString());
			return -1;
		}
		else
			gcm.setLocalDescriptors(locDescriptors);
		
		if (globalDescriptors != null)
		{
			List<DescriptorInfo> globDescriptors = gcmParser.getGlobalDescriptorsFromString(globalDescriptors);
			if (!gcmParser.getErrors().isEmpty())
			{
				System.out.println("Errors:\n" + gcmParser.getAllErrorsAsString());
				return -1;
			}
			else
			{
				if (!globDescriptors.isEmpty())
					gcm.setDescriptors(globDescriptors);
			}
		}
		
		if (threshold != null)
			gcm.setColStatPercentageThreshold(threshold);
			
		if (validation != null)
		{
			ValidationConfig valCfg = getValidationConfigFromString(validation);
			if (valCfg == null)
				return -2;
			gcm.setValidationConfig(valCfg);
		}
		
		//Fragmentation.makeFragmentation(trainDataSet, gcm);
		
		Learner learner = new Learner();
		learner.setModel(gcm);
		learner.setTrainDataSet(trainDataSet);
		
		int res = learner.train();
		if (res != 0)
			System.out.println(learner.getAllErrorsAsString());
		
		
		System.out.println();
		learner.validate();
		
		return 0;
	}	
	
	GroupContributionModel.Type getModelType(String s)
	{
		if (s.equalsIgnoreCase("atomic"))
			return GroupContributionModel.Type.ATOMIC;
		if (s.equalsIgnoreCase("bond_based"))
			return GroupContributionModel.Type.BOND_BASED;
		//...		
		return null;
	}
	
	ValidationConfig getValidationConfigFromString(String v)
	{
		ValidationConfig valCfg = new ValidationConfig();
		valCfg.reset();
		int nErrors = 0;
		StringBuffer errors = new StringBuffer();
		String tokens[] = v.split(",");
		for (int i = 0; i < tokens.length; i++)
		{
			if (tokens[i].equalsIgnoreCase("self"))
			{
				valCfg.selfTest = true;
				continue;
			}
			
			if (tokens[i].equalsIgnoreCase("loo"))
			{
				valCfg.leaveOneOutValidation = true;
				continue;
			}
			
			if (tokens[i].equalsIgnoreCase("verbose"))
			{
				valCfg.verboseReport = true;
				continue;
			}
			
			if (tokens[i].toLowerCase().startsWith("ys"))
			{
				String t[] = tokens[i].split("-");
				if (t.length == 2)
				{
					try {
						int nYSIter = Integer.parseInt(t[1]);
						valCfg.yScramblingIterations = nYSIter;
						continue;
					}
					catch (Exception e)
					{	
					}
				}
				errors.append("Incorrect Y-scrambling (YS) valiadation: " + tokens[i] + "\n");
				nErrors++;
				continue;
			}
			
			if (tokens[i].toLowerCase().startsWith("boot"))
			{
				String t[] = tokens[i].split("-");
				if (t.length == 2)
				{
					try {
						int nBSIter = Integer.parseInt(t[1]);
						valCfg.bootStrapIterations = nBSIter;
						continue;
					}
					catch (Exception e)
					{	
					}
				}
				errors.append("Incorrect Bootstrap (boot) valiadation: " + tokens[i] + "\n");
				nErrors++;
				continue;
			}
			
			if (tokens[i].toLowerCase().startsWith("cv"))
			{
				CrossValidation cv = new CrossValidation();
				String t[] = tokens[i].split("-");
				if (t.length == 2 || t.length == 3)
				{
					try {
						int nFolds = Integer.parseInt(t[1]);
						cv.numFolds = nFolds;
					}
					catch (Exception e)
					{	
					}
					if (t.length == 3)
					{
						try {
							int nCycles = Integer.parseInt(t[2]);
							cv.numCycles = nCycles;
						}
						catch (Exception e)
						{	
						}
					}
					valCfg.crossValidation = cv;
					continue;
				}
				
				errors.append("Incorrect Cross Valication (CV): " + tokens[i] + "\n");
				nErrors++;
				continue;
			}
			
			errors.append("Unknow validation method: " + tokens[i] + "\n");
			nErrors++;
		}
		
		if (nErrors > 0)
		{	
			System.out.println("Validation specification errors:\n" + errors.toString());
			return null;
		}	
		
		return valCfg;
	}
}
