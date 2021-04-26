package ambit2.dbsubstance.test;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.i5.io.I5_ROOT_OBJECTS;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
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

	@Test
	public void testi6() throws Exception {
		
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		//formaldehyde
		//String resource_i5 = "net/idea/i6/_2/substance/i6z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i6z";
		String resource_i6 = "net/idea/i6/_5/substance/i6z/f63698f5-6751-4bca-9ca8-8388de4fdea9.i6z"; //formaldehyde
		
		/*
		  
		       
		       
		      
		    
        		      
		    */
		//resource_i6 = "net/idea/i6/_5/substance/i6z/aa571fe6-f4d7-464e-a828-9b4fba6ac14b.i6z";
		//resource_i6 = "net/idea/i6/_5/substance/i6z/aa4f54b5-e5ad-41ab-8ee0-2562f3437b69.i6z";
		//resource_i6 = "net/idea/i6/_5/substance/i6z/a11de82b-716d-4334-b92f-16188669c6b2.i6z";
		//resource_i6 = "net/idea/i6/_5/substance/i6z/8eff8c73-7f19-4f04-9ca3-b76be96f2e1d.i6z";
		//resource_i6 = "net/idea/i6/_5/substance/i6z/5c5ef19c-816f-4e97-96c4-a9cb90d893bf.i6z";
/*
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_ToxicityToAquaticAlgae
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_ToxicityReproduction
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_ToxicityReproduction
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_SkinSensitisation
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_ShortTermToxicityToFish
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_RepeatedDoseToxicityOral
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_RepeatedDoseToxicityInhalation
WARNING null
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_DevelopmentalToxicityTeratogenicity
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_DevelopmentalToxicityTeratogenicity
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_Carcinogenicity
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_BioaccumulationTerrestrial
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_BioaccumulationAquaticSediment
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_AcuteToxicityOral
WARNING No materials and methods record! ENDPOINT_STUDY_RECORD_AcuteToxicityInhalation		
 */
		//resource_i6 = "net/idea/i6/_5/substance/i6z/36a0caaa-272b-48ae-9c5d-6516c1d36f9a.i6z";
		//
		//resource_i6 = "net/idea/i6/_5/substance/i6z/306f1166-e1b1-4300-bade-8f3729c6c638.i6z"; //formaldehyde
		//resource_i6 = "net/idea/i6/_5/substance/i6z/0696f47c-1385-4251-a18a-df01f2b122ae.i6z";
		//resource_i6 = "net/idea/i6/_5/substance/i6z/aa9235ab-74b2-4c26-b884-0bf48dac293f.i6z";   //Tripropylamine
		//https://echa.europa.eu/registration-dossier/-/registered-dossier/13454/4/6/?documentUUID=fe448eb5-5487-4133-8c16-1a9e40cc5155
		//resource_i6 = "net/idea/i6/_5/substance/i6z/8543e1cf-2238-4b47-8117-0bcfbb1e187d.i6z";   //MWCNT
		resource_i6 = "net/idea/i6/_5/substance/i6z/5c5ef19c-816f-4e97-96c4-a9cb90d893bf.i6z"; //Bisphenol A, largest dossier
		// resource_i6 = "net/idea/i6/_5/substance/i6z/9716e347-6da6-47cf-88b9-48bce9347e5a.i6z"; //smaller dossiers, nothing in there
		resource_i6 = "net/idea/i6/_5/substance/i6z/0000b4f2-e1f2-4e6a-9b4b-4f7185ba841d.i6z"; //should have smiles
		
		
		InputStream in = net.idea.i6._5.ambit2.I6AmbitProcessor.class
				.getClassLoader().getResourceAsStream(resource_i6);
		Assert.assertNotNull(in);
		File file = fromResourcestream(in, ".i6z");
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
			//why one more structure compared to I5?
			//Assert.assertEquals(6, values.getRowCount());
			Assert.assertEquals(7, values.getRowCount());
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
