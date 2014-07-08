/* UpdateStructure.java
 * Author: nina
 * Date: Mar 31, 2009
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

package ambit2.db.update.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.update.AbstractObjectUpdate;

public class UpdateStructure extends AbstractObjectUpdate<IStructureRecord> {
	
	public static final String[] create_sql = {
		"UPDATE structure set structure=compress(?),format=?,user_name=SUBSTRING_INDEX(user(),'@',1),type_structure=?,atomproperties=NULL,updated=CURRENT_TIMESTAMP where idstructure=?"
	};

	public UpdateStructure(IStructureRecord structure) {
		super(structure);
	}
	public UpdateStructure() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class, getObject().getWritableContent()==null?"":getObject().getWritableContent()));		
		params1.add(new QueryParam<String>(String.class, getObject().getFormat()==null?"INC":getObject().getFormat()));
		params1.add(new QueryParam<String>(String.class,  getObject().getType()==null?STRUC_TYPE.NA.name():getObject().getType().toString()));
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdstructure()));		
		return params1;
		
	}
	public void setID(int index, int id) {
		getObject().setIdstructure(id);

	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
}
