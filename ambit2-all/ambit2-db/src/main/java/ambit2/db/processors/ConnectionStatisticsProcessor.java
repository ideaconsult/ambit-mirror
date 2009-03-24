package ambit2.db.processors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.base.exceptions.AmbitException;

public class ConnectionStatisticsProcessor extends ConnectionStatusProcessor {
	protected static final String[] sql = {
			"Select concat(idmajor,'.',idminor) as 'Ambit database version' from version",
			"Select concat(DATE(date),' ',time(date)) as 'Ambit database created on' from version",
			"Select count(*) as 'Chemicals' from chemicals",
			"Select count(*) as 'Structures' from structure",
			"Select count(*) as 'Datasets' from src_dataset",
			"select concat('\tDataset\t','Reference\t','\tURL') as 'Datasets list'",
			"select concat('\t',name,'\t',title,'\t',URL,'\n') as 'Dataset' from src_dataset join catalog_references using(idreference)",
			"Select count(*) as 'Properties' from properties",
			"Select count(*) as 'Property values' from property_values",
			"Select count(*) as 'Literature references' from catalog_references",
			"Select count(*) as 'Templates' from template",
			"Select count(*) as 'Template definitions' from template_def",
			"Select count(*) as 'Fingerprints' from fp1024",
			"Select count(*) as 'Users' from users",
			"Select count(*) as 'Queries' from query",			
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
		    		b.append(rs.getString(1));
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
