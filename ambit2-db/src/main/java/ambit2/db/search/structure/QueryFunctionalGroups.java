/* QueryFunctionalGroups.java
 * Author: nina
 * Date: May 1, 2009
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.PropertyTemplateStats;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.StringCondition;

public class QueryFunctionalGroups  extends AbstractStructureQuery<List<PropertyTemplateStats>,Boolean,StringCondition> {
	protected QueryCombinedStructure query;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5310078588280973557L;
	
	public QueryFunctionalGroups() {
		query = new QueryCombinedStructure();
		query.setScope(null);
		setValue(true);
	}
	public String getSQL() throws AmbitException {
		if (query == null) createQuery();
		return query.getSQL();
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (query == null) createQuery(); 
		return query.getParameters();
	}
	@Override
	public void setFieldname(List<PropertyTemplateStats> stats) {
		super.setFieldname(stats);
		Collections.sort(stats,new Comparator<PropertyTemplateStats>() {
			public int compare(PropertyTemplateStats o1,
					PropertyTemplateStats o2) {

				return (int) (o2.getCount()-o1.getCount());
			}
			
		});
		query = null;
	}
	protected void createQuery() {
		query = new QueryCombinedStructure();
		query.setScope(null);
		for (PropertyTemplateStats p : getFieldname()) {
			if ((getValue()==null) || getValue().equals(p.getProperty().isEnabled())) {
				QueryFieldNumeric f = new QueryFieldNumeric();
				f.setFieldname(p.getProperty());
				f.setValue(1);
				f.setCondition(NumberCondition.getInstance(">="));
				query.add(f);
			}
		}		
	}
	@Override
	public void setId(Integer id) {
		super.setId(id);
		if (query!=null) query.setId(id);
	}
	@Override
	public Integer getId() {
		if (query!=null) query.setId(id);
		return super.getId();
	}
}
