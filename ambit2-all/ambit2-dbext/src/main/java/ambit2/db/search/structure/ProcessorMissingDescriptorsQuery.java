/* ProcessorMissingDescriptorsQuery.java
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

import java.util.Iterator;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Creates a query to retrieve structures without given list of descriptors
 * @author nina
 *
 */
public class ProcessorMissingDescriptorsQuery {
	protected long maxRecords;
	public ProcessorMissingDescriptorsQuery() {
		this(10000);
	}	
	public ProcessorMissingDescriptorsQuery(long maxRecords) {
		super();
		this.maxRecords = maxRecords;
	}
	public IQueryObject<IStructureRecord> process(Profile<Property> descriptors)
			throws AmbitException {
		QueryCombinedStructure query = new QueryCombinedStructure();
		query.setPageSize(maxRecords);
		query.setCombine_as_and(false);
		Iterator<Property> i = descriptors.getProperties(true);
		while (i.hasNext()) {
			try {
				Property p = i.next();
				if (p.isEnabled()) {
					QueryMissingDescriptor q = new QueryMissingDescriptor();
					q.setFieldname(LiteratureEntry.getInstance(p.getClazz().getName()));
					query.add(q);
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
			
		}    	
		return query;
	}
}
