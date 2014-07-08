package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.search.StringCondition;

/**
 * Retrieves Property-es used by particular dataset
 * @author nina
 *
 */
public class PropertiesByDataset extends AbstractPropertyRetrieval<Template, SourceDataset, StringCondition> {
	public enum QField  {
		name {
			@Override
			public String getSQL(PropertiesByDataset value) {
				return " src_dataset.name %s ?";
			}
		},
		id {
			@Override
			public String getSQL(PropertiesByDataset value) {
				return " id_srcdataset = ?";
			}
		},
		properties {
			@Override
			public String getSQL(PropertiesByDataset value) {
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
		public abstract String getSQL(PropertiesByDataset value);
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -7265871901186884735L;

	public static String wherepropertyName  = " property.name = ?";
	public static String join  = 
		"\njoin template_def using(idproperty) join src_dataset using(idtemplate) where %s ";
	protected static String base_sql_type_confidence = 
		"select properties.idproperty,properties.name,units,title,url,properties.idreference,comments,ptype as idtype,islocal,type,rdf_type,predicate,object,`order` from properties join catalog_references using(idreference)\n"+
		"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") a using(idproperty)";
	
	/**
 * 
 */
	public PropertiesByDataset() {
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
			String wherePropertyID = " ";
			if (getFieldname()!=null) {
				and = " and ";
				String w = QField.properties.getSQL(this);
				wherePropertyID = w==null?"":(and + QField.properties.getSQL(this));
			} 
			
			return base_sql_type_confidence + String.format(join,whereDataset+wherePropertyID) + "\n order by template_def.idproperty";
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
	
	@Override
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			Property p = Property.getInstance(rs.getString(2),rs.getString(4),rs.getString(5));
			p.setId(rs.getInt(1));
			p.setUnits(rs.getString(3));
			p.setLabel(rs.getString(7));
			p.getReference().setId(rs.getInt(6));
			try { p.setOrder(rs.getInt("order"));} catch (Exception x) {}
			try {
				String type = rs.getString(8);
				
				if (type != null) {
					String[] types = type.split(",");
					for (String t:types) //TODO support multiple types per property
						p.setClazz(_PROPERTY_TYPE.valueOf(t).getClazz());
				}
			} catch (Exception x) {}
			try {
				p.setNominal(rs.getBoolean(9));
			} catch (Exception x) { p.setNominal(false);}
			try {
				String _type = rs.getString(10);
				if (_type != null)
				p.getReference().setType(ILiteratureEntry._type.valueOf(_type));
			} catch (Exception x) {}			
			
			PropertyAnnotation<String> a = null;
			for (ANNOTATIONS_TABLE f : ANNOTATIONS_TABLE.values()) try {
				String value = rs.getString(f.name());
				if (value == null) break;
				if (a==null) a = new PropertyAnnotation<String>();
				f.setValue(a, value);
			} catch (Exception x) {}	
			
			if (a!=null) {
				if (p.getAnnotations()==null) p.setAnnotations(new PropertyAnnotations());
				p.getAnnotations().add(a);
			}			
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
