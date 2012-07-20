/* CreateUser.java
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
REVOKE ALL PRIVILEGES ON * . * FROM 'guest'@'localhost';

REVOKE GRANT OPTION ON * . * FROM 'guest'@'localhost';

GRANT SELECT ,
INSERT ,

UPDATE ,
DELETE ,
INDEX ,
CREATE TEMPORARY TABLES ,
SHOW VIEW ,
EXECUTE ON * . * TO 'guest'@'localhost' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

----
GRANT SELECT , INSERT , UPDATE , INDEX , CREATE TEMPORARY TABLES , CREATE VIEW , SHOW VIEW , EXECUTE ON `ambit2` . * TO 'guest'@'host';
 * @author nina
 *
 */
public class CreateUser extends AbstractObjectUpdate<AmbitUser> {

	public static String[] sql = {
		"insert ignore into roles (role_name) values (?)",
		"insert into users (user_name,password,email,firstname,lastname,registration_date,registration_status,webpage) values (?,MD5(?),?,?,?,now(),\"confirmed\",?) ON DUPLICATE KEY UPDATE password=MD5(?)",
		"insert ignore into user_roles (user_name,role_name) values (?,?)",		
		"GRANT USAGE ON *.* TO ?@? IDENTIFIED BY ?;",
		"GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON *.* TO ?@? IDENTIFIED BY ?;",
		"GRANT EXECUTE ON FUNCTION sortstring TO ?@?;",
		"GRANT EXECUTE ON PROCEDURE findByProperty TO ?@?;"
		//	        st.executeQuery("GRANT EXECUTE ON FUNCTION sql_xtab TO 'guest'@'localhost';");
        //st.close();    	        
        //st.executeQuery("GRANT EXECUTE ON PROCEDURE p_xtab TO 'guest'@'localhost';");
        //st.close();   
	};
	public String[] getSQL() throws AmbitException {
		return sql;
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case 0 :{
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getObject().getType().toString()));
			return params1;
		} 
		case 1: {
			List<QueryParam> params2 = new ArrayList<QueryParam>();
			params2.add(new QueryParam<String>(String.class, getObject().getName()));
			params2.add(new QueryParam<String>(String.class, getObject().getPassword()));
			params2.add(new QueryParam<String>(String.class, getObject().getEmail()));
			params2.add(new QueryParam<String>(String.class, getObject().getFirstName()));
			params2.add(new QueryParam<String>(String.class, getObject().getLastName()));
			params2.add(new QueryParam<String>(String.class, getObject().getWww()));
			params2.add(new QueryParam<String>(String.class, getObject().getPassword()));
			return params2;
		}
		case 2: {
			List<QueryParam> params3 = new ArrayList<QueryParam>();
			params3.add(new QueryParam<String>(String.class, getObject().getName()));
			params3.add(new QueryParam<String>(String.class, getObject().getType().toString()));
			return params3;
		}
		case 3: {
			List<QueryParam> params3 = new ArrayList<QueryParam>();
			params3.add(new QueryParam<String>(String.class, getObject().getName()));
			params3.add(new QueryParam<String>(String.class,"%"));
			params3.add(new QueryParam<String>(String.class, getObject().getPassword()));
			return params3;			
		}
		case 4: {
			List<QueryParam> params3 = new ArrayList<QueryParam>();
			params3.add(new QueryParam<String>(String.class, getObject().getName()));
			params3.add(new QueryParam<String>(String.class,"%"));
			params3.add(new QueryParam<String>(String.class, getObject().getPassword()));
			return params3;					
		}		
		case 5: {
			List<QueryParam> params3 = new ArrayList<QueryParam>();
			params3.add(new QueryParam<String>(String.class, getObject().getName()));
			params3.add(new QueryParam<String>(String.class,"%"));
			return params3;					
		}				
		default : 
			throw new AmbitException("index out of range "+index);
		}
		
	}


	public void setID(int index, int id) {
	
	}
}
