package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Test;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import junit.framework.Assert;
import net.idea.loom.nm.nanowiki.ENanoMapperRDFReader;

/**
 * These are tests reading eNanoMapper RDF and writing to database
 * @author egonw
 *
 */
public class ENanoMapperRDFTest {

	public static File getRDFFile() throws Exception {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(baseDir, "datamodel.ttl");
		if (!file.exists()) {
			URL url = new URL("https://gist.githubusercontent.com/egonw/07e5fc9b1be7f9ece44834a169ddc155/raw/datamodel.ttl");
			DownloadTool.download(url, file);
		}
		return file;
	}

	@Test
	public void testRead() throws Exception {
		ENanoMapperRDFReader reader = null;
		int records = 0;
		try {
			reader = new ENanoMapperRDFReader(
				new InputStreamReader(new FileInputStream(getRDFFile()))
			);
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				System.out.println("Substance: " + ((SubstanceRecord) record).getSubstanceName());
				records++;
			}
		} finally {
			if (reader != null) reader.close();
		}
	}

}
