package ambit2.db.processors.test;

import java.io.File;
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

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.IInputState;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.FP1024Writer;

public class FP1024WriterTest {

	
	@Before
	public void setUp() throws Exception {
		setUpDatabase();
	}

	@Test
	public void testProcess() throws Exception {
		IDatabaseConnection dbConnection = getConnection();
		String query = "SELECT idchemical,idstructure,unco FROM structure";
		ITable chemicals = 	dbConnection.createQueryTable("EXPECTED_CHEMICALS",query);
		Assert.assertEquals(3, chemicals.getRowCount());
		
		/*
		databaseData = 	dbConnection.createQueryTable("EXPECTED_DATA",query);
		Assert.assertEquals(3, databaseData.getRowCount());		
		*/
		FP1024Writer writer = new FP1024Writer();
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
	  
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:33060/ambit-test", "root", "");
	        
	   return new DatabaseConnection(jdbcConnection);
	}
    private void setUpDatabase() throws Exception {

        
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
