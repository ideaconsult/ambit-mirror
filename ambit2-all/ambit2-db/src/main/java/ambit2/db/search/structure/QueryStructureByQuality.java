package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class QueryStructureByQuality extends AbstractStructureQuery<IStructureRecord, QLabel, StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,1 as selected,cast(quality_structure.label as unsigned) as metric,quality_structure.label from structure left join quality_structure using(idstructure) ";
	public final static String where = " where quality_structure.label %s ?";
	public final static String where_null = " where quality_structure.label is null";

	public QueryStructureByQuality() {
		setValue(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryStructureByQuality(QLabel label) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(label);
	}	
	public QueryStructureByQuality(QUALITY label) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(new QLabel(label));
	}	
	public String getSQL() throws AmbitException {
		if (getValue()==null)
			return sql + where_null;
		else
			return String.format(sql+where,getCondition());
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue().getLabel().toString()));
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Structure quality");
		if (getValue()!=null) {
			b.append(getCondition().toString());
			b.append(getValue());
		}
		return b.toString();
	}
	@Override
	public Object retrieveValue(ResultSet rs) throws SQLException {
		if (rs.getObject(5)!=null)
			return new QLabel(QUALITY.values()[rs.getInt(5)-1]);
		else return null;
	}
	public AmbitUser getUser() {
		if (getValue()!=null)
			return getValue().getUser();
		else return null;
	}
	public void setUser(AmbitUser user) {
		if (getValue() == null) setValue(new QLabel());
		getValue().setUser(user);
	}
	
}
