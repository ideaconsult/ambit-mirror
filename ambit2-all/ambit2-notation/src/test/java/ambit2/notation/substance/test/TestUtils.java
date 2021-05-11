package ambit2.notation.substance.test;

import java.io.File;

import ambit2.notation.NotationConfig;

public class TestUtils 
{
	public static void testNotationConfigFromJSONFile(String fileName) throws Exception 
	{
		NotationConfig notCfg = NotationConfig.loadFromJSON(new File (fileName));
		System.out.println(notCfg.toString());
	}
}
