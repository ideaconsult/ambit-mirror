package ambit2.db.readers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public class ReadChemicalIds  extends AbstractQuery<Object,IStructureRecord,EQCondition,IStructureRecord> implements IQueryRetrieval<IStructureRecord>  {
	private static String sql = "select idchemical,inchi,smiles,formula,inchikey,label,lastmodified from chemicals where idchemical=?";
	/**
	 * 
	 */
	private static final long serialVersionUID = 2064999650126789112L;

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
		return params;		
	}
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		IStructureRecord r = getValue();
		try {
			r.setIdchemical(rs.getInt("idchemical"));
			r.setFormula(rs.getString("formula"));
			r.setInchi(rs.getString("inchi"));
			r.setInchiKey(rs.getString("inchikey"));
			r.setSmiles(rs.getString("smiles"));
			return r;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}
	
	

}
