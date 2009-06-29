package ambit2.base.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.processors.CASProcessor;

public class CASProcessorTest {
	@Test
	public void test() throws Exception {
		CASProcessor p = new CASProcessor();
		Assert.assertEquals("123-45-6",p.process("0000123456"));
		Assert.assertEquals("50-00-0",p.process("000050000"));
		Assert.assertEquals("130032-94-9",p.process("130032949"));
		Assert.assertEquals("18903-01-0",p.process("018903010"));
		
		
	}
}
