package ambit2.test.io;

import java.io.FileReader;

import ambit2.io.RawIteratingSDFReader;
import junit.framework.TestCase;

public class TestRawIteratingSDFReader extends TestCase {
	public void test() throws Exception  {
		String filename = "";
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
		while (reader.hasNext()) {
			Object o = reader.next();
			//System.out.println(o);
		}
		reader.close();
	}
}
