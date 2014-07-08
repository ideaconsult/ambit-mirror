package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.search.StringCondition;

/**
 * 
 * @author nina
 *
 */
public class QueryStrucType extends AbstractStructureQuery<IStructureRecord,STRUC_TYPE[],StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1725564560950361449L;

	public QueryStrucType() {
		setValue(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryStrucType(STRUC_TYPE label) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(new STRUC_TYPE[] {label});
	}	
	public QueryStrucType(STRUC_TYPE[] label) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(label);
	}

	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,cast(type_structure as unsigned) as metric,type_structure from structure where %s";
	public final static String where = " type_structure %s ?";

	
	public String getSQL() throws AmbitException {
		if (getValue()==null)
			throw new AmbitException("Structure type not defined");
		else {
			StringBuilder b = new StringBuilder();
			String or = "";
			for (STRUC_TYPE st : getValue()) {
				b.append(or);
				b.append(String.format(where,getCondition().getSQL()));
				or = " or ";
			}
			return String.format(sql,b.toString());
		}
		
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null)
			throw new AmbitException("Structure type not defined");
		else {
			List<QueryParam> params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class, getId()));
			
			for (STRUC_TYPE st : getValue()) 
				params.add(new QueryParam<String>(String.class, st.toString()));
				
			return params;
				
		}
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
			return new QLabel(QUALITY.values()[rs.getInt(5)-1]);
		else return null;
	}
	@Override
	protected void retrieveStrucType(IStructureRecord record, ResultSet rs)
			throws SQLException {
	}
	
}
