/* CreateProperty.java
 * Author: nina
 * Date: Mar 30, 2009
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

package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.reference.CreateReference;

public class CreateProperty extends AbstractObjectUpdate<Property> {
	protected CreateReference createReference;
	public static final String create_sql = 
		"INSERT IGNORE INTO properties (idproperty,idreference,name,units,comments,islocal,ptype) SELECT ?,idreference,?,?,?,?,? from catalog_references where title=?"
	;

	public CreateProperty(Property property) {
		super(property);
	}
	public CreateProperty() {
		this(null);
	}		
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
	@Override
	public void setObject(Property object) {
		super.setObject(object);
		if (object != null ) {
			if (createReference == null) createReference = new CreateReference(object.getReference());
			else createReference.setObject(getObject().getReference());
		} 
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (index < createReference.getSQL().length)
			return createReference.getParameters(index);
		else {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			if (getObject().getId()>0)
				params1.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
			else
				params1.add(new QueryParam<Integer>(Integer.class, null));
			params1.add(new QueryParam<String>(String.class, getObject().getName()));
			params1.add(new QueryParam<String>(String.class, getObject().getUnits()));
			params1.add(new QueryParam<String>(String.class, getObject().getLabel()));
			params1.add(new QueryParam<Boolean>(Boolean.class, getObject().isNominal()));
			params1.add(new QueryParam<String>(String.class, 
					getObject().getClazz()==null?null:
					getObject().getClazz().equals(String.class)?"STRING":"NUMERIC" ));
			params1.add(new QueryParam<String>(String.class, getObject().getReference().getTitle()));
			return params1;
		}
	}

	public String[] getSQL() throws AmbitException {
		String[] ref = createReference.getSQL();
		String[] sql = new String[ref.length+1];
		for (int i=0;i< ref.length;i++)
			sql[i] = ref[i];
		sql[ref.length] = create_sql;
		return sql;
	}
	public void setID(int index, int id) {
		try {
			if (index < createReference.getSQL().length)
				createReference.setID(index, id);
			else getObject().setId(id);
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		
	}

}
