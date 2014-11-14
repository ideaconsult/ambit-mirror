/* UpdateSelectedRecords.java
 * Author: nina
 * Date: Apr 12, 2009
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

package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.ISelectableRecords;
import ambit2.db.search.IStoredQuery;

public class UpdateSelectedRecords extends AbstractUpdate<IStoredQuery,ISelectableRecords.SELECTION_MODE> {

	public static final String[] update_sql = {
		"update query_results,query, sessions set %s\n"+
		"where query_results.idquery=? and query_results.idquery=query.idquery\n"+
		"and query.idsessions=sessions.idsessions and user_name=SUBSTRING_INDEX(user(),'@',1) %s"
	};
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null) throw new AmbitException("Query not defined");
		if (getObject()==null) throw new AmbitException("Selection value not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		switch (getObject()) {
		case INVERT_SELECTIONS: {
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));				
			break;
		}
		case SELECT_ALL: {
			params.add(new QueryParam<Boolean>(Boolean.class, true));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));				
			break;
		}
		case UNSELECT_ALL: {
			params.add(new QueryParam<Boolean>(Boolean.class, false));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));				
			break;
		}		
		default: {
			return null;
		}
		}		
		
		return params;
	}

	public String[] getSQL() throws AmbitException {
		if (getObject()==null) return null;
		switch (getObject()) {
		case INVERT_SELECTIONS: {
			return new String[] {String.format(update_sql[0],"selected = !selected","")}; 
		}
		case SELECT_ALL :{
			return new String[] {String.format(update_sql[0],"selected = ?","and selected=false")};
		}
		case UNSELECT_ALL :{
			return new String[] {String.format(update_sql[0],"selected = ?","and selected=true")};
		}
		default:  return null;
		}
		
	}

	public void setID(int index, int id) {
		
	}
}
