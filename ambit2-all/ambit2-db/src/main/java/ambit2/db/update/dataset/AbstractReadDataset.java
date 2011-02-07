package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public abstract class AbstractReadDataset<T> extends AbstractQuery<T,SourceDataset,StringCondition,SourceDataset>  implements IQueryRetrieval<SourceDataset>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6358006970038485516L;

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
	

	public double calculateMetric(SourceDataset object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}
}
