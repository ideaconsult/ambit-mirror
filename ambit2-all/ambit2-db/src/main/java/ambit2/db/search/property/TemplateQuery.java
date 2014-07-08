/* TemplateQuery.java
 * Author: nina
 * Date: Feb 6, 2009
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
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.db.search.StringCondition;

public class TemplateQuery extends AbstractPropertyRetrieval<String, String, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6746077496508519227L;

	public static String SQL = 
			"select idproperty,properties.name,units,title,url,idreference,comments,ptype,islocal,type,`order` " +
			"from template join template_def using(idtemplate) join properties using(idproperty)" +
			" join catalog_references using(idreference) %s";
	
	public static final String SQL_WHERE = "where template.name=?"; 
	public TemplateQuery() {
		setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(SQL,getValue()==null?"":SQL_WHERE);
	}
	@Override
	public String getFieldname() {
		return "name";
	}
	@Override
	public void setFieldname(String fieldname) {
	}

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
}
