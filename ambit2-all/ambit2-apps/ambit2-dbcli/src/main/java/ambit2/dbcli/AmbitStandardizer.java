package ambit2.dbcli;

import java.util.logging.Level;

import ambit2.dbcli.CliOptions._commandmode;

public class AmbitStandardizer extends AmbitPipeline {

	public AmbitStandardizer(CliOptions options) {
		super(options);
	}
	
	public long go(String command, String subcommand) throws Exception {
		try {
			inchi_warmup(3);
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, "MSG_INCHI", new Object[] { "ERROR", x.getMessage() });
		}
		long now = System.currentTimeMillis();
		_commandmode cmd = null;
		try {
			cmd = _commandmode.valueOf(command);
		} catch (Exception x) {
			if ("import".equals(command))
				cmd = _commandmode.imports;
			else
				return -1;
		}
		switch (cmd) {
		
		case standardize: {
			parseCommandStandardize(subcommand, now);
			break;
		}
		
		default:
			break;
		}
		return -1;
	}
}
