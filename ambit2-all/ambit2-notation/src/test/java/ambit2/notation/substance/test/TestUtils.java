package ambit2.notation.substance.test;

import java.io.File;

import org.junit.Test;

import ambit2.notation.NotationConfig;

public class TestUtils 
{
	
	@Test
	public void fakeTest() {
		// there should be at least one test
	}
	
	public static void testNotationConfigFromJSONFile(String fileName) throws Exception 
	{
		NotationConfig notCfg = NotationConfig.loadFromJSON(new File (fileName));
		if (notCfg.errors.isEmpty())
			System.out.println(notCfg.toJSONString());
		else {
			System.out.println("Notation configuration errors:");
			for (String err: notCfg.errors)
				System.out.println(err);
		}	
	}
}
