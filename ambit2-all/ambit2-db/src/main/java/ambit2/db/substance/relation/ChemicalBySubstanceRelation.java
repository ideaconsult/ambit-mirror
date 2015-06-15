package ambit2.db.substance.relation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.structure.AbstractStructureQuery;

/**
 * Returns all chemicals which are component of a substance. 
 * Main intention to be used as a scope query for {@link QueryCombined}
 * (e.g. restricting similarity and substructure search to compounds having associated substances)
 * @author nina
 *
 */
public class ChemicalBySubstanceRelation extends AbstractStructureQuery<STRUCTURE_RELATION,String,EQCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = "select idchemical from substance_relation where relation != 'HAS_STRUCTURE'";
	
	public ChemicalBySubstanceRelation() {
		this(null);
	}
	public ChemicalBySubstanceRelation(STRUCTURE_RELATION relation) {
		super();
		setFieldname(relation);
		setCondition(EQCondition.getInstance());
	}	
	public String getSQL() throws AmbitException {
	    	if (getFieldname() ==null) return sql;
	    	else return sql + " where relation=?";
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname().name()));
		return params;
	}
	@Override
	public String toString() {
		return "Chemicals by relation";
	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			record.setIdstructure(-1);
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}