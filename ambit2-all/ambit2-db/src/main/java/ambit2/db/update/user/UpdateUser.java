/* UpdateUser.java
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

package ambit2.db.update.user;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * 
 * @author nina
 *
 */
public class UpdateUser extends AbstractObjectUpdate<AmbitUser> {

	public static final String[] update_sql = {"update users set email=?,title=?,firstname=?,lastname=?,address=?,country=?,webpage=?,affiliation=? where user_name=?"};

	public UpdateUser(AmbitUser user) {
		super(user);
	}
	public UpdateUser() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getEmail()));
		params.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params.add(new QueryParam<String>(String.class, getObject().getFirstName()));
		params.add(new QueryParam<String>(String.class, getObject().getLastName()));
		params.add(new QueryParam<String>(String.class, getObject().getAddress()));
		params.add(new QueryParam<String>(String.class, getObject().getCountry()));
		params.add(new QueryParam<String>(String.class, getObject().getWww()));
		params.add(new QueryParam<String>(String.class, getObject().getAffiliation()));
		params.add(new QueryParam<String>(String.class, getObject().getName()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return update_sql;
	}
	public void setID(int index, int id) {
			
	}

}
