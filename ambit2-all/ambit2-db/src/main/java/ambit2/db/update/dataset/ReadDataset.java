package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Retrieve {@link SourceDataset} by id
 * @author nina
 *
 */
public class ReadDataset extends AbstractQuery<String,SourceDataset,StringCondition,SourceDataset>  implements IQueryRetrieval<SourceDataset>{
	public static final String select_datasets = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM src_dataset join catalog_references using(idreference) %s order by name";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560670663328542819L;

	public double calculateMetric(SourceDataset object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		else
			params.add(new QueryParam<String>(String.class,getValue().getName()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(select_datasets,
				getValue()==null?
					"":
					getValue().getId()>0?"where id_srcdataset=?":"where name=?");
	}

	public SourceDataset getObject(ResultSet rs) throws AmbitException {
		try {
			
	        LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(5),rs.getString(6));
	        le.setId(rs.getInt(4));
	        SourceDataset d = new SourceDataset(rs.getString(2),le);
	        d.setUsername(rs.getString(3));
	        d.setId(rs.getInt(1));
	        return d;
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
    }
	@Override
	public String toString() {
		return (getValue()!=null?getValue().getId()>0
				?String.format("Dataset: id=%d",getValue().getId())
				:String.format("Dataset: '%s'",getValue().getName())
				:"Datasets");
	}

}
