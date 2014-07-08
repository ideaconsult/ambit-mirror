package ambit2.db.cache;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.EQCondition;

/**
 * Returns results of a previously executed query (e.g. smarts)
 * @author nina
 *
 */
public class QueryCachedResultsBoolean extends QueryCachedResults<Boolean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5484965371846346063L;
	/**
	 * 
	 */

	

	public QueryCachedResultsBoolean() {
		setCondition(EQCondition.getInstance());
	}
	public QueryCachedResultsBoolean(String category,String key, IStructureRecord record) {
		super(category,key,record);
		what = "metric";
	}	



	public Boolean getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getBoolean(1);
		} catch (SQLException x) { throw new AmbitException(x);}
	}
}