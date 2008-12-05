package ambit2.db.processors.test;

import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.BitSet;
import java.util.Iterator;

import junit.framework.Assert;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.IInputState;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.readers.MoleculeReader;

public class FP1024WriterTest {

	
	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testProcess() throws Exception {
		IDatabaseConnection dbConnection = getConnection();
		String query = "SELECT idchemical,idstructure,uncompress(structure) as c,format FROM structure";
		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_CHEMICALS",query);
		ITable fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM FP1024");
		Assert.assertEquals(4, chemicals.getRowCount());
		Assert.assertEquals(0, fp.getRowCount());

		
		IStructureRecord record = new StructureRecord();
		MoleculeReader reader = new MoleculeReader();
		FingerprintGenerator gen = new FingerprintGenerator();
		FP1024Writer writer = new FP1024Writer();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		for (int i=0; i < chemicals.getRowCount();i++) {
			record.setIdchemical(Integer.parseInt(chemicals.getValue(i,"idchemical").toString()));
			record.setIdstructure(Integer.parseInt(chemicals.getValue(i,"idstructure").toString()));
			record.setContent(chemicals.getValue(i,"c").toString());
			record.setFormat(chemicals.getValue(i,"format").toString());
			System.out.println(chemicals.getValue(i,"c").getClass().getName());
			IIteratingChemObjectReader mReader = new MyIteratingMDLReader(
					new StringReader(record.getContent()),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IMolecule) {
					long mark = System.currentTimeMillis();
					BitSet bitset = gen.process((IMolecule)mol);
	      			//o.getProperties().put(AmbitCONSTANTS.FingerprintTIME,System.currentTimeMillis()-mark);
					//o.getProperties().put(AmbitCONSTANTS.Fingerprint,bitset);
					//fpWriter.write(o);
				}
				
			}			
			record.getProperties().put(AmbitCONSTANTS.Fingerprint,gen.process(reader.process(record)));
			
			writer.write(record);
		}
		writer.close();
		fp = 	dbConnection.createQueryTable("EXPECTED_FP","SELECT * FROM FP1024");
		Assert.assertEquals(4, fp.getRowCount());		
		/*
		databaseData = 	dbConnection.createQueryTable("EXPECTED_DATA",query);
		Assert.assertEquals(3, databaseData.getRowCount());		
		*/
		
		BatchDBProcessor<IStructureRecord, BitSet> batch = new BatchDBProcessor<IStructureRecord, BitSet>() {
			@Override
			protected Iterator getIterator(IInputState target)
					throws AmbitException {
				return null;//new RepositoryReader<IInputState>();
			}

			@Override
			protected void closeIterator(Iterator iterator)
					throws AmbitException {
				// TODO Auto-generated method stub
				
			}
		};
		
		dbConnection.close();
		
	}

	protected IDatabaseConnection getConnection() throws Exception {
	  
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:33060/ambit-test", "root", "");
	        
	   return new DatabaseConnection(jdbcConnection);
	}
    private void setUpDatabase() throws Exception {
    	/*
    	Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:33060/mysql", "root", "");
        DbCreateDatabase db = new DbCreateDatabase();
        try {
        	jdbcConnection.setAutoCommit(true);

	        db.setConnection(jdbcConnection);
	        db.write(new StringBean("ambit-test"));

	        //jdbcConnection.commit();
        } catch (Exception x) {
        	x.printStackTrace();
        	//jdbcConnection.rollback();
        } finally {
	        db.close();
        }
        */
        IDatabaseConnection connection = getConnection();
        // initialize your dataset here
        IDataSet dataSet = new FlatXmlDataSet(new File("src/test/resources/ambit2/db/processors/test/dataset_nofp.xml"));
        
        try {
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } finally {
            connection.close();
        }
    }

}
