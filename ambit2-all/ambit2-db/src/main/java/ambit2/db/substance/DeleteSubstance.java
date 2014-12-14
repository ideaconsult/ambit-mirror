/* DeleteSubstance
 * Author: nina
 * Date: Aug 06, 2013
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: www.ideaconsult.net
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

package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.update.AbstractObjectUpdate;

public class DeleteSubstance extends AbstractObjectUpdate<SubstanceRecord>  {
	
	
	public static final String[] delete_sql = {
		"delete from substance where prefix =? and uuid =unhex(?) "
	};

	public DeleteSubstance(SubstanceRecord substance) {
		super(substance);
	}
	public DeleteSubstance() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getCompanyUUID()==null) throw new AmbitException("Substance UUID not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		String[] uuid = I5Utils.splitI5UUID(getObject().getCompanyUUID());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}
	public void setID(int index, int id) {
		
	}
}
