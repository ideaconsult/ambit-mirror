package ambit2.rest.admin;

import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import ambit2.db.version.AmbitDBVersion;
import ambit2.db.version.DBVersionQuery;

public class DBTextReporter extends QueryReporter<AmbitDBVersion,DBVersionQuery,Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5343958374065342370L;

	@Override
	public void open() throws DbAmbitException {
	}


	@Override
	public void footer(Writer output, DBVersionQuery query) {

	}
	public Object processItem(AmbitDBVersion item) throws AmbitException {
		try {
			getOutput().write(String.format("\n%s\nVersion: %d.%d\nCreated: %s\nNote: %s\n\n",
				item.getDbname(),
				item.getMajor(),item.getMinor(),
				item.getCreated(),
				item.getComments()));
			return item;
	} catch (Exception x) { return null;}
	}
	
	@Override
	public void header(Writer output, DBVersionQuery query) {
		
	}
}
