/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * select ?,idstructure,? from structure_fields as f using (idstructure) join field_names using (idfieldname) where f.idfieldname=? and value=?
 * @author nina
 *
 */
public class QueryFields extends QueryID {
	public final static String sqlField = 
		"insert ignore into query_results (idquery,idstructure,selected) select ?,idstructure,1 from structure_fields join field_names as f using (idfieldname) where f.name=? and value=?";
	
	protected String fieldname;
	protected String fieldValue;
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	@Override
	public void setSQL(String sql) {
		super.setSQL(sqlField);
	}
	@Override
	public String getSQL() {
		return sqlField;
	}
	public List<QueryParam> getParameters() {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<String>(String.class, getFieldValue()));
		return params;
	}
}


