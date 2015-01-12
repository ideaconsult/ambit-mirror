package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public abstract class AbstractReadDataset<T,M extends ISourceDataset> extends AbstractQuery<T,M,StringCondition,M>  implements IQueryRetrieval<M>{
	protected enum _fields {
		id_srcdataset,
		name,
		user_name,
		idreference,
		title,
		url,
		licenseURI,
		rightsHolder,
		maintainer,
		stars;
		public int getIndex() {
			return ordinal()+1;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6358006970038485516L;

	protected M createObject(String title,LiteratureEntry le,String username) {
		SourceDataset d = new SourceDataset(title,le);
		d.setUsername(username);
		return (M) d;
	}
	
	public M getObject(ResultSet rs) throws AmbitException {
		M d = null;
		try {
			
	        LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(_fields.title.getIndex()),rs.getString(_fields.url.getIndex()));
	        le.setId(rs.getInt(_fields.idreference.name()));
	        d = createObject(rs.getString(_fields.name.name()),le, rs.getString(_fields.user_name.getIndex()));
	        d.setID(rs.getInt(_fields.id_srcdataset.name()));
	        d.setLicenseURI(rs.getString(_fields.licenseURI.name()));
	        d.setrightsHolder(rs.getString(_fields.rightsHolder.name()));
	        d.setMaintainer(rs.getString(_fields.maintainer.name()));
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
        try {
	        d.setStars(rs.getInt(_fields.stars.name()));        	
        } catch (SQLException x) {
        	d.setStars(-1);
        }
        return (M)d;
    }
	
	@Override
	public double calculateMetric(M object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}
}
