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

package ambit2.db.chemrelation;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;

public class UpdateStructureRelation extends AbstractUpdateStructureRelation<IStructureRecord,IStructureRecord,String,Double> {

	public static final String[] create_sql = {
		"INSERT INTO chem_relation (idchemical1,idchemical2,relation,metric) values(?,?,?,?) on duplicate key update metric=values(metric)"
	};
	public UpdateStructureRelation() {
		this(null,null,null,null);
	}
	public UpdateStructureRelation(IStructureRecord structure1,IStructureRecord structure2,String relation) {
		this(structure1,structure2,relation,null);
	}
	public UpdateStructureRelation(IStructureRecord structure1,IStructureRecord structure2,String relation,Double metric) {
		super(structure1,structure2,relation,metric);
	}

	public void setID(int index, int id) {
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = super.getParameters(index);
		params1.add(new QueryParam<Double>(Double.class, getMetric()));
		return params1;
	}
}
