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

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.config.Preferences;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.ECHAPreregistrationListReader;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.io.bcf.EurasBCFReader;
import ambit2.core.io.pdb.RawIteratingPDBReader;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.EINECSKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.InchiKey;
import ambit2.core.processors.structure.key.NameKey;
import ambit2.core.processors.structure.key.NoneKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.PubchemCID;
import ambit2.core.processors.structure.key.PubchemSID;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.processors.PropertyImporter;
import ambit2.db.processors.RepositoryWriter;

public class RepositoryWriterTest extends DbUnitTest {

    @Test
    public void testWrite() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/input.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("input.sdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(3, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	// verifies if trigger insert_dataset_template works ok
	Assert.assertNotNull(srcdataset.getValue(0, "idtemplate"));

	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(7, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(72, property.getRowCount());

	// verifies if insert_property_tuple works ok
	property = c.createQueryTable("EXPECTED",
		"SELECT * FROM template_def join src_dataset using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(72, property.getRowCount());

	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(224, property_values.getRowCount());
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

    public void testWriteTXT() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/txt/test.txt");
	Assert.assertNotNull(in);
	IIteratingChemObjectReader reader1 = FileInputState.getReader(in, "test.txt");
	RawIteratingWrapper reader = new RawIteratingWrapper(reader1);
	reader.setReference(LiteratureEntry.getInstance("test.txt"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	// 8 or 9
	Assert.assertEquals(9, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(9, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(9, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	// verifies if trigger insert_dataset_template works ok
	Assert.assertNotNull(srcdataset.getValue(0, "idtemplate"));

	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(9, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(5, property.getRowCount());

	// verifies if insert_property_tuple works ok
	property = c.createQueryTable("EXPECTED",
		"SELECT * FROM template_def join src_dataset using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(5, property.getRowCount());

	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(45, property_values.getRowCount());
	ITable tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(9, tuples.getRowCount());
	ITable p_tuples = c.createQueryTable("EXPECTED", "SELECT * FROM property_tuples");
	Assert.assertEquals(45, p_tuples.getRowCount());
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

    @Test
    public void testWriteBCFFormat() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/bcf.xls");
	Assert.assertNotNull(in);
	RawIteratingWrapper reader = new RawIteratingWrapper(new EurasBCFReader(in, 0));
	// reader.setReference(LiteratureEntry.getInstance("input.sdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(17, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(0, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(28, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(28, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM catalog_references");
	Assert.assertEquals(17, property.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT name,count(idreference) as c FROM properties  group by name");
	Assert.assertEquals(37, property.getRowCount());
	for (int i = 0; i < 37; i++) {
	    Assert.assertEquals(new BigInteger("14"), property.getValue(i, "c"));
	}

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(518, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(28 * 37, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(518, srcdataset.getRowCount());
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

    @Test
    public void testWriteEmptySmiles() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/problem.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("Empty smiles"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(3, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(3, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(12, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(36, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(12, srcdataset.getRowCount());
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

    @Test
    public void testMultiStrucSameSmiles() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader()
		.getResourceAsStream("ambit2/db/processors/test/struc_cas.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("Multi strucsame smiles"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(3, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(3, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(12, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(36, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(12, srcdataset.getRowCount());
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

    @Test
    public void testMarkush() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader()
		.getResourceAsStream("ambit2/db/processors/markush/68915-31-1.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("markush"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(1, strucs.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure where type_structure='MARKUSH'");
	Assert.assertEquals(1, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(1, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(18, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(18, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(18, srcdataset.getRowCount());
	c.close();

    }

    @Test
    public void testECHAPreregistrationList() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader()
		.getResourceAsStream("ambit2/db/processors/test/echa_preregistration.echaxml");
	Assert.assertNotNull(in);
	ECHAPreregistrationListReader reader = new ECHAPreregistrationListReader(in);
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(12, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(12, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(12, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(7, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(53, property_values.getRowCount());

	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(7, srcdataset.getRowCount());

	ITable p_str = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_string where value=\"5\u03b2,14-dihydroxy-19-oxocard-20(22)enolide 3\u03b2-acetate\"");
	Assert.assertEquals(1, p_str.getRowCount());

	p_str = c.createQueryTable("EXPECTED", "SELECT * FROM property_string where value=\"4'-methoxyacetanilide\"");
	Assert.assertEquals(1, p_str.getRowCount());
	c.close();
    }

    /*
     * @Test public void testECHACSVPreregistrationList() throws Exception {
     * 
     * setUpDatabase(
     * "src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
     * IDatabaseConnection c = getConnection();
     * 
     * ITable chemicals =
     * c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
     * Assert.assertEquals(0,chemicals.getRowCount()); ITable strucs =
     * c.createQueryTable("EXPECTED","SELECT * FROM structure");
     * Assert.assertEquals(0,strucs.getRowCount()); ITable srcdataset =
     * c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
     * Assert.assertEquals(0,srcdataset.getRowCount()); ITable struc_src =
     * c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
     * Assert.assertEquals(0,struc_src.getRowCount()); ITable property =
     * c.createQueryTable("EXPECTED","SELECT * FROM properties");
     * Assert.assertEquals(0,property.getRowCount()); ITable property_values =
     * c.createQueryTable("EXPECTED","SELECT * FROM property_values");
     * Assert.assertEquals(0,property_values.getRowCount());
     * 
     * InputStream in = this.getClass().getClassLoader().getResourceAsStream(
     * "ambit2/db/processors/test/echa.csv"); Assert.assertNotNull(in);
     * IteratingDelimitedFileReader reader = new
     * IteratingDelimitedFileReader(in, new DelimitedFileFormat(";",'"'));
     * write(reader,c.getConnection()); c.close();
     * 
     * c = getConnection(); chemicals =
     * c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
     * Assert.assertEquals(11,chemicals.getRowCount()); strucs =
     * c.createQueryTable("EXPECTED","SELECT * FROM structure");
     * Assert.assertEquals(11,strucs.getRowCount()); srcdataset =
     * c.createQueryTable
     * ("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
     * Assert.assertEquals(1,srcdataset.getRowCount()); struc_src =
     * c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
     * Assert.assertEquals(11,struc_src.getRowCount());
     * 
     * property = c.createQueryTable("EXPECTED","SELECT * FROM properties");
     * Assert.assertEquals(7,property.getRowCount()); property_values =
     * c.createQueryTable("EXPECTED","SELECT * FROM property_values");
     * Assert.assertEquals(48,property_values.getRowCount()); ITable tuples =
     * c.createQueryTable("EXPECTED","SELECT * FROM tuples");
     * Assert.assertEquals(11,tuples.getRowCount()); ITable p_tuples =
     * c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
     * Assert.assertEquals(48,p_tuples.getRowCount()); ITable p_str =
     * c.createQueryTable("EXPECTED",
     * "SELECT * FROM property_string where value=\"5\u03b2,14-dihydroxy-19-oxocard-20(22)enolide 3\u03b2-acetate\""
     * ); Assert.assertEquals(1,p_str.getRowCount());
     * 
     * p_str = c.createQueryTable("EXPECTED",
     * "SELECT * FROM property_string where value=\"4'-methoxyacetanilide\"");
     * Assert.assertEquals(1,p_str.getRowCount()); c.close(); }
     */
    public int write(IRawReader<IStructureRecord> reader, Connection connection) throws Exception {
	return write(reader, connection, null);
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key) throws Exception {
	return write(reader, connection, key, false);
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key,
	    boolean useExistingStructure) throws Exception {
	return write(reader, connection, key, useExistingStructure, -1);
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key,
	    boolean useExistingStructure, int maxrecords) throws Exception {
	RepositoryWriter writer = new RepositoryWriter();

	writer.setUseExistingStructure(useExistingStructure);
	if (key != null)
	    writer.setPropertyKey(key);
	writer.setDataset(new SourceDataset("TEST INPUT", LiteratureEntry.getInstance("File", "file:input.sdf")));
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

    protected int importProperties(IIteratingChemObjectReader reader, Connection connection, IStructureKey key)
	    throws Exception {

	IStructureRecord record = new StructureRecord();
	MoleculeWriter molwriter = new MoleculeWriter();
	PropertyImporter writer = new PropertyImporter();
	if (key != null)
	    writer.setPropertyKey(key);
	writer.setDataset(new SourceDataset("Imported properties", LiteratureEntry.getInstance("File",
		"file:property.csv")));
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
		throw new AmbitException("Unsupported class " + o.getClass().getName());
	}
	reader.close();
	writer.close();
	return records;
    }

    @Test
    public void testWriteMultipleFilesNames() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	File dir = new File("src/test/resources/ambit2/db/processors/name");

	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
		return !name.startsWith(".");
	    }
	};

	File[] files = dir.listFiles(filter);
	Assert.assertEquals(2, files.length);
	RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
	write(reader, c.getConnection(), new NameKey());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(2, chemicals.getRowCount());
	// there are two empty file without $$$$ sign, which are skipped
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(2, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(2, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	// Assert.assertEquals(34,property.getRowCount());
	Assert.assertEquals(33, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(34, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(33, srcdataset.getRowCount());
	ITable p_cas = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name=\"Names\"");
	Assert.assertEquals(1, p_cas.getRowCount());

	c.close();

    }

    @Test
    public void testWriteMultipleFilesEinecs() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	File dir = new File("src/test/resources/ambit2/db/processors/einecs");

	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
		return !name.startsWith(".");
	    }
	};

	File[] files = dir.listFiles(filter);
	Assert.assertEquals(3, files.length);
	RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
	write(reader, c.getConnection(), new EINECSKey());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(3, chemicals.getRowCount());
	// there are two empty file without $$$$ sign, which are skipped
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(3, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	// Assert.assertEquals(34,property.getRowCount());
	Assert.assertEquals(1, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(3, property_values.getRowCount());

	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	ITable p_cas = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name=\"EC\"");
	Assert.assertEquals(3, p_cas.getRowCount());

	c.close();

    }

    @Test
    public void testWriteMultipleFiles_i5d() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	File dir = new File("src/test/resources/ambit2/db/processors/i5d");

	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
		return !name.startsWith(".");
	    }
	};

	File[] files = dir.listFiles(filter);
	Assert.assertEquals(3, files.length);

	RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
	write(reader, c.getConnection(), new EINECSKey());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(3, chemicals.getRowCount());
	// there are two empty file without $$$$ sign, which are skipped
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(3, struc_src.getRowCount());
	property = c
		.createQueryTable(
			"EXPECTED",
			"SELECT * FROM properties join catalog_references using(idreference) where name='Names' and title in ('IUCLID5 SYNONYM#2','IUCLID5')");
	Assert.assertEquals(2, property.getRowCount());
	property = c.createQueryTable("EXPECTED",
		"SELECT * FROM properties join catalog_references using(idreference) order by name");
	// Assert.assertEquals(34,property.getRowCount());
	Assert.assertEquals(7, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(19, property_values.getRowCount());

	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(7, srcdataset.getRowCount());

	ITable p_cas = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='CasRN'");
	Assert.assertEquals(3, p_cas.getRowCount());
	ITable p_ec = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='EC'");
	Assert.assertEquals(3, p_ec.getRowCount());
	ITable p_uuid = c
		.createQueryTable(
			"EXPECTED",
			"SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='I5UUID'");
	Assert.assertEquals(3, p_uuid.getRowCount());
	c.close();
    }

    @Test
    public void testWriteMultipleFiles() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	File dir = new File("src/test/resources/ambit2/db/processors/sdf");

	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
		return !name.startsWith(".");
	    }
	};

	File[] files = dir.listFiles(filter);
	Assert.assertEquals(11, files.length);
	RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
	write(reader, c.getConnection());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(8, chemicals.getRowCount());
	// there are two empty file without $$$$ sign, which are skipped
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(15, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(15, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	// Assert.assertEquals(34,property.getRowCount());
	Assert.assertEquals(213, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(367, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(213, srcdataset.getRowCount());
	ITable p_cas = c
		.createQueryTable("EXPECTED",
			"SELECT idchemical,idstructure,name,value FROM structure join values_string using(idstructure) where name=\"CasRN\"");
	Assert.assertEquals(12, p_cas.getRowCount());

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

    @Test
    public void testSeekByCID() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/712.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("712.sdf"));
	write(reader, c.getConnection(), new PubchemCID());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(3, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(38, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(63, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(38, srcdataset.getRowCount());
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

    @Test
    public void testImportPropertiesBySMILES() throws Exception {

	Preferences.setProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES, "false");
	setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(1, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(2, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(4, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(4, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values where idstructure=100215");
	Assert.assertEquals(2, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/property.csv");
	Assert.assertNotNull(in);
	IIteratingChemObjectReader reader = FileInputState.getReader(in, ".csv");

	importProperties(reader, c.getConnection(), new SmilesKey());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='Imported properties'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED",
		"SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Imported properties'");
	Assert.assertEquals(2, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertTrue(property.getRowCount() >= 7);
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(4 + 5, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values where idstructure=100215");
	Assert.assertEquals(4, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='Imported properties'");
	Assert.assertEquals(3, srcdataset.getRowCount());
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

    @Test
    public void testImportPropertiesByKey() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(1, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(2, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(4, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(4, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/712.sdf");
	Assert.assertNotNull(in);
	// IIteratingChemObjectReader reader =
	// FileInputState.getReader(in,".sdf");
	ITable tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(0, tuples.getRowCount());

	IRawReader<IStructureRecord> reader = new RawIteratingSDFReader(new InputStreamReader(in));
	// reader.setReference("predictions.sdf");
	write((RawIteratingSDFReader) reader, c.getConnection(), new PubchemCID(), true);
	// importProperties(reader,c.getConnection(),new PubchemCID());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED",
		"SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='TEST INPUT'");
	Assert.assertEquals(1, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(42, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(42, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values where idstructure=100215");
	Assert.assertEquals(2, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(38, srcdataset.getRowCount());

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

    @Test
    public void testImport3Same() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/match/99-72-9.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("input.sdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(1, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(2, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/match/99-72-9.csv");
	Assert.assertNotNull(in);
	IIteratingChemObjectReader reader1 = FileInputState.getReader(in, "99-72-9.csv");
	RawIteratingWrapper wrapper = new RawIteratingWrapper(reader1);

	wrapper.setReference(LiteratureEntry.getInstance("input.csv"));
	write(wrapper, c.getConnection());

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(3, chemicals.getRowCount());
	// Should be 2, but structures from SDF and SMILES come with different
	// stereo ...
	// Assert.assertEquals(2,chemicals.getRowCount());

	c.close();

    }

    @Test
    public void testZeroLeadingCAS() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	InputStream in = this.getClass().getClassLoader()
		.getResourceAsStream("ambit2/db/processors/test/weirdcasformatting.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("input.sdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(10, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(10, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(10, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	ITable table = c.createQueryTable("EXPECTED", "SELECT value from property_string where value='1728-95-6'");
	Assert.assertEquals(1, table.getRowCount());

	table = c
		.createQueryTable(
			"Z0",
			"SELECT value,name FROM property_values left join property_string using(idvalue_string) join properties using(idproperty) join catalog_references using(idreference) where comments='http://www.opentox.org/api/1.1#CASRN' and !(value regexp '^0')");
	Assert.assertEquals(10, table.getRowCount());

	c.close();

    }

    @Test
    public void testImportByInChI() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	InputStream in = this.getClass().getClassLoader()
		.getResourceAsStream("ambit2/db/processors/match/atropine.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("input.sdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(1, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(1, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	// verifies if trigger insert_dataset_template works ok
	Assert.assertNotNull(srcdataset.getValue(0, "idtemplate"));

	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(1, struc_src.getRowCount());

	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(2, property.getRowCount());

	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(2, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(2, srcdataset.getRowCount());
	c.close();

	c = getConnection();
	// now import properties and match by inchi
	in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/match/props.csv");
	Assert.assertNotNull(in);
	IIteratingChemObjectReader reader1 = FileInputState.getReader(in, ".csv");

	importProperties(reader1, c.getConnection(), new InchiKey());
	// importProperties(reader1,c.getConnection(),new InchiPropertyKey());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(1, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='Imported properties'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED",
		"SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Imported properties'");
	Assert.assertEquals(1, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertTrue(property.getRowCount() >= 6);
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(6, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED",
		"SELECT * FROM property_values join properties using(idproperty) where name='chiSquared'");
	Assert.assertEquals(1, property_values.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT * FROM src_dataset join template_def using(idtemplate) where name='Imported properties'");
	Assert.assertEquals(4, srcdataset.getRowCount());

	c.close();

    }

    @Test
    public void testImportDX() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(1, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(2, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(4, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(4, property_values.getRowCount());

	ITable template_def = c.createQueryTable("EXPECTED", "SELECT * FROM template_def");
	Assert.assertEquals(3, template_def.getRowCount());
	
	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/data/dx/predictions.sdf");
	Assert.assertNotNull(in);
	RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
	// reader.setReference("predictions.sdf");
	write(reader, c.getConnection(), new CASKey());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(6, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(6, strucs.getRowCount());
	// srcdataset =
	// c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='Imported properties'");
	// Assert.assertEquals(1,srcdataset.getRowCount());
	// struc_src =
	// c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Imported properties'");
	// Assert.assertEquals(1,struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(31, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(31, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values where idstructure=100215");
	Assert.assertEquals(2, property_values.getRowCount());

	// ITable p_tuples =
	// c.createQueryTable("EXPECTED","SELECT * FROM property_tuples join tuples using(idtuple) join src_dataset using(id_srcdataset) where name='Imported properties'");
	// Assert.assertEquals(66,p_tuples.getRowCount());
	c.close();

	c = getConnection();
	in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/data/dx/predictions1.sdf");
	Assert.assertNotNull(in);
	reader = new RawIteratingSDFReader(new InputStreamReader(in));
	// reader.setReference(LiteratureEntry.getDXReference());
	write(reader, c.getConnection(), new CASKey());
	reader.close();

	c = getConnection();
	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(31, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(47, property_values.getRowCount());
	template_def = c.createQueryTable("EXPECTED", "SELECT * FROM template_def");
	Assert.assertEquals(30, template_def.getRowCount());
	c.close();

    }

    @Test
    public void testImportTox21() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/pubchem/tox21_excerpt.sdf");
	Assert.assertNotNull(in);
	IRawReader<IStructureRecord> reader = new RawIteratingSDFReader(new InputStreamReader(in));
	// reader.setReference("predictions.sdf");
	write((RawIteratingSDFReader) reader, c.getConnection(), new CASKey());
	reader.close();
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(3, strucs.getRowCount());
	// srcdataset =
	// c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='Imported properties'");
	// Assert.assertEquals(1,srcdataset.getRowCount());
	// struc_src =
	// c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Imported properties'");
	// Assert.assertEquals(1,struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(19, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(57, property_values.getRowCount());
	property_values = c.createQueryTable("EXPECTED",
		"SELECT * FROM property_values join properties using(idproperty) where name = 'PUBCHEM_SID'");
	Assert.assertEquals(3, property_values.getRowCount());

	// ITable p_tuples =
	// c.createQueryTable("EXPECTED","SELECT * FROM property_tuples join tuples using(idtuple) join src_dataset using(id_srcdataset) where name='Imported properties'");
	// Assert.assertEquals(66,p_tuples.getRowCount());
	c.close();

	c = getConnection();

	in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/pubchem/aid720516.csv");
	Assert.assertNotNull(in);
	reader = new RawIteratingWrapper<IIteratingChemObjectReader>(FileInputState.getReader(in, ".csv"));
	// reader.setReference(LiteratureEntry.getDXReference());
	write(reader, c.getConnection(), new PubchemSID(), true);
	reader.close();

	c = getConnection();
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	// Assert.assertEquals(3,strucs.getRowCount());
	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(19 + 9, property.getRowCount()); // does not write
							     // another
							     // PUBCHEM_SID
	property_values = c.createQueryTable("EXPECTED",
		"SELECT * FROM property_values join properties using(idproperty) where name = 'PUBCHEM_CID'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = 'PUBCHEM_ACTIVITY_OUTCOME'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = 'PUBCHEM_ACTIVITY_SCORE'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '1^Activity Summary^STRING^^^^'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '2^ATAD5 Activity^STRING^^^^'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '3^ATAD5 Potency (uM)^FLOAT^uM^AC^^'");
	Assert.assertEquals(0, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '4^ATAD5 Efficacy (%)^FLOAT^%^^^'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '5^Viability Activity^STRING^^^^'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '6^Viability Potency (uM)^FLOAT^uM^^^'");
	Assert.assertEquals(0, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '7^Viability Efficacy (%)^FLOAT^%^^^'");
	Assert.assertEquals(3, property_values.getRowCount());
	property_values = c
		.createQueryTable("EXPECTED",
			"SELECT * FROM property_values join properties using(idproperty) where name = '8^Sample Source^STRING^^^^'");
	Assert.assertEquals(3, property_values.getRowCount());

	// Assert.assertEquals(57+9*3,property_values.getRowCount());
	c.close();

    }

    @Test
    public void testEmpty() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
    }

    @Test
    public void testWritePDB() throws Exception {

	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(0, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(0, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(0, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(0, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/pdb/test.pdb");
	Assert.assertNotNull(in);
	//IIteratingChemObjectReader pdbreader = FileInputState.getReader(in, ".pdb");
	RawIteratingPDBReader reader = new RawIteratingPDBReader(new InputStreamReader(in));
	reader.setReference(LiteratureEntry.getInstance("input.pdb"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(1, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(1, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(1, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED",
		"SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());

	// verifies if trigger insert_dataset_template works ok
	Assert.assertNotNull(srcdataset.getValue(0, "idtemplate"));

	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(1, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(0, property.getRowCount());

	// verifies if insert_property_tuple works ok
	property = c.createQueryTable("EXPECTED",
		"SELECT * FROM template_def join src_dataset using(idtemplate) where name='TEST INPUT'");
	Assert.assertEquals(0, property.getRowCount());

	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(0, property_values.getRowCount());
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

    public static void main(String[] args) {
	if (args == null || args.length == 0)
	    System.exit(-1);
	RepositoryWriterTest w = new RepositoryWriterTest();
	File file = new File(args[0]);
	RawIteratingSDFReader reader = null;
	IDatabaseConnection c = null;
	long now = System.currentTimeMillis();
	try {
	    w.setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	    c = w.getConnection();
	    reader = new RawIteratingSDFReader(new FileReader(file));
	    reader.setReference(LiteratureEntry.getInstance(file.getName()));
	    w.write(reader, c.getConnection(), new NoneKey(), false, 1000000);
	    c.close();
	} catch (Exception x) {
	    x.printStackTrace();
	} finally {
	    try {
		if (reader != null)
		    reader.close();
	    } catch (Exception x) {
	    }
	    try {
		if (c != null)
		    c.close();
	    } catch (Exception x) {
	    }
	    System.out.print("Elapsed ");
	    System.out.println(System.currentTimeMillis() - now);
	}

    }
}
