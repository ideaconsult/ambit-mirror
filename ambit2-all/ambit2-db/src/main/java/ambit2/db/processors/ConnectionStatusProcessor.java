package ambit2.db.processors;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;


public class ConnectionStatusProcessor<T> extends AbstractDBProcessor<T,StringBuffer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3836666411270315603L;

	public StringBuffer process(T target) throws AmbitException {
	    StringBuffer b = new StringBuffer();
	    if (target != null)
	    	b.append(target.toString());
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
	protected StringBuffer print(ResultSet rs, StringBuffer b) throws SQLException {
		if (rs.getMetaData().getColumnCount()==1) {
    		while (rs.next()) {	
	    		b.append(rs.getMetaData().getColumnLabel(1));
		    	b.append('\t');
		    	b.append(rs.getString(1));
	    		b.append('\n');
    		}	    				
		} else {
			b.append('\n');
			for (int j=0; j < rs.getMetaData().getColumnCount();j++) {
				b.append(rs.getMetaData().getColumnLabel(j+1));
				b.append('\t');
			}
    		b.append('\n');	    				
    		while (rs.next()) {	
    			for (int j=0; j < rs.getMetaData().getColumnCount();j++) {
		    		b.append(rs.getString(j+1));
		    		b.append('\t');					    		
    			}
	    		b.append('\n');
    		}
		}		
		return b;
	}
}
