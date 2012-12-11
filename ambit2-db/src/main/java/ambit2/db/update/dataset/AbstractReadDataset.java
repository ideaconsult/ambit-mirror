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
	protected enum _fields {
		id_srcdataset,
		name,
		user_name,
		idreference,
		title,
		url,
		licenseURI,
		rightsHolder,
		stars;
		public int getIndex() {
			return ordinal()+1;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6358006970038485516L;

	public SourceDataset getObject(ResultSet rs) throws AmbitException {
		SourceDataset d = null;
		try {
			
	        LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(_fields.title.getIndex()),rs.getString(_fields.url.getIndex()));
	        le.setId(rs.getInt(_fields.idreference.getIndex()));
	        d = new SourceDataset(rs.getString(_fields.name.getIndex()),le);
	        d.setUsername(rs.getString(_fields.user_name.getIndex()));
	        d.setId(rs.getInt(_fields.id_srcdataset.getIndex()));
	        d.setLicenseURI(rs.getString(_fields.licenseURI.getIndex()));
	        d.setrightsHolder(rs.getString(_fields.rightsHolder.getIndex()));
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
        try {
	        d.setStars(rs.getInt(_fields.stars.getIndex()));        	
        } catch (SQLException x) {
        	d.setStars(-1);
        }
        return d;
    }
	

	public double calculateMetric(SourceDataset object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}
}
