package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.nm.csv.CSV12Reader;
import net.idea.loom.nm.csv.CSV12SubstanceReader;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;

public class ProteinCoronaPaperReaderTest  extends DbUnitTest {
	@Test
	public void test() throws Exception {
		RawIteratingWrapper reader = null;
		try {
			LiteratureEntry entry = new LiteratureEntry("Protein Corona","http://dx.doi.org/10.1021/nn406018q");
    		entry.setType(_type.Dataset);
    		
			CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(
					new File("D:/src-ideaconsult/Protein_Corona/MergedSheets.csv")),entry,"PRCR-");
			reader = new CSV12SubstanceReader(chemObjectReader);
			int r = 0;
			while (reader.hasNext()) {
				IStructureRecord mol = reader.nextRecord();
				Assert.assertTrue(mol instanceof SubstanceRecord);
				System.out.println(((SubstanceRecord)mol).getPublicName());
				System.out.println(((SubstanceRecord)mol).getMeasurements());
				r++;
			}
			Assert.assertEquals(120,r);
		} finally {
			reader.close();
		}
	}
	
	@Test
	public void testWriteProteinCoronaData() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        IRawReader<IStructureRecord> parser = null;
        try {
    		LiteratureEntry entry = new LiteratureEntry("Protein Corona","http://dx.doi.org/10.1021/nn406018q");
    		entry.setType(_type.Dataset);
    		
    		CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(
					new File("D:/src-ideaconsult/Protein_Corona_fork/MergedSheets.csv")),entry,"PRCR-");
			parser = new CSV12SubstanceReader(chemObjectReader);
	        write(parser,c.getConnection(),new ReferenceSubstanceUUID(),false);
	        
        } finally {
        	parser.close();
        	c.close();
        }
        
	}
	
	
	@Test
	public void testWriteLLNAData() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        IRawReader<IStructureRecord> parser = null;
        try {
    		//LiteratureEntry entry = new LiteratureEntry("NICEATM LLNA Database","ITS-2 lipid Training and Test Sets Oct 1 2013_format 200514");
        	LiteratureEntry entry = new LiteratureEntry("ITS-2 lipid Training set","ITS-2 lipid TrainingSets Oct 1 2013_format 200514.csv");
    		entry.setType(_type.Dataset);

    		CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(
					new File("D:/src-ideaconsult/LLNA/ITS-2 lipid TrainingSets Oct 1 2013_format 200514.csv")),entry,"LLNA-");
			parser = new CSV12SubstanceReader(chemObjectReader);
	        write(parser,c.getConnection(),new ReferenceSubstanceUUID(),false);
	        
        } finally {
        	parser.close();
        	c.close();
        }
        
	}
	
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord) throws Exception  {
		
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("Protein corona",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		
		
		DBSubstanceWriter writer = new DBSubstanceWriter(dataset,new SubstanceRecord(),true,true);
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
