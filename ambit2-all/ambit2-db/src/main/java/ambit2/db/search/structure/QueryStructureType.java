package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

public class QueryStructureType extends AbstractStructureQuery<ISourceDataset, IStructureRecord.STRUC_TYPE, StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;
	public final static String sql = 
		"select ? as idquery,structure.idchemical,idstructure,if(type_structure='NA',0,1) as selected," +
		"cast(type_structure as unsigned) as metric,type_structure as text from structure " +
		"%s"+
		"%s %s";

	public final static String where = " where type_structure %s ?";
	public final static String where_null = " where type_structure is null";

	protected final static String join_struc_dataset = "join struc_dataset using(idstructure)\n";
	protected final static String where_struc_dataset = "and id_srcdataset = ?\n";
	
	protected final static String join_struc_query = "join query_results using(idstructure)\n";
	protected final static String where_struc_query = "and idquery = ?\n";
	
	public QueryStructureType() {
		setValue(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryStructureType(IStructureRecord.STRUC_TYPE label) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(label);
	}	

	public String getSQL() throws AmbitException {
		String join = "";
		String where_dataset = null;
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) {
			if (getFieldname() instanceof SourceDataset) {
				join = join_struc_dataset;
				where_dataset = where_struc_dataset;
			}
			else {
				join = join_struc_query;
				where_dataset = where_struc_query;
			}
		};
		
		if (getValue()==null)
			return String.format(sql,join, where_null,"");
		else
			return String.format(sql,join, String.format(where,getCondition()),where_dataset==null?"":where_dataset);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue().toString()));
		
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) {
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		}
				
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Structure type");
		if (getValue()!=null) {
			b.append(getCondition().toString());
			b.append(getValue());
		}
		return b.toString();
	}
	@Override
	public Object retrieveValue(ResultSet rs) throws SQLException {
		if (rs.getObject(5)!=null)
			return IStructureRecord.STRUC_TYPE.values()[rs.getInt(5)-1];
		else return null;
	}
	
	
}
