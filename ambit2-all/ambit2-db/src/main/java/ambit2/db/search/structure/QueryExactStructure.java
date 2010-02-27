/* QueryExactStructureBitSet.java
 * Author: nina
 * Date: Apr 10, 2009
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

package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

public class QueryExactStructure extends AbstractStructureQuery<String,IMoleculeSet,NumberCondition>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4140784963337312377L;
	public final static String sqlSMILES = 
		"select ? as idquery,idchemical,%s,1 as selected,1 as metric,null as text from structure join chemicals using(idchemical) where (smiles = ?) %s";
	protected SmilesKey smilesKey = new SmilesKey();

	
	public String getSQL() throws AmbitException {
		return String.format(sqlSMILES,
				isChemicalsOnly()?"max(idstructure) as idstructure":"idstructure",
				isChemicalsOnly()?" group by idchemical":"");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getSmiles(getValue())));		
		return params;
	}
	
	
	public String getSmiles(IMoleculeSet set) throws AmbitException {
		IMolecule a = NoNotificationChemObjectBuilder.getInstance().newMolecule();
		for (int i=0; i < set.getAtomContainerCount();i++)
			a.add(set.getMolecule(i));
		return smilesKey.process(a);
	}

	@Override
	public String toString() {
		return "Exact structure";
	}
}
