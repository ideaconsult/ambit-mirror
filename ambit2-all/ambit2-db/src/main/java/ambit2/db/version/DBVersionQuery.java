package ambit2.db.version;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.NumberCondition;

public class DBVersionQuery extends AbstractQuery<String, String, NumberCondition, AmbitDBVersion> 
										implements IQueryRetrieval<AmbitDBVersion>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -915159203431471645L;
	public DBVersionQuery() {
		super();
		setPage(0);
		setPageSize(1);
	}
	public List<QueryParam> getParameters() throws AmbitException {

		return null;
	}

	public String getSQL() throws AmbitException {
		return "Select idmajor,idminor,date,comment from version order by idmajor*100000+idminor desc";
	}
	public double calculateMetric(AmbitDBVersion object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	public AmbitDBVersion getObject(ResultSet rs) throws AmbitException {
		
		
		AmbitDBVersion db = new AmbitDBVersion();
		try {
			db.setDbname(rs.getMetaData().getCatalogName(1));
			db.setMajor(rs.getInt(1));
			db.setMinor(rs.getInt(2));
			db.setCreated(rs.getLong(3));
			db.setComments(rs.getString(4));
			return db;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
}
