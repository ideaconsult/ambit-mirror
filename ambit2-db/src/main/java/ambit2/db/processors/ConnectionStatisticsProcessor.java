package ambit2.db.processors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.idea.modbcum.i.exceptions.AmbitException;

public class ConnectionStatisticsProcessor<T> extends ConnectionStatusProcessor<T> {
	protected boolean metadata = true;
	protected String[] sql = {
			"Select concat(idmajor,'.',idminor,' created on ',DATE(date),' ',time(date)) as 'Ambit database version' from version order by idmajor,idminor desc limit 1",
			//"Select concat(DATE(date),' ',time(date)) as 'Ambit database created on' from version order by idmajor,idminor desc limit 1",
			"Select count(*) as 'Chemicals' from chemicals",
			"Select count(*) as 'Structures' from structure",
			"Select label as \"Quality Label\",count(*) as 'Number of structures' from quality_structure group by label",
			"Select count(*) as 'Datasets' from src_dataset",
			"select concat('\tDataset\t','Reference\t','\tURL') as 'Datasets list'",
			"select concat('\t',name,'\t',title,'\t',URL,'\n') as 'Dataset' from src_dataset join catalog_references using(idreference)",
			"Select count(*) as 'Properties' from properties",
			"Select count(*) as 'Property values' from property_values",
			"Select count(*) as 'Literature references' from catalog_references",
			"Select count(*) as 'Templates' from template",
			"Select count(*) as 'Template definitions' from template_def",
			"Select count(*) as 'Fingerprints' from fp1024",
			"Select count(*) as 'Structural keys' from sk1024",
			"Select count(*) as 'Queries' from query",		
			"select template.name as template,properties.name as property,count(*) as 'Number of entries' from template\n"+
			"join template_def using(idtemplate)\n"+
			"join properties using(idproperty)\n"+
			"join property_values using(idproperty)\n"+
			"group by idtemplate,idproperty"
	};
	public String[] getSql() {
		return sql;
	}
	public void setSql( String[] sql) {
		this.sql = sql;
	}	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2178161153920499747L;
	@Override
	public StringBuffer process(T target) throws AmbitException {
		StringBuffer b = new StringBuffer();
		if (metadata)
		b.append(super.process(target));
		b.append('\n');
		try {
	    	Statement st = connection.createStatement();
	    	ResultSet rs = null;
	    	for (int i=0;i < sql.length;i++) {
	    		rs = st.executeQuery(sql[i]);
	    		if (rs != null) {
	    			print(rs,b);
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
