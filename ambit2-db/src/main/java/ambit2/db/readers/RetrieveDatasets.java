package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class RetrieveDatasets extends AbstractQuery<String,SourceDataset,EQCondition,SourceDataset>  implements IRetrieval<SourceDataset>{
    public static final String select_datasets = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM src_dataset join catalog_references using(idreference) order by name";
    public static final String select_dataset_byname = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM src_dataset join catalog_references using(idreference) WHERE name=?";

	/**
	 * 
	 */
	private static final long serialVersionUID = -7226561858311078951L;

	public String getSQL() throws AmbitException {
		if (getValue() == null)
			return select_datasets;
		else 
			return select_dataset_byname;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> p = new ArrayList<QueryParam>();
		if (getValue() != null)
			p.add(new QueryParam<String>(String.class,getValue().getName()));
		return p;
	}

	public SourceDataset getObject(ResultSet rs) throws AmbitException {
		try {
	        LiteratureEntry le = new LiteratureEntry(rs.getString(5),rs.getString(6));
	        le.setId(rs.getInt(4));
	        SourceDataset d = new SourceDataset(rs.getString(2),le);
	        d.setUsername(rs.getString(3));
	        d.setId(rs.getInt(1));
	        return d;
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
    }
}
