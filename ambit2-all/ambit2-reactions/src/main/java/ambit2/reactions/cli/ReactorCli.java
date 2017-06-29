package ambit2.reactions.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class ReactorCli 
{
	public static void main(String[] args) {
		ReactorCli app = new ReactorCli();
    	app.run(args);
    	
	}
	
	public int run(String[] args) {
		//TODO
		return 0;
	}
}
