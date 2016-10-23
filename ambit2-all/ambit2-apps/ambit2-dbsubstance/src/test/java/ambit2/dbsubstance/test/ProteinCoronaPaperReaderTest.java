package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;

import junit.framework.Assert;
import net.idea.loom.nm.csv.CSV12Reader;
import net.idea.loom.nm.csv.CSV12SubstanceReader;
import net.idea.loom.nm.nanowiki.ENanoMapperRDFReader;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.rest.substance.SubstanceRDFReporter;

public class ProteinCoronaPaperReaderTest  extends DbUnitTest {
	@Test
	public void test() throws Exception {
		RawIteratingWrapper reader = null;
		try {
			LiteratureEntry entry = new LiteratureEntry("Protein Corona","http://dx.doi.org/10.1021/nn406018q");
    		entry.setType(_type.Dataset);

    		File baseDir = new File(System.getProperty("java.io.tmpdir"));
    		File datafile = new File(baseDir, "MergedSheets.csv");
    		if (!datafile.exists()) {
    			URL url = new URL(
    					"https://raw.githubusercontent.com/ideaconsult/Protein_Corona/master/MergedSheets.csv");
    			DownloadTool.download(url, datafile);
    		}

			CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(datafile),entry,"PRCR-");
			reader = new CSV12SubstanceReader(chemObjectReader);
			int r = 0;
			while (reader.hasNext()) {
				IStructureRecord mol = reader.nextRecord();
				Assert.assertTrue(mol instanceof SubstanceRecord);
				System.out.println(((SubstanceRecord)mol).getPublicName());
				System.out.println(((SubstanceRecord)mol).getMeasurements());
				r++;
			}
			Assert.assertTrue(r >= 120);
		} finally {
			reader.close();
		}
	}
	
	@Test
	public void testWriteProteinCoronaData() throws Exception {
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        IRawReader<IStructureRecord> parser = null;
        try {
    		LiteratureEntry entry = new LiteratureEntry("Protein Corona","http://dx.doi.org/10.1021/nn406018q");
    		entry.setType(_type.Dataset);
    		
    		File baseDir = new File(System.getProperty("java.io.tmpdir"));
    		File datafile = new File(baseDir, "MergedSheets.csv");
    		if (!datafile.exists()) {
    			URL url = new URL(
    					"https://raw.githubusercontent.com/ideaconsult/Protein_Corona/master/MergedSheets.csv");
    			DownloadTool.download(url, datafile);
    		}
    		
    		CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(datafile),entry,"PRCR-");
			parser = new CSV12SubstanceReader(chemObjectReader);
	        write(parser,c.getConnection(),new ReferenceSubstanceUUID(),false);
	        
        } finally {
        	parser.close();
        	c.close();
        }
        
	}

	@Test
	public void testRDFExport() throws Exception {
		CSV12SubstanceReader reader = null;
		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		File datafile = new File(baseDir, "MergedSheets.csv");
		if (!datafile.exists()) {
			URL url = new URL(
					"https://raw.githubusercontent.com/ideaconsult/Protein_Corona/master/MergedSheets.csv");
			DownloadTool.download(url, datafile);
		}
		SubstanceRDFReporter r = new SubstanceRDFReporter(hack,
				MediaType.TEXT_RDF_N3);
		Model model = ModelFactory.createDefaultModel();
		r.header(model, null);
		r.setOutput(model);
		LiteratureEntry entry = new LiteratureEntry("Protein Corona","http://dx.doi.org/10.1021/nn406018q");
		entry.setType(_type.Dataset);

		try {
    		CSV12Reader chemObjectReader = new CSV12Reader(new FileReader(datafile),entry,"PRCR-");
			reader = new CSV12SubstanceReader(chemObjectReader);
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				r.processItem((SubstanceRecord) record);
			}
			r.footer(model, null);
			File output = new File(System.getProperty("java.io.tmpdir") + "/"
					+ "protein_export.ttl");
			System.out.println("Exported to " + output.getAbsolutePath());
			OutputStream writer = new FileOutputStream(output);

			RDFDataMgr.write(writer, model, RDFFormat.TURTLE);
		} finally {
			if (reader != null) reader.close();
		}
	}

	
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key, boolean splitRecord) throws Exception  {
		
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("Protein corona",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		
		
		DBSubstanceWriter writer = new DBSubstanceWriter(dataset,new SubstanceRecord(),true,true);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.next();
            if (record==null) continue;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}
}
