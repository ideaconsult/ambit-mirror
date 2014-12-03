/* QueryUser.java
 * Author: nina
 * Date: Mar 28, 2009
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

package ambit2.db.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;

/**
 * Searches in users table
 * @author nina
 *
 */
public class QueryUser extends AbstractQuery<String,String,StringCondition, AmbitUser> implements IQueryRetrieval<AmbitUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1174549562613435168L;
	public static final String SQL = 
		"SELECT user_name,email,title,firstname,lastname,address,country,homepage,institute,keywords,reviewer FROM ausers ";
	public static final String WHERE = " where %s %s ?";
	
	public QueryUser(String value) {
		this();
		setValue(value);
	}	
	
	public QueryUser() {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setFieldname("user_name");
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue() != null) && (getFieldname() != null)) 
			params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	public String getSQL() throws AmbitException {
		if ((getValue() != null) && (getFieldname() != null))
			return String.format(SQL+WHERE, getFieldname(), getCondition());
		else return SQL;

	}	

	public AmbitUser getObject(ResultSet rs) throws AmbitException {
		try {
			AmbitUser user = new AmbitUser();
			user.setAddress(rs.getString("address"));
			user.setAffiliation(rs.getString("institute"));
			user.setCountry(rs.getString("country"));
			user.setEmail(rs.getString("email"));
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setName(rs.getString("user_name"));
			user.setTitle(rs.getString("title"));
			user.setWww(rs.getString("homepage"));
			//user.setType()
			return user;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(AmbitUser object) {
		return 1;
	
	}
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public String toString() {
		return getValue()==null?"All users":String.format("User %s", getValue());
	}
}
