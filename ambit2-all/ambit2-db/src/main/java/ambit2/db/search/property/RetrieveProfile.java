/* RetrieveProfile.java
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
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class RetrieveProfile extends AbstractPropertyRetrieval<String, Profile<Property>, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -347765028858989655L;
	public static String sql = "select idproperty,name,units,title,url,idreference,comments from properties join catalog_references using(idreference) %s";

	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	public String getSQL() throws AmbitException {
		if (getValue()==null) return sql;
		
		StringBuilder b = new StringBuilder();

		String delimiter = "where name in ( ";
		Iterator<Property> i = getValue().getProperties(true);
		int count = 0;
		while (i.hasNext()) {
			b.append(delimiter);
			b.append('\'');
			b.append(i.next().getName());
			b.append('\'');
			delimiter= ",";
			count++;
		}
		if (count > 0){	
			b.append(")");
			return String.format(sql, b.toString());
		} else return String.format(sql, "");
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
