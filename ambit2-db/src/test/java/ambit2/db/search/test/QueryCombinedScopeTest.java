/* QueryCombinedScopeTest.java
 * Author: nina
 * Date: Apr 19, 2009
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

package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.processors.ProcessorSetQueryScope;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.QueryStructure;

public class QueryCombinedScopeTest extends QueryTest<QueryCombinedStructure> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
	}

	@Override
	protected QueryCombinedStructure createQuery() throws Exception {
		/*
		 * QuerySimilarityBitset q = new QuerySimilarityBitset();
		 * FingerprintGenerator gen = new FingerprintGenerator(); BitSet bitset
		 * = gen.process(MoleculeFactory.makeAlkane(10)); q.setValue(bitset);
		 */
		QueryStructure q = new QueryStructure();
		q.setFieldname(ExactStructureSearchMode.smiles);
		q.setValue("CCCCC(CC)C(=O)[O-].CCCCC(CC)C(=O)[O-].[SnH4+2]");

		ProcessorSetQueryScope p = new ProcessorSetQueryScope();
		p.setQuery(q);
		StoredQuery storedQuery = new StoredQuery();
		storedQuery.setId(1);
		QueryStoredResults query_results = new QueryStoredResults();
		query_results.setFieldname(storedQuery);
		return (QueryCombinedStructure) p.process(query_results);
	}

	@Override
	protected void verify(QueryCombinedStructure query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records++;
			Assert.assertEquals(7,rs.getInt(2));
		}
		Assert.assertEquals(1, records);

	}
}
