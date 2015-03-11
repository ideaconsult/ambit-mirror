/* CreateDataset.java
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

package ambit2.db.update.bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateBundle extends AbstractObjectUpdate<SubstanceEndpointsBundle> {
	
	public static final String[] create_sql = {
		"INSERT IGNORE INTO catalog_references (idreference, title, url) VALUES (null,?,?)",
		"INSERT IGNORE INTO bundle (idbundle, name,user_name,idreference,licenseURI,rightsHolder,description,created,bundle_number) SELECT ?,?,?,idreference,?,?,?,now(),unhex(replace(?,'-','')) FROM catalog_references WHERE title=?"
	};

	public CreateBundle(SubstanceEndpointsBundle dataset) {
		super(dataset);
	}
	public CreateBundle() {
		this(null);
	}		
	
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case 0: {
			if (getObject().getSource()==null) throw new AmbitException("Empty source");
			if (getObject().getURL()==null) throw new AmbitException("Empty url");
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getObject().getSource()));
			params1.add(new QueryParam<String>(String.class, getObject().getURL()));
			return params1;
		} 
		case 1: {
			if (getObject().getName()==null) throw new AmbitException("Empty name");
			if (getObject().getSource()==null) throw new AmbitException("Empty source");
			List<QueryParam> params2 = new ArrayList<QueryParam>();
			params2.add(new QueryParam<Integer>(Integer.class, getObject().getID()>0?getObject().getID():null));
			params2.add(new QueryParam<String>(String.class, getObject().getName()));
			params2.add(new QueryParam<String>(String.class, getObject().getUserName()));
			params2.add(new QueryParam<String>(String.class, getObject().getLicenseURI()));
			params2.add(new QueryParam<String>(String.class, getObject().getrightsHolder()));
			params2.add(new QueryParam<String>(String.class, getObject().getDescription()));
			if (getObject().getBundle_number()==null) getObject().setBundle_number(UUID.randomUUID());
			params2.add(new QueryParam<String>(String.class, getObject().getBundle_number().toString()));			
			params2.add(new QueryParam<String>(String.class, getObject().getSource()));
			return params2;
		}
		default: {
			throw new AmbitException("Out of range "+index);
		}
		}
		
	}
	@Override
	public void setID(int index, int id) {
		switch (index) {
		case 0: {
			getObject().getReference().setId(id);
			break;
		}
			
		case 1: {
			getObject().setID(id);
		}
		}
		
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
