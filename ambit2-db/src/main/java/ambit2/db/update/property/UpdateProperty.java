/* UpdateProperty.java
 * Author: nina
 * Date: Mar 30, 2009
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

package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.db.update.AbstractObjectUpdate;

public class UpdateProperty extends AbstractObjectUpdate<Property> {

	enum _props {
		name {
			@Override
			public String getSQL() {
				return "name=?";
			}
			@Override
			public boolean isEnabled(Property p) {
				return p.getName()!=null;
			}
			@Override
			public QueryParam getParam(Property p) {
				
				return new QueryParam<String>(String.class, p.getName());
			}
		},
		comments {
			@Override
			public String getSQL() {
				return "comments=?";
			}
			@Override
			public boolean isEnabled(Property p) {
				return p.getLabel()!=null;
			}
			@Override
			public QueryParam getParam(Property p) {
				
				return new QueryParam<String>(String.class, p.getLabel());
			}
		},
		units {
			@Override
			public String getSQL() {
				return "units=?";
			}
			@Override
			public boolean isEnabled(Property p) {
				return p.getUnits()!=null;
			}
			@Override
			public QueryParam getParam(Property p) {
				return new QueryParam<String>(String.class, p.getUnits());

			}
		},
		ptype {
			@Override
			public String getSQL() {
				return "ptype=?";
			}
			@Override
			public boolean isEnabled(Property p) {
				return p.getClazz()!=null;
			}
			@Override
			public QueryParam getParam(Property p) {
				return new QueryParam<String>(String.class, p.getClazz().equals(String.class)?"STRING":"NUMERIC");

			}
		},		
		isLocal {
			@Override
			public String getSQL() {
				return "isLocal =?";
			}
			@Override
			public boolean isEnabled(Property p) {
				return true;
			}
			@Override
			public QueryParam getParam(Property p) {
				return new QueryParam<Boolean>(Boolean.class, p.isNominal());
			}
		};
		public abstract String getSQL();
		public abstract boolean isEnabled(Property p);
		public abstract QueryParam getParam(Property p);

	}
	public static final String update_sql = 
		"update properties,catalog_references set %s where idproperty=? and properties.idreference=catalog_references.idreference";

	public UpdateProperty(Property property) {
		super(property);
	}
	public UpdateProperty() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (_props p : _props.values()) 
			if (p.isEnabled(getObject())) {
				params.add(p.getParam(getObject()));
			}

			
		if (params.size()==0) throw new AmbitException("No parameters!");
		else
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "";
		for (_props p : _props.values()) {
			if (p.isEnabled(getObject())) {
				b.append(d);
				b.append(p.getSQL());
				d = ",";
			}
		}
		return new String[] {String.format(update_sql,b.toString())};
	}
	public void setID(int index, int id) {
			
	}
}
