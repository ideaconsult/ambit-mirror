/* DeeleteStructure.java
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

package ambit2.db.chemrelation;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

public class DeleteStructureRelation extends AbstractUpdateStructureRelation<IStructureRecord,IStructureRecord,String,Double> {


	public static final String[] delete_sql = {"delete from chem_relation where idchemical1=? and idchemical2=? and relation=?"};

	public DeleteStructureRelation() {
		this(null,null,null);
	}
	public DeleteStructureRelation(IStructureRecord structure1,IStructureRecord structure2,String relation) {
		super(structure1,structure2,relation,null);
	}
	
	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}
	public void setID(int index, int id) {
		
	}

}
