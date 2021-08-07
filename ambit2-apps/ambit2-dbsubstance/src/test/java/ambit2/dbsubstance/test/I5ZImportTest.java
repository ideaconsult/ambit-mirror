package ambit2.dbsubstance.test;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Properties;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Test;

import ambit2.base.io.DownloadTool;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.dbsubstance.DBSubstanceImport;

/**
 * <pre>
 *   -c,--config <file>                    Config file (DB connection
 *                                        parameters)
 *  -h,--help                             This help
 *  -i,--input <file>                     Input file or folder
 *  -j,--json <file>                      JSON config file
 *  -m,--clearMeasurements <value>        true|false
 *  -o,--output <file>                    Output file
 *  -p,--parser <type>                    File parser mode :
 *                                        i5z|xlsx|xls|nanowiki
 *  -r,--maxReferenceSubstances <value>   Maximum reference substances in
 * .i5z archive
 *  -s,--isSplitRecord <value>            true|false
 *  -t,--clearComposotion <value>         true|false
 *  -x,--structureMatch <value>           Match structure by
 *                                        uuid|cas|einecs|smiles|inchi
 * </pre>
 * 
 */
public class I5ZImportTest extends DbUnitTest {
	final String i66_ironore = "net/idea/i6/_6/substance/i6z/56e49ed8-0bec-49a2-8050-f8e87844b2e8";
	final String i66_formaldehyde = "net/idea/i6/_6/substance/i6z/04e6dcc1-42bf-4e24-8be2-7f23ba7d3fb5";
	final String i66_mwcnt = "net/idea/i6/_6/substance/i6z/366325e3-c0e8-4381-a1ab-c7d2ae2c298e";
	final String release_tag = "2021-02-19 00:00:00.0";

	@Test
	public void testi5() throws Exception {

		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		String resource_i5 = "net/idea/i5/_5/substance/i5z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i5z";
		InputStream in = net.idea.i5._5.ambit2.I5AmbitProcessor.class
				.getClassLoader().getResourceAsStream(resource_i5);
		Assert.assertNotNull(in);
		File file = fromResourcestream(in, ".i5z");
		file.deleteOnExit();
		System.out.println(file);

		String resource_config = "ambit2/db/conf/test.properties";

		in = DbUnitTest.class.getClassLoader().getResourceAsStream(
				resource_config);
		File fileconfig = fromResourcestream(in, ".properties");
		fileconfig.deleteOnExit();
		System.out.println(fileconfig);

		String[] args = new String[] { "-i", file.getAbsolutePath(), "-c",
				fileconfig.getAbsolutePath(), "-m", "true", "-t", "true", "-r",
				"-1", "x", "einecs" };
		
		DBSubstanceImport.main(args);

		IDatabaseConnection c = getConnection();
		try {
			ITable table = c.createQueryTable("EXPECTED_SUBSTANCES",
					"SELECT prefix,hex(uuid) as huuid,rs_prefix,hex(rs_uuid) as rs_huuid FROM substance");
			Assert.assertEquals(1, table.getRowCount());
			Assert.assertNotNull(table.getValue(0,"huuid"));
			Assert.assertNotNull(table.getValue(0,"rs_huuid"));
			ITable values = c.createQueryTable("EXPECTED_STRUCTURES",
					"SELECT * FROM structure ");
			Assert.assertEquals(6, values.getRowCount());
			//structures should not be empty
			values = c.createQueryTable("EXPECTED_STRUCTURES",
					"SELECT count(*) as c FROM structure where format='SDF' and type_structure!='NA' and uncompress(structure) regexp 'M  END' ");
			Assert.assertEquals(BigInteger.valueOf(4),values.getValue(0, "c"));

			values = c.createQueryTable("EXPECTED_CHEMICALS",
					"SELECT * FROM chemicals");
			Assert.assertEquals(6, values.getRowCount());
			values = c
					.createQueryTable(
							"EXPECTED_ref_subst_uuids",
							"SELECT * FROM properties join catalog_references using(idreference) where name='I5UUID'");
			Assert.assertEquals(7, values.getRowCount());
		} finally {
			c.close();
		}

	}
	protected Properties getTestProperties(String resource) {
		Properties properties = new Properties();
		try (InputStream in = I5ZImportTest.class.getClassLoader().getResourceAsStream(resource)) {
			properties.load(in);	
		} catch (Exception x) {
			x.printStackTrace();
		}
		return properties;
	}
	@Test
	public void testProperties() {
		
		Properties endpoints = getTestProperties(i66_ironore+".properties");
		Assert.assertNotNull(endpoints);

	}
	@Test
	public void testi6_ironore() throws Exception {
		testi6(i66_ironore,35,false);
	}
	@Test
	public void testi6_formaldehyde() throws Exception {
		testi6(i66_formaldehyde,3,true);
	}	
	@Test
	public void testi6_mwcnt() throws Exception {
		testi6(i66_mwcnt,4,false);
	}	
		
	@Test
	public void testi6_formaldehyde2() throws Exception {
		testi6("net/idea/i6/_6/substance/i6z/afd3c0dd-b398-4a4d-864e-afeefc324cf5",1,false);
	}	
	public void testi6(String resource_i6, int expected_structures, boolean checkstructures) throws Exception {
		try (InputStream in = net.idea.i6._6.ambit2.I6AmbitProcessor.class
				.getClassLoader().getResourceAsStream(resource_i6+".i6z")) {
			Assert.assertNotNull(in);
			File file = fromResourcestream(in, ".i6z");
			file.deleteOnExit();
			Properties endpoints = getTestProperties(resource_i6+".properties");
			Assert.assertTrue(endpoints.size()>0);
			testi6(file,expected_structures,checkstructures,endpoints);
		} catch (Exception x) {
			throw x;
		}
	}
	public void testi6(File file , int expected_structures, boolean checkstructures, Properties expected_endpoints) throws Exception {
		Assert.assertTrue(file.exists());
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
<<<<<<< HEAD

		String resource_i6 = "net/idea/i6/_5/substance/i6z/f63698f5-6751-4bca-9ca8-8388de4fdea9.i6z"; //formaldehyde
	
		
		InputStream in = net.idea.i6._5.ambit2.I6AmbitProcessor.class
				.getClassLoader().getResourceAsStream(resource_i6);
		Assert.assertNotNull(in);
		File file = fromResourcestream(in, ".i6z");
		file.deleteOnExit();

		System.out.println(file);

		String resource_config = "ambit2/db/conf/test.properties";
		File fileconfig = null;
		try (InputStream in = DbUnitTest.class.getClassLoader().getResourceAsStream(
				resource_config)) {
			fileconfig = fromResourcestream(in, ".properties");
			fileconfig.deleteOnExit();
			System.out.println(fileconfig);
		}
		String[] args = new String[] { "-i", file.getAbsolutePath(), "-c",
				fileconfig.getAbsolutePath(), "-m", "true", "-t", "true", "-r",
				"-1", "x", "einecs","-d","false", "-a",release_tag };
		
		DBSubstanceImport.main(args);

		IDatabaseConnection c = getConnection();
		try {
			ITable table = c.createQueryTable("EXPECTED_SUBSTANCES",
					"SELECT prefix,hex(uuid) as huuid,rs_prefix,hex(rs_uuid) as rs_huuid FROM substance");
			Assert.assertEquals(1, table.getRowCount());
			Assert.assertNotNull(table.getValue(0,"huuid"));
			Assert.assertNotNull(table.getValue(0,"rs_huuid"));
			ITable values = c.createQueryTable("EXPECTED_STRUCTURES",
					"SELECT * FROM structure ");
			//why one more structure compared to I5?
			//Assert.assertEquals(6, values.getRowCount());
			Assert.assertEquals(expected_structures, values.getRowCount());

			values = c.createQueryTable("release",
					"SELECT updated from substance_protocolapplication  GROUP BY updated ");
			Assert.assertEquals(1, values.getRowCount());
			Assert.assertEquals(release_tag,values.getValue(0, "updated").toString());

			if (expected_endpoints!=null)
				expected_endpoints.forEach((type, value) -> {
					String sql = String.format("SELECT endpointcategory,COUNT(*) as c from substance_protocolapplication WHERE endpointcategory='%s' GROUP BY topcategory,endpointcategory",type);
					
					try {
						final ITable t = c.createQueryTable("ENDPOINT_STUDYRECORD",sql);
					
					Assert.assertEquals(BigInteger.valueOf(Long.parseLong(value.toString())),t.getValue(0, "c"));
										
					} catch (Exception x) {
						x.printStackTrace();
					}
				});
					
	

			//structures should not be empty
			if (checkstructures) {
			values = c.createQueryTable("EXPECTED_STRUCTURES",
					"SELECT count(*) as c FROM structure where format='SDF' and type_structure!='NA' and uncompress(structure) regexp 'M  END' ");
			Assert.assertEquals(BigInteger.valueOf(4),values.getValue(0, "c"));

			values = c.createQueryTable("EXPECTED_CHEMICALS",
					"SELECT * FROM chemicals");
			Assert.assertEquals(6, values.getRowCount());
			values = c
					.createQueryTable(
							"EXPECTED_ref_subst_uuids",
							"SELECT * FROM properties join catalog_references using(idreference) where name='I5UUID'");
			Assert.assertEquals(7, values.getRowCount());
			}
			
		} finally {
			c.close();
		}

	}
	
	private File fromResourcestream(InputStream in, String ext)
			throws Exception {
		File file;
		try {
			file = File.createTempFile("test", ext);
			file.deleteOnExit();
			DownloadTool.download(in, file);
			Assert.assertTrue(file.exists());
			return file;
		} catch (Exception x) {
			throw x;
		} finally {
			in.close();
		}

	}
}
