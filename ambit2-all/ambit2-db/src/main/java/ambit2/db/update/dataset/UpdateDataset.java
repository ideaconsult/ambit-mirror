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

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.db.update.AbstractObjectUpdate;

public class UpdateDataset extends AbstractObjectUpdate<SourceDataset> {
	public static final String MSG_EMPTY = "No name, license or rightsHolder to update!";
	private static final String update_sql = 

		"update src_dataset set %s, user_name=SUBSTRING_INDEX(user(),'@',1) where id_srcdataset=?"
	;
	private static final String _license = "licenseURI=?";
	
	private static final String _name = "name=?";
	
	private static final String _rightsHolder = "rightsHolder=?";
	
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
		
		if (getObject().getrightsHolder()!=null)
			params3.add(new QueryParam<String>(String.class, getObject().getrightsHolder()));
		
		if (params3.size()==0)
			throw new AmbitException(MSG_EMPTY);
		params3.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		
		return params3;
		
	}

	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		int i=0;
		if (getObject().getName()!=null) {
			b.append(_name.toString());
			i++;
		}
		if (getObject().getLicenseURI()!=null) {
			b.append(i>0?",":"");
			b.append(_license);
			i++;
		}

		if (getObject().getrightsHolder()!=null) {
			b.append(i>0?",":"");
			b.append(_rightsHolder);
			i++;
		}
			
		if (i>0)
			return new String[] {String.format(update_sql,b.toString())};
		else
			throw new AmbitException(MSG_EMPTY);
	}
	public void setID(int index, int id) {
		
	}
}
