package ambit2.dbsubstance.test;

import java.io.File;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.tox21.Tox21SubstanceReader;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.DBSubstanceWriter;
import ambit2.db.processors.test.DbUnitTest;

public class Tox21ReaderTest extends DbUnitTest {
	
	@Test
	public void testWriteTox21() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        
        
		File dir = new File("F:/Downloads/Chemical data/TOXCAST/Tox21/");
		File[] files = dir.listFiles();
		IDatabaseConnection c = null;
			for (int i=0; i < files.length; i++) 
			if (files[i].getName().endsWith(".csv")) {
				System.out.println(files[i].getAbsolutePath());
				c = getConnection();
				Connection conn = c.getConnection();
		        Tox21SubstanceReader parser = null;
		        try {
		    		LiteratureEntry entry = new LiteratureEntry("Tox21","???");
		    		entry.setType(_type.Dataset);
		    		
		    		parser = new Tox21SubstanceReader(
		    				files[i]
		    				);
			        write(parser,conn,new ReferenceSubstanceUUID(),false,2);
		        } catch (Exception x) {
		        	x.printStackTrace();
		        } finally {
		        	parser.close();
		        	conn.close();
		        }
			}
	}
	
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord, int max) throws Exception  {
		
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("Tox21",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		
		
		DBSubstanceWriter writer = new DBSubstanceWriter(dataset,new SubstanceRecord(),false,false);
		writer.setCloseConnection(false);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.next();
            if (record==null) continue;
            if (records>=max) break;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}
	
}
