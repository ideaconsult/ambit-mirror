package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class PropertiesByQuery  extends AbstractPropertyRetrieval<Property, IStoredQuery, StringCondition> {
	public enum QField  {
		name {
			@Override
			public String getSQL() {
				return " name %s ?";
			}
		},
		id {
			@Override
			public String getSQL() {
				return " idquery = ?";
			}
		};
		public abstract String getSQL();
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -7265871901186884735L;
	public static String whereproperty  = " idproperty = ?";
	public static String wherepropertyName  = " property.name = ?";
	public static String join  = " join property_values using(idproperty) join template_def using(idproperty) join query using(idtemplate) where %s %s %s group by idproperty";

	
	/**
 * 
 */
	public PropertiesByQuery() {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setFieldname(null);
		setValue(null);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()!=null) {
			List<QueryParam> params = new ArrayList<QueryParam>();
			if (getValue().getId() > 0) 
				params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
			else if (getValue().getName()!=null) 
				params.add(new QueryParam<String>(String.class, getValue().getName()));
			else throw new AmbitException(getValue().toString()); 
			if (getFieldname()!=null)
				params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
			return params;
		} else return null;
		
	}
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			String whereDataset;
			String and = "";
			if (getValue().getId() >  0) {
				whereDataset = QField.id.getSQL();
			} else if (getValue().getName()!=null) {
				whereDataset = String.format(QField.name.getSQL(),getCondition().getSQL());
			} else throw new AmbitException(getValue().toString());
			String wherePropertyID = "";
			if (getFieldname()!=null) {
				and = " and ";
				wherePropertyID = whereproperty;
			}
			
			return PropertiesByDataset.base_sql_type + String.format(join,whereDataset,and,wherePropertyID);
		}
		throw new AmbitException("No dataset defined");
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
		return Property.class;
	}
	public Class getValueType() {
		return SourceDataset.class;
	}
}