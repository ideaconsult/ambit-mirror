package ambit2.db.processors.test;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;
import net.idea.i5.io.I5ZReader;
import net.idea.i5.io.QASettings;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.io.DownloadTool;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;

public class I5SubstanceWriterTest extends SubstanceWriterTest {

	@Test
	public void testWriteMultipleFiles_i5d() throws Exception {

		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
		IDatabaseConnection c = getConnection();

		ITable chemicals = c.createQueryTable("EXPECTED",
				"SELECT * FROM chemicals");
		Assert.assertEquals(0, chemicals.getRowCount());
		ITable strucs = c.createQueryTable("EXPECTED",
				"SELECT * FROM structure");
		Assert.assertEquals(0, strucs.getRowCount());
		ITable srcdataset = c.createQueryTable("EXPECTED",
				"SELECT * FROM src_dataset");
		Assert.assertEquals(0, srcdataset.getRowCount());
		ITable struc_src = c.createQueryTable("EXPECTED",
				"SELECT * FROM struc_dataset");
		Assert.assertEquals(0, struc_src.getRowCount());
		ITable property = c.createQueryTable("EXPECTED",
				"SELECT * FROM properties");
		Assert.assertEquals(0, property.getRowCount());
		ITable property_values = c.createQueryTable("EXPECTED",
				"SELECT * FROM property_values");
		Assert.assertEquals(0, property_values.getRowCount());

		/**
		 * Now reading only substances and reference substances Document types:
		 * EndpointStudyRecord: 877 AttachmentDocument: 5 LegalEntity: 1
		 * ReferenceSubstance: 6 Substance: 1 EndpointRecord: 14
		 */
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream("ambit2/db/substance/testNM.i5z");
		// InputStream in =
		// I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i5z");
		// InputStream in =
		// I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-e2b69497-1c50-3d0b-a2b2-41d0a4d74c54.i5z");
		// InputStream in =
		// I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-f5dd46ce-6fc9-316f-a468-c4f9acfcfc3c.i5z");
		Assert.assertNotNull(in);
		File i5z = File.createTempFile("test_", ".i5z");
		try {
			DownloadTool.download(in, i5z);
		} finally {
			in.close();
		}
		Assert.assertTrue(i5z.exists());
		I5ZReader reader = null;
		int records = 0;
		try {
			reader = new I5ZReader(i5z);
			QASettings qa = new QASettings(false);
			qa.setAll();
			reader.setQASettings(qa);
			PropertyKey key = new ReferenceSubstanceUUID();
			records = write(null,reader, c.getConnection(), key, true, false, false);
		} finally {
			try {
				reader.close();
			} catch (Exception x) {
			}
			try {
				c.close();
			} catch (Exception x) {
			}
			try {
				i5z.delete();
			} catch (Exception x) {
			}
		}

		Assert.assertEquals(8, records);

		c = getConnection();
		ITable substance = c.createQueryTable("EXPECTED",
				"SELECT * FROM substance");
		Assert.assertEquals(1, substance.getRowCount());
		Assert.assertNotNull(substance.getValue(0, "uuid"));

		chemicals = c.createQueryTable("EXPECTED", "SELECT * FROM chemicals");
		Assert.assertEquals(1, chemicals.getRowCount());
		// there are two empty file without $$$$ sign, which are skipped
		strucs = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
		Assert.assertEquals(1, strucs.getRowCount());
		srcdataset = c.createQueryTable("EXPECTED",
				"SELECT * FROM src_dataset where name='IUCLID5 .i5z file'");
		Assert.assertEquals(1, srcdataset.getRowCount());
		struc_src = c.createQueryTable("EXPECTED",
				"SELECT * FROM struc_dataset");
		Assert.assertEquals(1, struc_src.getRowCount());

		property = c
				.createQueryTable(
						"EXPECTED",
						"SELECT * FROM substance_protocolapplication where topcategory='P-CHEM' and endpointcategory='ASPECT_RATIO_SHAPE_SECTION' and interpretation_result='spherical'");
		Assert.assertEquals(1, property.getRowCount());


		property = c
				.createQueryTable(
						"EXPECTED",
						"SELECT * FROM substance_experiment where topcategory='P-CHEM' and endpointcategory='ASPECT_RATIO_SHAPE_SECTION'");
		Assert.assertEquals(4, property.getRowCount());


		c.close();
	}

}
