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

package ambit2.rest.test.dataset;

import java.io.InputStream;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.RepositoryWriter;
import ambit2.rest.dataset.RDFIteratingReader;
import ambit2.rest.test.ResourceTest;

public class RepositoryWriterRDFTest extends ResourceTest {
    /**
     * Writes "Property 1" = "XXXXX" for structure 100215
     * 
     * @throws Exception
     */
    @Test
    public void testWriteForeign() throws Exception {

	setUpDatabase("src/test/resources/src-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(4, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(3, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(8, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(3, property.getRowCount());
	ITable tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(1, tuples.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(1, property_values.getRowCount());
	ITable p_tuples = c.createQueryTable("EXPECTED", "SELECT * FROM property_tuples");
	Assert.assertEquals(1, p_tuples.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("import_dataset2.rdf");
	Assert.assertNotNull(in);
	RDFIteratingReader reader = new RDFIteratingReader(in, SilentChemObjectBuilder.getInstance(), String.format(
		"http://somethingelse.com:%d", port), "RDF/XML");
	// reader.setReference(LiteratureEntry.getInstance("input.rdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(5, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(2, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(7, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(10, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(34, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(57, property_values.getRowCount());
	tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(3, tuples.getRowCount());
	p_tuples = c.createQueryTable("EXPECTED", "SELECT * FROM property_tuples");
	Assert.assertEquals(57, p_tuples.getRowCount());

	p_tuples = c.createQueryTable("EXPECTED",
		"SELECT * FROM values_string where name=\"http://localhost:8181/feature/1\" and value = \"XXXXX\"");
	Assert.assertEquals(1, p_tuples.getRowCount());

	p_tuples = c
		.createQueryTable(
			"EXPECTED",
			"SELECT value_num FROM property_values join properties using(idproperty) where name='http://anotherservice.com:8080/feature/1'");
	Assert.assertEquals(1, p_tuples.getRowCount());
	Assert.assertEquals("3.14", p_tuples.getValue(0, "value_num").toString());

	c.close();

    }

    /**
     * This will not work as initially intended, because the dataset title /id
     * is not taken into account now to overwrite a dataset, post to its URI
     * 
     * @throws Exception
     */
    public void testWriteSelf() throws Exception {
	testWrite(String.format("http://localhost:%d", port));
    }

    public void testWrite(String baseref) throws Exception {

	setUpDatabase("src/test/resources/src-datasets.xml");
	IDatabaseConnection c = getConnection();

	ITable chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(4, chemicals.getRowCount());
	ITable strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	ITable srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset");
	Assert.assertEquals(3, srcdataset.getRowCount());
	ITable struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(8, struc_src.getRowCount());
	ITable property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(3, property.getRowCount());
	ITable tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(1, tuples.getRowCount());
	ITable property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(1, property_values.getRowCount());
	ITable p_tuples = c.createQueryTable("EXPECTED", "SELECT * FROM property_tuples");
	Assert.assertEquals(1, p_tuples.getRowCount());

	InputStream in = this.getClass().getClassLoader().getResourceAsStream("import_dataset2.rdf");
	Assert.assertNotNull(in);
	RDFIteratingReader reader = new RDFIteratingReader(in, SilentChemObjectBuilder.getInstance(), baseref,
		"RDF/XML");
	// reader.setReference(LiteratureEntry.getInstance("input.rdf"));
	write(reader, c.getConnection());
	c.close();

	c = getConnection();
	chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
	Assert.assertEquals(4, chemicals.getRowCount());
	chemicals = c.createQueryTable("EXPECTED",
		"SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
	Assert.assertEquals(0, chemicals.getRowCount());
	strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	Assert.assertEquals(5, strucs.getRowCount());
	srcdataset = c.createQueryTable("EXPECTED", "SELECT * FROM src_dataset where name='TEST INPUT'");
	Assert.assertEquals(1, srcdataset.getRowCount());
	struc_src = c.createQueryTable("EXPECTED", "SELECT * FROM struc_dataset");
	Assert.assertEquals(8, struc_src.getRowCount());

	property = c.createQueryTable("EXPECTED", "SELECT * FROM properties");
	Assert.assertEquals(33, property.getRowCount());
	property_values = c.createQueryTable("EXPECTED", "SELECT * FROM property_values");
	Assert.assertEquals(57, property_values.getRowCount());
	tuples = c.createQueryTable("EXPECTED", "SELECT * FROM tuples");
	Assert.assertEquals(3, tuples.getRowCount());
	p_tuples = c.createQueryTable("EXPECTED", "SELECT * FROM property_tuples");
	Assert.assertEquals(57, p_tuples.getRowCount());

	p_tuples = c.createQueryTable("EXPECTED",
		"SELECT * FROM values_string where name=\"Property 1\" and value = \"XXXXX\" and idstructure=100215");
	Assert.assertEquals(1, p_tuples.getRowCount());

	p_tuples = c
		.createQueryTable(
			"EXPECTED",
			"SELECT value_num FROM property_values join properties using(idproperty) where name='http://anotherservice.com:8080/feature/1' and idstructure=100215");
	Assert.assertEquals(1, p_tuples.getRowCount());
	Assert.assertEquals("3.14", p_tuples.getValue(0, "value_num").toString());
	c.close();

    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection) throws Exception {
	return write(reader, connection, null);
    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key) throws Exception {

	RepositoryWriter writer = new RepositoryWriter();
	if (key != null)
	    writer.setPropertyKey(key);
	writer.setDataset(new SourceDataset("TEST INPUT", LiteratureEntry.getInstance("File", "file:input.sdf")));
	writer.setConnection(connection);
	writer.open();
	int records = 0;
	while (reader.hasNext()) {
	    IStructureRecord record = reader.nextRecord();
	    System.out.println(record);
	    writer.write(record);
	    records++;
	    System.out.println(record);
	}
	reader.close();
	writer.close();
	return records;
    }

    @Override
    public void testGetJavaObject(String uri, MediaType media, Status expectedStatus) throws Exception {

    }
}
