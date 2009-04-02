package ambit2.db.processors.test;

import java.sql.ResultSet;
import java.util.BitSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.readers.RetrieveAtomContainer;
import ambit2.db.search.QueryExecutor;

public class FP1024WriterTest extends DbUnitTest {

	@Test
	public void testProcess() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset_nofp.xml");
		IDatabaseConnection dbConnection = getConnection();
		String query = "SELECT idchemical,idstructure,uncompress(structure) as c,format FROM structure";
		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_CHEMICALS",query);
		ITable fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM fp1024 where status='valid'");
		Assert.assertEquals(5, chemicals.getRowCount());
		Assert.assertEquals(0, fp.getRowCount());
		
        RepositoryReader reader = new RepositoryReader();
        RetrieveAtomContainer molReader = new RetrieveAtomContainer();
        reader.setConnection(dbConnection.getConnection());
        FingerprintGenerator gen = new FingerprintGenerator();
        FP1024Writer fpWriter = new FP1024Writer();
        fpWriter.setConnection(dbConnection.getConnection());
        fpWriter.open();
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		QueryExecutor<RetrieveAtomContainer> exec = new QueryExecutor<RetrieveAtomContainer>();
		exec.setConnection(dbConnection.getConnection());
		
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			molReader.setValue(o);
			
			ResultSet rs = exec.process(molReader);
			while (rs.next()) {
				IAtomContainer mol = molReader.getObject(rs);
				long mark = System.currentTimeMillis();
				BitSet bitset = null;
				try {
					bitset = gen.process((IMolecule)mol);
					o.setProperty(Property.getInstance(AmbitCONSTANTS.Fingerprint,AmbitCONSTANTS.Fingerprint),bitset);					
				} catch (Exception x) {

				}
	  			o.setProperty(Property.getInstance(AmbitCONSTANTS.FingerprintTIME,AmbitCONSTANTS.Fingerprint),System.currentTimeMillis()-mark);

				fpWriter.write(o);	
			}
			rs.close();						
			o.clear();

			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		
		fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM fp1024 where status = 'valid'");
		Assert.assertEquals(4, fp.getRowCount());		
		fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM fp1024 where status = 'error'");
		Assert.assertEquals(1, fp.getRowCount());				
		fpWriter.close();

	}		

}
