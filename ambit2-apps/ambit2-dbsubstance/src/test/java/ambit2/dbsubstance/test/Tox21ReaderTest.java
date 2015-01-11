package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.pubchem.rest.PubChemAIDReader;

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
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;

public class Tox21ReaderTest extends DbUnitTest {
	
	@Override
	protected boolean isProfileSQL() {
		return false;
	}
	@Test
	public void testWriteTox21() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        
        
		File dir = new File("F:/Downloads/Chemical data/TOXCAST/Tox21/");
		File[] files = dir.listFiles();
		/*
		File[] files = new File[] {
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_720681_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_743228_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_743210_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_743209_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_720687_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_720685_data.csv"),
				new File("F:/Downloads/Chemical data/TOXCAST/Tox21/AID_720678_data.csv")
		};
		*/

		IDatabaseConnection c = null;
			for (int i=0; i < files.length; i++) 
			if (files[i].getName().endsWith(".csv")) {
				System.out.println(files[i].getAbsolutePath());
		        PubChemAIDReader parser = null;
		        InputStream jsonmeta = null;
		        Connection conn = null;
		        try {
		    		LiteratureEntry entry = new LiteratureEntry("Tox21","???");
		    		entry.setType(_type.Dataset);
		    		
		    		String key = files[i].getName().replace(".csv", "").replace("_data","");
		    		URL meta = this.getClass().getClassLoader().getResource(String.format("net/idea/loom/tox21/%s.json",key));
		    		if (meta==null) throw new FileNotFoundException(key);
		    		jsonmeta = new FileInputStream(meta.getFile());
		    		if (jsonmeta==null) throw new FileNotFoundException(meta.getFile());
		    		
		    		parser = new PubChemAIDReader(
		    				files[i],jsonmeta,
		    				new String[] {"summary"}
		    				);
					c = getConnection(true);
					conn = c.getConnection();		    		
		    		parser.setReadPubchemScoreOnly(false);
			        write(parser,conn,new ReferenceSubstanceUUID(),false,10);
		        } catch (Exception x) {
		        	System.err.println(x.getMessage());
		        } finally {
		        	if (jsonmeta!=null) jsonmeta.close();
		        	if (parser!=null) parser.close();
		        	if (conn!=null) conn.close();
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
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
            if (max>0 && records>=max) break;
		}
		writer.close();
		return records;
	}
	
}
