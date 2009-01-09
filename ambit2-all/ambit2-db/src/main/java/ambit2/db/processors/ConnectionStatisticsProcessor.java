package ambit2.db.processors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.core.exceptions.AmbitException;

public class ConnectionStatisticsProcessor extends ConnectionStatusProcessor {
	protected static final String[] sql = {
			"Select count(*) as 'Datasets' from src_dataset",
			"Select count(*) as 'Chemical compounds' from chemicals",
			"Select count(*) as 'Properties' from properties",
			"Select count(*) as 'Literature references' from catalog_references",
			"Select count(*) as 'Descriptor specifications' from descriptors",
			"Select count(*) as 'Study templates' from template"
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -2178161153920499747L;
	@Override
	public StringBuffer process(String target) throws AmbitException {
		StringBuffer b = new StringBuffer();
		b.append(super.process(target));
		b.append('\n');
		try {
	    	Statement st = connection.createStatement();
	    	ResultSet rs = null;
	    	for (int i=0;i < sql.length;i++) {
	    		rs = st.executeQuery(sql[i]);
	    		if (rs != null) {
	    		while (rs.next()) {	
		    		b.append(rs.getMetaData().getColumnName(1));
		    		b.append('\t');
		    		b.append(rs.getInt(1));
		    		b.append('\n');
	    		}
	    		rs.close();
	    		}
	    		rs = null;
	    		
	    	}
	    	st.close();
	    	st = null;
	    	return b;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
