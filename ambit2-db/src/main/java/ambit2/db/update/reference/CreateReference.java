/* CreateReference.java
 * Author: nina
 * Date: Mar 28, 2009
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

package ambit2.db.update.reference;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateReference extends AbstractObjectUpdate<ILiteratureEntry>{
	public static final String[] create_sql = {
		"INSERT IGNORE INTO catalog_references (idreference, title, url,type) VALUES (?,?,?,?)",
	};

	public CreateReference(ILiteratureEntry ref) {
		super(ref);
	}
	public CreateReference() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getId()>0?getObject().getId():null));
		params1.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params1.add(new QueryParam<String>(String.class, getObject().getURL()));		
		params1.add(new QueryParam<String>(String.class, getObject().getType().name()));
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	public void setID(int index, int id) {
		getObject().setId(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
