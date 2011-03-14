/* UpdateDataset.java
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

package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

public class UpdateDataset extends AbstractObjectUpdate<SourceDataset> {
	public static final String MSG_EMPTY = "No name or license to update!";
	public static final String[] update_sql = {
		/*
		"INSERT IGNORE INTO catalog_references (idreference, title, url) VALUES (null,?,?)",
		
		"INSERT INTO src_dataset (id_srcdataset, name,user_name,idreference)\n"+
			"SELECT null,?,SUBSTRING_INDEX(user(),'@',1),idreference FROM catalog_references WHERE title=?\n"+
			"ON DUPLICATE KEY UPDATE name=?, src_dataset.idreference = catalog_references.idreference",
			*/
		"update src_dataset set name=?, licenseURI=?, user_name=SUBSTRING_INDEX(user(),'@',1) where id_srcdataset=?"
	};
	public static final String[] update_license = {
		"update src_dataset set licenseURI=?, user_name=SUBSTRING_INDEX(user(),'@',1) where id_srcdataset=?"
	};	
	public static final String[] update_name = {
		"update src_dataset set name=?, user_name=SUBSTRING_INDEX(user(),'@',1) where id_srcdataset=?"
	};	
	public UpdateDataset(SourceDataset dataset) {
		super(dataset);
	}
	public UpdateDataset() {
		this(null);
	}	
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		/*
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params1.add(new QueryParam<String>(String.class, getObject().getURL()));		

		List<QueryParam> params2 = new ArrayList<QueryParam>();
		params2.add(new QueryParam<String>(String.class, getObject().getName()));
		params2.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params2.add(new QueryParam<String>(String.class, getObject().getName()));	
		*/
		List<QueryParam> params3 = new ArrayList<QueryParam>();
		if (getObject().getName()!=null)
			params3.add(new QueryParam<String>(String.class, getObject().getName()));
		if (getObject().getLicenseURI()!=null)
			params3.add(new QueryParam<String>(String.class, getObject().getLicenseURI()));
		if (params3.size()==0)
			throw new AmbitException(MSG_EMPTY);
		params3.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		
		return params3;
		
	}

	public String[] getSQL() throws AmbitException {
		if ((getObject().getName()!=null) && (getObject().getLicenseURI()!=null))
			return update_sql;
		else if (getObject().getName()!=null)
			return update_name;
		else if (getObject().getLicenseURI()!=null)
			return update_license;
		else throw new AmbitException(MSG_EMPTY);
	}
	public void setID(int index, int id) {
		
	}
}
