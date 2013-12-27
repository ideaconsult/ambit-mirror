package ambit2.base.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.processors.CASProcessor;

public class CASProcessorTest {
	@Test
	public void test() throws Exception {
		CASProcessor p = new CASProcessor();
	
		//Assert.assertEquals("123-45-6",p.process("0000123456"));

		Assert.assertEquals("50-00-0",p.process("000050000"));
		Assert.assertEquals("130032-94-9",p.process("130032949"));
		Assert.assertEquals("18903-01-0",p.process("018903010"));
		
		Assert.assertEquals("18903-01-0",p.process("018903-01-0"));
		
	}
	
	@Test
	public void testIsValidFormatCAS() {
		Assert.assertFalse(CASProcessor.isValidFormat("206-316-7"));
	}	
	
	@Test
	public void testIsValidFormatCAS2() {
		Assert.assertTrue(CASProcessor.isValidFormat("1019330-07-4"));
	}		
		
	@Test
	public void testIsValidFormatCAS1() {
		Assert.assertTrue(CASProcessor.isValidFormat("123-45-6"));
		Assert.assertTrue(CASProcessor.isValidFormat("130032-94-9"));
		Assert.assertTrue(CASProcessor.isValidFormat("18903-01-0"));
	}	
	
	@Test
	public void testLineSeparator() {
		String test = "abc\r\nxyz";
		String testx = "abc\nxyz";
		String ux = "(?<!\r)\n";
		Pattern p = Pattern.compile(ux);
		Matcher m = p.matcher(test);
		Assert.assertFalse(m.find());
		m = p.matcher(testx);
		Assert.assertTrue(m.find());
		
		
		Assert.assertEquals(test,test.replaceAll(ux,"\r\n"));
		Assert.assertEquals(test,testx.replaceAll(ux,"\r\n"));
	}	
	
	
}
