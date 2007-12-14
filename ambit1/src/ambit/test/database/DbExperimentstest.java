/**
 * Created on 2005-3-19
 *
 */
package ambit.test.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.experiment.DSSToxCarcinogenicityTemplate;
import ambit.data.experiment.DSSToxERBindingTemplate;
import ambit.data.experiment.DSSToxLC50Template;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentFactory;
import ambit.data.experiment.ExperimentList;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.data.literature.ReferenceFactory;
import ambit.database.DbConnection;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.processors.ExperimentSearchProcessor;
import ambit.database.writers.ExperimentWriter;
import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.io.FileInputState;
import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchConfig;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.IJobStatus;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.ProcessorsChain;
import ambit.processors.experiments.ExperimentParser;


/**
 * JUnit test for {@link ambit.database.writers.ExperimentWriter} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbExperimentstest extends TestCase {
	private DbConnection dbconn = null;
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbExperimentstest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dbconn = new DbConnection("localhost","33060","ambit","root","");
		dbconn.open();
		
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		dbconn.close();
		dbconn = null;
		
	}

	/**
	 * Constructor for DbExperimentstest.
	 * @param arg0
	 */
	public DbExperimentstest(String arg0) {
		super(arg0);
	}

	public void testAddExperiment() {
		try {
			ExperimentList list = ExperimentFactory.createGlendeExperiments();
			CASSmilesLookup processor = new CASSmilesLookup(dbconn.getConn(),true);
			ExperimentWriter writer = new ExperimentWriter(dbconn);
			for (int i=0; i < list.size(); i++) {
				Experiment e = (Experiment) list.getItem(i);
				Molecule mol = (Molecule) processor.process(e.getMolecule());
				mol.setProperty(AmbitCONSTANTS.EXPERIMENT, e);
				try {
					writer.write(mol);
				} catch (CDKException x) {
					x.printStackTrace();
				}				
			}
			writer.close();
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		} catch (IOException x) {
			x.printStackTrace();
			fail();
		}	
	}
	
	

	public void xtestAddEPAFHM() {
		try {
			StudyTemplate template = new DSSToxLC50Template("");
			Study study = new Study("LC50",template);
			String fname = "data/misc/EPAFHM_v2a_617_1Mar05.sdf";
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(new File(fname)), ".sdf",null);
			ExperimentWriter writer = new ExperimentWriter(dbconn);
			/*
			String outputFile = "data/misc/chemicals_epafhm.csv";
			ChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			*/
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new CASSmilesLookup(dbconn.getConn(),true));
			processors.add(new ExperimentParser(template,
					ReferenceFactory.createDatasetReference("DSSTox Fathead Minnow database", "http://www.epa.gov/nheerl/dsstox/sdf_epafhm.html"),""));			

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors, new DefaultBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());

			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			/*
			assertEquals(617, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(476, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(9, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(749, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
					*/
		} catch (BatchProcessingException x) {
			x.printStackTrace();
			fail();
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}

	}
	
	
	public void xtestAddEPAERB() {
		try {
			StudyTemplate template = new DSSToxERBindingTemplate("");
			Study study = new Study("ER RBA",template);
			String fname = "data/misc/NCTRER_v2a_232_1Mar05.sdf";
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(new File(fname)), ".sdf",null);
			ExperimentWriter writer = new ExperimentWriter(dbconn);
			/*
			String outputFile = "data/misc/chemicals_epafhm.csv";
			ChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			*/
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new CASSmilesLookup(dbconn.getConn(),true));
			processors.add(new ExperimentParser(template,ReferenceFactory.createDatasetReference("NCTRER_v2a_232_1Mar05.sdf", "http://www.epa.gov/nheerl/dsstox/sdf_epafhm.html"),""));			

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors, new DefaultBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());

			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			/*
			assertEquals(617, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(476, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(9, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(749, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
					*/
		} catch (BatchProcessingException x) {
			x.printStackTrace();
			fail();
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}

	}
	
	public void testAddEPACarcinogenicity() {
		try {
			StudyTemplate template = new DSSToxCarcinogenicityTemplate("");
			Study study = new Study("Carcinogenicity",template);
			String fname = "data/misc/CPDBAS_v2a_1451_1Mar05.sdf";
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(new File(fname)), ".sdf",null);
			ExperimentWriter writer = new ExperimentWriter(dbconn);
			/*
			String outputFile = "data/misc/chemicals_epacarc.csv";
			ChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			*/
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new CASSmilesLookup(dbconn.getConn(),true));
			processors.add(new ExperimentParser(template,ReferenceFactory.createDatasetReference("CPDBAS_v2a_1451_1Mar05.sdf", "http://www.epa.gov/nheerl/dsstox/sdf_epafhm.html"),""));			

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors, new DefaultBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());

			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			/*
			assertEquals(617, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(476, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(9, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(749, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
					*/
		} catch (BatchProcessingException x) {
			x.printStackTrace();
			fail();
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		}

	}	
	
	public void test2sql() {
		try {
			ExperimentSearchProcessor esp = new ExperimentSearchProcessor(dbconn.getConn());
			StudyTemplate template = new DSSToxCarcinogenicityTemplate("");
			Study study = new Study("Carcinogenicity",template);
			study.setStudyCondition("Endpoint","TD50, Tumor Target Sites");
			study.setStudyCondition("Species","mouse");
			String sql = esp.studyConditionsToSQL(study);
			System.out.println(sql);
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
			
		}
	}
}
