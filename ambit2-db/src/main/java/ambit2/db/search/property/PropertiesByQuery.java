package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StringCondition;

public class PropertiesByQuery  extends AbstractPropertyRetrieval<Template, IStoredQuery, StringCondition> {
	public enum QField  {
		name {
			@Override
			public String getSQL(PropertiesByQuery q) {
				return " query.name %s ?";
			}
		},
		id {
			@Override
			public String getSQL(PropertiesByQuery q) {
				return " idquery = ?";
			}
		},
		properties {
			@Override
			public String getSQL(PropertiesByQuery value) {
				if (value.getFieldname()==null) return null;
				StringBuilder b = new StringBuilder();
				int count =0;
				String d = " idproperty in (";
				Iterator<Property> p = value.getFieldname().getProperties(true);
				while (p.hasNext()) {
					Property property = p.next();
					if (property.getId()>-1) {
						b.append(d);
						b.append(property.getId());
						d = ",";
						count++;
					}
				}
				b.append(")");
				return count==0?null:b.toString();
			}
		};		
		public abstract String getSQL(PropertiesByQuery q);
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -7265871901186884735L;
	public static String whereproperty  = " idproperty = ?";
	public static String wherepropertyName  = " property.name = ?";
	public static String join  = " join template_def using(idproperty) join query using(idtemplate) where %s ";

	
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
			//if (getFieldname()!=null)
			//	params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
			return params;
		} else return null;
		
	}
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			String whereDataset;
			String and = "";
			if (getValue().getId() >  0) {
				whereDataset = QField.id.getSQL(this);
			} else if (getValue().getName()!=null) {
				whereDataset = String.format(QField.name.getSQL(this),getCondition().getSQL());
			} else throw new AmbitException(getValue().toString());
			String wherePropertyID = "";
			
			if (getFieldname()!=null) {
				and = " and ";
				String w = QField.properties.getSQL(this);
				wherePropertyID = w==null?"":(and + QField.properties.getSQL(this));
			} 
			return PropertiesByDataset.base_sql_type_confidence + String.format(join,whereDataset+wherePropertyID);

			
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