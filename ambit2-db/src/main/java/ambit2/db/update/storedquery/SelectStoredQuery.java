/* SelectStoredQuery.java
 * Author: nina
 * Date: Apr 10, 2009
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
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.IStoredQuery;

/**
 * Sets selected field in query table for a given query
 * @author nina
 *
 */
public class SelectStoredQuery  extends AbstractUpdate<IStoredQuery,IStructureRecord> {
	protected boolean selected = true;
	public SelectStoredQuery() {
		this(true);
	}
	public SelectStoredQuery(boolean selected) {
		this.selected = selected;
	}	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static final String[] select_sql = {
		"update query_results,query, sessions set selected= ?\n"+
		"where query_results.idquery=? and idstructure=? and query_results.idquery=query.idquery\n"+
		"and query.idsessions=sessions.idsessions and user_name=SUBSTRING_INDEX(user(),'@',1)"
	};
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null) throw new AmbitException("Query not defined");
		if (getObject()==null) throw new AmbitException("Structure not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Boolean>(Boolean.class, isSelected()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getIdstructure()));	
		
		return params;
	}

	public String[] getSQL() throws AmbitException {
		return select_sql;
	}

	public void setID(int index, int id) {
		
	}

}
