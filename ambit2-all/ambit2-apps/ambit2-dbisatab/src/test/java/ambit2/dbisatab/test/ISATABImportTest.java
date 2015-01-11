package ambit2.dbisatab.test;

import java.io.File;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.isa.ISAReader;

import org.dbunit.database.IDatabaseConnection;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
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
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;

public class ISATABImportTest extends DbUnitTest {
	
	@Override
	protected boolean isProfileSQL() {
		return false;
	}
	@Test
	public void testWriteISATAB() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        IRawReader<IStructureRecord> parser = null;
        try {
    		LiteratureEntry entry = new LiteratureEntry("ISA-TAB","test");
    		entry.setType(_type.Dataset);
    		
    		String root = "C://ToxBank//ISAcreator.SEURAT-v1.7.2//ISAcreator.SEURAT";
    		
    		//String investigation = root + "//isatab files//BII-I-1";
    		String investigation = root + "//isatab files//qHTSexample";

    		ISAConfigurationSet.setConfigPath(root+ "//Configurations/toxbank-config");
    		parser = new ISAReader(new File(investigation));
	        write(parser,c.getConnection(),new ReferenceSubstanceUUID(),false);
	        
        } finally {
        	parser.close();
        	c.close();
        }
        
	}
	
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord) throws Exception  {
		
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("ISA-TAB",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		
		
		DBSubstanceWriter writer = new DBSubstanceWriter(dataset,new SubstanceRecord(),false,false);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.nextRecord();
            if (record==null) continue;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}
	
}
