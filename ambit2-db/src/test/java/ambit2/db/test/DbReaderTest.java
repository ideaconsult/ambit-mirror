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

package ambit2.db.test;

import junit.framework.Assert;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.DbReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.VerboseDescriptorResult;

public class DbReaderTest extends DbUnitTest {

	DbReader<IStructureRecord> batch;

	int count = 0;

	@Before
	public void setUp() throws Exception {
		batch = new DbReader<IStructureRecord>();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadRecordsOnly() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(5, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(2, values.getRowCount());

		QueryStructureByID query = new QueryStructureByID(100215);
		batch.setConnection(c.getConnection());
		batch.open();
		IBatchStatistics stats = batch.process(query);
		Assert.assertEquals(1,
				stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ));
		Assert.assertEquals(0, stats
				.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED));
	}

	@Test
	public void testProcess() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(5, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(2, values.getRowCount());

		ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> p = new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>();

		p.add(new ProcessorStructureRetrieval());
		p.add(new IProcessor<IStructureRecord, IStructureRecord>() {
			public boolean isEnabled() {
				return true;
			}

			public IStructureRecord process(IStructureRecord target)
					throws AmbitException {
				// System.out.println(target.getContent());
				return target;
			}

			public void setEnabled(boolean value) {
			}

			public long getID() {
				return 0;
			}

			@Override
			public void open() throws Exception {
			}

			@Override
			public void close() throws Exception {
			}
		});

		p.add(new MoleculeReader());

		p.add(new AtomConfigurator());
		p.add(new FunctionalGroup("Test", "P", "Test"));
		p.add(new IProcessor<VerboseDescriptorResult, String>() {
			/**
		     * 
		     */
		    private static final long serialVersionUID = -3923864774580483348L;

			public String process(VerboseDescriptorResult target)
					throws AmbitException {
				Assert.assertEquals("1", target.getResult().toString());
				return target.toString();
			}

			public boolean isEnabled() {
				return true;
			}

			public void setEnabled(boolean value) {
			}

			public long getID() {
				return 0;
			}

			@Override
			public void open() throws Exception {
			}

			@Override
			public void close() throws Exception {
			}
		});

		batch.setProcessorChain(p);

		QueryStructureByID query = new QueryStructureByID(100215);
		batch.setConnection(c.getConnection());
		batch.open();
		IBatchStatistics stats = batch.process(query);
		Assert.assertEquals(1,
				stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ));
		Assert.assertEquals(1, stats
				.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED));
		// C20H20BrP
	}

}
