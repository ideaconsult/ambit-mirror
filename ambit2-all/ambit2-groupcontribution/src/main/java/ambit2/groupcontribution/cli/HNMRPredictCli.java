package ambit2.groupcontribution.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import ambit2.groupcontribution.cli.GroupContributionCli._option;

public class HNMRPredictCli {

	private static final String title = "HNMR predict";
	public String configFile = null;

	public static void main(String[] args) 
	{


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
				return "Input HNMR configuration (.ini or .json) file";
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


	public void setOption(_option option, String argument) throws Exception 
	{
		if (argument != null)
			argument = argument.trim();
		switch (option) {

		case config: 
			if ((argument == null) || "".equals(argument.trim()))
				return;
			configFile = argument;
			break;
		}	

	}	
		
}