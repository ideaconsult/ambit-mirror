package ambit2.db.processors.test;

import java.io.File;
import java.io.FileReader;
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
import ambit2.db.processors.DBSubstanceWriter;

public class NanoWikiRDFTest  extends DbUnitTest {
    @Test
	public void test() throws Exception {
	}

	public void testRead() throws Exception {
		NanoWikiRDFReader reader = null;
		try {
			reader = new NanoWikiRDFReader(new FileReader(new File("D:/src-other/nanowiki/backup_public.rdf")));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				//if (((SubstanceRecord)record).getMeasurements()!=null) System.out.println(((SubstanceRecord)record).getMeasurements());
			}
		} finally {
			reader.close();
		}
	}
	
	public void testWriteNanoWikiRDF() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        IRawReader<IStructureRecord> parser = null;
        try {
	        parser = new NanoWikiRDFReader(new FileReader(new File("D:/src-other/nanowiki/backup_public.rdf")));
	        write(parser,c.getConnection(),new ReferenceSubstanceUUID(),false);
	        
        } finally {
        	parser.close();
        	c.close();
        }
        
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord) throws Exception  {
		
		DBSubstanceWriter writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(),new SubstanceRecord(),true);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.next();
            if (record==null) continue;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}	
		
}
	
