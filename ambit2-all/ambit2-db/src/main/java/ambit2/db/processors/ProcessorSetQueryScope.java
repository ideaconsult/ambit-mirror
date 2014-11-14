/* ProcessorSetQueryScope.java
 * Author: nina
 * Date: Apr 19, 2009
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

package ambit2.db.processors;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.ScopeQuery;

/**
 * Sets a scope to the {@link #query}
 * @author nina
 *
 */
public class ProcessorSetQueryScope extends AbstractDBProcessor<IQueryRetrieval<IStructureRecord>, IQueryRetrieval<IStructureRecord>> {
	protected IQueryRetrieval<IStructureRecord> query;
	public IQueryRetrieval<IStructureRecord> getQuery() {
		return query;
	}

	public void setQuery(IQueryRetrieval<IStructureRecord> query) {
		this.query = query;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6401864475830513693L;

	public IQueryRetrieval<IStructureRecord> process(
			IQueryRetrieval<IStructureRecord> target) throws AmbitException {
		if (query == null) return target;
		if (target == null) return query;
		if (target instanceof ScopeQuery)
			target = ((ScopeQuery)target).getValue();
		if (target == null) return query;
		
		if (query instanceof QueryCombinedStructure) {
			((QueryCombinedStructure) query).setScope(target);
			return query;
		} else {
			QueryCombinedStructure q = new QueryCombinedStructure();
			q.add(query);
			q.setScope(target);
			return q;
		}
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}
