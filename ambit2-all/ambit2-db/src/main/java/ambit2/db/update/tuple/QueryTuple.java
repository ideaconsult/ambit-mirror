package ambit2.db.update.tuple;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.PropertiesTuple;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class QueryTuple  extends AbstractQuery<IStructureRecord, SourceDataset, StringCondition, PropertiesTuple>  implements IQueryRetrieval<PropertiesTuple> {
	protected static String sql_chemicals = "select idtuple,id_srcdataset,src_dataset.name,idreference from structure join property_values using(idstructure) join property_tuples using(id) join tuples using(idtuple) join src_dataset using(id_srcdataset) where idchemical=? %s group by idtuple";
	protected static String sql_structure = "select idtuple,id_srcdataset,src_dataset.name,idreference from property_values join property_tuples using(id) join tuples using(idtuple) join src_dataset using(id_srcdataset)  where idstructure = ? %s group by idtuple";
	protected static String where_dataset = "and id_srcdataset=?";
	protected boolean chemicalsOnly = true;
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}
	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public QueryTuple(IStructureRecord id) {
		super();
		setFieldname(id);
	}
	public QueryTuple() {
		this(null);
	}
		
	public double calculateMetric(PropertiesTuple object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Undefined compound");
		List<QueryParam> params = null;
		params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getFieldname().getIdchemical():getFieldname().getIdstructure()));
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		return params;
	}

	public String getSQL() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Undefined compound");
		return String.format(isChemicalsOnly()?sql_chemicals:sql_structure,getValue()==null?"":where_dataset);
	}

	public PropertiesTuple getObject(ResultSet rs) throws AmbitException {
		try {
			if ((getValue()!=null) && (getValue().getId()>0))
				return new PropertiesTuple(rs.getInt(1),getValue());
			else {
				SourceDataset ds = new SourceDataset();
				ds.setId(rs.getInt(2));
				ds.setName(rs.getString(3));
				return new PropertiesTuple(rs.getInt(1),ds);
			}
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}
	@Override
	public String toString() {
		return "Tuples";
	}
}
