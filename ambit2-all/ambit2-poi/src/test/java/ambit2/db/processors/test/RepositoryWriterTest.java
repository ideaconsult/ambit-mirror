/* RepositoryWriterTest.java
 * Author: nina
 * Date: Jan 9, 2009
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

package ambit2.db.processors.test;

import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.io.bcf.EurasBCFReader;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.PropertyImporter;
import ambit2.db.processors.RepositoryWriter;

public class RepositoryWriterTest extends DbUnitTest {

    @Deprecated 
  //TODO change to use with nmdataparser
	@Test
	public void testWriteBCFFormat() throws Exception {

		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		IDatabaseConnection c = getConnection();

		ITable chemicals = c.createQueryTable("EXPECTED",
				"SELECT * FROM chemicals");
		Assert.assertEquals(0, chemicals.getRowCount());
		ITable strucs = c.createQueryTable("EXPECTED",
				"SELECT * FROM structure");
		Assert.assertEquals(0, strucs.getRowCount());
		ITable srcdataset = c.createQueryTable("EXPECTED",
				"SELECT * FROM src_dataset");
		Assert.assertEquals(0, srcdataset.getRowCount());
		ITable struc_src = c.createQueryTable("EXPECTED",
				"SELECT * FROM struc_dataset");
		Assert.assertEquals(0, struc_src.getRowCount());
		ITable property = c.createQueryTable("EXPECTED",
				"SELECT * FROM properties");
		Assert.assertEquals(0, property.getRowCount());
		ITable property_values = c.createQueryTable("EXPECTED",
				"SELECT * FROM property_values");
		Assert.assertEquals(0, property_values.getRowCount());

		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream("ambit2/db/processors/test/bcf.xls");
		Assert.assertNotNull(in);
		RawIteratingWrapper reader = new RawIteratingWrapper(
				new EurasBCFReader(in, 0));
		// reader.setReference(LiteratureEntry.getInstance("input.sdf"));
		write(reader, c.getConnection());
		c.close();

		c = getConnection();
		chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
		Assert.assertEquals(17, chemicals.getRowCount());
		chemicals = c
				.createQueryTable(
						"EXPECTED",
						"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
		Assert.assertEquals(0, chemicals.getRowCount());
		strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
		Assert.assertEquals(28, strucs.getRowCount());
		srcdataset = c.createQueryTable("EXPECTED",
				"SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1, srcdataset.getRowCount());
		struc_src = c.createQueryTable("EXPECTED",
				"SELECT * FROM struc_dataset");
		Assert.assertEquals(28, struc_src.getRowCount());

		property = c.createQueryTable("EXPECTED",
				"SELECT * FROM catalog_references");
		Assert.assertEquals(17, property.getRowCount());
		int n=36; //was 37
		property = c
				.createQueryTable("EXPECTED",
						"SELECT name,count(idreference) as c FROM properties  group by name");
		Assert.assertEquals(n, property.getRowCount());
		for (int i = 0; i < n; i++) {
			Assert.assertEquals(new BigInteger("14"), property.getValue(i, "c"));
		}

		property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
		//was 518
		Assert.assertEquals(504, property.getRowCount());
		property_values = c.createQueryTable("EXPECTED",
				"SELECT * FROM property_values");
		Assert.assertEquals(28 * n, property_values.getRowCount());
		srcdataset = c
				.createQueryTable(
						"EXPECTED",
						"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
		Assert.assertEquals(504, srcdataset.getRowCount());
		c.close();
		/**
		 * Removing redundant properties insert ignore into property_values
		 * select id,idproperty,idstructure,idvalue,idtype,user_name,status from
		 * property_values where idstructure>3 on duplicate key update
		 * idstructure=3 delete from property_values where idstructure>3
		 * 
		 * insert ignore into struc_dataset select idstructure,id_srcdataset
		 * from struc_dataset where idstructure>3 on duplicate key update
		 * idstructure=3 delete from struc_dataset where idstructure>3
		 */

	}

	public int write(IRawReader<IStructureRecord> reader, Connection connection)
			throws Exception {
		return write(reader, connection, null);
	}

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key) throws Exception {
		return write(reader, connection, key, false);
	}

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key, boolean useExistingStructure)
			throws Exception {
		return write(reader, connection, key, useExistingStructure, -1);
	}

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key,
			boolean useExistingStructure, int maxrecords) throws Exception {
		RepositoryWriter writer = new RepositoryWriter();

		writer.setUseExistingStructure(useExistingStructure);
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("TEST INPUT", LiteratureEntry
				.getInstance("File", "file:input.sdf")));
		writer.setConnection(connection);
		writer.open();
		int records = 0;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			writer.write(record);
			records++;
			if (maxrecords <= 0 || (records <= maxrecords))
				continue;
			else
				break;

		}
		reader.close();
		writer.close();
		return records;
	}

	protected int importProperties(IIteratingChemObjectReader reader,
			Connection connection, IStructureKey key) throws Exception {

		IStructureRecord record = new StructureRecord();
		MoleculeWriter molwriter = new MoleculeWriter();
		PropertyImporter writer = new PropertyImporter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("Imported properties",
				LiteratureEntry.getInstance("File", "file:property.csv")));
		writer.setConnection(connection);
		writer.open();
		int records = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			if (o instanceof IAtomContainer) {
				/*
				 * record.setReference(LiteratureEntry.getInstance("File",
				 * "file:property.csv")); record.setIdchemical(-1);
				 * record.setIdstructure(-1);
				 * record.setFormat(IStructureRecord.MOL_TYPE.SDF.toString());
				 * record.clearProperties();
				 * record.addProperties(((IAtomContainer)o).getProperties());
				 * record.setContent(molwriter.process((IAtomContainer)o));
				 * writer.write(record);
				 */
				writer.write((IAtomContainer) o);
				records++;
			} else
				throw new AmbitException("Unsupported class "
						+ o.getClass().getName());
		}
		reader.close();
		writer.close();
		return records;
	}

}
