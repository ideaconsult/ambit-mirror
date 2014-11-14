package ambit2.db.update.reference;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

/**
 * Retrieve references (by id or all)
 * @author nina
 *
 */
public class ReadReference  extends AbstractQuery<String, Integer, EQCondition, ILiteratureEntry>  implements IQueryRetrieval<ILiteratureEntry> {
	protected static String sql = "select idreference,title,url,type from catalog_references %s";
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
		
	public double calculateMetric(ILiteratureEntry object) {
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

	public ILiteratureEntry getObject(ResultSet rs) throws AmbitException {
		try {
			ILiteratureEntry le =  new LiteratureEntry(rs.getString(2), rs.getString(3));
			le.setId(rs.getInt(1));
			try {
				le.setType(_type.valueOf(rs.getString(4)));
			} catch (Exception x) {
				le.setType(_type.Unknown);
			}
			return le;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All references":String.format("Reference id=%d",getValue());
	}
}
