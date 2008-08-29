package ambit2.db.processors;

import java.sql.SQLException;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;


public class ConnectionStatusProcessor extends AbstractDBProcessor<String,StringBuffer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3836666411270315603L;

	public StringBuffer process(String target) throws AmbitException {
	    StringBuffer b = new StringBuffer();
	    b.append(target);
	    b.append('\n');
	    try {
			java.sql.DatabaseMetaData db = connection.getMetaData();
			b.append(db.getDatabaseProductName());
			b.append('\t');
			b.append(db.getDatabaseProductVersion());
	    	b.append('\n');
	    	
			b.append(db.getDriverName());
			b.append('\t');
			b.append(db.getDriverVersion());
	    	b.append('\n');
	    	
			b.append(db.getCatalogTerm());
			b.append('\t');        	
			b.append(db.getURL());
	    	b.append('\n');
	    	b.append("user\t");
	    	b.append(db.getUserName());
	    	b.append('\n');
	    	        	
	    	b.append('\n');
	    } catch (SQLException x) {
	    	b.append(x.getMessage());
	    }
	    return b;
	}

	public void open() throws DbAmbitException {
	}

}
