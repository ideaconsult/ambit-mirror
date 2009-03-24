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

package ambit2.db.search;

import java.util.List;

import ambit2.base.exceptions.AmbitException;


/**
 * StoredQuery (table queries)
 * @author nina
 *
 */
public class StoredQuery implements IStoredQuery {
	protected Integer id;
	protected String name="Default query";
	protected IQueryObject query;
	//protected String sql = null;
	protected int rows= 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public StoredQuery() {
	}
	public StoredQuery(Integer id) {
		setId(id);
	}	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		if (query != null)
			query.setId(id);
	}
	/**
	 * Selects should be in the form 
	 * <pre>
	 * 	insert into query_results (idquery,idstructure,selected) select _IDQUERY_,idstructure,_SELECTED(default 1)_ from ?????
	 * </pre>
	 */
	public List<QueryParam> getParameters() throws AmbitException {
		if (getQuery() == null)
			return null;
		else {
			return getQuery().getParameters();
		}
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public IQueryObject getQuery() {
		return query;
	}
	public void setQuery(IQueryObject query) {
		this.query = query;
		query.setId(getId());
	}
	public String getSQL() throws AmbitException {
		if (query == null) 
			throw new AmbitException("Query not available!");
		
		return SQL_INSERT + query.getSQL();
	}
	
}


