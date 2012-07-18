package ambit2.db.processors.test;

import java.math.BigInteger;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.QueryExecutor;
import ambit2.descriptors.processors.BitSetGenerator;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class FP1024WriterTest extends DbUnitTest {
	
	protected FPTable getMode() {
		return FPTable.fp1024;
	}

	@Test
	public void testProcess() throws Exception {
		BitSetGenerator generator = new BitSetGenerator(getMode());
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset_nofp.xml");
		IDatabaseConnection dbConnection = getConnection();
		String query = "SELECT idchemical,idstructure,uncompress(structure) as c,format FROM structure";
		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_CHEMICALS",query);
		ITable fp = 	dbConnection.createQueryTable("EXPECTED_FP",
					String.format("SELECT * FROM %s where status='valid'",generator.getFpmode().getTable()));
		Assert.assertEquals(5, chemicals.getRowCount());
		Assert.assertEquals(0, fp.getRowCount());
		
        RepositoryReader reader = new RepositoryReader();
        RetrieveStructure molReader = new RetrieveStructure();
        reader.setConnection(dbConnection.getConnection());
        
        FP1024Writer fpWriter = new FP1024Writer(generator.getFpmode());
        fpWriter.setConnection(dbConnection.getConnection());
        fpWriter.open();
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		QueryExecutor<RetrieveStructure> exec = new QueryExecutor<RetrieveStructure>();
		exec.setConnection(dbConnection.getConnection());
		int errors = 0;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			molReader.setValue(o);
			
			ResultSet rs = exec.process(molReader);
			while (rs.next()) {
				IStructureRecord record = molReader.getObject(rs);
				if (record==null) { errors++; continue;}
				long mark = System.currentTimeMillis();
				try {
					record = generator.process(record);
					fpWriter.write(record);
				} catch (Exception x) {
					errors++;
				}
			}
			rs.close();						
			o.clear();

			records ++;

		}
		reader.close();
		
		fp = 	dbConnection.createQueryTable("EXPECTED_FP",
					String.format("SELECT count(*) as c FROM %s where status = 'valid'",generator.getFpmode().getTable()));
		Assert.assertEquals(new BigInteger("4"), fp.getValue(0,"c"));		
		fp = 	dbConnection.createQueryTable("EXPECTED_FP",
				String.format("SELECT count(*) as c FROM %s where status = 'error'",generator.getFpmode().getTable()));
		Assert.assertEquals(new BigInteger("1"), fp.getValue(0,"c"));					
		fpWriter.close();

	}		

}
