/* ScopeQuery.java
 * Author: nina
 * Date: May 3, 2009
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

package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

/**
 * A convenience class for a query used to restrict the scope of other queries
 * @author nina
 *
 */
public class ScopeQuery extends AbstractQuery<SCOPE,IQueryRetrieval<IStructureRecord>,EQCondition,IStructureRecord> 
												implements IQueryRetrieval<IStructureRecord>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8782175098760501557L;
	
	public ScopeQuery(SCOPE scope) {
		setFieldname(scope);
	}	
	public ScopeQuery() {
		this(SCOPE.scope_entiredb);
	}
	@Override
	public void setFieldname(SCOPE fieldname) {
		super.setFieldname(fieldname);
		setValue(fieldname.createQuery());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() != null) return getValue().getParameters();
		else return null;
	}

	public String getSQL() throws AmbitException {
		if (getValue() != null) return getValue().getSQL();
		else return null;
	}
	@Override
	public void setId(Integer id) {
		if (getValue() != null) getValue().setId(id);
		super.setId(id);
	}
	
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		if (getValue()!=null) return getValue().getObject(rs);
		return null;
	}
	@Override
	public String toString() {
		if (getFieldname()!= null)
			return getFieldname().toString();
		else return "Scope";
	}
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}	
}
