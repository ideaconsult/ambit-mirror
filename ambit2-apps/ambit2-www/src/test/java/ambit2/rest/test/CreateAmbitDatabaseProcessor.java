package ambit2.rest.test;

import net.idea.restnet.db.CreateDatabaseProcessor;
import ambit2.db.DBVersion;

public class CreateAmbitDatabaseProcessor extends CreateDatabaseProcessor {


	private static final long serialVersionUID = -335737998721944578L;
	public static final String SQLFile = "ambit2/db/sql/create_tables.sql";
	
	public CreateAmbitDatabaseProcessor() {
		super();
	}
	
	protected String getVersion() {
		return String.format("%s.%s",DBVersion.AMBITDB_VERSION_MAJOR,DBVersion.AMBITDB_VERSION_MINOR);
	}
	
	@Override
	public synchronized String getSQLFile() {
		return SQLFile;
	}

}
