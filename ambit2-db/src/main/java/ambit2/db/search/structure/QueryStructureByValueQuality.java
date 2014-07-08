package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.db.search.StringCondition;

public class QueryStructureByValueQuality extends AbstractStructureQuery<String, QLabel, StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,cast(label as unsigned) as metric,label as text from structure\n"+
		"where idstructure in\n"+
		"(\n"+
		"select idstructure from properties join property_values  using(idproperty) left join quality_labels using(id) where sameas=? %s\n"+
		")\n";
	public final static String where = " and label %s ?";
	public final static String where_null = " and label is null";

	public QueryStructureByValueQuality() {
		super();
		setFieldname(null);
		setValue(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryStructureByValueQuality(QLabel label) {
		super();
		setFieldname(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(label);
	}	
	public QueryStructureByValueQuality(QUALITY label) {
		super();
		setFieldname(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(new QLabel(label));
	}	
	@Override
	public void setFieldname(String fieldname) {
		super.setFieldname(fieldname);
	}
	@Override
	public String getFieldname() {

		return super.getFieldname();
	}
	public String getSQL() throws AmbitException {
		if (getValue()==null)
			return String.format(sql,where_null);
		else
			if (getValue()!=null)
				return  String.format(String.format(sql,where),getCondition());
			else
				return  String.format(sql,where_null);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue().getLabel().toString()));
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getFieldname() + "quality");
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