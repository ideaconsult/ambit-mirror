package ambit2.pubchem.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.pubchem.NCISearchProcessor;
import ambit2.pubchem.SearchApplication;

public class NCISearchProcessorTest {
	protected NCISearchProcessor processor;


	@Before
	public void setUp() throws Exception {
		processor = new NCISearchProcessor();
	}

	@After
	public void tearDown() throws Exception {
		processor = null;
	}
	
	@Test	
	public void test() throws Exception {
		List<String> ids = new ArrayList<String>();
		process("/pug/cas.txt", 0, 1,ids);
	}	
	public void process(String filename, int numids, int error, List<String> ids) throws Exception {
		System.out.println("ambit2/pubchem"+filename);
		SearchApplication app = new SearchApplication();

		app.processCactus("src/test/resources/ambit2/pubchem"+filename,"",null,10000,null);
	}
}
