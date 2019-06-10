package ambit2.rest.substance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import ambit2.rest.study.BucketJson2CSVConvertor;

public class BucketJson2CSVConvertorTest {
	@Test
	public void test() throws Exception {
		String delimiter=",";
		String ext = ".csv";
		
		delimiter="\t";
		ext = ".tsv";
		BucketJson2CSVConvertor c = new BucketJson2CSVConvertor(delimiter);
		
		File temp = File.createTempFile("json2tsv", ext);
		//temp.deleteOnExit();

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
		System.out.println(temp.getAbsolutePath());
		CSVFormat format = CSVFormat.TDF;
		System.out.println(format.getDelimiter());
		try (FileReader reader = new FileReader(temp)) {
			CSVParser parser = format.withHeader().parse(reader);
			Iterator<CSVRecord> i  =parser.iterator();
			while (i.hasNext()) {
				CSVRecord record = i.next();
				Iterator<String> v = record.iterator();
				int fields = 0;
				while (v.hasNext()) {
					String value = v.next();
					//System.out.println(value);
					fields++;
				}
				//System.out.println(String.format("%d%s%d%s%d",record.getRecordNumber(),delimiter,record.size(),delimiter,fields));
			}
		}
		
	}
}
