package ambit2.db.processors.test;

import java.util.Hashtable;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.readers.MoleculeReader;

public class FP1024WriterTest extends DbUnitTest {

	
	@Before
	public void setUp() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset_nofp.xml");
	}

	@Test
	public void testProcess() throws Exception {
		IDatabaseConnection dbConnection = getConnection();
		String query = "SELECT idchemical,idstructure,uncompress(structure) as c,format FROM structure";
		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_CHEMICALS",query);
		ITable fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM FP1024 where status='valid'");
		Assert.assertEquals(5, chemicals.getRowCount());
		Assert.assertEquals(0, fp.getRowCount());

		
		IStructureRecord record = new StructureRecord();
		record.setProperties(new Hashtable());
		MoleculeReader reader = new MoleculeReader();
		FingerprintGenerator gen = new FingerprintGenerator();
		FP1024Writer writer = new FP1024Writer();
		writer.setConnection(dbConnection.getConnection());		
		writer.open();

		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		for (int i=0; i < chemicals.getRowCount();i++) {
			record.setIdchemical(Integer.parseInt(chemicals.getValue(i,"idchemical").toString()));
			record.setIdstructure(Integer.parseInt(chemicals.getValue(i,"idstructure").toString()));

			record.setFormat(chemicals.getValue(i,"format").toString());
			if (DataType.VARBINARY == chemicals.getTableMetaData().getColumns()[2].getDataType()) {
				byte[] bytes = (byte[])chemicals.getValue(i,"c");	
				//System.out.println(new String(bytes));				
				record.setContent(new String(bytes));
			} else
				record.setContent(chemicals.getValue(i,"c").toString());			
			long mark = System.currentTimeMillis();
			try {
				record.getProperties().put(AmbitCONSTANTS.Fingerprint,gen.process(reader.process(record)));
				record.getProperties().put(AmbitCONSTANTS.FingerprintSTATUS,FP1024Writer.FP1024_status.valid);
			} catch (Exception x) {
				record.getProperties().put(AmbitCONSTANTS.FingerprintSTATUS,FP1024Writer.FP1024_status.error);
			} finally {
				record.getProperties().put(AmbitCONSTANTS.FingerprintTIME,System.currentTimeMillis()-mark);
			}
			
			try {
				writer.write(record);
			} catch (Exception x) {
				Assert.assertEquals(666, chemicals.getValue(i,"idchemical"));
			}
		}

		fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM FP1024 where status = 'valid'");
		Assert.assertEquals(4, fp.getRowCount());		
		fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM FP1024 where status = 'error'");
		Assert.assertEquals(1, fp.getRowCount());				
		writer.close();		
		dbConnection.close();
		
	}

}
