package ambit2.test.database.readers;

import java.io.FileOutputStream;
import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.database.ConnectionPool;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadCASProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.processors.ReadSubstanceProcessor;
import ambit2.database.processors.SubstructureSearchProcessor;
import ambit2.database.search.DbExactSearchReader;
import ambit2.database.search.DbSimilarityByAtomenvironmentsReader;
import ambit2.database.search.DbSimilarityByFingerprintsReader;
import ambit2.database.search.DbSubstructurePrescreenReader;
import ambit2.io.batch.DefaultBatchProcessing;
import ambit2.io.batch.EmptyBatchConfig;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.test.ITestDB;

public class DbSearchReaderTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbSearchReaderTest.class);
	}

	public void testExactSearch() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbExactSearchReader reader = new DbExactSearchReader(
					conn,
					MoleculeFactory.makeBenzene(),
					null,
					0,100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbExactSearchReader.sdf"));
			
			IAmbitProcessor processor = new DefaultAmbitProcessor();

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);
			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertTrue(rr>0);
			assertTrue(rr<100);
			assertEquals(rr,rp);
			assertTrue(rw <= rr);
			assertTrue(rw > 0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
	
	public void testSubstructureSearch() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			IMolecule benzene = MoleculeFactory.makeBenzene();
			DbSubstructurePrescreenReader reader = new DbSubstructurePrescreenReader(
					conn,
					benzene,
					null,
					0,100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbSubstructureSearchReaderTest.sdf"));
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn));
			processors.add(new SubstructureSearchProcessor(benzene));
			
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);

			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw < rr);
			assertTrue(rw >0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
	public void testSimilarityByTanimoto() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbSimilarityByFingerprintsReader reader = new DbSimilarityByFingerprintsReader(
					conn,
					MoleculeFactory.makeBenzene(),
					null,
                    0.5,
					1,
					100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbSimilarityByFingerprintsReaderTest.sdf"));
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn));
			
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);
			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw <= rr);
			assertTrue(rw > 0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
	
	public void testSimilarityByAtomEnvironments() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbSimilarityByAtomenvironmentsReader reader = new DbSimilarityByAtomenvironmentsReader(
					conn,
					MoleculeFactory.makeBenzene(),
					null,
                    0.5,
					0,100
					);
            assertEquals("Atom environment Similarity",reader.getSimilarityLabel());
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbSimilarityByAtomenvironmentsReader.sdf"));
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn));
			processors.add(new ReadCASProcessor(conn));
			processors.add(new ReadSMILESProcessor(conn));
			processors.add(new ReadNameProcessor(conn));
			processors.add(new ReadAliasProcessor(conn));
			
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);
			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw <= rr);
			assertTrue(rw > 0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}

}
