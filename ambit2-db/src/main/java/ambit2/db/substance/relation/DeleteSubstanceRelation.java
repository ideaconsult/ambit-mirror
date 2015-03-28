/* DeleteSubstanceRelation
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

package ambit2.db.substance.relation;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.chemrelation.AbstractUpdateStructureRelation;

/**
 * Deletes row from substance_relation table
 * @author nina
 *
 */
public class DeleteSubstanceRelation extends AbstractUpdateStructureRelation<SubstanceRecord,IStructureRecord,STRUCTURE_RELATION,Proportion> {


	public static final String[] delete_sql = {"delete from substance_relation where idsubstance=? and idchemical=? and relation=?"};
	
	public static final String[] delete_composition = {"delete r from substance_relation r, substance s where s.idsubstance=r.idsubstance and s.prefix =? and s.uuid =unhex(?)"};

	public DeleteSubstanceRelation() {
		this(null,null,null);
	}
	public DeleteSubstanceRelation(SubstanceRecord structure1,IStructureRecord structure2,STRUCTURE_RELATION relation) {
		super(structure1,structure2,relation,null);
	}
	
	public String[] getSQL() throws AmbitException {
		if (getObject()!=null) 
			return delete_sql;
		else return delete_composition;
	}
	public void setID(int index, int id) {
		
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getObject()!=null) {
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdsubstance()));
			params.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
			params.add(new QueryParam<String>(String.class, getRelation().name()));
		} else {
			String[] uuid = new String[]{null,getGroup().getSubstanceUUID()};
			uuid = I5Utils.splitI5UUID(getGroup().getSubstanceUUID());
			params.add(new QueryParam<String>(String.class, uuid[0]));
			params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		}
		return params;
	}
}
