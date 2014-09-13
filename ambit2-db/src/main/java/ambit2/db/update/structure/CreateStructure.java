/* CreateStructure.java
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
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.chemical.CreateChemical;

public class CreateStructure extends AbstractObjectUpdate<IStructureRecord> {
	protected CreateChemical chemical;
	protected static Property struc_typeProperty = Property.getInstance(AmbitCONSTANTS.STRUCTURETYPE, LiteratureEntry.getInstance(AmbitCONSTANTS.STRUCTURETYPE));
	
	public static final String create_sql = 
		"INSERT INTO structure (idstructure,idchemical,structure,format,updated,user_name,type_structure,preference) values (null,?,compress(?),?,CURRENT_TIMESTAMP,SUBSTRING_INDEX(user(),'@',1),?,?)"
	;

	public CreateStructure(IStructureRecord structure) {
		super(structure);
	}
	public CreateStructure() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null) throw new AmbitException("Structure not defined");
		if (index < chemical.getSQL().length)
			return chemical.getParameters(index);
		else {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			if (getObject().getIdchemical()>0)
				params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
			else
				params1.add(new QueryParam<Integer>(Integer.class, null));	
			String content = getObject().getWritableContent();
			params1.add(new QueryParam<String>(String.class, content==null?"":content));	
			String format = getObject().getFormat();
			params1.add(new QueryParam<String>(String.class, format==null?"SDF":format));
			//struc type
			//Property
			//getObject().getProperty(struc_typeProperty)
			IStructureRecord.STRUC_TYPE structype = (content==null)||("".equals(content.trim()))?
							IStructureRecord.STRUC_TYPE.NA:getObject().getType();
					
			params1.add(new QueryParam<String>(String.class, structype.toString()));
			params1.add(new QueryParam<Integer>(Integer.class, 1000+(10-structype.ordinal())));
			return params1;
		}
		
	}
	

	@Override
	public void setObject(IStructureRecord object) {
		super.setObject(object);
		if (chemical == null) chemical = new CreateChemical();
		chemical.setObject(object);
	}
	public void setID(int index, int id) {
		try {
			if (index < chemical.getSQL().length)
				chemical.setID(index, id);
			else
				getObject().setIdstructure(id);
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}

	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}

	public String[] getSQL() throws AmbitException {
		String[] dataset = chemical.getSQL();
		String[] sql = new String[dataset.length+1];
		for (int i=0; i < dataset.length;i++)
			sql[i]=dataset[i];
		sql[sql.length-1]=create_sql;
		return sql;
	}

}
