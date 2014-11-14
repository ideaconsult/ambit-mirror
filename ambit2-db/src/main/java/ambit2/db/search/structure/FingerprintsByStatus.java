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
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class FingerprintsByStatus extends AbstractStructureQuery<FPTable, String, StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8554160549010780854L;
	
	public final static String sql =
		"select idchemical from chemicals where idchemical not in (select idchemical from %s where status %s ? or status = 'error')\n";

	public FingerprintsByStatus(FPTable table) {
		super();
		setFieldname(table==null?FPTable.fp1024:table);
		setCondition(StringCondition.getInstance("="));
		setValue("valid");
	}	
	public FingerprintsByStatus() {
		this(FPTable.fp1024);
	}
	public String getSQL() throws AmbitException {
		return String.format(sql,getFieldname().getTable(),getCondition().getSQL());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	@Override
	public String toString() {
		return (getFieldname()==null)?"Fingerprints to be calculated":getFieldname().toString();
	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
