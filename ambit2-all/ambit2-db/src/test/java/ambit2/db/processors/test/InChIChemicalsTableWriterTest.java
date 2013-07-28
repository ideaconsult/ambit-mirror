package ambit2.db.processors.test;

import java.math.BigInteger;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.StructureNormalizer;
import ambit2.db.RepositoryReader;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.chemical.InChIChemicalsWriter;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class InChIChemicalsTableWriterTest extends DbUnitTest {
	
	protected FPTable getMode() {
		return FPTable.inchi;
	}

	@Test
	public void testProcess() throws Exception {
		StructureNormalizer normalizer = new StructureNormalizer();
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset_nofp.xml"); //no inchis as well
		IDatabaseConnection dbConnection = getConnection();

		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM chemicals where inchi is null");
		Assert.assertEquals(5, chemicals.getRowCount());

		
        RepositoryReader reader = new RepositoryReader();
        RetrieveStructure molReader = new RetrieveStructure(true);
        reader.setConnection(dbConnection.getConnection());
        
        InChIChemicalsWriter inchiWriter = new InChIChemicalsWriter();
        inchiWriter.setConnection(dbConnection.getConnection());
        inchiWriter.open();
        reader.open();
        int records = 0;
	
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
				try {
					record = normalizer.process(record);
					inchiWriter.process(record);
				} catch (Exception x) {
					errors++;
				}
			}
			rs.close();						
			o.clear();

			records ++;

		}
		reader.close();
		
		chemicals = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT count(*) as c FROM chemicals where inchi is not null");
		Assert.assertEquals(new BigInteger("4"), chemicals.getValue(0,"c"));		
							
		inchiWriter.close();

	}		

}