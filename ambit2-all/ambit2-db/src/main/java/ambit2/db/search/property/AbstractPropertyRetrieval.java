/* AbstractPropertyRetrieval.java
 * Author: nina
 * Date: Apr 26, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.db.search.AbstractQuery;

public abstract class AbstractPropertyRetrieval<F, T, C extends IQueryCondition> extends AbstractQuery<F, T, C, Property> 
											implements IQueryRetrieval<Property> {
	public enum PROPERTY_TABLE {
		idproperty,
		name,
		units,
		comments,
		islocal,
		idreference
	}
	public enum REFERENCE_TABLE {
		idreference,
		title,
		url
	}	
	public enum ANNOTATIONS_TABLE {
		rdf_type {
			@Override
			public void setValue(PropertyAnnotation<String>  a,String value) {
				a.setType(value);
			}
		},
		predicate {
			@Override
			public void setValue(PropertyAnnotation<String>  a,String value) {
				a.setPredicate(value);
			}
		},
		object {
			@Override
			public void setValue(PropertyAnnotation<String> a,String value) {
				a.setObject(value);
			}
		};
		public abstract void setValue(PropertyAnnotation<String>  a,String value);
	}		
	public static String base_sql = "select properties.idproperty,properties.name,units,title,url,idreference,comments,ptype as idtype,islocal,type,rdf_type,predicate,object,properties.idproperty as `order` from properties join catalog_references using(idreference)\n"+
			"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") a using(idproperty)\n";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6129319550824253087L;
	public enum SearchMode {
		name,
		alias {
			@Override
			public String getSQL() {
				return "comments";
			}
		},
		idproperty;
		public String getSQL() {
			return toString();
		}
	}	
	protected boolean chemicalsOnly = false;
	protected final static String XSDInt = "http://www.w3.org/2001/XMLSchema#int";
	protected final static String XSDString = "http://www.w3.org/2001/XMLSchema#string";
	protected final static String XSDDouble = "http://www.w3.org/2001/XMLSchema#double";
	public  enum _PROPERTY_TYPE {
		STRING {
			@Override
			public Class getClazz() {
				return String.class;
			}
		},
		NUMERIC {
			@Override
			public Class getClazz() {
				return Number.class;
			}			
			@Override
			public String getXSDType() {
				return XSDDouble;
			}
		};
		public abstract Class getClazz();
		public String getXSDType() {
			return XSDString;
		}
	};
	public boolean isChemicalsOnly() {
	return chemicalsOnly;
	}
	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}	
	public double calculateMetric(Property property) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	
	@Override
	public Property getObject(ResultSet rs) throws AmbitException {
		return getObject(rs,null);

	}
/*
 WARNING: No enum const class ambit2.db.search.property.AbstractPropertyRetrieval$_PROPERTY_TYPE.
 java.lang.IllegalArgumentException: No enum const class ambit2.db.search.property.AbstractPropertyRetrieval$_PROPERTY_TYPE.
         at java.lang.Enum.valueOf(Enum.java:214)
         at ambit2.db.search.property.AbstractPropertyRetrieval$_PROPERTY_TYPE.valueOf(AbstractPropertyRetrieval.java:104)
         at ambit2.db.search.property.AbstractPropertyRetrieval.getObject(AbstractPropertyRetrieval.java:164)
         at ambit2.db.search.property.AbstractPropertyRetrieval.getObject(AbstractPropertyRetrieval.java:141)
         at ambit2.db.search.property.AbstractPropertyRetrieval.getObject(AbstractPropertyRetrieval.java:45)
         at ambit2.db.DbReader$1.next(DbReader.java:159)
         at ambit2.db.processors.AbstractBatchProcessor.process(AbstractBatchProcessor.java:105)
         at ambit2.db.reporters.QueryAbstractReporter.process(QueryAbstractReporter.java:115)
         at ambit2.rest.AbstractObjectConvertor.process(AbstractObjectConvertor.java:41)
         at ambit2.rest.AbstractObjectConvertor.process(AbstractObjectConvertor.java:17)
         at ambit2.rest.query.QueryResource.getRepresentation(QueryResource.java:183)
         at ambit2.rest.AbstractResource.get(AbstractResource.java:98)
         at org.restlet.resource.ServerResource.doHandle(ServerResource.java:519)
         at org.restlet.resource.ServerResource.doNegotiatedHandle(ServerResource.java:579)
         at org.restlet.resource.ServerResource.doConditionalHandle(ServerResource.java:258)
         at org.restlet.resource.ServerResource.handle(ServerResource.java:818)
	
 */
	public Property getObject(ResultSet rs,Property p) throws AmbitException {
		try {
			if (p==null) {
				p = Property.getInstance(rs.getString(2),rs.getString(4),rs.getString(5));
			} else {
				p.setName(rs.getString(2));
				//!!!!!!!!!!!!!!!!!! update the rest 
			}
			try { p.setOrder(rs.getInt("order"));} catch (Exception x) {}
			p.setId(rs.getInt(1));
			p.setUnits(rs.getString(3));
			p.setLabel(rs.getString(7));
			p.getReference().setId(rs.getInt(6));

			try {
				String type = rs.getString(8);
				String[] types = null;
				if (type != null && !"".equals(type)) {
					types = type.split(",");
					for (String t:types) try {
						p.setClazz(_PROPERTY_TYPE.valueOf(t).getClazz());
					} catch (Exception x) {
						logger.log(Level.WARNING,x.getMessage(),t);
					}
				}
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
			}
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
