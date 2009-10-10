package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Retrieves Property-es used by particular dataset
 * @author nina
 *
 */
public class PropertiesByDataset extends AbstractPropertyRetrieval<String, SourceDataset, StringCondition> {
	public enum QField  {
		name {
			@Override
			public String getSQL() {
				
				return " where id_srcdataset in (select id_srcdataset from src_dataset where %s %s ?) group by idproperty";
			}
		},
		id {
			@Override
			public String getSQL() {
				return " where id_srcdataset = ?";
			}
		};
		public abstract String getSQL();
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -7265871901186884735L;
	public static String join  = 
		" join property_values using(idproperty) join  property_tuples using(id) join tuples using (idtuple) %s group by idproperty";

	
	/**
 * 
 */
	public PropertiesByDataset() {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setFieldname(QField.name.toString());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()!=null) && (getValue()!=null)) {
			List<QueryParam> params = new ArrayList<QueryParam>();
			if (QField.name.toString().equals(getFieldname()))
				params.add(new QueryParam<String>(String.class, getValue().getName()));
			else if (QField.id.toString().equals(getFieldname()))
				params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
			else return null;
			return params;
		} else return null;
		
	}
	public String getSQL() throws AmbitException {
		if (getFieldname()!=null) {
			String where;
			if (QField.name.toString().equals(getFieldname()))
				where = String.format(QField.name.getSQL(),getFieldname(),getCondition().getSQL());
			else if (QField.id.toString().equals(getFieldname()))
				where = QField.id.getSQL();
			else throw new AmbitException(getFieldname());
			return base_sql + String.format(join,where);
		}
			
		
		return base_sql;
	}
	@Override
	public void setFieldname(String fieldname) {
		try {
			this.fieldname = QField.valueOf(fieldname).toString();
		} catch (Exception x) {
			this.fieldname = QField.name.toString();
		}
	}
	/*
	 
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			Property p = Property.getInstance(rs.getString(2),rs.getString(4),rs.getString(5));
			p.setId(rs.getInt(1));
			p.setUnits(rs.getString(3));
			p.setLabel(rs.getString(7));
			p.getReference().setId(rs.getInt(6));
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	*/
	public String getFieldID() {
		return "id_srcdataset";
	}
	public String getValueID() {
		return "name";
	}
	public Class getFieldType() {
		return SourceDataset.class;
	}
	public Class getValueType() {
		return String.class;
	}
}
