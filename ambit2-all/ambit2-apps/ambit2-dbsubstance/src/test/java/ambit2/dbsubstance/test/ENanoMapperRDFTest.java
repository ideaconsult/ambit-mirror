package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.nm.nanowiki.ENanoMapperRDFReader;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.dbsubstance.DBSubstanceImport;
import ambit2.rest.substance.SubstanceRDFReporter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * These are tests reading eNanoMapper RDF and writing to database
 * 
 * @author egonw
 * 
 */
public class ENanoMapperRDFTest extends DbUnitTest {

	public static File getRDFFile() throws Exception {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(baseDir, "datamodel.ttl");
		if (!file.exists()) {
			URL url = new URL(
					"https://gist.githubusercontent.com/egonw/07e5fc9b1be7f9ece44834a169ddc155/raw/datamodel.ttl");
			DownloadTool.download(url, file);
		}
		return file;
	}

	@Test
	public void testWriteENMRDF() throws Exception {
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		URL propertiesFile = this.getClass().getClassLoader()
				.getResource("ambit2/db/conf/test.properties");
		Assert.assertNotNull(propertiesFile);
		Assert.assertTrue(new File(propertiesFile.getFile()).exists());
		File rdf = getRDFFile();
		Assert.assertNotNull(rdf);
		Assert.assertTrue(rdf.exists());
		

		String[] args = new String[] {  "-p", "enmrdf", "-i",
				rdf.getAbsolutePath(),  "-c",
				propertiesFile.getFile(),
		// "-m", "false", "-t", "true"
				"-t", "false"
		};
		DBSubstanceImport.main(args);

		IDatabaseConnection c = getConnection();
		try {

			ITable table = c.createQueryTable("EXPECTED",
					"SELECT count(*) as c FROM substance");
			//
			Assert.assertEquals(new BigInteger("1"), table.getValue(0, "c"));

			table = c.createQueryTable("EXPECTED",
					"SELECT count(*) as c FROM substance_protocolapplication");
			Assert.assertEquals(new BigInteger("2"), table.getValue(0, "c"));			
			
			table = c.createQueryTable("EXPECTED",
					"SELECT count(*) as c FROM substance_experiment");
			Assert.assertEquals(new BigInteger("2"), table.getValue(0, "c"));		
			
		} finally {
			c.close();
		}
	}
	

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key, boolean splitRecord)
			throws Exception {

		DBSubstanceWriter writer = new DBSubstanceWriter(
				DBSubstanceWriter.datasetMeta(), new SubstanceRecord(), true,
				true);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
		writer.open();
		int records = 0;
		while (reader.hasNext()) {
			Object record = reader.next();
			if (record == null)
				continue;
			Assert.assertTrue(record instanceof IStructureRecord);
			writer.process((IStructureRecord) record);
			records++;
		}
		writer.close();
		return records;
	}

	@Test
	public void testRead() throws Exception {
		ENanoMapperRDFReader reader = null;
		try {
			reader = new ENanoMapperRDFReader(new InputStreamReader(
					new FileInputStream(getRDFFile())), "DEMO");
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				System.out.println("Substance: "
						+ ((SubstanceRecord) record).toJSON(""));
			}
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	@Test
	public void testRoundtrip() throws Exception {
		ENanoMapperRDFReader reader = null;
		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));
		SubstanceRDFReporter r = new SubstanceRDFReporter(hack,
				MediaType.TEXT_RDF_N3);
		Model model = ModelFactory.createDefaultModel();
		r.header(model, null);
		r.setOutput(model);
		try {
			reader = new ENanoMapperRDFReader(new InputStreamReader(
					new FileInputStream(getRDFFile())), "DEMO");
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				r.processItem((SubstanceRecord) record);
			}
			r.footer(model, null);
			File output = new File(System.getProperty("java.io.tmpdir") + "/"
					+ "enmrdf_export.ttl");
			System.out.println("Exported to " + output.getAbsolutePath());
			OutputStream writer = new FileOutputStream(output);

			RDFDataMgr.write(writer, model, RDFFormat.TURTLE);
		} finally {
			if (reader != null)
				reader.close();
		}
	}

}
