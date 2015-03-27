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

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;


/**
 * StoredQuery (table queries)
 * @author nina
 *
 */
public class StoredQuery implements IStoredQuery {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4144504244895601171L;
	protected String licenseURI;
	protected String  rightsHolder;
	protected String  maintainer;
	protected int stars = 0;
	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	@Override
	public String getrightsHolder() {
		return rightsHolder;
	}
	
	public void setrightsHolder(String uri) {
		this.rightsHolder = uri;
	};
	public String getLicenseURI() {
		return licenseURI;
	}
	public void setLicenseURI(String licenseURI) {
		this.licenseURI = licenseURI;
	}
	protected Integer id;
	protected String name="Default query";
	protected String content=null;
	protected Template template = null;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	protected IQueryObject<IStructureRecord> query;

	protected int rows= 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if ((name!= null) && (name.length()>200))
			this.name = name.substring(0,200);
		else this.name = name;
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
	 * 	insert into query_results (idquery,idstructure,selected,metrix,text) select _IDQUERY_,idstructure,_SELECTED(default 1)_ from ?????
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
	public IQueryObject<IStructureRecord> getQuery() {
		return query;
	}
	public void setQuery(IQueryObject<IStructureRecord> query) {
		this.query = query;
		query.setId(getId());
	}
	public String getSQL() throws AmbitException {
		if (query == null) 
			throw new AmbitException("Query not available!");
		
		return SQL_INSERT + query.getSQL();
	}
	@Override
	public String toString() {
		if (rows>0)
			return getContent() + "["+rows+"]";
		return getContent()==null?String.format("R%d",getId()):getContent();
	}
	public long getPageSize() {
		return query==null?0:query.getPageSize();
	}
	public void setPageSize(long records) {
		if (query!=null) query.setPageSize(records);
	}
	public void setPage(int page) {
		if (query!=null) query.setPage(page);
	}
	public int getPage() {
		return query==null?0:query.getPage();
	}
	public String getKey() {
		return null;
	}
	public String getCategory() {
		return null;
	}
	@Override
	public int getID() {
		return getId();
	}
	@Override
	public void setID(int id) {
		setId(id);
		
	}
	@Override
	public String getSource() {
		return content;
	}
	@Override
	public void setSource(String name) {
		this.content = name;
	}
	@Override
	public boolean supportsPaging() {
		return true;
	}
    public int getStars() {
    	return stars;
    }
    public void setStars(int rating) {
    	this.stars = rating;
    }
    @Override
    public String getStatus() {
    return "published";
    }
    
    @Override
    public void setStatus(String value) {
    //does nothing
    
    }
}


