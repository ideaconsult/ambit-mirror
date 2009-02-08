package ambit2.db.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;

/**
 * Search for smiles, inchi, formula
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructure extends AbstractStructureQuery<String,String,StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1446001922520148275L;

	public QueryStructure() {
		setCondition(StringCondition.getInstance("="));
	}

	
	public final static String sqlSMILES = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure join chemicals using(idchemical) where ";
	
	public String getSQL() throws AmbitException {
		return sqlSMILES + getFieldname() + " " + getCondition().getSQL() + " ?";
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(rs.getInt(3));
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
