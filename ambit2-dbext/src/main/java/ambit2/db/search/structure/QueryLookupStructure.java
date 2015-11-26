/* QueryLookupStructure.java
 * Author: nina
 * Date: Apr 27, 2009
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

import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.search.EQCondition;

/**
 * This is a wrapper around {@link QueryStructure} , allowing to use IStructureRecord as a parameter
 * @author nina
 *
 */
public class QueryLookupStructure extends AbstractStructureQuery<IStructureKey,IStructureRecord, EQCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 960145706994951798L;
	protected QueryStructure query;
	protected MoleculeReader reader = new MoleculeReader();
	protected SmilesKey smiles = new SmilesKey();
	
	public QueryLookupStructure() {
		this(ExactStructureSearchMode.smiles);
	}
	public QueryLookupStructure(ExactStructureSearchMode structureSearchMode) {
		query = new QueryStructure();
		query.setFieldname(structureSearchMode);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		return query.getParameters();
	}

	public String getSQL() throws AmbitException {
		return query.getSQL();
	}
	@Override
	public void setValue(IStructureRecord value) {
		super.setValue(value);
		try {
			query.setValue(smiles.process(reader.process(value)));
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage(),x);
		}
	}
	@Override
	public String toString() {
		return query.toString();
	}
}
