/* DatasetDeleteStructure.java
 * Author: nina
 * Date: Apr 1, 2009
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

package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;

public class DatasetDeleteStructure extends AbstractUpdate<SourceDataset,IStructureRecord> {
	
	
	public static final String[] delete_sql = {
		"delete d from struc_dataset d, src_dataset s where d.idstructure=? and s.id_srcdataset=d.id_srcdataset and d.id_srcdataset=? and stars<=5"
	};
	public static final String[] delete_sql_chemical = {
		"delete d from struc_dataset d , structure t where idchemical=? and id_srcdataset=? and d.idstructure=t.idstructure"
	};	

	public DatasetDeleteStructure(SourceDataset dataset,IStructureRecord record) {
		super();
		setGroup(dataset);
		setObject(record);
	}
	public DatasetDeleteStructure() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null || getGroup().getId()<=0) throw new AmbitException("Dataset not defined!");

		if (getObject()==null) throw new AmbitException("Structure not defined!");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getObject().getIdstructure()<=0) {
			if (getObject().getIdchemical()<=0) 
				throw new AmbitException("Compound not defined!");
			else params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		} else params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdstructure()));
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));		
	
		return params1;
		
	}
	public void setID(int index, int id) {
	
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getIdstructure()<=0) 
			if (getObject().getIdchemical()<=0)
				throw new AmbitException("Compound not defined!");
			else return delete_sql_chemical;
		else return delete_sql;
	}
}
