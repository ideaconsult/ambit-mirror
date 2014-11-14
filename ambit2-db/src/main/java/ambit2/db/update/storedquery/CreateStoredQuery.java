/* CreateStoredQuery.java
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
import ambit2.base.data.AmbitUser;
import ambit2.db.SessionID;
import ambit2.db.search.IStoredQuery;
import ambit2.db.update.queryfolder.CreateQueryFolder;

public class CreateStoredQuery extends AbstractUpdate<SessionID,IStoredQuery> {
	protected CreateQueryFolder queryFolder;
	public static final String sql_byname = "insert ignore into query (idquery,idsessions,name,content) select ?,idsessions,?,? from sessions where title=?";
	public static final String sql_byid = "insert ignore into query (idquery,idsessions,name,content) select ?,idsessions,?,? from sessions where idsessions=?";

	
	public CreateStoredQuery() {
		super();
		queryFolder = new CreateQueryFolder();
	}
	@Override
	public void setGroup(SessionID id) {
		super.setGroup(id);
		queryFolder.setObject(id);
		queryFolder.setGroup(null);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case 0:{
			return queryFolder.getParameters(0);
		}
		case 1:{
			List<QueryParam> params = new ArrayList<QueryParam>();
			if ((getObject().getId()==null) || (getObject().getId()<=0))
				params.add(new QueryParam<Integer>(Integer.class, null));
			else
				params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
				
			params.add(new QueryParam<String>(String.class, getObject().getName()));
			params.add(new QueryParam<String>(String.class, getObject().getSQL()));
			if ((getGroup()==null)||(getGroup().getId()==null))
				params.add(new QueryParam<String>(String.class, getGroup()==null?"Default":getGroup().getName()));
			else
				params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
			
			return params;			
		}
		case 2: {
			return getObject().getQuery().getParameters();
		}
		default: throw new AmbitException(String.format("invalid id %d",index));
		}
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {
				queryFolder.getSQL()[0],
				(getGroup()==null)||(getGroup().getId()==null)?sql_byname:sql_byid,
				getObject().getSQL()
		};
	}

	public void setID(int index, int id) {
		switch (index) {
		case 0: { 
			/*
			SessionID s = getGroup()==null?new SessionID(id):getGroup();
			s.setId(id);
			setGroup(s);
			queryfolder.setObject(s);
			queryfolder.setID(0,id);
			*/ 
			break;
		}
		case 1: { getObject().setId(id); break;}
		}
	}
	
	public AmbitUser getUser() {
		return queryFolder.getGroup();
	}
	public void setUser(AmbitUser user) {
		queryFolder.setGroup(user);
	}	

}
