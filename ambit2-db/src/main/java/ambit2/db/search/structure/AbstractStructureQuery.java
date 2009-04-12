/* AbstractStructureRetrieval.java
 * Author: nina
 * Date: Feb 8, 2009
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
import java.sql.SQLException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IQueryCondition;

public abstract class AbstractStructureQuery<F, T, C extends IQueryCondition> 
			extends AbstractQuery<F, T, C, IStructureRecord> 
		    implements IQueryRetrieval<IStructureRecord>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3149398052063483705L;
	public static enum FIELD_NAMES  {idquery, idchemical,idstructure,selected,metric};
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(rs.getInt(3));
			//metric
			record.setProperty(Property.getInstance(rs.getMetaData().getColumnName(5),toString(),"http://ambit.sourceforge.net"), rs.getFloat(5));
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
