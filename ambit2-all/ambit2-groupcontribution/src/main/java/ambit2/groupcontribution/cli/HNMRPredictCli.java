package ambit2.groupcontribution.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import ambit2.groupcontribution.cli.GroupContributionCli._option;

public class HNMRPredictCli {

	private static final String title = "HNMR predict";
	
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

}
