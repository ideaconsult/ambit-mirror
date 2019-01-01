package ambit2.dbcli.exceptions;

public class InvalidCommand extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 211135729399347009L;

	public InvalidCommand(String command) {
		super("Invalid command "+ command);
	}
}
