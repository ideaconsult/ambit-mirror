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

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IQueryCondition;

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
	public static String base_sql = "select idproperty,properties.name,units,title,url,idreference,comments,ptype as idtype,islocal,type from properties join catalog_references using(idreference)";
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
	public Property getObject(ResultSet rs,Property p) throws AmbitException {
		try {
			if (p==null) {
				p = Property.getInstance(rs.getString(2),rs.getString(4),rs.getString(5));
			} else {
				p.setName(rs.getString(2));
				//!!!!!!!!!!!!!!!!!! update the rest 
			}
			p.setId(rs.getInt(1));
			p.setUnits(rs.getString(3));
			p.setLabel(rs.getString(7));
			p.getReference().setId(rs.getInt(6));

			try {
				String type = rs.getString(8);
				if (type != null)
				p.setClazz(_PROPERTY_TYPE.valueOf(type).getClazz());
			} catch (Exception x) {}
			try {
				p.setNominal(rs.getBoolean(9));
			} catch (Exception x) { p.setNominal(false);}
			try {
				String _type = rs.getString(10);
				if (_type != null)
				p.getReference().setType(ILiteratureEntry._type.valueOf(_type));
			} catch (Exception x) {}			
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
