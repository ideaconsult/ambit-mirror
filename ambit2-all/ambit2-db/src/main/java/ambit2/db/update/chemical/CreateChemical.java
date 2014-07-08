/* CreateChemical.java
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
import ambit2.base.interfaces.IChemical;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateChemical  extends AbstractObjectUpdate<IChemical> {
	
	
	public static final String[] create_sql = {
		"INSERT INTO chemicals (idchemical,smiles,inchikey,inchi,formula,label) values (?,?,?,?,?,?) " +
		"on duplicate key update smiles=ifnull(smiles,values(smiles))," +
		"inchikey=ifnull(inchikey,values(inchikey))," +
		"inchi=ifnull(inchi,values(inchi))," +
		"formula=ifnull(formula,values(formula))"
	};

	public CreateChemical(IChemical chemical) {
		super(chemical);
	}
	public CreateChemical() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getObject().getIdchemical()<=0)
			params1.add(new QueryParam<Integer>(Integer.class, null));
		else
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		params1.add(new QueryParam<String>(String.class, getObject().getSmiles()));
		params1.add(new QueryParam<String>(String.class, getObject().getInchiKey()));		
		params1.add(new QueryParam<String>(String.class, getObject().getInchi()));
		String formula = getObject().getFormula();
		if (formula!=null && formula.length()>64) formula = formula.substring(0,63);
		params1.add(new QueryParam<String>(String.class, formula));
		params1.add(new QueryParam<String>(String.class, getObject().getInchi()==null?"UNKNOWN":"OK"));	
		return params1;
		
	}
	public void setID(int index, int id) {
		getObject().setIdchemical(id);

	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
