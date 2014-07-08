package ambit2.pubchem.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.pubchem.FileNotFoundException;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.pubchem.NCISearchProcessor.METHODS;
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
	public void testSDF() throws Exception {
		processor.setOption(METHODS.sdf);
		System.out.println(processor.process("50-00-0"));
	}
	@Test
	public void testSDF_404() throws Exception {
		processor.setOption(METHODS.sdf);
		System.out.println(processor.process("102242-50-2"));
	}
	
	@Test
	public void testSDFNotAvailable() throws Exception {
		try {
			processor.setOption(METHODS.sdf);
			System.out.println(processor.process("20827-47-8"));
			Assert.assertTrue(false);
		} catch (FileNotFoundException x) {
			Assert.assertTrue(true);
		}
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
	
	public static void main(String args[]) {
		SearchApplication app = new SearchApplication();
		app.main(args);
		
	}
}
