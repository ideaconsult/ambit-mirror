package ambit.test.io.batch;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.ConnectionPool;
import ambit.database.processors.CASSmilesLookup;
import ambit.exceptions.AmbitIOException;
import ambit.io.FileInputState;
import ambit.io.FileOutputState;
import ambit.io.ImageWriter;
import ambit.io.IteratingDelimitedFileReader;
import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchConfig;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.io.batch.IBatchStatistics;
import ambit.io.batch.IJobStatus;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.results.FingerprintProfile;
import ambit.processors.structure.AtomEnvironmentGenerator;
import ambit.processors.structure.FingerprintProfileGenerator;
import ambit.test.ITestDB;
import ambit.ui.batch.BatchProcessingDialog;

public class DefaultBatchProcessingTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DefaultBatchProcessingTest.class);
	}

	public DefaultBatchProcessingTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFiles() {
		try {
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					new FileInputState("data/misc/Debnath_smiles.csv"),
					new FileOutputState("data/misc/Debnath_testbatch.html"),
					new DefaultAmbitProcessor(), new DefaultBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());

			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
		} catch (BatchProcessingException x) {
			x.printStackTrace();
			fail();
		}
	}
	public void testReaderWriter() {
		batch("data/misc/Debnath_testbatch.csv");
		batch("data/misc/Debnath_testbatch.sdf");
	}
	public void batch(String outputFile) {
		try {
			IIteratingChemObjectReader reader = new IteratingDelimitedFileReader(
					new FileInputStream("data/misc/Debnath_smiles.csv"));

			IChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer, 
					new DefaultAmbitProcessor(),new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}
	}
	
	public void testFingerprintProfile() {
		try {
			String inFile = "data/misc/Debnath_smiles.csv";
			IIteratingChemObjectReader reader = new IteratingDelimitedFileReader(
					new FileInputStream(inFile));

			String outfile = "data/misc/Debnath_smiles.png";
			
			Object outWriter = FileOutputState.getWriter(new FileOutputStream(outfile),outfile);
			assertTrue(outWriter instanceof ImageWriter);
			ImageWriter writer = (ImageWriter) outWriter;
			ProcessorsChain processor = new ProcessorsChain();
	
			FingerprintProfileGenerator profiler = new FingerprintProfileGenerator();
			FingerprintProfile profile = (FingerprintProfile)profiler.createResult();
			profile.setTitle(inFile);
			profiler.setResult(profile);
			
			processor.add(profiler);
	
        	
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
			
			//should be the same instance
			assertEquals(profile,profiler.getResult());
			double[] histogram = profile.getHist(1);
			assertEquals(profile.getLength(),histogram[0],0.001);
			File file = new File("data/misc/Debnath_smiles.jpg");
			assertTrue(file.exists());
			;
			
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}
	}
	
	public void testCASLookup() {
		try {
		    int n = 238;
		    //int n = 8544;
			//String outputFile = "data/misc/BCF_Grammatica_lookup.csv";
			String outputFile = "data/misc/ECVAM selCASnumbers.txt_lookup.csv";
		    //String outputFile = "data/misc/chemicals_epafhm.csv";
		    //EPAFHM_v2a_617_1Mar05.sdf
		    /*
			IteratingChemObjectReader reader = new IteratingDelimitedFileReader(
			        new FileInputStream("data/misc/chemicals.txt"),
			        new DelimitedFileFormat('|','"'));
				//	new FileInputStream("data/misc/BCF_Grammatica.csv"));
				 
				 */
//			IteratingChemObjectReader reader = new IteratingMDLReader(
			//        new FileInputStream("data/misc/EPAFHM_v2a_617_1Mar05.sdf")
			  //      );
	
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream("data/misc/BCF_Grammatica.csv"),".csv",null);
					//new FileInputStream("data/misc/ECVAM selCASnumbers.txt"),".txt");
			
			IChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			
			//ConnectionPool pool = new ConnectionPool(ITestDB.host,ITestDB.port,ITestDB.database,"root","",1,1);
			ConnectionPool pool = new ConnectionPool(ITestDB.host,"33060",ITestDB.database,"root","",1,1);
			Connection conn = pool.getConnection();
			ProcessorsChain pc = new ProcessorsChain();
			pc.add(new IdentifiersProcessor());
			pc.add(new CASSmilesLookup(conn,true));
			

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					pc,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			
			BatchProcessingDialog d = new BatchProcessingDialog(batch,(Frame)null,true);
			d.show();
			//batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
			
			pool.returnConnection(conn);
			pool = null;
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

	public void testAtomEnvironments() {
		try {
			String inFile = "data/misc/Debnath_smiles.csv";
			IIteratingChemObjectReader reader = new IteratingDelimitedFileReader(
					new FileInputStream(inFile));

			String outfile = "data/misc/Debnath_smiles_atomenvironments.sdf";
			
			IChemObjectWriter outWriter = FileOutputState.getWriter(new FileOutputStream(outfile),outfile);
			ProcessorsChain processor = new ProcessorsChain();
	
			AtomEnvironmentGenerator profiler = new AtomEnvironmentGenerator();
			processor.add(profiler);
	
        	
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					outWriter,
					processor,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(88, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
			

			
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}
	}
	
	
}
