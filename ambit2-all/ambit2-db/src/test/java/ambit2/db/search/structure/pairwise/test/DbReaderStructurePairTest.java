/* DbReaderTest.java
 * Author: nina
 * Date: Feb 8, 2009
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

package ambit2.db.search.structure.pairwise.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.search.structure.pairwise.PairPropertiesWriter;
import ambit2.db.search.structure.pairwise.ProcessorStructurePairsRetrieval;
import ambit2.db.search.structure.pairwise.QueryStructurePairsDataset;
import ambit2.descriptors.pairwise.SMSDProcessor;

public class DbReaderStructurePairTest extends DbUnitTest {
	
	DbReader<IStructureRecord[]> batch ;

	int count = 0;
	@Before
	public void setUp() throws Exception {
		batch = new DbReader<IStructureRecord[]>();
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadRecordsOnly() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(3,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(0,values.getRowCount());
		
		QueryStructurePairsDataset query = new QueryStructurePairsDataset();
		SourceDataset dataset = new SourceDataset();
		dataset.setId(1);
		query.setFieldname(dataset);
		query.setValue(dataset);
		
		batch.setConnection(c.getConnection());
		batch.open();
		IBatchStatistics stats = batch.process(query);
		Assert.assertEquals(6,stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ));
		Assert.assertEquals(0,stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED));
	}

	@Test
	public void testProcess() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(3,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(0,values.getRowCount());
		
		
		ProcessorsChain<IStructureRecord[], IBatchStatistics,IProcessor>  p = 
				new ProcessorsChain<IStructureRecord[], IBatchStatistics, IProcessor>();

		p.add(new ProcessorStructurePairsRetrieval());

		LiteratureEntry prediction = new LiteratureEntry("smsd","/model/smsd");
		prediction.setType(_type.Model);				
		p.add(new SMSDProcessor(prediction));
		
		p.add(new PairPropertiesWriter());
		/*
		p.add(new IProcessor<IStructureRecord[], IStructureRecord[]>() {
			public boolean isEnabled() {
				return true;
			}
			public IStructureRecord[] process(IStructureRecord[] target)
					throws AmbitException {
				System.out.println(String.format("%s\t%s",target[0],target[1]));
				System.out.println(String.format("%s\t%s",target[0].getProperties(),target[1].getProperties()));
				//System.out.println(target[1].getProperties());
				return target;
			}
			public void setEnabled(boolean value) {
			}
			public long getID() {
				return 0;
			}
		});
		*/
		
		batch.setProcessorChain(p);
		
		QueryStructurePairsDataset query = new QueryStructurePairsDataset();
		SourceDataset dataset = new SourceDataset();
		dataset.setId(1);
		query.setFieldname(dataset);
		query.setValue(dataset);
		
		batch.setConnection(c.getConnection());
		batch.open();
		IBatchStatistics stats = batch.process(query);
		Assert.assertEquals(6,stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ));
		Assert.assertEquals(6,stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED));
		//C20H20BrP
	}
	
}
