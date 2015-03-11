package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.nm.nanowiki.NanoWikiRDFReader;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;

public class NanoWikiRDFTest extends DbUnitTest {
    @Test
    public void test() throws Exception {
    }

    @Test
    public void testRead() throws Exception {
	NanoWikiRDFReader reader = null;
	int records = 0;
	try {
	    reader = new NanoWikiRDFReader(new InputStreamReader(new FileInputStream(new File(
		    "D:/src-other/nanowiki/backup_figshare.rdf")), "UTF-8"));
	    while (reader.hasNext()) {
		IStructureRecord record = reader.nextRecord();
		Assert.assertTrue(record instanceof SubstanceRecord);
		System.out.println(((SubstanceRecord) record).getPublicName());
		records++;
	    }
	} finally {
	    reader.close();
	}
    }

    @Test
    public void testWriteNanoWikiRDF() throws Exception {
	setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
	IDatabaseConnection c = getConnection();
	IRawReader<IStructureRecord> parser = null;
	try {
	    parser = new NanoWikiRDFReader(new InputStreamReader(new FileInputStream(new File(
		    "D:/src-other/nanowiki/backup_figshare.rdf")), "UTF-8"));
	    write(parser, c.getConnection(), new ReferenceSubstanceUUID(), false);

	} finally {
	    parser.close();
	    c.close();
	}

    }

    public int write(IRawReader<IStructureRecord> reader, Connection connection, PropertyKey key, boolean splitRecord)
	    throws Exception {

	DBSubstanceWriter writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(), new SubstanceRecord(), true,
		true);
	writer.setSplitRecord(splitRecord);
	writer.setConnection(connection);
	writer.open();
	int records = 0;
	while (reader.hasNext()) {
	    Object record = reader.next();
	    if (record == null)
		continue;
	    Assert.assertTrue(record instanceof IStructureRecord);
	    writer.process((IStructureRecord) record);
	    records++;
	}
	writer.close();
	return records;
    }

}
