package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.zip.GZIPInputStream;

import junit.framework.Assert;
import net.idea.loom.nm.nanowiki.ENanoMapperRDFReader;
import net.idea.loom.nm.nanowiki.NanoWikiRDFReader;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.rest.substance.SubstanceRDFReporter;

/**
 * These are tests reading NanoWiki RDF and writing to database
 * @author nina
 *
 */
public class NanoWikiRDFTest extends DbUnitTest {

	public static File getNanoWikiFile() throws Exception {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(baseDir, "nanowiki3.rdf.gz");
		if (!file.exists()) {
			URL url = new URL("https://ndownloader.figshare.com/files/5228257");
			DownloadTool.download(url, file);
		}
		return file;
	}

	@Test
	public void test() throws Exception {
	}

	@Test
	public void testRead() throws Exception {
		NanoWikiRDFReader reader = null;
		int records = 0;
		try {
			reader = new NanoWikiRDFReader(
					new InputStreamReader(new GZIPInputStream(
							new FileInputStream(getNanoWikiFile()))));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				System.out.println(((SubstanceRecord) record).getPublicName());
				records++;
			}
		} finally {
			reader.close();
		}
	}

	@Test
	public void testWriteNanoWikiRDF() throws Exception {
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		IDatabaseConnection c = getConnection();
		IRawReader<IStructureRecord> parser = null;
		try {
			parser = new NanoWikiRDFReader(
					new InputStreamReader(new GZIPInputStream(
							new FileInputStream(getNanoWikiFile()))));
			write(parser, c.getConnection(), new ReferenceSubstanceUUID(),
					false);

		} finally {
			parser.close();
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
	public void testRoundtrip() throws Exception {
		NanoWikiRDFReader reader = null;
		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));
		SubstanceRDFReporter r = new SubstanceRDFReporter(hack,
				MediaType.TEXT_RDF_N3);
		Model model = ModelFactory.createDefaultModel();
		r.header(model, null);
		r.setOutput(model);
		try {
			reader = new NanoWikiRDFReader(
					new InputStreamReader(new GZIPInputStream(
							new FileInputStream(getNanoWikiFile()))));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				r.processItem((SubstanceRecord) record);
			}
			r.footer(model, null);
			File output = new File(System.getProperty("java.io.tmpdir") + "/"
					+ "nanordf_enm-export.ttl");
			System.out.println("Exported to " + output.getAbsolutePath());
			OutputStream writer = new FileOutputStream(output);

			RDFDataMgr.write(writer, model, RDFFormat.TURTLE);
		} finally {
			if (reader != null) reader.close();
		}
	}
}
