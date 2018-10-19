package ambit2.groupcontribution.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;


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
				return "c";
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
				return "i";
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
	
	public int run(String[] args) 
	{
		return 0;
	}

}
