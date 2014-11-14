/* UpdateChemical.java
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

package ambit2.db.update.chemical;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IChemical;

public class UpdateChemical<C extends IChemical> extends AbstractUpdate<C,C>  {
	public static final String update_sql =	"update chemicals set %s,label=?,lastmodified=now() where idchemical=?";
	
	enum keys {
		smiles {
			@Override
			public String getField(IChemical object) {
				return object.getSmiles();
			}
		},
		inchikey {
			@Override
			public String getField(IChemical object) {
				return object.getInchiKey();
			}		
		},
		inchi {
			@Override
			public String getField(IChemical object) {
				return object.getInchi();
			}		
		},
		formula {
			@Override
			public String getField(IChemical object) {
				return object.getFormula();
			}
		};
		public QueryParam<String> getParam(IChemical object) {
			if (getField(object)==null) return null;
			return new QueryParam<String>(String.class, getField(object));
		}
		public abstract String getField(IChemical object);
		public String getSQL() {
			//smiles=?,inchikey=?,inchi=?,formula=?
			return String.format("%s = ?",name());
		}
	};
	public UpdateChemical(C chemical) {
		super(chemical);
	}
	public UpdateChemical() {
		this(null);
	}	
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject().getIdchemical()<=0) throw new AmbitException("Chemical not defined");
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		int nulls = 0;
		for (keys key:keys.values()) {
			QueryParam<String> param = key.getParam(getObject());
			if (param == null) nulls++; 
			params1.add(param==null?new QueryParam<String>(String.class,null):param);
		}
	//f	if (nulls == keys.values().length) throw new AmbitException("No smiles, inchi or formula!");
		
		params1.add(new QueryParam<String>(String.class, getObject().getInchi()==null?"UNKNOWN":"OK"));
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String delimiter = "";
		for (keys key:keys.values()) {
		//	String field = key.getField(getObject());
		//	if (field==null) continue;
			b.append(delimiter);
			b.append(key.getSQL());
			delimiter = ",";
		}
		return new String[] { String.format(update_sql,b.toString()) };
	}
	public void setID(int index, int id) {
		
	}
}
