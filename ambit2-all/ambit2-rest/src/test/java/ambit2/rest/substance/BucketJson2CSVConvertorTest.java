package ambit2.rest.substance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Test;

import ambit2.rest.study.BucketJson2CSVConvertor;

public class BucketJson2CSVConvertorTest {
	@Test
	public void test() throws Exception {
		String[] headers = new String[] { "dbtag_hss", "name_hs", "publicname_hs", "owner_name_hs", "substanceType_hs",
				"substance_uuid" };
		String[] paramHeaders = { "document_uuid_s", "Dispersion protocol_s", "MEDIUM_s", "Vial_s" };
		String[] studyHeaders = {"s_uuid_s","topcategory_s",
				"endpointcategory_s","guidance_s","effectendpoint_s","reference_owner_s","reference_year_s","reference_s","loQualifier_s","loValue_d","upQualifier_s","upValue_d","unit_s","err_d","errQualifier_s","textValue_s"};
		String[] conditionHeaders = {"DATA_GATHERING_INSTRUMENTS_s","size_measurement_s","size_measurement_type_s"};
		BucketJson2CSVConvertor c = new BucketJson2CSVConvertor(headers, paramHeaders,studyHeaders,conditionHeaders);
		File temp = File.createTempFile("json2tsv", ".tsv");
		temp.deleteOnExit();
		System.out.println(temp.getAbsolutePath());
		try (FileOutputStream out = new FileOutputStream(temp)) {
			c.setOut(out);
			try (InputStream in = getClass().getClassLoader().getResourceAsStream("response.json")) {
				c.process(in);
			} catch (Exception x) {
				throw x;
			}
		} catch (Exception xx) {
			xx.printStackTrace();
		}
	}
}
