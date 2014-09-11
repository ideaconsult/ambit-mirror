package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

/**
 * Retrieves chemicals from set of datasets, defined in the query table
 * restricted by sessions.title 
 * @author nina
 *
 */
public class ChemicalByQueryFolder extends AbstractStructureQuery<String[],Integer,StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1888965075376808332L;
	public final static String sql = 
		"select idchemical,idstructure from query_results join query using(idquery) join sessions using(idsessions) where title in (%s)";
	
	public ChemicalByQueryFolder(String[] folder) {
		super();
		setFieldname(folder);
	}
	
	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String comma = "";
		if (getFieldname()!=null)
			for (String p: getFieldname()) {
				b.append(comma);
				b.append("?");
				comma = ",";
			}
		else throw new AmbitException("No parameters!");
		return String.format(sql,b);
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			for (String p: getFieldname())
				params.add(new QueryParam<String>(String.class,p));
		else throw new AmbitException("No parameters!");
		return params;
	}

	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			record.setIdstructure(rs.getInt(2));
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
