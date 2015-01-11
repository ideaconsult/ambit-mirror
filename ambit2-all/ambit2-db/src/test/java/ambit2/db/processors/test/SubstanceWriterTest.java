package ambit2.db.processors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.substance.processor.DBBundleStudyWriter;
import ambit2.db.substance.processor.DBSubstanceWriter;

public class SubstanceWriterTest extends DbUnitTest {

	public int write(SubstanceEndpointsBundle bundle,IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(bundle,reader, connection,new ReferenceSubstanceUUID());
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(null,reader, connection,new ReferenceSubstanceUUID());
	}

	public int write(SubstanceEndpointsBundle bundle,IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		return write(bundle,reader, connection,key,true,true,true);
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		return write(null,reader, connection,key,true,true,true);
	}
	
	public int write(SubstanceEndpointsBundle bundle,IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord,boolean clearMeasurements, boolean clearComposition) throws Exception  {
		DBSubstanceWriter writer;
		if (bundle!=null)
			writer = new DBBundleStudyWriter(bundle,DBSubstanceWriter.datasetMeta(),new SubstanceRecord());
		else 
			writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(),new SubstanceRecord(),clearMeasurements,clearComposition);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.next();
            if (record==null) continue;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.setImportedRecord((SubstanceRecord)record);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}	
	

	public SubstanceStudyParser getJSONReader(String json) throws Exception {
		InputStream in= null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/data/json/"+ json );
			return new SubstanceStudyParser(new InputStreamReader(in,"UTF-8"));
		} catch (Exception x) {
			throw x;
		} finally {
 			try { if (in!=null) in.close();} catch (Exception x) {}
		}
	}

	@Test
	public void testWriteJSONStudies() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
		ITable substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_experiment");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(0,substance.getRowCount()); 
        try {
        	
	        IRawReader<IStructureRecord> parser = getJSONReader("study.json");
	        write(null,parser,c.getConnection(),new ReferenceSubstanceUUID(),false,false,false);
	        parser.close();
	        
	        c = getConnection();
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
		    Assert.assertEquals(2,substance.getRowCount());
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_protocolapplication");
		    Assert.assertEquals(7,substance.getRowCount());
		    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_experiment");
		    Assert.assertEquals(21,substance.getRowCount());
        } finally {
        	c.close();
        }
	}
	
	@Test
	public void testWriteJSONStudiesBundle() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
		ITable substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_experiment");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(0,substance.getRowCount()); 
        try {
        	
	        IRawReader<IStructureRecord> parser = getJSONReader("study.json");
	        SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	        write(bundle,parser,c.getConnection(),new ReferenceSubstanceUUID(),false,false,false);
	        parser.close();
	        
	        c = getConnection();
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
		    Assert.assertEquals(2,substance.getRowCount());
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM bundle_substance_protocolapplication where idbundle=1");
		    Assert.assertEquals(7,substance.getRowCount());
		    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM bundle_substance_experiment where idbundle=1");
		    Assert.assertEquals(21,substance.getRowCount());
        } finally {
        	c.close();
        }
	}
	@Test
	public void testWriteJSONSubstanceStudies() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
		ITable substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_experiment");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
	    Assert.assertEquals(0,substance.getRowCount());
	    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_protocolapplication");
	    Assert.assertEquals(0,substance.getRowCount()); 
        try {
        	
	        IRawReader<IStructureRecord> parser = getJSONReader("substance.json");
	
	        write(null,parser,c.getConnection(),new ReferenceSubstanceUUID(),false,true,true);
	        parser.close();
	        
	        c = getConnection();
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
		    Assert.assertEquals(1,substance.getRowCount());
	        substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_protocolapplication");
		    Assert.assertEquals(104,substance.getRowCount());
		    substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_experiment");
		    Assert.assertEquals(110,substance.getRowCount());
        } finally {
        	c.close();
        }
	}
	
}
