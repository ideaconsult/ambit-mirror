/* ProcessorCreateQueryFunctionalGroups.java
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

package ambit2.db.processors;

import java.util.ArrayList;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyTemplateStats;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.property.PropertyCount;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QueryFunctionalGroups;

public class ProcessorCreateProfileQuery extends
		AbstractDBProcessor<Profile<Property>, IQueryRetrieval<IStructureRecord>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3849627442305332766L;
	protected Boolean tag = true;

	public Boolean getTag() {
		return tag;
	}

	public void setTag(Boolean tag) {
		this.tag = tag;
	}

	public IQueryRetrieval<IStructureRecord> process(Profile<Property> target) throws AmbitException {
		
		
		if ((target.getName()== null) && (target.size()>0)) {
			QueryCombinedStructure query = new QueryCombinedStructure();
			for (Property p:target.values()) {
				QueryFieldNumeric f = new QueryFieldNumeric();
				f.setFieldname(p);
				f.setCondition(NumberCondition.getInstance("between"));
				query.add(f);				
			}
			return query;
				
		} else {
			
			PropertyCount p = new PropertyCount();
			p.setFieldname(PropertyCount.PropertyCriteria.template.toString());
			p.setValue(target.getName());
			AmbitRows<PropertyTemplateStats> rows = new AmbitRows<PropertyTemplateStats>();
			rows.setConnection(getConnection());
			ArrayList<PropertyTemplateStats> list = new ArrayList<PropertyTemplateStats>();
			try {
				rows.setQuery(p);
				while (rows.next()) 
					list.add(rows.getObject());
				if (list.size()>0) {
					QueryFunctionalGroups query = new QueryFunctionalGroups();
					query.setValue(getTag()); //all fields, enabled or disabled
					query.setFieldname(list);
					return query;
				} else throw new AmbitException("No properties");
			} catch (Exception x) {
				throw new AmbitException(x);
			} finally {
				try {rows.close();} catch (Exception x) {}
			}
		}

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
