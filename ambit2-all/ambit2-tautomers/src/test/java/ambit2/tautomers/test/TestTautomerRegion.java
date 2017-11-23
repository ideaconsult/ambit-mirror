package ambit2.tautomers.test;

import java.util.List;

import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsHelper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTautomerRegion extends TestCase
{
	public LoggingTool logger;
	//SmartsHelper helper = new SmartsHelper(SilentChemObjectBuilder.getInstance());
	
	public TestTautomerRegion()
	{
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(TestTautomerRegion.class);
	}
	
	void checkNitroGroupPistions(String smiles, List<Integer[]> expectedPos)
	{
		//TODO
	}
	
	public void test0()
	{
		//TODO  
	}
	
}
