/* FindSimilarSubstances.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

package ambit2.plugin.analogs;

import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMoleculeSet;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QuerySimilarityStructure;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.library.QueryExecution;

import com.microworkflow.process.Sequence;

/**
 * A placeholder for finding similar substances
 * @author nina
 *
 */
public class FindSimilarSubstances extends Sequence {
	public FindSimilarSubstances() {
		super();
		ActivityPrimitive<List<IStructureRecord>, IQueryRetrieval<IStructureRecord>> records2query = 
					new ActivityPrimitive<List<IStructureRecord>, IQueryRetrieval<IStructureRecord>>(
				DBWorkflowContext.RECORDS,
				DBWorkflowContext.QUERY,
				new Records2QueryProcessor()
				) {
			@Override
			public synchronized String getName() {
				return "Query initialization";
			};		
		};
		addStep(records2query);
		addStep(new QueryExecution(null));
		/*
        //addStep(new QuerySelection());
		addStep(records2query);
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
        p1.setName("Search");           
		addStep(p1);
		*/
	}
	
	
}

class Records2QueryProcessor extends DefaultAmbitProcessor<List<IStructureRecord>, IQueryRetrieval<IStructureRecord>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IQueryRetrieval<IStructureRecord> process(List<IStructureRecord> target)
			throws AmbitException {
		QueryCombinedStructure q = new QueryCombinedStructure();
		QuerySimilarityStructure similarity = new QuerySimilarityStructure();
		IMoleculeSet molecules = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		MoleculeReader reader = new MoleculeReader();
		for (IStructureRecord record : target) {
			molecules.addAtomContainer(reader.process(record));
		}
		similarity.setValue(molecules);
		similarity.setThreshold(0.5);
		similarity.setName(similarity.toString());
		q.add(similarity);
		q.setScope(null);
		return q;
	}
}
