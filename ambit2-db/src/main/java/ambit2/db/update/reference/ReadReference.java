package ambit2.db.update.reference;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadReference  extends AbstractQuery<String, Integer, EQCondition, LiteratureEntry>  implements IQueryRetrieval<LiteratureEntry> {
	protected static String sql = "select idreference,title,url from catalog_references %s";
	protected static String where = "where idreference = ?";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public ReadReference(Integer id) {
		super();
		setValue(id);
	}
	public ReadReference() {
		this(null);
	}
		
	public double calculateMetric(LiteratureEntry object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class, getValue()));
		}
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(sql,getValue()==null?"":where);
	}

	public LiteratureEntry getObject(ResultSet rs) throws AmbitException {
		try {
			return LiteratureEntry.getInstance(rs.getString(2), rs.getString(3),rs.getInt(1));
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All references":String.format("Reference id=%d",getValue());
	}
}
