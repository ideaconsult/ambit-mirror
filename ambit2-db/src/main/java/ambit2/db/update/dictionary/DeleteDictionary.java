/* DeleteDictionary.java
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

package ambit2.db.update.dictionary;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Dictionary;
import ambit2.db.update.AbstractObjectUpdate;

public class DeleteDictionary extends AbstractObjectUpdate<Dictionary> {

	public static final String[] delete_sql = {"delete d from dictionary as d,template as t1,template as t2 where d.idsubject=t1.idtemplate and d.idobject=t2.idtemplate and t1.name=? and t2.name=?"};

	public DeleteDictionary(Dictionary dictionary) {
		super(dictionary);
	}
	public DeleteDictionary() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getTemplate()));
		params.add(new QueryParam<String>(String.class, getObject().getParentTemplate()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
