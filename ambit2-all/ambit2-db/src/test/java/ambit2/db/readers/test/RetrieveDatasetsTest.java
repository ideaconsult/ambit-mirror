/* RetrieveDatasetsTest.java
 * Author: nina
 * Date: Jan 7, 2009
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

package ambit2.db.readers.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

public class RetrieveDatasetsTest extends RetrieveTest<SourceDataset> {

	@Test
	public void testGetSQL() throws Exception {
		Assert.assertEquals(RetrieveDatasets.select_datasets,((IQueryObject)query).getSQL());
	}

	@Test
	public void testGetParameters() throws Exception {
		Assert.assertEquals(0,((IQueryObject)query).getParameters().size());
	}


	@Test
	public void testGetObject() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM SRC_DATASET");		
		Assert.assertEquals(3,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		ResultSet rs = qe.process((RetrieveDatasets)query);
		
		while (rs.next()) {
			SourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM SRC_DATASET where id_srcdataset="+dataset.getId() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
		}
		rs.close();
		qe.close();
		c.close();
	}
	
	@Test
	public void testGetObjectByName() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM SRC_DATASET where name='Dataset 1'");		
		Assert.assertEquals(1,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		((RetrieveDatasets)query).setValue(new SourceDataset("Dataset 1"));
		ResultSet rs = qe.process((RetrieveDatasets)query);
		
		while (rs.next()) {
			SourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM SRC_DATASET where id_srcdataset="+dataset.getId() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
		}
		rs.close();
		qe.close();
		c.close();
	}	

	@Override
	protected IRetrieval<SourceDataset> createQuery() {
		return new RetrieveDatasets();
	}	
}
